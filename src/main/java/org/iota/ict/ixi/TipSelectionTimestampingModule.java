package org.iota.ict.ixi;

import org.iota.ict.ixi.model.Interval;
import org.iota.ict.ixi.model.TimestampType;
import org.iota.ict.ixi.model.TipSelectionTimestampingCalculation;
import org.iota.ict.ixi.util.Generator;
import org.iota.ict.model.Transaction;

import java.util.*;

public class TipSelectionTimestampingModule extends AbstractTimestampingModule {

    public TipSelectionTimestampingModule(Ixi ixi) {
        super(ixi);
    }

    @Override
    public String beginTimestampCalculation(String txToInspect, Object... args) {
        String identifier = Generator.getRandomHash();
        String entry = (String) args[0];
        calculations.put(identifier, new TipSelectionTimestampingCalculation(txToInspect, entry));
        return identifier;
    }

    @Override
    public Interval getTimestampInterval(String identifier, Map<String, Transaction> tangle) {

        TipSelectionTimestampingCalculation calculation = (TipSelectionTimestampingCalculation) calculations.get(identifier);
        String txToInspect = calculation.getTxToInspect();
        String entry = calculation.getEntry();

        Set<String> past = getPast(txToInspect, tangle);
        Set<String> future = getFuture(txToInspect, past, tangle);
        Set<String> path = getPath(entry, tangle);

        past.retainAll(path);
        future.retainAll(path);

        List<Long> t_minus = getTimestamps(past, TimestampType.ATTACHMENT_TIMESTAMP_LOWERBOUND, tangle);
        List<Long> t_plus = getTimestamps(future, TimestampType.ATTACHMENT_TIMESTAMP_UPPERBOUND, tangle);

        Collections.sort(t_minus);
        long av = t_minus.get(t_minus.size()-1);

        Collections.sort(t_plus);
        long bv = t_plus.get(0);

        return new Interval(av, bv);
    }

    @Override
    public double getTimestampConfidence(String identifier) {
        return 0;
    }

    public Map<String, Integer> calculateRatings(Map<String, Transaction> tangle) {
        Map<String, Integer> ratings = new HashMap<>();
        for(String txToInspect: tangle.keySet())
            ratings.put(txToInspect, 1 + getFuture(txToInspect, getPast(txToInspect, tangle), tangle).size());
        return ratings;
    }

    public LinkedHashSet<String> getPath(String entry, Map<String, Transaction> tangle) {
        Map<String, Integer> ratings = calculateRatings(tangle);
        LinkedHashSet<String> visited = new LinkedHashSet<>();
        visited.add(entry);
        walk(entry, visited, ratings, tangle);
        return visited;
    }

    private void walk(String current, Set<String> visited, Map<String, Integer> ratings, Map<String, Transaction> tangle) {

        Set<String> past = getPast(current, tangle);
        Set<String> future = getFuture(current, past, tangle);
        Set<String> approvers = getApprovers(current, future, tangle);

        if(approvers.size() == 0)
            return;

        Map<String, Integer> ratingsOfApprovers = new HashMap<>();
        for(String approver: approvers)
            ratingsOfApprovers.put(approver, ratings.get(approver));

        String tx = ratingsOfApprovers.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).iterator().next().getKey();
        visited.add(tx);

        walk(tx, visited, ratings, tangle);

    }

}
