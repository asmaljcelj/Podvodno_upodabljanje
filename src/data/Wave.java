package data;

public class Wave {

    private int startX;
    private int startY;
    private double amplitude;
    private double frequency;

    public Wave() {
    }

    public Wave withStartX(int startX) {
        this.startX = startX;
        return this;
    }

    public Wave withStartY(int startY) {
        this.startY = startY;
        return this;
    }

    public Wave withAmplitude(double amplitude) {
        this.amplitude = amplitude;
        return this;
    }

    public Wave withFrequency(double frequency) {
        this.frequency = frequency;
        return this;
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
