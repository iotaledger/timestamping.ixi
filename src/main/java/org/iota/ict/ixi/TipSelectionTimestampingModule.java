package org.iota.ict.ixi;

import org.iota.ict.ixi.model.Interval;
import org.iota.ict.ixi.model.TipSelectionTimestampingCalculation;
import org.iota.ict.ixi.util.Generator;
import org.iota.ict.model.Transaction;

import java.util.*;
import java.util.stream.Stream;

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

        Map<String, Long> t_minus = new HashMap<>();
        Map<String, Long> t_plus = new HashMap<>();

        for(String hash: past)
            t_minus.put(hash, tangle.get(hash).attachmentTimestampLowerBound);

        for(String hash: future)
            t_plus.put(hash, tangle.get(hash).attachmentTimestampUpperBound);

        Stream<Map.Entry<String, Long>> t_minus_sorted = t_minus.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue()));
        long av = t_minus_sorted.iterator().next().getValue();

        Stream<Map.Entry<String, Long>> t_plus_sorted = t_plus.entrySet().stream().sorted(Map.Entry.comparingByValue());
        long bv = t_plus_sorted.iterator().next().getValue();

        return new Interval(av, bv);
    }

    @Override
    public double getTimestampConfidence(String identifier) {
        return 0;
    }

    public Map<String, Integer> calculateRatings(Map<String, Transaction> tangle) {

        Map<String, Integer> ratings = new HashMap<>();

        for(String txToInspect: tangle.keySet()) {

            int rating = 1;

            for(String hash: tangle.keySet())
                if(isReferencing(hash, txToInspect, tangle))
                    rating++;

            ratings.put(txToInspect, rating);

        }

        return ratings;
    }

    public Set<String> getPath(String entry, Map<String, Transaction> tangle) {
        Map<String, Integer> ratings = calculateRatings(tangle);
        Set<String> visited = new HashSet<>();
        walk(entry, visited, ratings, tangle);
        return visited;
    }

    private void walk(String current, Set<String> visited, Map<String, Integer> ratings, Map<String, Transaction> tangle) {

        Set<String> approvers = getApprovers(current, getFuture(current, getPast(current, tangle), tangle), tangle);
        Map<String, Integer> ratingsOfApprovers = new HashMap<>();
        for(String approver: approvers)
            ratingsOfApprovers.put(approver, ratings.get(approver));

        Stream<Map.Entry<String,Integer>> sorted = ratingsOfApprovers.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue()));
        Iterator<Map.Entry<String, Integer>> i = sorted.iterator();
        if(!i.hasNext())
            return;

        String tx = i.next().getKey();
        visited.add(tx);

        walk(tx, visited, ratings, tangle);


    }

}
