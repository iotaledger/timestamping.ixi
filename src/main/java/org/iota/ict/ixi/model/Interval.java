package org.iota.ict.ixi.model;

public class Interval {

    private long lowerbound;
    private long upperbound;

    public Interval(long lowerbound, long upperbound) {
        this.lowerbound = lowerbound;
        this.upperbound = upperbound;
    }

    public void setLowerbound(long lowerbound) {
        this.lowerbound = lowerbound;
    }

    public void setUpperbound(long upperbound) {
        this.upperbound = upperbound;
    }

    public long getLowerbound() {
        return lowerbound;
    }

    public long getUpperbound() {
        return upperbound;
    }

    @Override
    public String toString() {
        return lowerbound + ":" + upperbound;
    }

}
