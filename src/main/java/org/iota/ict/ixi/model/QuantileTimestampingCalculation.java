package org.iota.ict.ixi.model;

public class QuantileTimestampingCalculation  extends TimestampingCalculation {

    private double beta;

    public QuantileTimestampingCalculation(String txToInspect, double beta) {
        super(txToInspect);
        this.beta = beta;
    }

    public double getBeta() {
        return beta;
    }

}
