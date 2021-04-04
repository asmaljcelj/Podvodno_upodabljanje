package fluid_generation;

import java.util.Random;

/**
 * second attempt at perlin noise algorithm
 */

public class PerlinNoiseGeneration {
    private int[] permutation;

    /**
     *
     * @param seed seed if desired to generate pseudo-random permutations. -1 means no specified seed
     */
    public PerlinNoiseGeneration(long seed) {
        generateHashTable(seed);
    }

    private void generateHashTable(Long seed) {
        Random r = new Random();
        if (seed != -1)
            r.setSeed(seed);
        this.permutation = new int[512];
        for (int i = 0; i < permutation.length; i++) {
            this.permutation[i] = r.nextInt(256);
        }
    }

    /**
     *
     * @param fromZeroToOne boolean value if returned value is in bounds [0, 1] or [-1, 1]
     * @return perlin noise value in specified point
     */
    public double perlin(double x, double y, double z, boolean fromZeroToOne) {
//        x %= 256;
//        y %= 256;
//        z %= 256;

//        int cubeX = (int) Math.floor(x);
//        int cubeY = (int) Math.floor(y);
//        int cubeZ = (int) Math.floor(z);

        int xi = (int) x & 255;
        int yi = (int) y & 255;
        int zi = (int) z & 255;

//        double relativeX = x - cubeX;
//        double relativeY = y - cubeY;
//        double relativeZ = z - cubeZ;

        double xf = x - (int) x;
        double yf = y - (int) y;
        double zf = z - (int) z;

//        double u = fade(relativeX);
//        double v = fade(relativeY);
//        double w = fade(relativeZ);

        double u = fade(xf);
        double v = fade(yf);
        double w = fade(zf);

//        int aaa = permutation[permutation[permutation[cubeX] + cubeY] + cubeZ];
//        int aba = permutation[permutation[permutation[cubeX] + inc(cubeY)] + cubeZ];
//        int aab = permutation[permutation[permutation[cubeX] + cubeY] + inc(cubeZ)];
//        int abb = permutation[permutation[permutation[cubeX] + inc(cubeY)] + inc(cubeZ)];
//        int baa = permutation[permutation[permutation[inc(cubeX)] + cubeY] + cubeZ];
//        int bba = permutation[permutation[permutation[inc(cubeX)] + inc(cubeY)] + cubeZ];
//        int bab = permutation[permutation[permutation[inc(cubeX)] + cubeY] + inc(cubeZ)];
//        int bbb = permutation[permutation[permutation[inc(cubeX)] + inc(cubeY)] + inc(cubeZ)];

        int aaa = permutation[permutation[permutation[xi] + yi] + zi];
        int aba = permutation[permutation[permutation[xi] + inc(yi)] + zi];
        int aab = permutation[permutation[permutation[xi] + yi] + inc(zi)];
        int abb = permutation[permutation[permutation[xi] + inc(yi)] + inc(zi)];
        int baa = permutation[permutation[permutation[inc(xi)] + yi] + zi];
        int bba = permutation[permutation[permutation[inc(xi)] + inc(yi)] + zi];
        int bab = permutation[permutation[permutation[inc(xi)] + yi] + inc(zi)];
        int bbb = permutation[permutation[permutation[inc(xi)] + inc(yi)] + inc(zi)];

//        double x1 = lerp(grad(aaa, relativeX, relativeY, relativeZ), grad(baa, relativeX - 1, relativeY, relativeZ), u);
//        double x2 = lerp(grad(aba, relativeX, relativeY - 1, relativeZ), grad(bba, relativeX - 1, relativeY - 1, relativeZ), u);
//        double y1 = lerp(x1, x2, v);

//        x1 = lerp(grad(aab, relativeX, relativeY, relativeZ - 1), grad(bab, relativeX - 1, relativeY, relativeZ - 1), u);
//        x2 = lerp(grad(abb, relativeX, relativeY - 1, relativeZ - 1), grad(bbb, relativeX - 1, relativeY - 1, relativeZ - 1), u);

        double x1 = lerp(grad(aaa, xf, yf, zf), grad(baa, xf - 1, yf, zf), u);
        double x2 = lerp(grad(aba, xf, yf - 1, zf), grad(bba, xf - 1, yf - 1, zf), u);
        double y1 = lerp(x1, x2, v);

        x1 = lerp(grad(aab, xf, yf, zf - 1), grad(bab, xf - 1, yf, zf - 1), u);
        x2 = lerp(grad(abb, xf, yf - 1, zf - 1), grad(bbb, xf - 1, yf - 1, zf - 1), u);

        double y2 = lerp(x1, x2, v);

        if (fromZeroToOne)
            return (lerp(y1, y2, w) + 1) / 2;
        return lerp(y1, y2, w);
    }

    private int inc(int a) {
        return a + 1;
    }

    private double grad(int hash, double x, double y, double z) {
        switch (hash & 0xF) {
            case 0x0:
                return x + y;
            case 0x1:
                return -x + y;
            case 0x2:
                return x - y;
            case 0x3:
                return -x - y;
            case 0x4:
                return x + z;
            case 0x5:
                return -x + z;
            case 0x6:
                return x - z;
            case 0x7:
                return -x - z;
            case 0x8:
                return y + z;
            case 0x9:
                return -y + z;
            case 0xA:
                return y - z;
            case 0xB:
                return -y - z;
            case 0xC:
                return y + x;
            case 0xD:
                return -y + z;
            case 0xE:
                return y - x;
            case 0xF:
                return -y - z;
            default:
                return 0;
        }
    }

    // fade function 6t^5-15t^4+10t^3
    private double fade(double t) {
        return t * t * t * (t * (6 * t - 15) + 10);
    }

    private double lerp(double a, double b, double x) {
        return a + x * (b - a);
    }

}
