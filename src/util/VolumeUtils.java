package util;

import data.VoxelType;

public class VolumeUtils {

    public static double[] initializeArray3D(int size) {
        return new double[size * size * size];
    }

    public static double[] initializeArray2D(int size) {
        return new double[size * size];
    }

    public static int indexIn3D(int size, int x, int y, int z) {
        return (x + y * size + z * size * size);
    }

    public static int indexIn2D(int size, int x, int y) {
        return (x + y * size);
    }

    public static double boundValue(double value) {
        if (value > 1.0)
            return 1.0;
        if (value < -1.0)
            return -1.0;
        return value;
    }

    public static double round(double a, int precision) {
        long factor = (long) Math.pow(10, precision);
        a *= factor;
        long tmp = Math.round(a);
        return (double) tmp / factor;
    }

    public static double calculateDistance(double currentDistance, VoxelType[] terrain, boolean[] visited, int size, int currentX, int currentY, int currentZ, double maxDistance) {
        if (!terrain[indexIn3D(size, currentX, currentY, currentZ)].equals(VoxelType.FLUID) || currentDistance == maxDistance)
            return currentDistance;
        double minDistance = -1;
        // x + 1
        if (indexIn3D(size, currentX + 1, currentY, currentZ) < visited.length && !visited[indexIn3D(size, currentX + 1, currentY, currentZ)]) {
            visited[indexIn3D(size, currentX + 1, currentY, currentZ)] = true;
            minDistance = calculateDistance(currentDistance + 1, terrain, visited, size, currentX + 1, currentY, currentZ, maxDistance);
        }
        // x - 1
        if (indexIn3D(size, currentX - 1, currentY, currentZ) >= 0 && !visited[indexIn3D(size, currentX - 1, currentY, currentZ)]) {
            visited[indexIn3D(size, currentX - 1, currentY, currentZ)] = true;
            double min = calculateDistance(currentDistance + 1, terrain, visited, size, currentX - 1, currentY, currentZ, maxDistance);
            if (min < minDistance)
                minDistance = min;
        }
        // y - 1
        if (indexIn3D(size, currentX, currentY - 1, currentZ) >= 0 && !visited[indexIn3D(size, currentX, currentY - 1, currentZ)]) {
            visited[indexIn3D(size, currentX, currentY - 1, currentZ)] = true;
            double min = calculateDistance(currentDistance + 1, terrain, visited, size, currentX, currentY - 1, currentZ, maxDistance);
            if (min < minDistance)
                minDistance = min;
        }
        // y + 1
        if (indexIn3D(size, currentX, currentY + 1, currentZ) < visited.length && !visited[indexIn3D(size, currentX, currentY + 1, currentZ)]) {
            visited[indexIn3D(size, currentX, currentY + 1, currentZ)] = true;
            double min = calculateDistance(currentDistance + 1, terrain, visited, size, currentX, currentY + 1, currentZ, maxDistance);
            if (min < minDistance)
                minDistance = min;
        }
        // z - 1
        if (indexIn3D(size, currentX, currentY, currentZ - 1) >= 0 && !visited[indexIn3D(size, currentX, currentY, currentZ - 1)]) {
            visited[indexIn3D(size, currentX, currentY, currentZ - 1)] = true;
            double min = calculateDistance(currentDistance + 1, terrain, visited, size, currentX, currentY, currentZ - 1, maxDistance);
            if (min < minDistance)
                minDistance = min;
        }
        // z + 1
        if (indexIn3D(size, currentX, currentY, currentZ + 1) < visited.length && !visited[indexIn3D(size, currentX, currentY, currentZ + 1)]) {
            visited[indexIn3D(size, currentX, currentY, currentZ + 1)] = true;
            double min = calculateDistance(currentDistance + 1, terrain, visited, size, currentX, currentY, currentZ + 1, maxDistance);
            if (min < minDistance)
                minDistance = min;
        }
        return minDistance;
    }

    // fade function 6t^5-15t^4+10t^3
    public static double fade(double t) {
        return t * t * t * (t * (6 * t - 15) + 10);
    }

    // PRIVATE METHODS
    private static double calculateDistance(int x, int y, int z, int voxelX, int voxelY, int voxelZ) {
        double diffX = Math.pow(x - voxelX, 2);
        double diffY = Math.pow(y - voxelY, 2);
        double diffZ = Math.pow(z - voxelZ, 2);
        return Math.sqrt(diffX + diffY + diffZ);
    }

}
