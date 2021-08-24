package simulation;

import data.VolumeState;
import data.VoxelType;
import util.ProgramUtils;
import util.VolumeUtils;

public class FluidSimulation {

    private final VolumeState volumeState;

    public FluidSimulation(VolumeState volumeState) {
        this.volumeState = volumeState;
    }

    public void simulateStep() {
        ProgramUtils.displayMessageWithTimestamp("velocity solver - diffusion");
        volumeState.swapVelocityX();
        diffuse(1, volumeState.getVelocityX(), volumeState.getOldVelocityX(), volumeState.getViscosityRate());
        volumeState.swapVelocityY();
        diffuse(2, volumeState.getVelocityY(), volumeState.getOldVelocityY(), volumeState.getViscosityRate());
        volumeState.swapVelocityZ();
        diffuse(3, volumeState.getVelocityZ(), volumeState.getOldVelocityZ(), volumeState.getViscosityRate());
        project(volumeState.getVelocityX(), volumeState.getVelocityY(), volumeState.getVelocityZ(), volumeState.getOldVelocityX(), volumeState.getOldVelocityY());
        volumeState.swapVelocityX();
        volumeState.swapVelocityY();
        volumeState.swapVelocityZ();
        ProgramUtils.displayMessageWithTimestamp("velocity solver - advection");
        advect(1, volumeState.getVelocityX(), volumeState.getOldVelocityX(), volumeState.getOldVelocityX(), volumeState.getOldVelocityY(), volumeState.getOldVelocityZ());
        advect(2, volumeState.getVelocityY(), volumeState.getOldVelocityY(), volumeState.getOldVelocityX(), volumeState.getOldVelocityY(), volumeState.getOldVelocityZ());
        advect(3, volumeState.getVelocityZ(), volumeState.getOldVelocityZ(), volumeState.getOldVelocityX(), volumeState.getOldVelocityY(), volumeState.getOldVelocityZ());
        project(volumeState.getVelocityX(), volumeState.getVelocityY(), volumeState.getVelocityZ(), volumeState.getOldVelocityX(), volumeState.getOldVelocityX());

        ProgramUtils.displayMessageWithTimestamp("density solver");
//        volumeState.swapDensity();
        diffuse(0, volumeState.getNewDensities(), volumeState.getOldDensities(), volumeState.getDiffusionRate());
        volumeState.swapDensity();
        advect(0, volumeState.getNewDensities(), volumeState.getOldDensities(), volumeState.getVelocityX(), volumeState.getVelocityY(), volumeState.getVelocityZ());
//        volumeState.swapDensity();
    }

    private void diffuse(int b, double[] newValues, double[] oldValues, double diff) {
        double a = volumeState.getDt() * diff * volumeState.getN() * volumeState.getN();
        for (int it = 0; it < volumeState.getIter(); it++) {
            for (int i = 1; i <= volumeState.getN(); i++) {
                for (int j = 1; j <= volumeState.getN(); j++) {
                    for (int k = 1; k <= volumeState.getN(); k++) {
                        if (!volumeState.getTerrain()[VolumeUtils.indexIn3D(volumeState.getN(), k - 1, j - 1, i - 1)].equals(VoxelType.FLUID))
                            continue;
                        newValues[VolumeUtils.indexIn3D(volumeState.getSize(), k, j, i)] = (oldValues[VolumeUtils.indexIn3D(volumeState.getSize(), k, j, i)] + a *
                                (newValues[VolumeUtils.indexIn3D(volumeState.getSize(), k - 1, j, i)] + newValues[VolumeUtils.indexIn3D(volumeState.getSize(), k + 1, j, i)] +
                                        newValues[VolumeUtils.indexIn3D(volumeState.getSize(), k, j - 1, i)] + newValues[VolumeUtils.indexIn3D(volumeState.getSize(), k, j + 1, i)] +
                                        newValues[VolumeUtils.indexIn3D(volumeState.getSize(), k, j, i - 1)] + newValues[VolumeUtils.indexIn3D(volumeState.getSize(), k, j, i + 1)])) / (1 + 4 * a);
                    }
                }
            }
            setBnd(b, newValues);
        }
    }

