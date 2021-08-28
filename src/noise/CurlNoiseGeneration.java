package noise;

import data.Vector;
import data.Voxel;
import data.VoxelType;
import parameters.PotentialGenerationParameters;
import util.VolumeUtils;

public class CurlNoiseGeneration {

    private final PotentialGenerationParameters parameters;

    public CurlNoiseGeneration(PotentialGenerationParameters parameters) {
        this.parameters = parameters;
    }

    public Vector[] calculatePotentialField() {
        int size = parameters.getSize();
        Vector[] vectors = new Vector[size * size * size];
        PerlinNoiseGeneration png = new PerlinNoiseGeneration(parameters.getCurlSeed());
        for (int i = 0; i < size; i++) {
            System.out.println(i);
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < size; k++) {
                    Voxel v = parameters.getData()[VolumeUtils.indexIn3D(size, k, j, i)];
                    if (!parameters.getTerrain()[VolumeUtils.indexIn3D(size, k, j, i)].equals(VoxelType.FLUID)) {
                        v.setVelocity(new Vector(0, 0, 0));
                    } else {
                        v.calculateVelocity(parameters.getDisplacement(), png);
                        double distanceToNearestNonFluidVoxelWeighted = VolumeUtils.calculateDistance(0, parameters.getTerrain(), new boolean[size * size * size], size, k, j, i, parameters.getMaxRecursionDistance()) / parameters.getMaxRecursionDistance();
                        v.setVelocity(v.getVelocity().setToSpecificLengthBasedOnSmoothFactor(distanceToNearestNonFluidVoxelWeighted));
                    }
                    if (Double.isNaN(v.getVelocity().getX()) || Double.isNaN(v.getVelocity().getY()) || Double.isNaN(v.getVelocity().getZ()))
                        System.out.println();
                    vectors[VolumeUtils.indexIn3D(size, k, j, i)] = v.getVelocity();
                }
            }
        }
        return vectors;
    }
}

