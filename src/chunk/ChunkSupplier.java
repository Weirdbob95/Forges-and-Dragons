package chunk;

import static chunk.Chunk.SIDE_LENGTH;
import java.util.function.DoubleBinaryOperator;
import static util.MathUtils.clamp;
import static util.MathUtils.mixColors;
import util.Noise;

public class ChunkSupplier {

    private static final double BIOME_FREQUENCY = 1 / 2000.;
    private static final double DETAIL_FREQUENCY = 1 / 250.;
    private static final int DETAIL_OCTAVES = 6;
    private static final double SCALE = .2;

    public static final double MAX_Z = 2. * (SCALE / BIOME_FREQUENCY) / SIDE_LENGTH;

    private final double seed;

    public ChunkSupplier(double seed) {
        this.seed = seed;
    }

    public ChunkSupplier() {
        this(Math.random() * 1e6);
    }

    public BlockColumns getLOD(int chunkX, int chunkY, int lod) {
        int detailDownsampling = Math.min(4 * lod, SIDE_LENGTH);
        int biomeDownsampling = Math.min(16 * lod, SIDE_LENGTH);

        double[][] temperatureArray = downsample(chunkX, chunkY, biomeDownsampling, (x, y) -> new Noise(seed).perlin(x, y, BIOME_FREQUENCY));
        double[][] humidityArray = downsample(chunkX, chunkY, biomeDownsampling, (x, y) -> new Noise(seed + 1).perlin(x, y, BIOME_FREQUENCY));
        double[][] elevationArray = downsample(chunkX, chunkY, biomeDownsampling, (x, y) -> new Noise(seed + 2).multi(x, y, 3, BIOME_FREQUENCY));
        //double[][] elevationArray = downsample(chunkX, chunkY, biomeDownsampling, (x, y) -> new Noise(seed + 2).perlin(x, y, BIOME_FREQUENCY));
        double[][] detailArray = downsample(chunkX, chunkY, detailDownsampling, (x, y) -> new Noise(seed + 3).multi(x, y, DETAIL_OCTAVES, DETAIL_FREQUENCY));
        int[][] biomes = {
            {0x20D600, 0x4CA127, 0x38761D, 0x274E13, 0xFFFFFF},
            {0x20D600, 0x4CA127, 0x38761D, 0x274E13, 0xFFFFFF},
            {0x20D600, 0x4CA127, 0x38761D, 0x7F745D, 0xFFFFFF},
            {0x6FC44C, 0x6FC44C, 0x6FC44C, 0x7F745D, 0xFFFFFF},
            {0xFFFA69, 0xFFFA69, 0xD6C794, 0xD6C794, 0x999999}};

        BlockColumns ba = new BlockColumns(SIDE_LENGTH / lod + 2);
        for (int i = -1; i <= SIDE_LENGTH / lod; i++) {
            for (int j = -1; j <= SIDE_LENGTH / lod; j++) {

                double temperature = sample(temperatureArray, i, j, biomeDownsampling / lod);
                double humidity = sample(humidityArray, i, j, biomeDownsampling / lod);
                double elevation = sample(elevationArray, i, j, biomeDownsampling / lod);
                double detail = sample(detailArray, i, j, detailDownsampling / lod);

                detail *= (elevation + .15) * (temperature + .5) * .3;
                elevation *= (temperature + .15);

                double cutoffAdd = (lod == 1 || (i >= 0 && j >= 0 && i < SIDE_LENGTH / lod && j < SIDE_LENGTH / lod)) ? 0 : lod;
                int height = (int) Math.round((elevation + detail) * (SCALE / BIOME_FREQUENCY) / lod - cutoffAdd);

                double temp = clamp(5 * temperature - .5, 0, 3.9999);
                double hum = clamp(5 * humidity - .5, 0, 3.9999);
                int c0 = mixColors(biomes[(int) hum][(int) temp], biomes[(int) hum + 1][(int) temp], sigmoid(hum - (int) hum));
                int c1 = mixColors(biomes[(int) hum][(int) temp + 1], biomes[(int) hum + 1][(int) temp + 1], sigmoid(hum - (int) hum));
                int color = mixColors(c0, c1, sigmoid(temp - (int) temp));

//                if (height < 0) {
//                    color = 0x0060FF;
//                }
//                int r = validateColor(200 * sample(red, i, j, colorDownsampling / lod));
//                int g = validateColor(220 + 30 * sample(red, i, j, colorDownsampling / lod));// - (z * SIDE_LENGTH + k * lod) / 1.);
//                int b = validateColor(-100 * sample(red, i, j, colorDownsampling / lod));
                ba.setBlockRangeInfinite(i, j, height, color);
                //ba.setBlockRangeInfinite(i, j, height - 1, colorToGrayscale(color));
            }
        }
        return ba;
    }