    private void advect(int b, double[] newValues, double[] oldValues, double[] velocX, double[] velocY, double[] velocZ) {
        int i0, j0, k0, i1, j1, k1;
        double x, y, z, s0, t0, u0, s1, t1, u1, dt0;

        dt0 = volumeState.getDt() * volumeState.getN();
        for (int i = 1; i <= volumeState.getN(); i++) {
            for (int j = 1; j <= volumeState.getN(); j++) {
                for (int k = 1; k <= volumeState.getN(); k++) {
                    x = k - dt0 * velocX[VolumeUtils.indexIn3D(volumeState.getSize(), k, j, i)];
                    y = j - dt0 * velocY[VolumeUtils.indexIn3D(volumeState.getSize(), k, j, i)];
                    z = i - dt0 * velocZ[VolumeUtils.indexIn3D(volumeState.getSize(), k, j, i)];
                    if (x < 0.5)
                        x = 0.5;
                    if (x > volumeState.getN() + 0.5)
                        x = volumeState.getN() + 0.5;
                    i0 = (int) x;
                    i1 = i0 + 1;
                    if (y < 0.5)
                        y = 0.5;
                    if (y > volumeState.getN() + 0.5)
                        y = volumeState.getN() + 0.5;
                    j0 = (int) y;
                    j1 = j0 + 1;
                    if (z < 0.5)
                        z = 0.5;
                    if (z > volumeState.getN() + 0.5)
                        z = volumeState.getN() + 0.5;
                    k0 = (int) z;
                    k1 = k0 + 1;

                    s1 = x - i0;
                    s0 = 1 - s1;
                    t1 = y - j0;
                    t0 = 1 - t1;
                    u0 = z - k0;
                    u1 = 1 - u0;

                    newValues[VolumeUtils.indexIn3D(volumeState.getSize(), k, j, i)] = s0 * (
                            t0 * (u0 * oldValues[VolumeUtils.indexIn3D(volumeState.getSize(), k0, j0, i0)] + u1 * oldValues[VolumeUtils.indexIn3D(volumeState.getSize(), k0, j0, i1)]) +
                                    t1 * (u0 * oldValues[VolumeUtils.indexIn3D(volumeState.getSize(), k0, j1, i0)] + u1 * oldValues[VolumeUtils.indexIn3D(volumeState.getSize(), k0, j1, i1)])) +
                            s1 * (
                                    t0 * (u0 * oldValues[VolumeUtils.indexIn3D(volumeState.getSize(), k1, j0, i0)] + u1 * oldValues[VolumeUtils.indexIn3D(volumeState.getSize(), k1, j0, i1)]) +
                                            t1 * (u0 * oldValues[VolumeUtils.indexIn3D(volumeState.getSize(), k1, j1, i0)] + u1 * oldValues[VolumeUtils.indexIn3D(volumeState.getSize(), k1, j1, i1)]));
                }
            }
        }
        setBnd(b, newValues);
    }

