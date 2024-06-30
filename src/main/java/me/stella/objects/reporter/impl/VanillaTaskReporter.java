package me.stella.objects.reporter.impl;

import me.stella.objects.reporter.Reporter;

public class VanillaTaskReporter implements Reporter {

    private double value;

    public VanillaTaskReporter() {
        this.value = 0.0D;
    }

    @Override
    public double getCompletion() {
        return this.value;
    }

    @Override
    public synchronized void setCompletion(double value) {
        this.value = value;
    }
}
