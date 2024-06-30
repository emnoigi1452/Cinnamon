package me.stella.objects.reporter.impl;

import me.stella.objects.reporter.Reporter;

import java.util.Arrays;

public class PlayerScanReporter implements Reporter {

    private static final byte[] order = new byte[] { (byte)3, (byte)1, (byte)2 };

    //private final String uid;
    private ScanPhase phase;
    private double completion;

    public PlayerScanReporter(/*String uid*/) {
        //this.uid = uid;
        this.phase = ScanPhase.get((byte)1);
        this.completion = 0.0D;
    }

    /*
    public final String getUid() {
        return this.uid;
    }
     */

    @Override
    public double getCompletion() {
        return this.completion;
    }

    @Override
    public ScanPhase getPhase() {
        return this.phase;
    }

    @Override
    public synchronized void setCompletion(double d) {
        this.completion = d;
    }

    public void nextPhase() {
        this.phase = this.phase.next();
        setCompletion(0.0D);
    }

    public enum ScanPhase {

        UUID_MAP((byte)1),
        NBT_MAP((byte)2),
        DATA_SCAN((byte)3);

        public static ScanPhase get(byte val) {
            int index = ((int)val) % 3;
            return Arrays.stream(ScanPhase.values())
                    .filter(phase -> PlayerScanReporter.order[index] == phase.getOrder())
                    .findFirst().orElse(null);
        }

        final byte order;

        ScanPhase(byte b) {
            this.order = b;
        }

        public byte getOrder() {
            return this.order;
        }

        public ScanPhase next() {
            return ScanPhase.get((byte)(order + 1));
        }

    }

}
