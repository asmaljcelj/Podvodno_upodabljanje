public class DensityGeneration {

    public static void main(String[] args) {
        // for TESTING purposes
        //int size = 8;
        //DensityGeneration g = new DensityGeneration();
        //DensityField d = g.calculateDensityField(size, 0.2, 0.5, 123456L, 12345L, 1.4, 0.2f, 1.0);
        //System.out.println("done with calculation. Results are: ");

        //for (int i = d.points.length - size; i >= 0; i -= size) {
        //    for (int j = 0; j < size; j++)
        //        System.out.printf("%.4f\t", d.points[i + j].getDensity());
        //    System.out.println();
        //    if (i % (size * size) == 0)
        //        System.out.println("--------------------------------------------------------------");
        //}
    }

    public DensityField calculateDensityField(int size, double densityRange, double densityBase, long densitySeed, long heightsSeed, double heightBase, double dimensionStep, double heightDiff, VoxelType[] terrain, double floorDensity) {
        DensityField densityField = new DensityField(size, densityRange, densityBase, heightBase, heightDiff, terrain, floorDensity);
        densityField.calculateHeights(heightsSeed, dimensionStep);
        densityField.calculateDensities(densitySeed, dimensionStep);
        return densityField;
    }

    static class DensityField {
        int size;
        double densityRange;
        double densityBase;
        double heightBase;
        double heightDiff;

        Point[] points;
        double[] heights;

        VoxelType[] terrain;
        double floorDensity;

        public DensityField(int size, double densityRange, double densityBase, double heightBase, double heightDiff, VoxelType[] terrain, double floorDensity) {
            this.size = size;
            this.densityRange = densityRange;
            this.densityBase = densityBase;
            this.heightBase = heightBase;
            this.heightDiff = heightDiff;
            this.points = new Point[size * size * size];
            this.heights = new double[size * size];
            this.terrain = terrain;
            this.floorDensity = floorDensity;
        }

        public void calculateHeights(long seed, double dimensionStep) {
            PerlinNoiseGeneration p = new PerlinNoiseGeneration(seed);
            for (int i = 0; i < this.size; i++) {
                for (int j = 0; j < this.size; j++) {
                    double perlin = octavePerlin(j * dimensionStep, i * dimensionStep, 50.0, 4, 0.75, p, true);
                    this.heights[index2D(j, i)] = this.heightBase + (perlin * heightDiff);
                }
            }
        }

        public void calculateDensities(long seed, double dimensionStep) {
            PerlinNoiseGeneration pg = new PerlinNoiseGeneration(seed);
            for (int i = 0; i < this.size; i++) {
                for (int j = 0; j < this.size; j++) {
                    for (int k = 0; k < this.size; k++) {
                        Point p = new Point(k * dimensionStep, j * dimensionStep, i * dimensionStep, densityBase);
                        if (i * dimensionStep > this.heights[index2D(k, j)])
                            p.setDensity(1.0);
                        else if (this.terrain[index3D(k, j, i)].equals(VoxelType.CUBE) || this.terrain[index3D(k, j, i)].equals(VoxelType.FLOOR))
                            p.setDensity(this.floorDensity);
                        else {
                            // noise without octaves
                            // double perlin = perlinNoiseGenerator2.perlin(p.getX(), p.getY(), p.getZ(), false);
                            // noise with octaves
                            double perlin = octavePerlin(p.getX(), p.getY(), p.getZ(), 4, 0.75, pg, false);
                            double d = p.getDensity() + (perlin * densityRange);
                            d = round(d, 6);
                            p.setDensity(d);
                        }
                        this.points[index3D(k, j, i)] = p;
                    }
                }
            }
        }

        public double octavePerlin(double x, double y, double z, int octaves, double persistance, PerlinNoiseGeneration p, boolean fromZeroToOne) {
            double total = 0;
            double frequency = 1;
            double amplitude = 1;
            double maxValue = 0;
            for (int i = 0; i < octaves; i++) {
                total += p.perlin(x * frequency, y * frequency, z * frequency, fromZeroToOne) * amplitude;
                maxValue += amplitude;
                amplitude *= persistance;
                frequency *= 2;
            }
            return total / maxValue;
        }

        private double round(double a, int precision) {
            long factor = (long) Math.pow(10, precision);
            a *= factor;
            long tmp = Math.round(a);
            return (double) tmp / factor;
        }

        private int index3D(int x, int y, int z) {
            return (x + y * this.size + z * this.size * this.size);
        }

        private int index2D(int x, int y) {
            return (x + y * this.size);
        }

        public double getDensityAtPoint(int x, int y, int z) {
            return this.points[index3D(x, y, z)].getDensity();
        }

    }

    static class Point {
        double x;
        double y;
        double z;
        double density;

        public Point(double x, double y, double z, double density) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.density = density;
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

        public double getDensity() {
            return density;
        }

        public void setDensity(double density) {
            this.density = density;
        }
    }

}

