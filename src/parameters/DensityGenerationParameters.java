package parameters;

import data.Voxel;
import data.VoxelType;

public class DensityGenerationParameters {

    private int size;
    private double densityRange;
    private double densityBase;
    private VoxelType[] terrain;
    private double floorDensity;
    private long densitySeed;
    private double dimensionStep;
    private Voxel[] data;

    public DensityGenerationParameters() {
    }

    // BUILDER methods
    public DensityGenerationParameters withSize(int size) {
        this.size = size;
        return this;
    }

    public DensityGenerationParameters withDensityRange(double range) {
        this.densityRange = range;
        return this;
    }

    public DensityGenerationParameters withDensityBase(double base) {
        this.densityBase = base;
        return this;
    }

    public DensityGenerationParameters withTerrain(VoxelType[] terrain) {
        this.terrain = terrain;
        return this;
    }

    public DensityGenerationParameters withFloorDensity(double floorDensity) {
        this.floorDensity = floorDensity;
        return this;
    }

    public DensityGenerationParameters withDensitySeed(long seed) {
        this.densitySeed = seed;
        return this;
    }

    public DensityGenerationParameters withDimensionStep(double dimensionStep) {
        this.dimensionStep = dimensionStep;
        return this;
    }

    public DensityGenerationParameters withData(Voxel[] data) {
        this.data = data;
        return this;
    }

    // GETTER methods
    public int getSize() {
        return size;
    }

    public double getDensityRange() {
        return densityRange;
    }

    public double getDensityBase() {
        return densityBase;
    }

    public VoxelType[] getTerrain() {
        return terrain;
    }

    public double getFloorDensity() {
        return floorDensity;
    }

    public long getDensitySeed() {
        return densitySeed;
    }

    public double getDimensionStep() {
        return dimensionStep;
    }

    public Voxel[] getData() {
        return data;
    }
}
