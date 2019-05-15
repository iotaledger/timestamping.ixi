package org.iota.ict.ixi.model;

import org.iota.ict.ixi.AbstractTimestampingProcedure;
import org.iota.ict.model.transaction.Transaction;

import java.util.*;

public class Tangle {

    private Map<String, Transaction> transactions = new LinkedHashMap<>();
    private Map<String, Set<String>> directApprovers = new HashMap<>();
    private Set<String> tips = new HashSet<>();

    public synchronized void add(Transaction transaction) {
        transactions.put(transaction.hash, transaction);
        setDirectApprovers(transaction);
        setTip(transaction);
    }

    private void setDirectApprovers(Transaction transaction) {

        Set<String> approversOfTrunk = directApprovers.get(transaction.trunkHash());
        Set<String> approversOfBranch = directApprovers.get(transaction.branchHash());

        if(approversOfTrunk == null)
            approversOfTrunk = new HashSet<>();

        if(approversOfBranch == null)
            approversOfBranch = new HashSet<>();

        approversOfTrunk.add(transaction.hash);
        approversOfBranch.add(transaction.hash);

        directApprovers.put(transaction.trunkHash(), approversOfTrunk);
        directApprovers.put(transaction.branchHash(), approversOfBranch);
    }

    private void setTip(Transaction transaction) {
        tips.add(transaction.hash);
        tips.remove(transaction.trunkHash());
        tips.remove(transaction.branchHash());
    }

    public LinkedHashMap<String, Transaction> getTransactions() {
        return new LinkedHashMap<>(transactions);
    }

    public Set<String> getDirectApprovers(String hash) {
        Set<String> approvers = directApprovers.get(hash);
        if(approvers == null)
            return new HashSet<>();
        return approvers;
    }

    public Set<String> getTips() {
        return tips;
    }

    public Set<String> getTips(String entry) {
        Set<String> future = AbstractTimestampingProcedure.getFuture(entry, this);
        Set<String> tips = getTips();
        tips.retainAll(future);
        return tips;
    }

}