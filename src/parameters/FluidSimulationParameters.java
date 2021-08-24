package parameters;

/**
 * @author Aljaž Šmaljcelj
 * Class, holding information about all the parameters user can specify before running the program to create and simulate fluid.
 * Currently, only cube volumes can be created (all 3 dimension are of same length).
 *
 * Through this documentation, many parameters will have 'volume coordinates' in their javadoc comments.
 * This means that value has to be given in actual volume coordinates.
 * Example: a volume with size 512 and dimensionDiscretization 0.1 is created. Meaning that volume height is 512 * 0.1 = 51.2,
 * that is the maximum value inside the volume.
 */
public class FluidSimulationParameters {

    private int size;
    private double heightBase;
    private double heightSpan;
    private double densityBase;
    private double densityRange;
    private double dimensionDiscretizationStep;
    private double diffusionRate;
    private double viscosityRate;
    private double timeStep;
    private long generationSeed = -1L;
    private int numOfSteps;
    private double densityFloor;
    private double heightFloor;
    private double cubeSize;
    private double cubePositionX;
    private double cubePositionY;
    private String endFileName;

    public FluidSimulationParameters() {
    }

    // BUILDER methods
    /**
     * @param size size of the volume in all 3 dimensions (volume will have size x size x size voxels)
     */
    public FluidSimulationParameters withSize(int size) {
        this.size = size;
        return this;
    }

    /**
     * @param dimensionStep size of each voxel in the volume (each voxel will span dimensionStep x dimensionStep x dimensionStep of volume)
     */
    public FluidSimulationParameters withDimensionDiscretizationStep(double dimensionStep) {
        this.dimensionDiscretizationStep = dimensionStep;
        return this;
    }

    /**
     * @param heightBase base height of the fluid in the volume, given in volume coordinates
     */
    public FluidSimulationParameters withHeightBase(double heightBase) {
        this.heightBase = heightBase;
        return this;
    }

    /**
     * @param heightSpan span of maximum value which the height can differ from heightBase
     */
    public FluidSimulationParameters withHeightSpan(double heightSpan) {
        this.heightSpan = heightSpan;
        return this;
    }

    /**
     * @param densityBase base density value in fluid voxels
     */
    public FluidSimulationParameters withDensityBase(double densityBase) {
        this.densityBase = densityBase;
        return this;
    }

    /**
     * @param densityRange span of maximum value which the density of fluid can differ from densityBase
     */
    public FluidSimulationParameters withDensityRange(double densityRange) {
        this.densityRange = densityRange;
        return this;
    }

    /**
     * @param diffusionRate diffusion coefficient, to control speed of diffusion of fluid
     */
    public FluidSimulationParameters withDiffusionRate(double diffusionRate) {
        this.diffusionRate = diffusionRate;
        return this;
    }

    /**
     * @param viscosityRate viscosity coefficient, to control viscosity of fluid
     */
    public FluidSimulationParameters withViscosityRate(double viscosityRate) {
        this.viscosityRate = viscosityRate;
        return this;
    }

    /**
     * @param timeStep length of each simulation step in seconds
     */
    public FluidSimulationParameters withTimeStep(double timeStep) {
        this.timeStep = timeStep;
        return this;
    }

    /**
     * @param generationSeed optional parameter of seed on which to calculate permutation table of Perlin noise function.
     *                       Specify -1 if you wish to use default permutation table
     */
    public FluidSimulationParameters withGenerationSeed(long generationSeed) {
        this.generationSeed = generationSeed;
        return this;
    }

    /**
     * @param steps number of simulation steps to be done on volume
     */
    public FluidSimulationParameters withNumOfSteps(int steps) {
        this.numOfSteps = steps;
        return this;
    }

    /**
     * @param floorHeight height of the floor in the volume in volume coordinates
     */
    public FluidSimulationParameters withFloorHeight(double floorHeight) {
        this.heightFloor = floorHeight;
        return this;
    }

    /**
     * @param floorDensity density of the volume floor
     */
    public FluidSimulationParameters withFloorDensity(double floorDensity) {
        this.densityFloor = floorDensity;
        return this;
    }

    /**
     * @param cubeSize size of the cube placed on the volume floor
     */
    public FluidSimulationParameters withCubeSize(double cubeSize) {
        this.cubeSize = cubeSize;
        return this;
    }

    /**
     * @param cubePositionX positionX of the cube in volume coordinates
     */
    public FluidSimulationParameters withCubePositionX(double cubePositionX) {
        this.cubePositionX = cubePositionX;
        return this;
    }

    /**
     * @param cubePositionY positionY of the cube in volume coordinates
     */
    public FluidSimulationParameters withCubePositionY(double cubePositionY) {
        this.cubePositionY = cubePositionY;
        return this;
    }

    /**
     * @param endFileName file name, as which the final result will be saved (include file type at the end, e.g. "test.raw").
     */
    public FluidSimulationParameters withEndFileName(String endFileName) {
        this.endFileName = endFileName;
        return this;
    }

    // GETTER methods
    public int getSize() {
        return size;
    }

    public double getHeightBase() {
        return heightBase;
    }

    public double getHeightSpan() {
        return heightSpan;
    }

    public double getDensityBase() {
        return densityBase;
    }

    public double getDensityRange() {
        return densityRange;
    }

    public double getDimensionDiscretizationStep() {
        return dimensionDiscretizationStep;
    }

    public double getDiffusionRate() {
        return diffusionRate;
    }

    public double getViscosityRate() {
        return viscosityRate;
    }

    public double getTimeStep() {
        return timeStep;
    }

    public long getGenerationSeed() {
        return generationSeed;
    }

    public int getNumOfSteps() {
        return numOfSteps;
    }

    public double getDensityFloor() {
        return densityFloor;
    }

    public double getHeightFloor() {
        return heightFloor;
    }

    public double getCubeSize() {
        return cubeSize;
    }

    public double getCubePositionX() {
        return cubePositionX;
    }

    public double getCubePositionY() {
        return cubePositionY;
    }

    public String getEndFileName() {
        return endFileName;
    }
}
