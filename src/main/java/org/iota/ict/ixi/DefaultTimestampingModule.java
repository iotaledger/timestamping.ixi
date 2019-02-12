package org.iota.ict.ixi;

import org.iota.ict.ixi.model.Interval;
import org.iota.ict.ixi.model.TimestampType;
import org.iota.ict.model.Transaction;

import java.util.*;

public class DefaultTimestampingModule extends IxiModule {

    private Map<String, Transaction> transactionsByHash = new HashMap<>();

    public DefaultTimestampingModule(Ixi ixi) {

        super(ixi);

        ixi.addGossipListener(event -> {
            Transaction transaction = event.getTransaction();
            transactionsByHash.put(transaction.hash, transaction);
        });

    }

    @Override
    public void run() { ; }

    private Interval getTimestampInterval(String hash) {

        Map<String, Transaction> tangle = new HashMap<>(transactionsByHash);
        double beta = 0.3;

        Set<String> past = getPast(hash, tangle);
        Set<String> future = getFuture(hash, past, tangle);
        Set<String> independent = getIndependent(hash, past, future, tangle);

        List<Long> lowerBounds = getTimestamps(independent, TimestampType.ATTACHMENT_TIMESTAMP_LOWERBOUND, tangle);
        List<Long> upperBounds = getTimestamps(independent, TimestampType.ATTACHMENT_TIMESTAMP_UPPERBOUND, tangle);

        long av = percentile(lowerBounds,beta * 100);
        long bv = percentile(upperBounds,(1 - beta) * 100);

        return new Interval(av, bv);

    }

    public List<Long> getTimestamps(Set<String> set, TimestampType timestampType, Map<String, Transaction> tangle) {
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
        Collections.sort(ret);
        return ret;
    }

    private long percentile(List<Long> list, double percentile) {
        Collections.sort(list);
        int index = (int) Math.ceil(( percentile / 100) * list.size());
        return list.get(index-1);
    }

    public Set<String> getPast(String transactionHash, Map<String, Transaction> tangle) {
        Set<String> ret = new HashSet<>();
        traverseApproved(transactionHash, ret, tangle);
        return ret;
    }

    public Set<String> getFuture(String transactionHash, Set<String> past, Map<String, Transaction> tangle) {
        Set<String> ret = new HashSet<>(tangle.keySet());
        ret.remove(transactionHash);
        ret.removeAll(past);
        for(String successor: new HashSet<>(ret))
            if(!isReferencing(successor, transactionHash, tangle))
                ret.remove(successor);
        return ret;
    }

    public Set<String> getIndependent(String transactionHash, Set<String> past, Set<String> future, Map<String, Transaction> tangle) {
        Set<String> ret = new HashSet<>(tangle.keySet());
        ret.remove(transactionHash);
        ret.removeAll(past);
        ret.removeAll(future);
        return ret;
    }

    public boolean isReferencing(String successor, String predecessor, Map<String, Transaction> tangle) {
        Set<String> past = getPast(successor, tangle);
        if(past.contains(predecessor))
            return true;
        return false;
    }

    private void traverseApproved(String transactionHash, Set<String> ret, Map<String, Transaction> tangle) {

        String[] approved = getApproves(transactionHash, tangle);

        if(approved == null)
            return;

        String trunk = approved[0];
        String branch = approved[1];

        ret.add(trunk);
        ret.add(branch);

        traverseApproved(trunk, ret, tangle);
        traverseApproved(branch, ret, tangle);

    }

    public String[] getApproves(String transactionHash, Map<String, Transaction> tangle) {
        Transaction transaction = tangle.get(transactionHash);
        if(transaction == null)
            return null;
        return new String[] { transaction.trunkHash(), transaction.branchHash() };
    }

}