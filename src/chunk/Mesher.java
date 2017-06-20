package chunk;

import graphics.SurfaceGroup.Surface;
import java.util.LinkedList;
import java.util.List;
import org.joml.Vector3i;
import static util.MathUtils.dirPos;
import static util.MathUtils.orderComponents;

public abstract class Mesher {

    public static class Quad {

        public final int x, y, w, h;

        public Quad(int x, int y, int w, int h) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }

        public Surface createSurface(int v, Vector3i dir) {
            Surface s = new Surface();
            s.corners = new Vector3i[]{orderComponents(v + dirPos(dir), dir, x, y), orderComponents(v + dirPos(dir), dir, x + w, y),
                orderComponents(v + dirPos(dir), dir, x, y + h), orderComponents(v + dirPos(dir), dir, x + w, y + h)};
            s.colorTexture = new float[w][h][3];
            s.shadeTexture = new float[w + 1][h + 1];
            return s;
        }

        private boolean containedIn(boolean[][] a) {
            for (int i = x; i < x + w; i++) {
                for (int j = y; j < y + h; j++) {
                    if (!a[i][j]) {
                        return false;
                    }
                }
            }
            return true;
        }

        private void setRegion(boolean[][] a, boolean val) {
            for (int i = x; i < x + w; i++) {
                for (int j = y; j < y + h; j++) {
                    a[i][j] = val;
                }
            }
        }

        @Override
        public String toString() {
            return "Quad{" + "x=" + x + ", y=" + y + ", w=" + w + ", h=" + h + '}';
        }
    }

    public static List<Quad> mesh(boolean[][] a) {
        List<Quad> r = new LinkedList();
        for (int x = 0; x < a.length; x++) {
            for (int y = 0; y < a[0].length; y++) {
                if (a[x][y]) {
                    int w = 1, h = 1;
                    while (x + w < a.length && a[x + w][y]) {
                        w++;
                    }
                    while (y + h < a[0].length && new Quad(x, y + h, w, 1).containedIn(a)) {
                        h++;
                    }
                    new Quad(x, y, w, h).setRegion(a, false);
                    r.add(new Quad(x, y, w, h));
                }
            }
        }
        return r;
    }

    public static void main(String[] args) {
        boolean[][] test = {
            {true, true},
            {true, false}};
        System.out.println(mesh(test));
    }
}
