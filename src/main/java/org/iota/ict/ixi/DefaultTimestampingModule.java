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

    public Interval getTimestampInterval(String hash) {
        Set<String> p = getPast(hash);
        return null;
    }

    private boolean isReferencing(String successor, String predecessor) {
        Set<String> ret = new HashSet<>();
        traverseApproved(successor, ret);
        if(ret.contains(predecessor))
            return true;
        return false;
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