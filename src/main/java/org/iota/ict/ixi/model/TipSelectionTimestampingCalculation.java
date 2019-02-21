package org.iota.ict.ixi.model;

public class TipSelectionTimestampingCalculation extends TimestampingCalculation {

    private String entry;

    public TipSelectionTimestampingCalculation(String txToInspect, String entry) {
        super(txToInspect);
        this.entry = entry;
    }


    public String getEntry() {
        return entry;
    }

}
