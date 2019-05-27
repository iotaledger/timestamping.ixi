package org.iota.ict.ixi;

import org.iota.ict.eee.call.EEEFunction;
import org.iota.ict.eee.call.FunctionEnvironment;
import org.iota.ict.ixi.model.Interval;
import org.iota.ict.ixi.model.Tangle;
import org.iota.ict.ixi.model.TimestampType;
import org.iota.ict.ixi.model.TimestampingCalculation;
import org.iota.ict.model.transaction.Transaction;
import org.iota.ict.network.gossip.GossipEvent;
import org.iota.ict.network.gossip.GossipListener;
import org.iota.ict.network.gossip.GossipPreprocessor;

import java.util.*;

public abstract class AbstractTimestampingProcedure extends IxiModule {

    protected Map<String, TimestampingCalculation> calculations = new HashMap<>();
    protected Tangle tangle = new Tangle();

    private final EEEFunction beginTimestampCalculation = new EEEFunction(new FunctionEnvironment("Timestamping.ixi", "beginTimestampCalculation"));
    private final EEEFunction getTimestampInterval = new EEEFunction(new FunctionEnvironment("Timestamping.ixi", "getTimestampInterval"));

    private GossipPreprocessor gossipPreprocessor = new GossipPreprocessor(ixi, -4000);

    public AbstractTimestampingProcedure(Ixi ixi) {

        super(ixi);

        //ixi.addListener(new GossipListener.Implementation() {
           //@Override
           // public void onReceive(GossipEvent effect) {
                //tangle.add(effect.getTransaction());
            //}
        //});

        ixi.addListener(beginTimestampCalculation);
        ixi.addListener(getTimestampInterval);

        ixi.addListener(gossipPreprocessor);

    }

    @Override
    public void run() {

        new Thread(() -> {
            try {

                while(isRunning()){

                    GossipEvent effect = gossipPreprocessor.takeEffect();
                    gossipPreprocessor.passOn(effect);

                    if(effect.isOwnTransaction())
                        continue;

                    tangle.add(effect.getTransaction());

                }
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }).start();

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

        System.out.println("Timestamping.ixi successfully started!");
    }

    private void processBeginTimestampCalculationRequest(EEEFunction.Request request) {
        String[] arguments = request.argument.split(";");
        String txToInsepct = arguments[0];
        String randomWalkEntry = arguments[1];
        String identifier = beginTimestampCalculation(txToInsepct, randomWalkEntry);
        request.submitReturn(ixi, identifier);
    }

    private void processGetTimestampIntervalRequest(EEEFunction.Request request) {
        String identifier = request.argument;
        Interval ret = getTimestampInterval(identifier, tangle);
        request.submitReturn(ixi, ret.getLowerbound() + ";" + ret.getUpperbound());
    }

    public abstract String beginTimestampCalculation(String txToInspect, Object... args) throws IllegalArgumentException;

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

}
