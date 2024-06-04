package me.stella.core.tasks.scanners.reporter;

import java.util.Arrays;

public class WorldScanReporter {

    private static final byte[] order = new byte[] { (byte)3, (byte)1, (byte)2 };
    private WorldScanReporter.ScanPhase phase;
    private double completion;

    public WorldScanReporter() {
        this.phase = WorldScanReporter.ScanPhase.get((byte)1);
        this.completion = 0.0D;
    }


    public double getCompletion() {
        return this.completion;
    }

    public WorldScanReporter.ScanPhase getPhase() {
        return this.phase;
    }

    public void setCompletion(double d) {
        this.completion = d;
    }

    public void nextPhase() {
        this.phase = this.phase.next();
        setCompletion(0.0D);
    }

    public enum ScanPhase {

        CHUNK_MAP((byte)1),
        NBT_MAP((byte)2),
        CHUNK_SCAN((byte)3);

        public static WorldScanReporter.ScanPhase get(byte val) {
            int index = ((int)val) % 3;
            return Arrays.stream(WorldScanReporter.ScanPhase.values())
                    .filter(phase -> WorldScanReporter.order[index] == phase.getOrder())
                    .findFirst().orElse(null);
        }

        final byte order;

        ScanPhase(byte b) {
            this.order = b;
        }

        public byte getOrder() {
            return this.order;
        }

        public WorldScanReporter.ScanPhase next() {
            return WorldScanReporter.ScanPhase.get((byte)(order + 1));
        }

    }

}
