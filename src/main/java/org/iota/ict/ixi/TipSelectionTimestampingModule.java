package org.iota.ict.ixi;

import com.sun.javaws.exceptions.InvalidArgumentException;
import org.iota.ict.ixi.model.Interval;
import org.iota.ict.ixi.model.Tangle;
import org.iota.ict.ixi.model.TimestampType;
import org.iota.ict.ixi.model.TipSelectionTimestampingCalculation;
import org.iota.ict.ixi.util.Generator;

import java.util.*;

public class TipSelectionTimestampingModule extends AbstractTimestampingModule {

    public TipSelectionTimestampingModule(Ixi ixi) {
        super(ixi);
    }

    @Override
    public String beginTimestampCalculation(String txToInspect, Object... args) throws InvalidArgumentException {
        String identifier = Generator.getRandomHash();
        String entry = null;
        try {
            entry = (String) args[0];
        } catch (Throwable t) {
            throw new InvalidArgumentException(new String[] { t.getMessage() });
        }
        calculations.put(identifier, new TipSelectionTimestampingCalculation(txToInspect, entry));
        return identifier;
    }

    @Override
    public Interval getTimestampInterval(String identifier, Tangle tangle) {

        TipSelectionTimestampingCalculation calculation = (TipSelectionTimestampingCalculation) calculations.get(identifier);
        String txToInspect = calculation.getTxToInspect();
        String entry = calculation.getEntry();

        Set<String> past = getPast(txToInspect, tangle);
        Set<String> future = getFuture(txToInspect, tangle);
        Set<String> path = getPath(entry, tangle);

        path.addAll(calculation.getTimestampHelpers());

        past.retainAll(path);
        future.retainAll(path);

        List<Long> t_minus = getTimestamps(past, TimestampType.ATTACHMENT_TIMESTAMP_LOWERBOUND, tangle);
        List<Long> t_plus = getTimestamps(future, TimestampType.ATTACHMENT_TIMESTAMP_UPPERBOUND, tangle);

        Collections.sort(t_minus);
        long av = t_minus.size() > 0 ? t_minus.get(t_minus.size()-1) : 0;

        Collections.sort(t_plus);
        long bv = t_plus.size() > 0 ? t_plus.get(0) : Long.MAX_VALUE;

        return new Interval(av, bv);
    }

    @Override
    public double getTimestampConfidence(String identifier) {
        return 0;
    }

    public Map<String, Integer> calculateRatings(String entry, Tangle tangle) {
        Map<String, Integer> ratings = new HashMap<>();
        for (String txToInspect : getFuture(entry, tangle))
            ratings.put(txToInspect, 1 + getFuture(txToInspect, tangle).size());
        return ratings;
    }

    public LinkedHashSet<String> getPath(String entry, Tangle tangle) {
        Map<String, Integer> ratings = calculateRatings(entry, tangle);
        LinkedHashSet<String> path = new LinkedHashSet<>();
        walk(entry, path, ratings, tangle);
        return path;
    }

    private void walk(String current, Set<String> path, Map<String, Integer> ratings, Tangle tangle) {

        path.add(current);

        Set<String> approvers = tangle.getDirectApprovers(current);

        if(approvers.size() == 0)
            return;

        Map<String, Integer> ratingsOfApprovers = new HashMap<>();
        for(String approver: approvers)
            ratingsOfApprovers.put(approver, ratings.get(approver));

        String next = ratingsOfApprovers.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).iterator().next().getKey();

        walk(next, path, ratings, tangle);

    }

}
