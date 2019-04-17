package org.iota.ict.ixi;

import com.sun.javaws.exceptions.InvalidArgumentException;
import org.iota.ict.eee.call.EEEFunction;
import org.iota.ict.eee.call.FunctionEnvironment;
import org.iota.ict.ixi.model.Interval;
import org.iota.ict.ixi.model.Tangle;
import org.iota.ict.ixi.model.TimestampType;
import org.iota.ict.ixi.model.TimestampingCalculation;
import org.iota.ict.model.transaction.Transaction;
import org.iota.ict.network.gossip.GossipEvent;
import org.iota.ict.network.gossip.GossipListener;

import java.util.*;

public abstract class AbstractTimestampingProcedure extends IxiModule {

    protected Map<String, TimestampingCalculation> calculations = new HashMap<>();
    protected Tangle tangle = new Tangle();

    private final EEEFunction beginTimestampCalculation = new EEEFunction(new FunctionEnvironment("Timestamping.ixi", "beginTimestampCalculation"));
    private final EEEFunction getTimestampInterval = new EEEFunction(new FunctionEnvironment("Timestamping.ixi", "getTimestampInterval"));

    public AbstractTimestampingProcedure(Ixi ixi) {

        super(ixi);

        ixi.addListener(new GossipListener.Implementation() {
            @Override
            public void onReceive(GossipEvent effect) {
                tangle.add(effect.getTransaction());
            }
        });

        ixi.addListener(beginTimestampCalculation);
        ixi.addListener(getTimestampInterval);

    }

    @Override
    public void run() {

        new Thread(() -> {
        while (isRunning()) {
            try {
                processBeginTimestampCalculationRequest(beginTimestampCalculation.requestQueue.take());
            } catch (Exception e) {
                if(isRunning()) throw new RuntimeException(e);
            }
        }
    }).start();

        new Thread(() -> {
            while (isRunning()) {
                try {
                    processGetTimestampIntervalRequest(getTimestampInterval.requestQueue.take());
                } catch (InterruptedException e) {
                    if(isRunning()) throw new RuntimeException(e);
                }
            }
        }).start();
    }

    private void processBeginTimestampCalculationRequest(EEEFunction.Request request) throws InvalidArgumentException {
        String argument = request.argument;
        String identifier = beginTimestampCalculation(argument);
        request.submitReturn(ixi, identifier);
    }

    private void processGetTimestampIntervalRequest(EEEFunction.Request request) {
        String identifier = request.argument;
        Interval ret = getTimestampInterval(identifier, tangle);
        request.submitReturn(ixi, ret.toString());
    }

    public abstract String beginTimestampCalculation(String txToInspect, Object... args) throws InvalidArgumentException;

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

        Set<String> approvers = tangle.getDirectApprovers(transactionHash);

        if(approvers.size() == 0)
            return;

        ret.addAll(approvers);

        for(String hash: approvers)
            traverseApprovers(hash, ret, visited, tangle);

    }

}
