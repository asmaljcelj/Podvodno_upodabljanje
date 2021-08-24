package parameters;

public class HeightGenerationParameters {

    private int size;
    private double heightBase;
    private double heightDiff;

    public HeightGenerationParameters() {
    }

    public HeightGenerationParameters withSize(int size) {
        this.size = size;
        return this;
    }

    public HeightGenerationParameters withHeightBase(double heightBase) {
        this.heightBase = heightBase;
        return this;
    }

    public HeightGenerationParameters withHeightDiff(double heightDiff) {
        this.heightDiff = heightDiff;
        return this;
    }

    public int getSize() {
        return size;
    }

    public double getHeightBase() {
        return heightBase;
    }

    public double getHeightDiff() {
        return heightDiff;
    }
}
