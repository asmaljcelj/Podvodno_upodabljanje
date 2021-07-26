import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Arrays;

/**
 * Fluid generation algorithm with Perlin noise algorithm
 */

public class FluidGeneration {

    public static void main(String[] args) {
        String endFileName = "example_512_waves_3.raw";

        // size of the cube (N)
        int size = 510;
        // base height of the fluid (in real-world measurements, including floor)
        double heightBase = 32.0;
        // range of height (must be equal in curl and density) - in real-world measurements
        double heightDiff = 10.0;
        // density range +- the base value
        double densityRange = 50;
        // density base value
        double densityBase = 1000;
        // how much arbitrary step does solver move in a fluid (by value in each step)
        // total length in each dimension = size * dimensionStep
        double dimensionStep = 0.1;
        // diffusion rate
        double diffusion = 0.005;
        // viscosity rate
        double viscosity = 0.05;
        // time between step
        double dt = 0.05;
        // density seed
        long densitySeed = 126879L;
        // curl seed
        long curlSeed = 1654987L;
        // number of steps to perform the simulation
        int steps = 0;
        // floor height
        double floorHeight = 3.0;
        // density of floor
        double floorDensity = 3000.0;
        // cube size on the floor (real world coordinates)
        double floorCubeSize = 11.0;
        // cube coordinates
        double cubePositionX = 18.0;
        double cubePositionY = 18.0;

        VoxelType[] terrain = createTerrain(size, dimensionStep, floorHeight, cubePositionX, cubePositionY, floorCubeSize);

        // initialize both generators
        DensityGeneration densityGenerator = new DensityGeneration();
        CurlNoiseGeneration curlNoiseGenerator = new CurlNoiseGeneration();
        HeightCalculation heightCalculation = new HeightCalculation(size, heightBase, heightDiff);
        FluidSimulation fluidSimulation = new FluidSimulation(size, diffusion, viscosity, dt, terrain);

        // calculate heights of fluid
        displayMessageWithTimestamp("Calculating wave generation");
        heightCalculation.addWaves(Arrays.asList(
                new HeightCalculation.Wave(200, 200, 0.5, 0.2),
                new HeightCalculation.Wave(400, 400, 0.2, 0.16),
                new HeightCalculation.Wave(150, 390, 0.4, 0.23)
        ));

        // calculate densities
        displayMessageWithTimestamp("Calculating density field");
        DensityGeneration.DensityField densityField = densityGenerator.calculateDensityField(size, densityRange, densityBase, densitySeed, dimensionStep, terrain, floorDensity, heightCalculation.getHeights());
        // add density
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < size; k++) {
                    fluidSimulation.addDensity(k, j, i, densityField.getDensityAtPoint(k, j, i));
                }
            }
        }
        // free up space
        densityField = null;
        System.gc();

        // calculate potential field
        displayMessageWithTimestamp("Calculating potential field");
        CurlNoiseGeneration.PotentialField potentialField = curlNoiseGenerator.calculatePotentialField(size, curlSeed, dimensionStep, terrain, heightCalculation.getHeights());
        displayMessageWithTimestamp("Starting setting up environment");
        // add speed
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < size; k++) {
                    CurlNoiseGeneration.Vector potentialVector = potentialField.getVectorAtPoint(k, j, i);
                    fluidSimulation.addVelocity(k, j, i, potentialVector.getX(), potentialVector.getY(), potentialVector.getZ());
                }
            }
        }
        terrain = potentialField.terrain;
        fluidSimulation.setTerrain(terrain);
        // free up space
        potentialField = null;
        System.gc();
        displayMessageWithTimestamp("Finished setting up environment");

        // simulate fluid for n steps
        for (int i = 0; i < steps; i++) {
            displayMessageWithTimestamp("Started simulation of step " + (i + 1));
            fluidSimulation.simulateStep();
        }

        displayMessageWithTimestamp("Finished with fluid simulation");

        fluidSimulation.writeDensitiesToFileAddAirAround(endFileName, floorDensity);

        displayMessageWithTimestamp("Finished writing densities to file. All done!");
    }

    private static void displayMessageWithTimestamp(String message) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        System.out.println(timestamp + ": " + message);
    }

    private static VoxelType[] createTerrain(int size, double dimensionStep, double floorHeight, double cubePositionX, double cubePositionY, double cubeSize) {
        VoxelType[] terrain = new VoxelType[size * size * size];
        for (int k = 0; k < size; k++) {
            for (int j = 0; j < size; j++) {
                for (int i = 0; i < size; i++) {
                    // handle floor
                    if (k < floorHeight / dimensionStep)
                        terrain[index(i, j, k, size)] = VoxelType.FLOOR;
                    else if (cube(dimensionStep, cubePositionX, cubePositionY, cubeSize, floorHeight, i, j, k))
                        // handle cube
                        terrain[index(i, j, k, size)] = VoxelType.CUBE;
                    else
                        terrain[index(i, j, k, size)] = VoxelType.FLUID;
                }
            }
        }
        return terrain;
    }

    private static int index(int x, int y, int z, int size) {
        return (x + y * size + z * size * size);
    }

    private static boolean cube(double dimensionStep, double cubePositionX, double cubePositionY, double cubeSize, double floorHeight, int positionX, int positionY, int positionZ) {
        boolean correctX = positionX >= transform(cubePositionX, dimensionStep) && positionX < transform(cubePositionX + cubeSize, dimensionStep);
        boolean correctY = positionY >= transform(cubePositionY, dimensionStep) && positionY < transform(cubePositionY + cubeSize, dimensionStep);
        boolean correctZ = positionZ < transform(cubeSize + floorHeight, dimensionStep);
        return correctX && correctY && correctZ;
    }

    private static int transform(double value, double dimensionStep) {
        return (int) (value / dimensionStep);
    }

    static class FluidSimulation {
        // size = N
        int n;
        // actualSize = N + 2
        int size;
        double dt;
        double diff;
        double visc;

        double[] s;
        double[] density;

        double[] velocityX;
        double[] velocityY;
        double[] velocityZ;

        double[] oldVelocityX;
        double[] oldVelocityY;
        double[] oldVelocityZ;

        int iter;

        VoxelType[] terrain;

        public FluidSimulation(int n, double diffusion, double viscosity, double dt, VoxelType[] terrain) {
            this.n = n;
            this.size = n + 2;
            this.diff = diffusion;
            this.dt = dt;
            this.visc = viscosity;
            this.iter = 4;

            this.s = initializeArray();
            this.density = initializeArray();
            this.velocityX = initializeArray();
            this.velocityY = initializeArray();
            this.velocityZ = initializeArray();
            this.oldVelocityX = initializeArray();
            this.oldVelocityY = initializeArray();
            this.oldVelocityZ = initializeArray();
            this.terrain = terrain;
        }

        private double[] initializeArray() {
            return new double[this.size * this.size * this.size];
        }

        private int index(int x, int y, int z) {
            return (x + y * this.size + z * this.size * this.size);
        }

        private int cubeIndex(int x, int y, int z) {
            int endX = x - 1;
            int endY = y - 1;
            int endZ = z - 1;
            return (endX + endY * this.n + endZ * this.n * this.n);
        }

        public void addDensity(int x, int y, int z, double amount) {
            // add density according to real cube
            int cubeX = x + 1;
            int cubeY = y + 1;
            int cubeZ = z + 1;
            this.density[index(cubeX, cubeY, cubeZ)] += amount;
        }

        public void addVelocity(int x, int y, int z, double amountX, double amountY, double amountZ) {
            // add density according to real cube
            int cubeX = x + 1;
            int cubeY = y + 1;
            int cubeZ = z + 1;
            this.velocityX[index(cubeX, cubeY, cubeZ)] = amountX;
            this.velocityY[index(cubeX, cubeY, cubeZ)] = amountY;
            this.velocityZ[index(cubeX, cubeY, cubeZ)] = amountZ;
        }

        public void simulateStep() {
            displayMessageWithTimestamp("Simulating step");
            displayMessageWithTimestamp("Start velocity solver - diffusion");
            // velocity step
            swapVelocityX();
            diffuse(1, this.velocityX, this.oldVelocityX, this.visc);
            swapVelocityY();
            diffuse(2, this.velocityY, this.oldVelocityY, this.visc);
            swapVelocityZ();
            diffuse(3, this.velocityZ, this.oldVelocityZ, this.visc);
            project(this.velocityX, this.velocityY, this.velocityZ, this.oldVelocityX, this.oldVelocityY);
            swapVelocityX();
            swapVelocityY();
            swapVelocityZ();
            displayMessageWithTimestamp("Velocity solver - advection");
            advect(1, this.velocityX, this.oldVelocityX, this.oldVelocityX, this.oldVelocityY, this.oldVelocityZ);
            advect(2, this.velocityY, this.oldVelocityY, this.oldVelocityX, this.oldVelocityY, this.oldVelocityZ);
            advect(3, this.velocityZ, this.oldVelocityZ, this.oldVelocityX, this.oldVelocityY, this.oldVelocityZ);
            project(this.velocityX, this.velocityY, this.velocityZ, this.oldVelocityX, this.oldVelocityY);

            // density step
            displayMessageWithTimestamp("Start density solver");
            swapDensity();
            diffuse(0, this.density, this.s, this.diff);
            swapDensity();
            advect(0, this.density, this.s, this.velocityX, this.velocityY, this.velocityZ);
            displayMessageWithTimestamp("Finished simulating step");
        }

        private void diffuse(int b, double[] newValues, double[] oldValues, double diff) {
            double a = this.dt * diff * this.n * this.n;
            for (int it = 0; it < this.iter; it++) {
                for (int i = 1; i <= this.n; i++) {
                    for (int j = 1; j <= this.n; j++) {
                        for (int k = 1; k <= this.n; k++) {
                            newValues[index(k, j, i)] = (oldValues[index(k, j, i)] + a *
                                    (newValues[index(k - 1, j, i)] + newValues[index(k + 1, j, i)] +
                                            newValues[index(k, j - 1, i)] + newValues[index(k, j + 1, i)] +
                                            newValues[index(k, j, i - 1)] + newValues[index(k, j, i + 1)])) / (1 + 4 * a);
                        }
                    }
                }
                setBnd(b, newValues);
            }
        }

        private void advect(int b, double[] newValues, double[] oldValues, double[] velocX, double[] velocY, double[] velocZ) {
            int i0, j0, k0, i1, j1, k1;
            double x, y, z, s0, t0, u0, s1, t1, u1, dt0;

            dt0 = this.dt * this.n;
            for (int i = 1; i <= this.n; i++) {
                for (int j = 1; j <= this.n; j++) {
                    for (int k = 1; k <= this.n; k++) {
                        x = k - dt0 * velocX[index(k, j, i)];
                        y = j - dt0 * velocY[index(k, j, i)];
                        z = i - dt0 * velocZ[index(k, j, i)];
                        if (x < 0.5)
                            x = 0.5;
                        if (x > this.n + 0.5)
                            x = this.n + 0.5;
                        i0 = (int) x;
                        i1 = i0 + 1;
                        if (y < 0.5)
                            y = 0.5;
                        if (y > this.n + 0.5)
                            y = this.n + 0.5;
                        j0 = (int) y;
                        j1 = j0 + 1;
                        if (z < 0.5)
                            z = 0.5;
                        if (z > this.n + 0.5)
                            z = this.n + 0.5;
                        k0 = (int) z;
                        k1 = k0 + 1;

                        s1 = x - i0;
                        s0 = 1 - s1;
                        t1 = y - j0;
                        t0 = 1 - t1;
                        u0 = z - k0;
                        u1 = 1 - u0;

                        newValues[index(k, j, i)] = s0 * (
                                t0 * (u0 * oldValues[index(k0, j0, i0)] + u1 * oldValues[index(k0, j0, i1)]) +
                                        t1 * (u0 * oldValues[index(k0, j1, i0)] + u1 * oldValues[index(k0, j1, i1)])) +
                                s1 * (
                                        t0 * (u0 * oldValues[index(k1, j0, i0)] + u1 * oldValues[index(k1, j0, i1)]) +
                                                t1 * (u0 * oldValues[index(k1, j1, i0)] + u1 * oldValues[index(k1, j1, i1)]));
                    }
                }
            }
            setBnd(b, newValues);
        }

        private void setBnd(int b, double[] x) {
            for (int j = 1; j <= this.n; j++) {
                for (int i = 1; i <= this.n; i++) {
                    x[index(i, j, 0)] = b == 3 ? -x[index(i, j, 1)] : x[index(i, j, 1)];
                    x[index(i, j, this.n + 1)] = b == 3 ? -x[index(i, j, this.n)] : x[index(i, j, this.n)];
                }
            }
            for (int k = 1; k <= this.n; k++) {
                for (int i = 1; i <= this.n; i++) {
                    x[index(i, 0, k)] = b == 2 ? -x[index(i, 1, k)] : x[index(i, 1, k)];
                    x[index(i, this.n + 1, k)] = b == 2 ? -x[index(i, this.n, k)] : x[index(i, this.n, k)];
                }
            }
            for (int k = 1; k <= this.n; k++) {
                for (int j = 1; j <= this.n; j++) {
                    x[index(0, j, k)] = b == 1 ? -x[index(1, j, k)] : x[index(1, j, k)];
                    x[index(this.n + 1, j, k)] = b == 1 ? -x[index(this.n, j, k)] : x[index(this.n, j, k)];
                }
            }

            // handle terrain inside fluid
            for (int i = 1; i <= this.n; i++) {
                for (int j = 1; j <= this.n; j++) {
                    for (int k = 1; k <= this.n; k++) {
                        if (!this.terrain[cubeIndex(k, j, i)].equals(VoxelType.FLUID)) {
                            if (b == 1) {
                                // handling x axis walls
                                if (this.terrain[cubeIndex(k, j, i)].equals(VoxelType.FLOOR) || this.terrain[cubeIndex(k, j, i)].equals(VoxelType.CUBE)) {
                                    if (k != 1 && this.terrain[cubeIndex(k - 1, j, i)].equals(VoxelType.FLUID))
                                        x[index(k, j, i)] = -x[index(k - 1, j, i)];
                                    else if (k != this.n && this.terrain[cubeIndex(k + 1, j, i)].equals(VoxelType.FLUID))
                                        x[index(k, j, i)] = -x[index(k + 1, j, i)];
                                }
                            } else if (b == 2) {
                                // handling y axis walls
                                if (this.terrain[cubeIndex(k, j, i)].equals(VoxelType.FLOOR) || this.terrain[cubeIndex(k, j, i)].equals(VoxelType.CUBE)) {
                                    if (j != 1 && this.terrain[cubeIndex(k, j - 1, i)].equals(VoxelType.FLUID))
                                        x[index(k, j, i)] = -x[index(k, j - 1, i)];
                                    else if (j != this.n && this.terrain[cubeIndex(k, j + 1, i)].equals(VoxelType.FLUID))
                                        x[index(k, j, i)] = -x[index(k, j + 1, i)];
                                }
                            } else if (b == 3) {
                                // handling z axis walls
                                if (this.terrain[cubeIndex(k, j, i)].equals(VoxelType.FLOOR) || this.terrain[cubeIndex(k, j, i)].equals(VoxelType.CUBE)) {
                                    if (i != 1 && this.terrain[cubeIndex(k, j, i - 1)].equals(VoxelType.FLUID))
                                        x[index(k, j, i)] = -x[index(k, j, i - 1)];
                                    else if (i != this.n && this.terrain[cubeIndex(k, j, i + 1)].equals(VoxelType.FLUID))
                                        x[index(k, j, i)] = -x[index(k, j, i + 1)];
                                }
                            }
                        }
                    }
                }
            }

            x[index(0, 0, 0)] = 0.33f * (x[index(1, 0, 0)] + x[index(0, 1, 0)] + x[index(0, 0, 1)]);
            x[index(0, this.n + 1, 0)] = 0.33f * (x[index(1, this.n + 1, 0)] + x[index(0, this.n, 0)] + x[index(0, this.n + 1, 1)]);
            x[index(0, 0, this.n + 1)] = 0.33f * (x[index(1, 0, this.n + 1)] + x[index(0, 1, this.n + 1)] + x[index(0, 0, this.n)]);
            x[index(0, this.n + 1, this.n + 1)] = 0.33f * (x[index(1, this.n + 1, this.n + 1)] + x[index(0, this.n, this.n + 1)] + x[index(0, this.n + 1, this.n)]);
            x[index(this.n + 1, 0, 0)] = 0.33f * (x[index(this.n, 0, 0)] + x[index(this.n + 1, 1, 0)] + x[index(this.n + 1, 0, 1)]);
            x[index(this.n + 1, this.n + 1, 0)] = 0.33f * (x[index(this.n, this.n + 1, 0)] + x[index(this.n + 1, this.n, 0)] + x[index(this.n + 1, this.n + 1, 1)]);
            x[index(this.n + 1, 0, this.n + 1)] = 0.33f * (x[index(this.n, 0, this.n + 1)] + x[index(this.n + 1, 1, this.n + 1)] + x[index(this.n + 1, 0, this.n)]);
            x[index(this.n + 1, this.n + 1, this.n + 1)] = 0.33f * (x[index(this.n, this.n + 1, this.n + 1)] + x[index(this.n + 1, this.n, this.n + 1)] + x[index(this.n + 1, this.n + 1, this.n)]);
        }

        private void project(double[] velX, double[] velY, double[] velZ, double[] p, double[] div) {
            double h = 1.0 / this.n;
            for (int i = 1; i <= this.n; i++) {
                for (int j = 1; j <= this.n; j++) {
                    for (int k = 1; k <= this.n; k++) {
                        div[index(k, j, i)] = -0.5 * h * (
                                velX[index(k + 1, j, i)] - velX[index(k - 1, j, i)]
                                        + velY[index(k, j + 1, i)] - velY[index(k, j - 1, i)]
                                        + velZ[index(k, j, i + 1)] - velZ[index(k, j, i - 1)]);
                        p[index(k, j, i)] = 0;
                    }
                }
            }
            setBnd(0, div);
            setBnd(0, p);

            for (int it = 0; it <= this.iter; it++) {
                for (int i = 1; i <= this.n; i++) {
                    for (int j = 1; j <= this.n; j++) {
                        for (int k = 1; k <= this.n; k++) {
                            p[index(k, j, i)] = (div[index(k, j, i)] + p[index(k - 1, j, i)] + p[index(k + 1, j, i)]
                                    + p[index(k, j - 1, i)] + p[index(k, j + 1, i)]
                                    + p[index(k, j, i - 1)] + p[index(k, j, i + 1)]) / 4;
                        }
                    }
                }
                setBnd(0, p);
            }

            for (int i = 1; i <= this.n; i++) {
                for (int j = 1; j <= this.n; j++) {
                    for (int k = 1; k <= this.n; k++) {
                        velX[index(k, j, i)] -= 0.5 * (p[index(k + 1, j, i)] - p[index(k - 1, j, i)]) / h;
                        velY[index(k, j, i)] -= 0.5 * (p[index(k, j + 1, i)] - p[index(k, j - 1, i)]) / h;
                        velZ[index(k, j, i)] -= 0.5 * (p[index(k, j, i + 1)] - p[index(k, j, i - 1)]) / h;
                    }
                }
            }
            setBnd(1, velX);
            setBnd(2, velY);
            setBnd(3, velZ);
        }

        // add air density around the cube
        public void writeDensitiesToFileAddAirAround(String fileName, double floorDensity) {
            int totalSize = this.size * this.size * this.size;
            double[] minMax = getMaxMinDensity(floorDensity);
            byte[] array = new byte[totalSize];
            for (int i = 0; i < this.size; i++) {
                for (int j = 0; j < this.size; j++) {
                    for (int k = 0; k < this.size; k++) {
                        if (i == 0 || j == 0 || k == 0 || i == this.size - 1 || j == this.size - 1 || k == this.size - 1) {
                            array[index(k, j, i)] = (byte) 0;
                            continue;
                        }
                        VoxelType t = this.terrain[cubeIndex(k, j, i)];
                        if (t.equals(VoxelType.CUBE))
                            array[index(k, j, i)] = (byte) 254;
                        else if (t.equals(VoxelType.FLOOR))
                            array[index(k, j, i)] = (byte) 255;
                        else if (t.equals(VoxelType.AIR))
                            array[index(k, j, i)] = (byte) 0;
                        else {
                            double d = this.density[index(k, j, i)];
                            array[index(k, j, i)] = byteMap(minMax[0], minMax[1], d, floorDensity);
                        }
                    }
                }
            }
            try {
                FileOutputStream fo = new FileOutputStream(fileName);
                fo.write(array);
                fo.close();
            } catch (IOException e) {
                displayMessageWithTimestamp("Error during writing to file");
            }
        }

        public double[] getMaxMinDensity(double floorDensity) {
            double[] minMax = new double[2];
            double min = Float.MAX_VALUE;
            double max = Float.MIN_VALUE;
            for (int i = 1; i <= this.n; i++)
                for (int j = 1; j <= this.n; j++)
                    for (int k = 1; k <= this.n; k++) {
                        if (!terrain[cubeIndex(k, j, i)].equals(VoxelType.FLUID))
                            continue;
                        double density = this.density[index(k, j, i)];
                        if (density >= floorDensity)
                            continue;
                        if (density <= 1)
                            continue;
                        if (density < min)
                            min = density;
                        if (density > max)
                            max = density;
                    }
            minMax[0] = min;
            minMax[1] = max;
            displayMessageWithTimestamp("Max density is " + minMax[1]);
            displayMessageWithTimestamp("Min density is " + minMax[0]);
            return minMax;
        }

        private byte byteMap(double min, double max, double value, double floorDensity) {
//            if (value <= 1)
//                return (byte) 0;
//            if (value >= floorDensity)
//                return (byte) 253;

            double interval = max - min;
            double percentage = (value - min) / interval;
            int mappedValue = (int) (percentage * 253);
            return (byte) mappedValue;
        }

        private void swapDensity() {
            double[] temp = this.density;
            this.density = this.s;
            this.s = temp;
        }

        private void swapVelocityX() {
            double[] temp = this.velocityX;
            this.velocityX = this.oldVelocityX;
            this.oldVelocityX = temp;
        }

        private void swapVelocityY() {
            double[] temp = this.velocityY;
            this.velocityY = this.oldVelocityY;
            this.oldVelocityY = temp;
        }

        private void swapVelocityZ() {
            double[] temp = this.velocityZ;
            this.velocityZ = this.oldVelocityZ;
            this.oldVelocityZ = temp;
        }

        private void setTerrain(VoxelType[] terrain) {
            this.terrain = terrain;
        }

    }

}
