package org.iota.ict.ixi;

import org.iota.ict.ixi.model.Interval;
import org.iota.ict.ixi.model.TimestampType;
import org.iota.ict.model.Transaction;

import java.util.*;

public class QuantileTimestampingModule extends AbstractTimestampingModule {

    private final double BETA = 0.3;

    public QuantileTimestampingModule(Ixi ixi) {
        super(ixi);
    }

    @Override
    public Interval getTimestampInterval(String hash, Map<String, Transaction> tangle) {

        Set<String> past = getPast(hash, tangle);
        Set<String> future = getFuture(hash, past, tangle);
        Set<String> independent = getIndependent(past, future, tangle);

        List<Long> lowerBounds = getTimestamps(independent, TimestampType.ATTACHMENT_TIMESTAMP_LOWERBOUND, tangle);
        List<Long> upperBounds = getTimestamps(independent, TimestampType.ATTACHMENT_TIMESTAMP_UPPERBOUND, tangle);

        long av = percentile(lowerBounds,BETA * 100);
        long bv = percentile(upperBounds,(1 - BETA) * 100);

        return new Interval(av, bv);

    }

    private long percentile(List<Long> list, double percentile) {
        Collections.sort(list);
        int index = (int) Math.ceil(( percentile / 100) * list.size());
        return list.get(index-1);
    }

}