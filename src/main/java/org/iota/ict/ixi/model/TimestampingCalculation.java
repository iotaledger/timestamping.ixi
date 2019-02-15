package org.iota.ict.ixi.model;

import java.util.HashSet;
import java.util.Set;

public abstract class TimestampingCalculation {

    private String txToInspect;
    private Set<String> helper = new HashSet<>();
    private Interval interval;

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

    public void setInterval(Interval interval){
        this.interval = interval;
    }

    public Interval getInterval(){
        return interval;
    }

}