    private void setBnd(int b, double[] x) {
        for (int j = 1; j <= volumeState.getN(); j++) {
            for (int i = 1; i <= volumeState.getN(); i++) {
                x[VolumeUtils.indexIn3D(volumeState.getSize(), i, j, 0)] = b == 3 ? -x[VolumeUtils.indexIn3D(volumeState.getSize(), i, j, 1)] : x[VolumeUtils.indexIn3D(volumeState.getSize(), i, j, 1)];
                x[VolumeUtils.indexIn3D(volumeState.getSize(), i, j, volumeState.getN() + 1)] = b == 3 ? -x[VolumeUtils.indexIn3D(volumeState.getSize(), i, j, volumeState.getN())] : x[VolumeUtils.indexIn3D(volumeState.getSize(), i, j, volumeState.getN())];
            }
        }
        for (int k = 1; k <= volumeState.getN(); k++) {
            for (int i = 1; i <= volumeState.getN(); i++) {
                x[VolumeUtils.indexIn3D(volumeState.getSize(), i, 0, k)] = b == 2 ? -x[VolumeUtils.indexIn3D(volumeState.getSize(), i, 1, k)] : x[VolumeUtils.indexIn3D(volumeState.getSize(), i, 1, k)];
                x[VolumeUtils.indexIn3D(volumeState.getSize(), i, volumeState.getN() + 1, k)] = b == 2 ? -x[VolumeUtils.indexIn3D(volumeState.getSize(), i, volumeState.getN(), k)] : x[VolumeUtils.indexIn3D(volumeState.getSize(), i, volumeState.getN(), k)];
            }
        }
        for (int k = 1; k <= volumeState.getN(); k++) {
            for (int j = 1; j <= volumeState.getN(); j++) {
                x[VolumeUtils.indexIn3D(volumeState.getSize(), 0, j, k)] = b == 1 ? -x[VolumeUtils.indexIn3D(volumeState.getSize(), 1, j, k)] : x[VolumeUtils.indexIn3D(volumeState.getSize(), 1, j, k)];
                x[VolumeUtils.indexIn3D(volumeState.getSize(), volumeState.getN() + 1, j, k)] = b == 1 ? -x[VolumeUtils.indexIn3D(volumeState.getSize(), volumeState.getN(), j, k)] : x[VolumeUtils.indexIn3D(volumeState.getSize(), volumeState.getN(), j, k)];
            }
        }

        // handle terrain inside fluid
        for (int i = 1; i <= volumeState.getN(); i++) {
            for (int j = 1; j <= volumeState.getN(); j++) {
                for (int k = 1; k <= volumeState.getN(); k++) {
                    if (!volumeState.getTerrain()[VolumeUtils.indexIn3D(volumeState.getN(), k - 1, j - 1, i - 1)].equals(VoxelType.FLUID)) {
                        if (b == 1) {
                            // handling x axis walls
                            if (volumeState.getTerrain()[VolumeUtils.indexIn3D(volumeState.getN(), k - 1, j - 1, i - 1)].equals(VoxelType.FLOOR) ||
                                    volumeState.getTerrain()[VolumeUtils.indexIn3D(volumeState.getN(), k - 1, j - 1, i - 1)].equals(VoxelType.CUBE)) {
                                if (k != 1 && volumeState.getTerrain()[VolumeUtils.indexIn3D(volumeState.getN(), k - 2, j - 1, i - 1)].equals(VoxelType.FLUID))
                                    x[VolumeUtils.indexIn3D(volumeState.getN(), k - 1, j - 1, i - 1)] = -x[VolumeUtils.indexIn3D(volumeState.getN(), k - 2, j - 1, i - 1)];
                                else if (k != volumeState.getN() && volumeState.getTerrain()[VolumeUtils.indexIn3D(volumeState.getN(), k, j - 1, i - 1)].equals(VoxelType.FLUID))
                                    x[VolumeUtils.indexIn3D(volumeState.getN(), k - 1, j - 1, i - 1)] = -x[VolumeUtils.indexIn3D(volumeState.getN(), k, j - 1, i - 1)];
                            }
                        } else if (b == 2) {
                            // handling y axis walls
                            if (volumeState.getTerrain()[VolumeUtils.indexIn3D(volumeState.getN(), k - 1, j - 1, i - 1)].equals(VoxelType.FLOOR) ||
                                    volumeState.getTerrain()[VolumeUtils.indexIn3D(volumeState.getN(), k - 1, j - 1, i - 1)].equals(VoxelType.CUBE)) {
                                if (j != 1 && volumeState.getTerrain()[VolumeUtils.indexIn3D(volumeState.getN(), k - 1, j - 2, i - 1)].equals(VoxelType.FLUID))
                                    x[VolumeUtils.indexIn3D(volumeState.getN(), k - 1, j - 1, i - 1)] = -x[VolumeUtils.indexIn3D(volumeState.getN(), k - 1, j - 2, i - 1)];
                                else if (j != volumeState.getN() && volumeState.getTerrain()[VolumeUtils.indexIn3D(volumeState.getN(), k - 1, j, i - 1)].equals(VoxelType.FLUID))
                                    x[VolumeUtils.indexIn3D(volumeState.getN(), k - 1, j - 1, i - 1)] = -x[VolumeUtils.indexIn3D(volumeState.getN(), k - 1, j, i - 1)];
                            }
                        } else if (b == 3) {
                            // handling z axis walls
                            if (volumeState.getTerrain()[VolumeUtils.indexIn3D(volumeState.getN(), k - 1, j - 1, i - 1)].equals(VoxelType.FLOOR) ||
                                    volumeState.getTerrain()[VolumeUtils.indexIn3D(volumeState.getN(), k - 1, j - 1, i - 1)].equals(VoxelType.CUBE)) {
                                if (i != 1 && volumeState.getTerrain()[VolumeUtils.indexIn3D(volumeState.getN(), k - 1, j - 1, i - 2)].equals(VoxelType.FLUID))
                                    x[VolumeUtils.indexIn3D(volumeState.getN(), k - 1, j - 1, i - 1)] = -x[VolumeUtils.indexIn3D(volumeState.getN(), k - 1, j - 1, i - 2)];
                                else if (i != volumeState.getN() && volumeState.getTerrain()[VolumeUtils.indexIn3D(volumeState.getN(), k - 1, j - 1, i)].equals(VoxelType.FLUID))
                                    x[VolumeUtils.indexIn3D(volumeState.getN(), k - 1, j - 1, i - 1)] = -x[VolumeUtils.indexIn3D(volumeState.getN(), k - 1, j - 1, i)];
                            }
                        }
                    }
                }
            }
        }

        x[VolumeUtils.indexIn3D(volumeState.getSize(), 0, 0, 0)] = 0.33f * (x[VolumeUtils.indexIn3D(volumeState.getSize(), 1, 0, 0)] + x[VolumeUtils.indexIn3D(volumeState.getSize(), 0, 1, 0)] + x[VolumeUtils.indexIn3D(volumeState.getSize(), 0, 0, 1)]);
        x[VolumeUtils.indexIn3D(volumeState.getSize(), 0, volumeState.getN() + 1, 0)] = 0.33f * (x[VolumeUtils.indexIn3D(volumeState.getSize(), 1, volumeState.getN() + 1, 0)] + x[VolumeUtils.indexIn3D(volumeState.getSize(), 0, volumeState.getN(), 0)] + x[VolumeUtils.indexIn3D(volumeState.getSize(), 0, volumeState.getN() + 1, 1)]);
        x[VolumeUtils.indexIn3D(volumeState.getSize(), 0, 0, volumeState.getN() + 1)] = 0.33f * (x[VolumeUtils.indexIn3D(volumeState.getSize(), 1, 0, volumeState.getN() + 1)] + x[VolumeUtils.indexIn3D(volumeState.getSize(), 0, 1, volumeState.getN() + 1)] + x[VolumeUtils.indexIn3D(volumeState.getSize(), 0, 0, volumeState.getN())]);
        x[VolumeUtils.indexIn3D(volumeState.getSize(), 0, volumeState.getN() + 1, volumeState.getN() + 1)] = 0.33f * (x[VolumeUtils.indexIn3D(volumeState.getSize(), 1, volumeState.getN() + 1, volumeState.getN() + 1)] + x[VolumeUtils.indexIn3D(volumeState.getSize(), 0, volumeState.getN(), volumeState.getN() + 1)] + x[VolumeUtils.indexIn3D(volumeState.getSize(), 0, volumeState.getN() + 1, volumeState.getN())]);
        x[VolumeUtils.indexIn3D(volumeState.getSize(), volumeState.getN() + 1, 0, 0)] = 0.33f * (x[VolumeUtils.indexIn3D(volumeState.getSize(), volumeState.getN(), 0, 0)] + x[VolumeUtils.indexIn3D(volumeState.getSize(), volumeState.getN() + 1, 1, 0)] + x[VolumeUtils.indexIn3D(volumeState.getSize(), volumeState.getN() + 1, 0, 1)]);
        x[VolumeUtils.indexIn3D(volumeState.getSize(), volumeState.getN() + 1, volumeState.getN() + 1, 0)] = 0.33f * (x[VolumeUtils.indexIn3D(volumeState.getSize(), volumeState.getN(), volumeState.getN() + 1, 0)] + x[VolumeUtils.indexIn3D(volumeState.getSize(), volumeState.getN() + 1, volumeState.getN(), 0)] + x[VolumeUtils.indexIn3D(volumeState.getSize(), volumeState.getN() + 1, volumeState.getN() + 1, 1)]);
        x[VolumeUtils.indexIn3D(volumeState.getSize(), volumeState.getN() + 1, 0, volumeState.getN() + 1)] = 0.33f * (x[VolumeUtils.indexIn3D(volumeState.getSize(), volumeState.getN(), 0, volumeState.getN() + 1)] + x[VolumeUtils.indexIn3D(volumeState.getSize(), volumeState.getN() + 1, 1, volumeState.getN() + 1)] + x[VolumeUtils.indexIn3D(volumeState.getSize(), volumeState.getN() + 1, 0, volumeState.getN())]);
        x[VolumeUtils.indexIn3D(volumeState.getSize(), volumeState.getN() + 1, volumeState.getN() + 1, volumeState.getN() + 1)] = 0.33f * (x[VolumeUtils.indexIn3D(volumeState.getSize(), volumeState.getN(), volumeState.getN() + 1, volumeState.getN() + 1)] + x[VolumeUtils.indexIn3D(volumeState.getSize(), volumeState.getN() + 1, volumeState.getN(), volumeState.getN() + 1)] + x[VolumeUtils.indexIn3D(volumeState.getSize(), volumeState.getN() + 1, volumeState.getN() + 1, volumeState.getN())]);
    }

