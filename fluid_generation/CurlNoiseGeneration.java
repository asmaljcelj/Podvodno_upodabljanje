package fluid_generation;

public class CurlNoiseGeneration {

    public static void main(String[] args) {
        // for TESTING purposes
        //        PotentialField p = new PotentialField(256);
        //        p.calculateVelocityField(123456L, 0.2f);
        int size = 8;
        CurlNoiseGeneration c = new CurlNoiseGeneration();
        PotentialField p = c.calculatePotentialField(size, 123654L, 12345L, 0.2f, 1.4, 1.0);
        System.out.println("done with calculation. Results are: ");

        for (int i = p.potentialField.length - size; i >= 0; i -= size) {
            for (int j = 0; j < size; j++)
                System.out.printf("%s", p.potentialField[i + j].getPotential());
            System.out.println();
            if (i % (size * size) == 0)
                System.out.println("--------------------------------------------------------------------------------------------------");
        }
    }

    public PotentialField calculatePotentialField(int size, long curlSeed, long heightSeed, double dimensionStep, double heightBase, double heightDiff) {
        PotentialField potentialField = new PotentialField(size, heightBase, heightDiff);
        potentialField.calculateHeights(heightSeed, dimensionStep);
        potentialField.calculateVelocityField(curlSeed, dimensionStep);
        return potentialField;
    }

    static class PotentialField {
        int size;
        double heightBase;
        double heightDiff;
        double displacement = 0.0001;

        PotentialCell[] potentialField;
        double[] heights;


        public PotentialField(int size, double heightBase, double heightDiff) {
            this.size = size;
            this.heightBase = heightBase;
            this.heightDiff = heightDiff;
            this.potentialField = new PotentialCell[size * size * size];
            this.heights = new double[size * size];
        }

        public void calculateHeights(long seed, double dimensionStep) {
            PerlinNoiseGeneration p = new PerlinNoiseGeneration(seed);
            for (int i = 0; i < this.size; i++) {
                for (int j = 0; j < this.size; j++) {
                    double perlin = octavePerlin(j * dimensionStep, i * dimensionStep, 0, 4, 0.75, p);
                    this.heights[index2D(j, i)] = this.heightBase + perlin * this.heightDiff;
                }
            }
        }

        public void calculateVelocityField(long seed, double dimensionStep) {
            PerlinNoiseGeneration perlinNoiseGenerator2 = new PerlinNoiseGeneration(seed);
            for (int i = 0; i < this.size; i++) {
                for (int j = 0; j < this.size; j++) {
                    for (int k = 0; k < this.size; k++) {
                        PotentialCell p = new PotentialCell(k * dimensionStep, j * dimensionStep, i * dimensionStep);
                        if (i * dimensionStep > this.heights[index2D(k, j)])
                            p.setPotential(new Vector(0, 0, 0));
                        else
                            p.calculatePotential(displacement, perlinNoiseGenerator2);
                        this.potentialField[index3D(k, j, i)] = p;
                    }
                }
            }
        }

        public double octavePerlin(double x, double y, double z, int octaves, double persistance, PerlinNoiseGeneration p) {
            double total = 0;
            double frequency = 1;
            double amplitude = 1;
            double maxValue = 0;
            for (int i = 0; i < octaves; i++) {
                total += p.perlin(x * frequency, y * frequency, z * frequency, false) * amplitude;
                maxValue += amplitude;
                amplitude *= persistance;
                frequency *= 2;
            }
            return total / maxValue;
        }

        private int index3D(int x, int y, int z) {
            return (x + y * this.size + z * this.size * this.size);
        }

        private int index2D(int x, int y) {
            return (x + y * this.size);
        }

        public Vector getVectorAtPoint(int x, int y, int z) {
            return this.potentialField[index3D(x, y, z)].getPotential();
        }
    }

    static class PotentialCell {
        double x;
        double y;
        double z;
        Vector potential;

        public PotentialCell(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        private void calculatePotential(double displacement, PerlinNoiseGeneration p) {
            Vector base = calculatePotentialField(x, y, z, p);
            Vector displacedX = calculatePotentialField(x + displacement, y, z, p);
            Vector displacedY = calculatePotentialField(x, y + displacement, z, p);
            Vector displacedZ = calculatePotentialField(x, y, z + displacement, p);

            Vector derivativeX = displacedX.minus(base).divide(displacement);
            Vector derivativeY = displacedY.minus(base).divide(displacement);
            Vector derivativeZ = displacedZ.minus(base).divide(displacement);

            double xValue = derivativeY.getZ() - derivativeZ.getY();
            double yValue = derivativeZ.getX() - derivativeX.getZ();
            double zValue = derivativeX.getY() - derivativeY.getX();

            this.potential = new Vector(xValue, yValue, zValue);
        }

        private Vector calculatePotentialField(double x, double y, double z, PerlinNoiseGeneration p) {
            double valueX = p.perlin(x, y, z, true);
            double valueY = p.perlin(x + 180, y + 180, z + 180, true);
            double valueZ = p.perlin(x + 260, y + 260, z + 260, true);

            return new Vector(valueX, valueY, valueZ);
        }

        public void setPotential(Vector potential) {
            this.potential = potential;
        }

        public Vector getPotential() {
            return potential;
        }
    }

    static class Vector {
        double x;
        double y;
        double z;

        public Vector(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public double getZ() {
            return z;
        }

        public Vector minus(Vector v) {
            return new Vector(this.x - v.x, this.y - v.y, this.z - v.z);
        }

        public Vector divide(double divisor) {
            this.x = this.x / divisor;
            this.y = this.y / divisor;
            this.z = this.z / divisor;
            return this;
        }

        private double round(double a, int precision) {
            long factor = (long) Math.pow(10, precision);
            a *= factor;
            long tmp = Math.round(a);
            return (double) tmp / factor;
        }

        @Override
        public String toString() {
//            return "Vector{" +
//                    "x=" + x +
//                    ", y=" + y +
//                    ", z=" + z +
//                    '}';
            double roundedX = round(x, 2);
            double roundedY = round(y, 2);
            double roundedZ = round(z, 2);
            return String.format("%.2f, %.2f, %.2f\t | ", roundedX, roundedY, roundedZ);
        }


    }

}

