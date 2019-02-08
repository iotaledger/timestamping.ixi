package org.iota.ict.ixi;

import org.iota.ict.ixi.model.Interval;
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

        Set<String> p = getPast(hash);
        Set<String> f = getFuture(hash, p);

        List<Long> timestampsOfPast = getTimestampsOfPast(p);
        List<Long> timestampsOfFuture = getTimestampsOfFuture(f);

        long av = percentile(timestampsOfPast,30);
        long bv = percentile(timestampsOfFuture,30);

        return new Interval(av, bv);

    }

    private List<Long> getTimestampsOfPast(Set<String> past) {
        List<Long> ret = new ArrayList<>();
        for(String hash: past) {
            Transaction transaction = transactionsByHash.get(hash);
            long timestamp = transaction.attachmentTimestampLowerBound;
            ret.add(timestamp);
        }
        return ret;
    }

    private List<Long> getTimestampsOfFuture(Set<String> future) {
        List<Long> ret = new ArrayList<>();
        for(String hash: future) {
            Transaction transaction = transactionsByHash.get(hash);
            long timestamp = transaction.attachmentTimestampUpperBound;
            ret.add(timestamp);
        }
        return ret;
    }

    private long percentile(List<Long> list, double percentile) {
        Collections.sort(list);
        int index = (int) Math.ceil(( percentile / 100) * list.size());
        return list.get(index-1);
    }

    private Set<String> getPast(String transactionHash) {
        Set<String> ret = new HashSet<>();
        traverseApproved(transactionHash, ret);
        return ret;
    }

    private Set<String> getFuture(String transactionHash, Set<String> past) {
        Set<String> ret = new HashSet<>(transactionsByHash.keySet());
        ret.remove(transactionHash);
        ret.removeAll(past);
        for(String successor: new HashSet<>(ret))
            if(!isReferencing(successor, transactionHash))
                ret.remove(successor);
        return ret;
    }

    private Set<String> getIndependent(String transactionHash, Set<String> past, Set<String> future) {
        Set<String> ret = new HashSet<>(transactionsByHash.keySet());
        ret.remove(transactionHash);
        ret.removeAll(past);
        ret.removeAll(future);
        return ret;
    }

    private boolean isReferencing(String successor, String predecessor) {
        Set<String> ret = new HashSet<>();
        traverseApproved(successor, ret);
        if(ret.contains(predecessor))
            return true;
        return false;
    }

    private void traverseApproved(String transactionHash, Set<String> ret) {

        String[] approved = getApproved(transactionHash);

        if(approved == null)
            return;

        String trunk = approved[0];
        String branch = approved[1];

        ret.add(trunk);
        ret.add(branch);

        traverseApproved(trunk, ret);
        traverseApproved(branch, ret);

    }

    private String[] getApproved(String transactionHash) {
        Transaction transaction = transactionsByHash.get(transactionHash);
        if(transaction == null)
            return null;
        return new String[] { transaction.trunkHash(), transaction.branchHash() };
    }

}