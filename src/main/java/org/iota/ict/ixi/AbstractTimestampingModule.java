package org.iota.ict.ixi;

import org.iota.ict.ixi.model.Interval;
import org.iota.ict.ixi.model.TimestampType;
import org.iota.ict.model.Transaction;

import java.util.*;

public abstract class AbstractTimestampingModule extends IxiModule {

    protected Map<String, Transaction> transactionsByHash = new HashMap<>();

    protected AbstractTimestampingModule(Ixi ixi) {

        super(ixi);

        ixi.addGossipListener(event -> {
            Transaction transaction = event.getTransaction();
            transactionsByHash.put(transaction.hash, transaction);
        });

    }

    @Override
    public void run() { ; }

    protected abstract Interval getTimestampInterval(String hash, Map<String, Transaction> tangle);

    public static List<Long> getTimestamps(Set<String> set, TimestampType timestampType, Map<String, Transaction> tangle) {
        List<Long> ret = new ArrayList<>();
        for(String hash: set) {
            Transaction transaction = tangle.get(hash);
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

    public static Set<String> getPast(String transactionHash, Map<String, Transaction> tangle) {
        Set<String> ret = new HashSet<>();
        traverseApproved(transactionHash, ret, tangle);
        return ret;
    }

    public static Set<String> getFuture(String transactionHash, Set<String> past, Map<String, Transaction> tangle) {
        Set<String> ret = new HashSet<>(tangle.keySet());
        ret.remove(transactionHash);
        ret.removeAll(past);
        for(String successor: new HashSet<>(ret))
            if(!isReferencing(successor, transactionHash, tangle))
                ret.remove(successor);
        return ret;
    }

    public static Set<String> getIndependent(Set<String> past, Set<String> future, Map<String, Transaction> tangle) {
        Set<String> ret = new HashSet<>(tangle.keySet());
        ret.removeAll(past);
        ret.removeAll(future);
        return ret;
    }

    public static boolean isReferencing(String successor, String predecessor, Map<String, Transaction> tangle) {
        Set<String> past = getPast(successor, tangle);
        if(past.contains(predecessor))
            return true;
        return false;
    }

    public static String[] getApproves(String transactionHash, Map<String, Transaction> tangle) {
        Transaction transaction = tangle.get(transactionHash);
        if(transaction == null)
            return null;
        return new String[] { transaction.trunkHash(), transaction.branchHash() };
    }

    private static void traverseApproved(String transactionHash, Set<String> ret, Map<String, Transaction> tangle) {

        String[] approved = getApproves(transactionHash, tangle);

        if(approved == null)
            return;

        ret.add(approved[0]);
        ret.add(approved[1]);

        traverseApproved(approved[0], ret, tangle);
        traverseApproved(approved[1], ret, tangle);

    }

}
