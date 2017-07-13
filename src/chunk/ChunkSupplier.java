package chunk;

import static chunk.Chunk.SIDE_LENGTH;
import static util.MathUtils.clamp;
import static util.MathUtils.mixColors;
import util.Noise;

public class ChunkSupplier {

    private static final int OCTAVES = 4;
    private static final double FREQUENCY = 1 / 1500.;
    private static final double HEIGHT = 150;

    public static final double MAX_Z = 2. * HEIGHT / SIDE_LENGTH;

    private final Noise noise;

    public ChunkSupplier(double seed) {
        noise = new Noise(seed);
    }

    public ChunkSupplier() {
        this(Math.random() * 1e6);
    }

    public BlockColumns getLOD(int x, int y, int lod) {
        int blockDownsampling = Math.min(4 * lod, SIDE_LENGTH);
        int colorDownsampling = Math.min(8 * lod, SIDE_LENGTH);

        double[][] elevation = fbmDownsample(noise, x, y, OCTAVES, FREQUENCY, blockDownsampling);
        double[][] temperature = fbmDownsample(noise, 10000 + x, y, 1, 1 / 2000., colorDownsampling);
        double[][] humidity = fbmDownsample(noise, 20000 + x, y, 1, 1 / 2000., colorDownsampling);
        int[][] biomes = {
            {0x20D600, 0x4CA127, 0x38761D, 0x274E13, 0xFFFFFF},
            {0x20D600, 0x4CA127, 0x38761D, 0x274E13, 0xFFFFFF},
            {0x20D600, 0x4CA127, 0x38761D, 0x7F745D, 0xFFFFFF},
            {0x6FC44C, 0x6FC44C, 0x6FC44C, 0x7F745D, 0xFFFFFF},
            {0xFFFA69, 0xFFFA69, 0xD6C794, 0x7F745D, 0x999999}};
        //double[][] red = fbmDownsample(noise, 1000 + x, y, 1, 1 / 400., colorDownsampling);
        //double[][][] green = fbmDownsample(noise, 2000 + x, y, z, 4, 1 / 200., colorDownsampling);
        //double[][][] blue = fbmDownsample(noise, 3000 + x, y, z, 4, 1 / 200., colorDownsampling);

        BlockColumns ba = new BlockColumns(SIDE_LENGTH / lod + 2);
        for (int i = -1; i <= SIDE_LENGTH / lod; i++) {
            for (int j = -1; j <= SIDE_LENGTH / lod; j++) {
                double cutoffAdd = (lod == 1 || (i >= 0 && j >= 0 && i < SIDE_LENGTH / lod && j < SIDE_LENGTH / lod)) ? 0 : lod;
                int height = (int) Math.round(sample(elevation, i, j, blockDownsampling / lod) * HEIGHT / lod - cutoffAdd);

                double temp = clamp(2 + sample(temperature, i, j, colorDownsampling / lod) + sample(elevation, i, j, blockDownsampling / lod), 0, 3.999);
                double hum = clamp(2 + 2 * sample(humidity, i, j, colorDownsampling / lod), 0, 3.999);
                int c0 = mixColors(biomes[(int) hum][(int) temp], biomes[(int) hum + 1][(int) temp], hum - (int) hum);
                int c1 = mixColors(biomes[(int) hum][(int) temp + 1], biomes[(int) hum + 1][(int) temp + 1], hum - (int) hum);
                int color = mixColors(c0, c1, temp - (int) temp);

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

    private static double[][] fbmDownsample(Noise noise, int x, int y, int octaves, double frequency, int downSampling) {
        double[][] samples = new double[SIDE_LENGTH / downSampling + 2][SIDE_LENGTH / downSampling + 2];
        for (int i = -1; i <= SIDE_LENGTH / downSampling; i++) {
            for (int j = -1; j <= SIDE_LENGTH / downSampling; j++) {
                samples[i + 1][j + 1] = noise.fbm(x * SIDE_LENGTH + i * downSampling, y * SIDE_LENGTH + j * downSampling, octaves, frequency);
            }
        }
        return samples;
    }

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

    private static int validateColor(double x) {
        return clamp((int) x, 0, 255);
    }
}
