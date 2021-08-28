package generation;

import data.VoxelType;
import parameters.TerrainParameters;
import util.VolumeUtils;

public class TerrainGeneration {

    private final TerrainParameters parameters;

    public TerrainGeneration(TerrainParameters parameters) {
        this.parameters = parameters;
    }

    public VoxelType[] createBaseTerrainData() {
        int size = parameters.getSize();
        VoxelType[] terrain = new VoxelType[size * size * size];
        for (int k = 0; k < size; k++) {
            for (int j = 0; j < size; j++) {
                for (int i = 0; i < size; i++) {
                    // handle floor
                    if (k < parameters.getFloorHeight() / parameters.getDimensionStep())
                        terrain[VolumeUtils.indexIn3D(size, i, j, k)] = VoxelType.FLOOR;
                    else if (cube(i, j, k))
                        // handle cube
                        terrain[VolumeUtils.indexIn3D(size, i, j, k)] = VoxelType.OBJECT;
                    else
                        terrain[VolumeUtils.indexIn3D(size, i, j, k)] = VoxelType.FLUID;
                }
            }
        }
        return terrain;
    }

    public VoxelType[] updateVoxelTypesWithAir(VoxelType[] voxelTypes, double[] heights) {
        int size = parameters.getSize();
        for (int k = 0; k < size; k++) {
            for (int j = 0; j < size; j++) {
                for (int i = 0; i < size; i++) {
                    if (k * parameters.getDimensionStep() > heights[VolumeUtils.indexIn2D(size, i, j)])
                        voxelTypes[VolumeUtils.indexIn3D(size, i, j, k)] = VoxelType.AIR;
                }
            }
        }
        return voxelTypes;
    }

    // PRIVATE methods

    private boolean cube(int positionX, int positionY, int positionZ) {
        boolean correctX = positionX >= transform(parameters.getCubePositionX(), parameters.getDimensionStep()) &&
                positionX < transform(parameters.getCubePositionX() + parameters.getFloorCubeSize(), parameters.getDimensionStep());
        boolean correctY = positionY >= transform(parameters.getCubePositionY(), parameters.getDimensionStep()) &&
                positionY < transform(parameters.getCubePositionY() + parameters.getFloorCubeSize(), parameters.getDimensionStep());
        boolean correctZ = positionZ < transform(parameters.getFloorCubeSize() + parameters.getFloorHeight(), parameters.getDimensionStep());
        return correctX && correctY && correctZ;
    }

    private int transform(double value, double dimensionStep) {
        return (int) (value / dimensionStep);
    }

}
