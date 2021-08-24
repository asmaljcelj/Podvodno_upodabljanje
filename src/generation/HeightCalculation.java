package generation;

import parameters.HeightGenerationParameters;
import data.Wave;
import util.VolumeUtils;

import java.util.List;

public class HeightCalculation {

    private HeightGenerationParameters parameters;
    private double[] waves;

    public HeightCalculation(HeightGenerationParameters parameters) {
        this.parameters = parameters;
        this.waves = VolumeUtils.initializeArray2D(parameters.getSize());
    }

    public double[] addWavesAndCalculateHeights(List<Wave> waves) {
        waves.forEach(this::addWave);
        return generateHeight();
    }

    private void addWave(Wave wave) {
        int size = parameters.getSize();
        double phaseShift = 0;
        if (wave.getStartX() != 0)
            phaseShift = (double) wave.getStartY() / wave.getStartX();
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                waves[VolumeUtils.indexIn2D(size, j, i)] += wave.getAmplitude() * Math.sin(wave.getFrequency() * Math.sqrt(Math.pow((double) j - wave.getStartX(), 2) + Math.pow((double) i - wave.getStartY(), 2)) + phaseShift);
    }

    private double[] generateHeight() {
        int size = parameters.getSize();
        double[] heights = VolumeUtils.initializeArray2D(size);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                heights[VolumeUtils.indexIn2D(size, j, i)] = parameters.getHeightBase() + (VolumeUtils.boundValue(waves[VolumeUtils.indexIn2D(size, j, i)]) * parameters.getHeightDiff());
            }
        }
        return heights;
    }

}
