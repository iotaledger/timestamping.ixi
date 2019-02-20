package org.iota.ict.ixi;

import org.iota.ict.ixi.model.Interval;
import org.iota.ict.ixi.model.TipSelectionTimestampingCalculation;
import org.iota.ict.ixi.util.Generator;
import org.iota.ict.model.Transaction;

import java.util.*;
import java.util.stream.Stream;

public class TipSelectionTimestampingModule extends AbstractTimestampingModule {

    protected TipSelectionTimestampingModule(Ixi ixi) {
        super(ixi);
    }

    @Override
    public String beginTimestampCalculation(String txToInspect, Object... args) {
        String identifier = Generator.getRandomHash();
        calculations.put(identifier, new TipSelectionTimestampingCalculation(txToInspect));
        return identifier;
    }

    @Override
    public Interval getTimestampInterval(String hash, Map<String, Transaction> tangle) {
        return null;
    }

    @Override
    public double getTimestampConfidence(String identifier) {
        return 0;
    }

    private Map<String, Integer> calculateRatings(Map<String, Transaction> tangle) {

        Map<String, Integer> ratings = new HashMap<>();

        for(String txToInspect: tangle.keySet()) {

            int rating = 1;

            for(String hash: tangle.keySet()) {
                if(isReferencing(hash, txToInspect, tangle))
                    rating++;
            }

            ratings.put(txToInspect, rating);

        }

        return ratings;
    }

    private void walk(String current, Set<String> path, Map<String, Integer> ratings, Map<String, Transaction> tangle) {

        Set<String> approvers = getApprovers(current, getFuture(current, getPast(current, tangle), tangle), tangle);
        Map<String, Integer> ratingsOfApprovers = new HashMap<>();
        for(String approver: approvers)
            ratingsOfApprovers.put(approver, ratings.get(approver));

        Stream<Map.Entry<String,Integer>> sorted = ratingsOfApprovers.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue()));
        Map.Entry<String,Integer> first = sorted.iterator().next();
        String tx = first.getKey();
        path.add(tx);

        walk(tx, path, ratings, tangle);

    }

}