    private static double[][] downsample(int x, int y, int downSampling, DoubleBinaryOperator function) {
        double[][] samples = new double[SIDE_LENGTH / downSampling + 2][SIDE_LENGTH / downSampling + 2];
        for (int i = -1; i <= SIDE_LENGTH / downSampling; i++) {
            for (int j = -1; j <= SIDE_LENGTH / downSampling; j++) {
                samples[i + 1][j + 1] = function.applyAsDouble((double) x * SIDE_LENGTH + i * downSampling, (double) y * SIDE_LENGTH + j * downSampling);
            }
        }
        return samples;
    }

//    private static double[][] multiDownsample(double seed, int x, int y, int octaves, double frequency, int downSampling) {
//        Noise noise = new Noise(seed);
//        double[][] samples = new double[SIDE_LENGTH / downSampling + 2][SIDE_LENGTH / downSampling + 2];
//        for (int i = -1; i <= SIDE_LENGTH / downSampling; i++) {
//            for (int j = -1; j <= SIDE_LENGTH / downSampling; j++) {
//                samples[i + 1][j + 1] = noise.multi(x * SIDE_LENGTH + i * downSampling, y * SIDE_LENGTH + j * downSampling, octaves, frequency);
//            }
//        }
//        return samples;
//    }
    private static double sample(double[][] samples, double i, double j, int downSampling) {
        double x = i / downSampling;
        double y = j / downSampling;

        int x0 = (int) Math.floor(x);
        int x1 = (int) Math.ceil(x);
        int y0 = (int) Math.floor(y);
        int y1 = (int) Math.ceil(y);

        double xd = x - x0;
        double yd = y - y0;

        double c00 = samples[x0 + 1][y0 + 1] * (1 - xd) + samples[x1 + 1][y0 + 1] * xd;
        double c10 = samples[x0 + 1][y1 + 1] * (1 - xd) + samples[x1 + 1][y1 + 1] * xd;

        double c0 = c00 * (1 - yd) + c10 * yd;

        return c0;
    }

    private static double sigmoid(double x) {
        double sharpness = 10, mix = .5;
        return mix * s1(x) + (1 - mix) * s2(x, sharpness);
        //return mix * x + (1 - mix) / (Math.sinh(sharpness) / Math.tanh(sharpness * x) - Math.cosh(sharpness) + 1);
        //return mix * x + (1 - mix) * (1 - Math.exp(2*sharpness*x)) / (1 - Math.exp(sharpness) / (1 + Math.exp(sharpness * (2 * x - 1))));
        //return mix * x + (1 - mix) * (1 + Math.exp(-sharpness * .5)) / (1 + Math.exp(-sharpness*(x - .5)));
        //return x / 2 + .5 * ((1 / (1 + Math.exp(-sharpness * (x - .5))) - .5) * .5 / (.5 - 1 / (1 + Math.exp(-sharpness * (0 - .5)))) + .5);
    }

    private static double s1(double x) {
        return 2 * x * x - x * x * x;
    }

    private static double s2(double x, double s) {
        return .5 + .5 * Math.tanh(s * (x - .5)) / Math.tanh(s * .5);
    }
}
