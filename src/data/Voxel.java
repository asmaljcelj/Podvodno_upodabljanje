package data;

import noise.PerlinNoiseGeneration;

public class Voxel {

    private final double x, y, z;
    private double density;
    private Vector velocity;

    public Voxel(double x, double y, double z, double density) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.density = density;
    }

    public void calculateVelocity(double displacement, PerlinNoiseGeneration p) {
        Vector potential1 = p.perlin(this.x, this.y, this.z);
        Vector potential2 = p.perlin(this.x + 36000, this.y + 36000, this.z + 36000);
        Vector potential3 = p.perlin(this.x + 12497, this.y + 12497, this.z + 12497);

        // calculate partial derivative
        Vector potential3Offset2 = p.perlin(this.x + 12497, this.y + 12497 + displacement, this.z + 12497);
        double p32 = (potential3Offset2.getY() - potential3.getY()) / displacement;
        Vector potential2Offset3 = p.perlin(this.x + 36000, this.y + 36000, this.z + 36000 + displacement);
        double p23 = (potential2Offset3.getZ() - potential2.getZ()) / displacement;
        Vector potential1Offset3 = p.perlin(this.x, this.y, this.z + displacement);
        double p13 = (potential1Offset3.getZ() - potential1.getZ()) / displacement;
        Vector potential3Offset1 = p.perlin(this.x + 12497 + displacement, this.y + 12497, this.z + 12497);
        double p31 = (potential3Offset1.getX() - potential3.getX()) / displacement;
        Vector potential2Offset1 = p.perlin(this.x + 36000 + displacement, this.y + 36000, this.z + 36000);
        double p21 = (potential2Offset1.getX() - potential2.getX()) / displacement;
        Vector potential1Offset2 = p.perlin(this.x, this.y + displacement, this.z);
        double p12 = (potential1Offset2.getY() - potential1.getY()) / displacement;

        // calculate final velocity field
        this.velocity = new Vector(p32 - p23, p13 - p31, p21 - p12).normalize();
    }

    // GETTER methods
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public double getDensity() {
        return density;
    }

    public Vector getVelocity() {
        return velocity;
    }

    // SETTER methods
    public void setDensity(double density) {
        this.density = density;
    }

    public void setVelocity(Vector velocity) {
        this.velocity = velocity;
    }

}
