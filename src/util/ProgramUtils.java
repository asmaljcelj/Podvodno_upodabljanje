package util;

import data.VolumeState;
import data.VoxelType;
import parameters.FluidSimulationParameters;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;

public class ProgramUtils {

    public static void displayMessageWithTimestamp(String message) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        System.out.println(timestamp + ": " + message);
    }

    public static void writeDensitiesToFileAddAirAround(FluidSimulationParameters parameters, VolumeState volumeState) {
        int totalSize = (int) Math.pow(volumeState.getSize(), 3);
        double[] minMax = getMaxMinDensity(volumeState, parameters.getDensityFloor());
        byte[] array = new byte[totalSize];
        for (int i = 0; i < volumeState.getSize(); i++) {
            for (int j = 0; j < volumeState.getSize(); j++) {
                for (int k = 0; k < volumeState.getSize(); k++) {
                    if (i == 0 || j == 0 || k == 0 || i == volumeState.getSize() - 1 || j == volumeState.getSize() - 1 || k == volumeState.getSize() - 1) {
                        array[VolumeUtils.indexIn3D(volumeState.getSize(), k, j, i)] = (byte) 0;
                        continue;
                    }
                    VoxelType t = volumeState.getTerrain()[VolumeUtils.indexIn3D(volumeState.getN(), k - 1, j - 1, i - 1)];
                    if (t.equals(VoxelType.OBJECT))
                        array[VolumeUtils.indexIn3D(volumeState.getSize(), k, j, i)] = (byte) 254;
                    else if (t.equals(VoxelType.FLOOR))
                        array[VolumeUtils.indexIn3D(volumeState.getSize(), k, j, i)] = (byte) 255;
                    else if (t.equals(VoxelType.AIR))
                        array[VolumeUtils.indexIn3D(volumeState.getSize(), k, j, i)] = (byte) 0;
                    else {
                        double d = volumeState.getOldDensities()[VolumeUtils.indexIn3D(volumeState.getSize(), k, j, i)];
                        array[VolumeUtils.indexIn3D(volumeState.getSize(), k, j, i)] = byteMap(minMax[0], minMax[1], d);
                    }
                }
            }
        }
        try {
            FileOutputStream fo = new FileOutputStream(parameters.getEndFileName());
            fo.write(array);
            fo.close();
        } catch (IOException e) {
            displayMessageWithTimestamp("Error during writing to file");
        }
    }

    private static double[] getMaxMinDensity(VolumeState volumeState, double floorDensity) {
        double[] minMax = new double[2];
        double min = -1.0;
        double max = -1.0;
        for (int i = 1; i <= volumeState.getN(); i++) {
            for (int j = 1; j <= volumeState.getN(); j++) {
                for (int k = 1; k <= volumeState.getN(); k++) {
                    if (!volumeState.getTerrain()[VolumeUtils.indexIn3D(volumeState.getN(), k - 1, j - 1, i - 1)].equals(VoxelType.FLUID))
                        continue;
                    double density = volumeState.getOldDensities()[VolumeUtils.indexIn3D(volumeState.getSize(), k, j, i)];
//                    if (density >= floorDensity)
//                        continue;
//                    if (density <= 1)
//                        continue;
                    if (density < min || min == -1.0)
                        min = density;
                    else if (density > max || max == -1.0)
                        max = density;
                }
            }
        }
        minMax[0] = min;
        minMax[1] = max;
        displayMessageWithTimestamp("Max density is " + minMax[1]);
        displayMessageWithTimestamp("Min density is " + minMax[0]);
        return minMax;
    }

    private static byte byteMap(double min, double max, double value) {
        double interval = max - min;
        double percentage = (value - min) / interval;
        int mappedValue = (int) (percentage * 252);
        return (byte) (mappedValue + 1);
    }

}
