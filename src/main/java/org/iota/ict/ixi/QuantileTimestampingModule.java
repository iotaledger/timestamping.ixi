package org.iota.ict.ixi;

import org.iota.ict.ixi.model.Interval;
import org.iota.ict.ixi.model.QuantileTimestampingCalculation;
import org.iota.ict.ixi.model.TimestampType;
import org.iota.ict.ixi.util.Generator;
import org.iota.ict.model.Transaction;

import java.util.*;

public class QuantileTimestampingModule extends AbstractTimestampingModule {

    private Map<String, QuantileTimestampingCalculation> calculations = new HashMap<>();

    public QuantileTimestampingModule(Ixi ixi) {
        super(ixi);
    }

    @Override
    public String beginTimestampCalculation(String txToInspect, Object... args) {
        String identifier = Generator.getRandomHash();
        double beta = (double) args[0];
        calculations.put(identifier, new QuantileTimestampingCalculation(txToInspect, beta));
        return identifier;
    }

    @Override
    public Interval getTimestampInterval(String identifier, Map<String, Transaction> tangle) {

        QuantileTimestampingCalculation calculation = calculations.get(identifier);
        String txToInspect = calculation.getTxToInspect();
        double beta = calculation.getBeta();

        Set<String> past = getPast(txToInspect, tangle);
        Set<String> future = getFuture(txToInspect, past, tangle);
        Set<String> independent = getIndependent(past, future, tangle);

        List<Long> lowerBounds = getTimestamps(independent, TimestampType.ATTACHMENT_TIMESTAMP_LOWERBOUND, tangle);
        List<Long> upperBounds = getTimestamps(independent, TimestampType.ATTACHMENT_TIMESTAMP_UPPERBOUND, tangle);

        long av = percentile(lowerBounds,beta * 100);
        long bv = percentile(upperBounds,(1 - beta) * 100);

        calculation.setResult(new Interval(av, bv));
        return calculation.getResult();

    }

    private long percentile(List<Long> list, double percentile) {
        Collections.sort(list);
        int index = (int) Math.ceil(( percentile / 100) * list.size());
        return list.get(index-1);
    }

    @Override
    public double getTimestampConfidence(String identifier) {
        QuantileTimestampingCalculation calculation = calculations.get(identifier);
        double beta = calculation.getBeta();
        return (1 - beta) * 100;
    }

}