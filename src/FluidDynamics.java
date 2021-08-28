import data.Wave;
import parameters.FluidSimulationParameters;
import data.VolumeState;
import generation.FluidGeneration;
import simulation.FluidSimulation;
import util.ProgramUtils;

import java.util.List;

/**
 * Main class for fluid dynamics program.
 * This program:
 *  1) generates a volume with the following materials: air, fluid, floor, object on the volume floor inside fluid.
 *  Air, floor and object are assigned constant density, fluid is assigned fluctuating density.
 *  Additionally, a potential field of velocity vectors in every fluid voxel is created for simulation purposes.
 *  2) simulates an arbitrary number of fluid movement inside itself.
 *  We observe fluctuations of density concentrations thanks to advection and viscosity, parameters that user can
 *  specify.
 *  3) after being done with simulating, add a layer of voxels with air density all around the volume
 *  4) saves the resulting volume in an output file in a raw 8-bit format
 *
 * To run this program, simply run this class as a java program.
 */
public class FluidDynamics {

    public static void main(String[] args) {
        // define parameters of the volume
        FluidSimulationParameters parameters = new FluidSimulationParameters()
                .withEndFileName("volume_256_5_steps.raw")
                .withSize(254)
                .withHeightBase(15.2)
                .withHeightSpan(5.1)
                .withDensityRange(30.0)
                .withDensityBase(1000.0)
                .withDimensionDiscretizationStep(0.1)
                .withDiffusionRate(0.001)
                .withViscosityRate(0.01)
                .withTimeStep(0.05)
                .withGenerationSeed(-1L)
                .withNumOfSteps(5)
                .withFloorHeight(2.5)
                .withFloorDensity(3000.0)
                .withCubeSize(3.8)
                .withCubePositionX(10.8)
                .withCubePositionY(10.8)
                .withWaves(List.of(
                        new Wave().withStartX(200).withStartY(200).withAmplitude(0.5).withFrequency(0.2),
                        new Wave().withStartX(300).withStartY(300).withAmplitude(0.2).withFrequency(0.16),
                        new Wave().withStartX(150).withStartY(390).withAmplitude(0.4).withFrequency(0.23)
                ));

        // create a volume alongside potential field
        ProgramUtils.displayMessageWithTimestamp("Creating volume");
        VolumeState volumeState = new FluidGeneration(parameters).createVolume(parameters);
        // simulate generated volume in user-defined number of steps
        ProgramUtils.displayMessageWithTimestamp("Simulating volume");
        FluidSimulation fluidSimulation = new FluidSimulation(volumeState);
        for (int i = 0; i < parameters.getNumOfSteps(); i++) {
            ProgramUtils.displayMessageWithTimestamp("Simulating step " + (i + 1) + " of " + parameters.getNumOfSteps());
            fluidSimulation.simulateStep();
        }
        VolumeState finalVolumeState = fluidSimulation.getVolumeState();
        // save volume to a new file
        ProgramUtils.displayMessageWithTimestamp("Saving volume to file");
        ProgramUtils.writeDensitiesToFileAddAirAround(parameters, finalVolumeState);
        ProgramUtils.displayMessageWithTimestamp("Volume prepared");
    }

}
