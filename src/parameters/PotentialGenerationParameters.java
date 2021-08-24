package parameters;

import data.Voxel;
import data.VoxelType;

public class PotentialGenerationParameters {

    private int size;
    private long curlSeed;
    private double dimensionStep;
    private VoxelType[] terrain;
    private double[] heights;
    private final double displacement = 0.01;
    private Voxel[] data;
    private final double maxRecursionDistance = 5;

    public PotentialGenerationParameters() {
    }

    // BUILDER methods
    public PotentialGenerationParameters withSize(int size) {
        this.size = size;
        return this;
    }

    public PotentialGenerationParameters withCurlSeed(long seed) {
        this.curlSeed = seed;
        return this;
    }

    public PotentialGenerationParameters withDimensionStep(double dimensionStep) {
        this.dimensionStep = dimensionStep;
        return this;
    }

    public PotentialGenerationParameters withTerrain(VoxelType[] terrain) {
        this.terrain = terrain;
        return this;
    }

    public PotentialGenerationParameters withHeights(double[] heights) {
        this.heights = heights;
        return this;
    }

    public PotentialGenerationParameters withData(Voxel[] data) {
        this.data = data;
        return this;
    }

    //GETTER methods


    public int getSize() {
        return size;
    }

    public long getCurlSeed() {
        return curlSeed;
    }

    public double getDimensionStep() {
        return dimensionStep;
    }

    public VoxelType[] getTerrain() {
        return terrain;
    }

    public double[] getHeights() {
        return heights;
    }

    public double getDisplacement() {
        return displacement;
    }

    public Voxel[] getData() {
        return data;
    }

    public double getMaxRecursionDistance() {
        return maxRecursionDistance;
    }
}
