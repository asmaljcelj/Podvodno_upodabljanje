package data;

import util.VolumeUtils;

public class Vector {

    double x;
    double y;
    double z;

    public Vector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public Vector divide(double divisor) {
        this.x = this.x / divisor;
        this.y = this.y / divisor;
        this.z = this.z / divisor;
        return this;
    }

    public Vector multiply(double mul) {
        return new Vector(this.x * mul, this.y * mul, this.z * mul);
    }

    public Vector normalize() {
        double length = Math.sqrt(x * x + y * y + z * z);
        if (length == 0)
            return this;
        return this.divide(length);
    }

    public Vector setToSpecificLengthBasedOnSmoothFactor(double distance) {
        double factor = VolumeUtils.fade(distance);
        return this.multiply(factor);
    }

}
