package org.iota.ict.ixi.model;

import java.util.HashSet;
import java.util.Set;

public abstract class TimestampingCalculation {

    private String txToInspect;
    private Set<String> helper = new HashSet<>();
    private Interval result;

    public TimestampingCalculation(String txToInspect) {
        this.txToInspect = txToInspect;
    }

    public void addTimestampHelper(String ... referringTx) {
        for(String hash: referringTx)
            helper.add(hash);
    }

    public String getTxToInspect() {
        return txToInspect;
    }

    public void setResult(Interval result){
        this.result = result;
    }

    public Interval getResult(){
        return result;
    }

}
