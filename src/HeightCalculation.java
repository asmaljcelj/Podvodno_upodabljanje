import java.util.List;

public class HeightCalculation {

    private int size;
    private double[] heights;
    private double[] waves;
    private double heightBase;
    private double heightDiff;

    public HeightCalculation(int size, double heightBase, double heightDiff) {
        this.size = size;
        this.heightBase = heightBase;
        this.heightDiff = heightDiff;
        this.waves = new double[size * size];
        this.heights = new double[size * size];
    }

    public void addWaves(List<Wave> waves) {
        waves.forEach(this::addWave);
        generateHeight();
    }

    private void addWave(Wave wave) {
        double phaseShift = 0;
        if (wave.getStartX() != 0)
            phaseShift = (double) wave.getStartY() / wave.getStartX();
        for (int i = 0; i < this.size; i++)
            for (int j = 0; j < this.size; j++)
                waves[index2D(j, i)] += wave.getAmplitude() * Math.sin(wave.getFrequency() * Math.sqrt(Math.pow((double) j - wave.getStartX(), 2) + Math.pow((double) i - wave.getStartY(), 2)) + phaseShift);
    }

    private void generateHeight() {
        for (int i = 0; i < this.size; i++) {
            for (int j = 0; j < this.size; j++) {
                this.heights[index2D(j, i)] = heightBase + (waves[index2D(j, i)] * heightDiff);
            }
        }
    }

    private int index2D(int x, int y) {
        return this.size * y + x;
    }

    public double[] getHeights() {
        return this.heights;
    }

    static class Wave {

        int startX;
        int startY;
        double amplitude;
        double frequency;

        public Wave(int startX, int startY, double amplitude, double frequency) {
            this.startX = startX;
            this.startY = startY;
            this.amplitude = amplitude;
            this.frequency = frequency;
        }

        public int getStartX() {
            return startX;
        }

        public int getStartY() {
            return startY;
        }

        public double getAmplitude() {
            return amplitude;
        }

        public double getFrequency() {
            return frequency;
        }
    }

}
