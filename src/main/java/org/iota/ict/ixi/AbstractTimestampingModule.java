package org.iota.ict.ixi;

import org.iota.ict.ixi.model.Interval;
import org.iota.ict.ixi.model.Tangle;
import org.iota.ict.ixi.model.TimestampType;
import org.iota.ict.ixi.model.TimestampingCalculation;
import org.iota.ict.model.Transaction;

import java.util.*;

public abstract class AbstractTimestampingModule extends IxiModule {

    protected Map<String, TimestampingCalculation> calculations = new HashMap<>();
    protected Tangle tangle = new Tangle();

    public AbstractTimestampingModule(Ixi ixi) {

        super(ixi);

        ixi.addGossipListener(event -> {
            tangle.addTransaction(event.getTransaction());
        });

    }

    @Override
    public void run() { ; }

    public abstract String beginTimestampCalculation(String txToInspect, Object... args);

    public void addTimestampHelper(String identifier, String referringTx) {
        TimestampingCalculation calculation = calculations.get(identifier);
        calculation.addTimestampHelper(referringTx);
    }

    public void addTimestampHelper(String identifier, String[] referringTx) {
        TimestampingCalculation calculation = calculations.get(identifier);
        for(String hash: referringTx)
            calculation.addTimestampHelper(referringTx);
    }

    public abstract Interval getTimestampInterval(String hash, Tangle tangle);

    public abstract double getTimestampConfidence(String identifier);

    public static List<Long> getTimestamps(Set<String> set, TimestampType timestampType, Tangle tangle) {
        List<Long> ret = new ArrayList<>();
        for(String hash: set) {
            Transaction transaction = tangle.getTransactions().get(hash);
            if(transaction == null)
                continue;
            switch (timestampType) {
                case ATTACHMENT_TIMESTAMP_LOWERBOUND: {
                    ret.add(transaction.attachmentTimestampLowerBound);
                    break;
                }
                case ATTACHMENT_TIMESTAMP: {
                    ret.add(transaction.attachmentTimestamp);
                    break;
                }
                case ATTACHMENT_TIMESTAMP_UPPERBOUND: {
                    ret.add(transaction.attachmentTimestampUpperBound);
                    break;
                }
            }
        }
        return ret;
    }

    public static Set<String> getPast(String transactionHash, Tangle tangle) {
        Set<String> ret = new HashSet<>();
        traverseApproved(transactionHash, ret, tangle);
        return ret;
    }

    public static Set<String> getFuture(String transactionHash, Set<String> past, Tangle tangle) {

        Set<String> ret = tangle.getTransactions().keySet();
        ret.removeAll(past);
        ret.remove(transactionHash);

        Set<String> visited = new HashSet<>();
        for(String successor: new HashSet<>(ret)) {

            if(visited.contains(successor))
                continue;

            Set<String> p = getPast(successor, tangle);
            visited.addAll(p);

            if(!p.contains(transactionHash)) {
                ret.remove(successor);
                ret.removeAll(p);
            }

        }

        return ret;
    }

    public static Set<String> getIndependent(Set<String> past, Set<String> future, Tangle tangle) {
        Set<String> ret = tangle.getTransactions().keySet();
        ret.removeAll(past);
        ret.removeAll(future);
        return ret;
    }

    public static String[] getApproves(String transactionHash, Tangle tangle) {
        Transaction transaction = tangle.getTransactions().get(transactionHash);
        if(transaction == null)
            return null;
        return new String[] { transaction.trunkHash(), transaction.branchHash() };
    }

    public static Set<String> getApprovers(String txToInspect, Tangle tangle) {
        return tangle.getDirectApprovers(txToInspect);
    }

    private static void traverseApproved(String transactionHash, Set<String> ret, Tangle tangle) {

        String[] approved = getApproves(transactionHash, tangle);

        if(approved == null)
            return;

        ret.add(approved[0]);
        ret.add(approved[1]);

        traverseApproved(approved[0], ret, tangle);
        traverseApproved(approved[1], ret, tangle);

    }

}
