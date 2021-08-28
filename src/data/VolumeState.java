package data;

import parameters.FluidSimulationParameters;
import util.VolumeUtils;

public class VolumeState {

    // size
    private final int n;
    // resulting size of volume for air around (size + 2)
    private final int N;
    // timeStep
    private final double dt;
    private final double diffusionRate;
    private final double viscosityRate;
    // density values (old - in previous step, new - in current step)
    private double[] newDensities;
    private double[] oldDensities;
    // new velocity values in every dimension
    private double[] velocityX;
    private double[] velocityY;
    private double[] velocityZ;
    // old velocity values in every dimension
    private double[] oldVelocityX;
    private double[] oldVelocityY;
    private double[] oldVelocityZ;
    // number of iterations for method resolution
    private final int iter = 6;
    // array containing, which material voxels possesses
    private VoxelType[] terrain;

    public VolumeState(FluidSimulationParameters parameters) {
        this.n = parameters.getSize();
        this.N = this.n + 2;
        this.dt = parameters.getTimeStep();
        this.diffusionRate = parameters.getDiffusionRate();
        this.viscosityRate = parameters.getViscosityRate();

        this.newDensities = VolumeUtils.initializeArray3D(this.N);
        this.oldDensities = VolumeUtils.initializeArray3D(this.N);
        this.velocityX = VolumeUtils.initializeArray3D(this.N);
        this.velocityY = VolumeUtils.initializeArray3D(this.N);
        this.velocityZ = VolumeUtils.initializeArray3D(this.N);
        this.oldVelocityX = VolumeUtils.initializeArray3D(this.N);
        this.oldVelocityY = VolumeUtils.initializeArray3D(this.N);
        this.oldVelocityZ = VolumeUtils.initializeArray3D(this.N);
    }

    public void setOldDensities(double[] densities) {
        for (int i = 1; i <= this.n; i++) {
            for (int j = 1; j <= this.n; j++) {
                for (int k = 1; k <= this.n; k++) {
                    this.oldDensities[VolumeUtils.indexIn3D(this.N, k, j, i)] = densities[VolumeUtils.indexIn3D(this.n, k - 1, j - 1, i - 1)];
                }
            }
        }
    }

    public void setTerrain(VoxelType[] terrain) {
        this.terrain = terrain;
    }

    public void setPotentials(Vector[] potentials) {
        for (int i = 1; i <= this.n; i++) {
            for (int j = 1; j <= this.n; j++) {
                for (int k = 1; k <= this.n; k++) {
                    Vector potential = potentials[VolumeUtils.indexIn3D(this.n, k - 1, j - 1, i -1 )];
                    this.velocityX[VolumeUtils.indexIn3D(this.N, k, j, i)] = potential.getX();
                    this.velocityY[VolumeUtils.indexIn3D(this.N, k, j, i)] = potential.getY();
                    this.velocityZ[VolumeUtils.indexIn3D(this.N, k, j, i)] = potential.getZ();
                }
            }
        }
    }

    // UTIL methods
    public void swapVelocityX() {
        double[] temp = this.velocityX;
        this.velocityX = this.oldVelocityX;
        this.oldVelocityX = temp;
    }

    public void swapVelocityY() {
        double[] temp = this.velocityY;
        this.velocityY = this.oldVelocityY;
        this.oldVelocityY = temp;
    }

    public void swapVelocityZ() {
        double[] temp = this.velocityZ;
        this.velocityZ = this.oldVelocityZ;
        this.oldVelocityZ = temp;
    }

    public void swapDensity() {
        double[] temp = this.newDensities;
        this.newDensities = this.oldDensities;
        this.oldDensities = temp;
    }

    // GETTER methods
    public int getN() {
        return n;
    }

    public int getSize() {
        return N;
    }

    public double getDt() {
        return dt;
    }

    public double getDiffusionRate() {
        return diffusionRate;
    }

    public double getViscosityRate() {
        return viscosityRate;
    }

    public double[] getNewDensities() {
        return newDensities;
    }

    public double[] getOldDensities() {
        return oldDensities;
    }

    public double[] getVelocityX() {
        return velocityX;
    }

    public double[] getVelocityY() {
        return velocityY;
    }

    public double[] getVelocityZ() {
        return velocityZ;
    }

    public double[] getOldVelocityX() {
        return oldVelocityX;
    }

    public double[] getOldVelocityY() {
        return oldVelocityY;
    }

    public double[] getOldVelocityZ() {
        return oldVelocityZ;
    }

    public int getIter() {
        return iter;
    }

    public VoxelType[] getTerrain() {
        return terrain;
    }
}
