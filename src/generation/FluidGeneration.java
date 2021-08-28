package generation;

import data.*;
import noise.CurlNoiseGeneration;
import parameters.*;
import util.ProgramUtils;
import util.VolumeUtils;

import java.util.List;

/**
 * Fluid generation algorithm with Perlin noise algorithm
 */

public class FluidGeneration {

    private final int size;//
    private final double dimensionStep;//
    private final double floorHeight;//
    private final double cubePositionX;//
    private final double cubePositionY;//
    private final double floorCubeSize;//
    private final double heightBase;//
    private final double heightDiff;//
    private final double densityBase;
    private final double densitySpan;
    private final long generationSeed;
    private final double floorDensity;

    public FluidGeneration(FluidSimulationParameters parameters) {
        this.size = parameters.getSize();
        this.dimensionStep = parameters.getDimensionDiscretizationStep();
        this.floorHeight = parameters.getHeightFloor();
        this.cubePositionX = parameters.getCubePositionX();
        this.cubePositionY = parameters.getCubePositionY();
        this.floorCubeSize = parameters.getCubeSize();
        this.heightBase = parameters.getHeightBase();
        this.heightDiff = parameters.getHeightSpan();
        this.densityBase = parameters.getDensityBase();
        this.densitySpan = parameters.getDensityRange();
        this.generationSeed = parameters.getGenerationSeed();
        this.floorDensity = parameters.getDensityFloor();
    }

    public VolumeState createVolume(FluidSimulationParameters parameters) {
        VolumeState volumeState = new VolumeState(parameters);
        // create voxels
        Voxel[] data = createVoxels();
        // create terrain
        ProgramUtils.displayMessageWithTimestamp("Calculating base terrain data");
        TerrainParameters terrainParameters = new TerrainParameters()
                .withSize(this.size)
                .withDimensionStep(this.dimensionStep)
                .withFloorHeight(this.floorHeight)
                .withCubePositionX(this.cubePositionX)
                .withCubePositionY(this.cubePositionY)
                .withCubeSize(this.floorCubeSize);
        TerrainGeneration terrainGeneration = new TerrainGeneration(terrainParameters);
        VoxelType[] terrain = terrainGeneration.createBaseTerrainData();
        // create surface and update terrain with air
        ProgramUtils.displayMessageWithTimestamp("Calculating surface");
        HeightGenerationParameters heightParameters = new HeightGenerationParameters()
                .withSize(this.size)
                .withHeightBase(this.heightBase)
                .withHeightDiff(this.heightDiff);
        double[] heights = new HeightCalculation(heightParameters).addWavesAndCalculateHeights(parameters.getWaves());
        terrain = terrainGeneration.updateVoxelTypesWithAir(terrain, heights);
        volumeState.setTerrain(terrain);
        // create densities
        ProgramUtils.displayMessageWithTimestamp("Calculating densities");
        DensityGenerationParameters densityGenerationParameters = new DensityGenerationParameters()
                .withSize(this.size)
                .withDensityRange(this.densitySpan)
                .withDensityBase(this.densityBase)
                .withDensitySeed(this.generationSeed)
                .withDimensionStep(this.dimensionStep)
                .withTerrain(terrain)
                .withFloorDensity(this.floorDensity)
                .withData(data);
        double[] densities = new DensityGeneration(densityGenerationParameters).generateDensities();
        volumeState.setOldDensities(densities);
        // create potentials
        ProgramUtils.displayMessageWithTimestamp("Calculating potentials");
        PotentialGenerationParameters potentialGenerationParameters = new PotentialGenerationParameters()
                .withSize(this.size)
                .withCurlSeed(this.generationSeed)
                .withDimensionStep(this.dimensionStep)
                .withTerrain(terrain)
                .withData(data)
                .withHeights(heights);
        Vector[] potentials = new CurlNoiseGeneration(potentialGenerationParameters).calculatePotentialField();
        volumeState.setPotentials(potentials);
        // done with volume creation, return accumulated volume state
        ProgramUtils.displayMessageWithTimestamp("Done with volume generation");
        return volumeState;
    }

    private Voxel[] createVoxels() {
        Voxel[] voxels = new Voxel[this.size * this.size * this.size];
        for (int i = 0; i < this.size; i++) {
            for (int j = 0; j < this.size; j++) {
                for (int k = 0; k < this.size; k++) {
                    voxels[VolumeUtils.indexIn3D(this.size, k, j, i)] = new Voxel(k * this.dimensionStep, j * this.dimensionStep, i * this.dimensionStep, this.densityBase);
                }
            }
        }
        return voxels;
    }
}
