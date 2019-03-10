package org.iota.ict.ixi.model;

public class RandomWalkTimestampingCalculation extends TimestampingCalculation {

    private String entry;

    public RandomWalkTimestampingCalculation(String txToInspect, String entry) {
        super(txToInspect);
        this.entry = entry;
    }

    public String getEntry() {
        return entry;
    }

}
