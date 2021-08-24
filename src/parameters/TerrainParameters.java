package parameters;

import data.Voxel;

public class TerrainParameters {

    private int size;
    private double dimensionStep;
    private double floorHeight;
    private double cubePositionX;
    private double cubePositionY;
    private double floorCubeSize;

    public TerrainParameters() {
    }

    // BUILDER methods
    public TerrainParameters withSize(int size) {
        this.size = size;
        return this;
    }

    public TerrainParameters withDimensionStep(double dimensionStep) {
        this.dimensionStep = dimensionStep;
        return this;
    }

    public TerrainParameters withFloorHeight(double floorHeight) {
        this.floorHeight = floorHeight;
        return this;
    }

    public TerrainParameters withCubePositionX(double cubePositionX) {
        this.cubePositionX = cubePositionX;
        return this;
    }

    public TerrainParameters withCubePositionY(double cubePositionY) {
        this.cubePositionY = cubePositionY;
        return this;
    }

    public TerrainParameters withCubeSize(double cubeSize) {
        this.floorCubeSize = cubeSize;
        return this;
    }

    // GETTER methods
    public int getSize() {
        return size;
    }

    public double getDimensionStep() {
        return dimensionStep;
    }

    public double getFloorHeight() {
        return floorHeight;
    }

    public double getCubePositionX() {
        return cubePositionX;
    }

    public double getCubePositionY() {
        return cubePositionY;
    }

    public double getFloorCubeSize() {
        return floorCubeSize;
    }

}
