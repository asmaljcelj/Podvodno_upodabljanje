package generation;

import data.Voxel;
import data.VoxelType;
import noise.PerlinNoiseGeneration;
import parameters.DensityGenerationParameters;
import util.VolumeUtils;

public class DensityGeneration {

    private final DensityGenerationParameters parameters;

    public DensityGeneration(DensityGenerationParameters parameters) {
        this.parameters = parameters;
    }

    public double[] generateDensities() {
        int size = parameters.getSize();
        PerlinNoiseGeneration png = new PerlinNoiseGeneration(parameters.getDensitySeed());
        double[] densities = VolumeUtils.initializeArray3D(size);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < size; k++) {
                    VoxelType voxelType = parameters.getTerrain()[VolumeUtils.indexIn3D(size, k, j, i)];
                    Voxel v = parameters.getData()[VolumeUtils.indexIn3D(size, k, j, i)];
                    if (voxelType.equals(VoxelType.AIR))
                        v.setDensity(1.0);
                    else if (voxelType.equals(VoxelType.OBJECT) || voxelType.equals(VoxelType.FLOOR))
                        v.setDensity(parameters.getFloorDensity());
                    else {
                        double perlinNoise = png.perlin(v.getX(), v.getY(), v.getZ(), false);
                        double density = v.getDensity() + (perlinNoise * parameters.getDensityRange());
                        density = VolumeUtils.round(density, 6);
                        v.setDensity(density);
                    }
                    densities[VolumeUtils.indexIn3D(size, k, j, i)] = v.getDensity();
                }
            }
        }
        return densities;
    }

}