    private void project(double[] velX, double[] velY, double[] velZ, double[] p, double[] div) {
        double h = 1.0 / volumeState.getN();
        for (int i = 1; i <= volumeState.getN(); i++) {
            for (int j = 1; j <= volumeState.getN(); j++) {
                for (int k = 1; k <= volumeState.getN(); k++) {
                    div[VolumeUtils.indexIn3D(volumeState.getSize(), k, j, i)] = -0.5 * h * (
                            velX[VolumeUtils.indexIn3D(volumeState.getSize(), k + 1, j, i)] - velX[VolumeUtils.indexIn3D(volumeState.getSize(), k - 1, j, i)]
                                    + velY[VolumeUtils.indexIn3D(volumeState.getSize(), k, j + 1, i)] - velY[VolumeUtils.indexIn3D(volumeState.getSize(), k, j - 1, i)]
                                    + velZ[VolumeUtils.indexIn3D(volumeState.getSize(), k, j, i + 1)] - velZ[VolumeUtils.indexIn3D(volumeState.getSize(), k, j, i - 1)]);
                    p[VolumeUtils.indexIn3D(volumeState.getSize(), k, j, i)] = 0;
                }
            }
        }
        setBnd(0, div);
        setBnd(0, p);

        for (int it = 0; it <= volumeState.getIter(); it++) {
            for (int i = 1; i <= volumeState.getN(); i++) {
                for (int j = 1; j <= volumeState.getN(); j++) {
                    for (int k = 1; k <= volumeState.getN(); k++) {
                        p[VolumeUtils.indexIn3D(volumeState.getSize(), k, j, i)] = (div[VolumeUtils.indexIn3D(volumeState.getSize(), k, j, i)] + p[VolumeUtils.indexIn3D(volumeState.getSize(), k - 1, j, i)] + p[VolumeUtils.indexIn3D(volumeState.getSize(), k + 1, j, i)]
                                + p[VolumeUtils.indexIn3D(volumeState.getSize(), k, j - 1, i)] + p[VolumeUtils.indexIn3D(volumeState.getSize(), k, j + 1, i)]
                                + p[VolumeUtils.indexIn3D(volumeState.getSize(), k, j, i - 1)] + p[VolumeUtils.indexIn3D(volumeState.getSize(), k, j, i + 1)]) / 4;
                    }
                }
            }
            setBnd(0, p);
        }

        for (int i = 1; i <= volumeState.getN(); i++) {
            for (int j = 1; j <= volumeState.getN(); j++) {
                for (int k = 1; k <= volumeState.getN(); k++) {
                    velX[VolumeUtils.indexIn3D(volumeState.getSize(), k, j, i)] -= 0.5 * (p[VolumeUtils.indexIn3D(volumeState.getSize(), k + 1, j, i)] - p[VolumeUtils.indexIn3D(volumeState.getSize(), k - 1, j, i)]) / h;
                    velY[VolumeUtils.indexIn3D(volumeState.getSize(), k, j, i)] -= 0.5 * (p[VolumeUtils.indexIn3D(volumeState.getSize(), k, j + 1, i)] - p[VolumeUtils.indexIn3D(volumeState.getSize(), k, j - 1, i)]) / h;
                    velZ[VolumeUtils.indexIn3D(volumeState.getSize(), k, j, i)] -= 0.5 * (p[VolumeUtils.indexIn3D(volumeState.getSize(), k, j, i + 1)] - p[VolumeUtils.indexIn3D(volumeState.getSize(), k, j, i - 1)]) / h;
                }
            }
        }
        setBnd(1, velX);
        setBnd(2, velY);
        setBnd(3, velZ);
    }

    // GETTER methods
    public VolumeState getVolumeState() {
        return volumeState;
    }
}
