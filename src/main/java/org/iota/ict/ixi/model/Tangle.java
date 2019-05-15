package org.iota.ict.ixi.model;

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
        Set<String> future = getFuture(entry, this);
        Set<String> tips = getTips();
        tips.retainAll(future);
        return tips;
    }

    public static Set<String> getPast(String transactionHash, Tangle tangle) {
        Set<String> ret = new HashSet<>();
        traverseApproved(transactionHash, ret, tangle);
        return ret;
    }

    public static Set<String> getFuture(String transactionHash, Tangle tangle) {
        Set<String> ret = new HashSet<>();
        traverseApprovers(transactionHash, ret, new HashSet<>(), tangle);
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

        if(ret.contains(transactionHash))
            return;

        traverseApproved(approved[0], ret, tangle);
        traverseApproved(approved[1], ret, tangle);

    }

    private static void traverseApprovers(String transactionHash, Set<String> ret, Set<String> visited, Tangle tangle) {

        if(visited.contains(transactionHash))
            return;
        visited.add(transactionHash);

        Set<String> approvers = getApprovers(transactionHash, tangle);

        if(approvers.size() == 0)
            return;

        ret.addAll(approvers);

        for(String hash: approvers)
            traverseApprovers(hash, ret, visited, tangle);

    }

}