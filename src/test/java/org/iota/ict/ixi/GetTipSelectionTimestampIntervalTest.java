package org.iota.ict.ixi;

import org.iota.ict.ixi.model.Interval;
import org.iota.ict.ixi.util.TangleGenerator;
import org.iota.ict.ixi.util.TipSelectionTimestampingTestTemplate;
import org.iota.ict.model.Transaction;
import org.iota.ict.model.TransactionBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class GetTipSelectionTimestampIntervalTest extends TipSelectionTimestampingTestTemplate {

    @Test
    public void testInterval() {

        List<Transaction> tips = TangleGenerator.findTips(tangle);

        // t1
        TransactionBuilder tb1 = new TransactionBuilder();
        tb1.trunkHash = tips.get(0).hash;
        tb1.branchHash = tips.get(1).hash;
        Transaction t1 = tb1.build();
        tangle.put(t1.hash, t1);

        TangleGenerator.continueTangle(tangle,50);

        Interval time1 = tipSelectionTimestampingModule.getTimestampInterval(tipSelectionTimestampingModule.beginTimestampCalculation(t1.hash, genesis), tangle);

        Assert.assertTrue(time1.getLowerbound() <= t1.attachmentTimestamp && t1.attachmentTimestamp <= time1.getUpperbound());
    }

    @Test
    public void testChain() {

        List<Transaction> tips = TangleGenerator.findTips(tangle);

        // t1
        TransactionBuilder tb1 = new TransactionBuilder();
        tb1.trunkHash = tips.get(0).hash;
        tb1.branchHash = tips.get(1).hash;
        Transaction t1 = tb1.build();
        tangle.put(t1.hash, t1);

        // t2
        TransactionBuilder tb2 = new TransactionBuilder();
        tb2.trunkHash = t1.hash;
        tb2.branchHash = t1.hash;
        Transaction t2 = tb2.build();
        tangle.put(t2.hash, t2);

        // t3
        TransactionBuilder tb3 = new TransactionBuilder();
        tb3.trunkHash = t2.hash;
        tb3.branchHash = t2.hash;
        Transaction t3 = tb3.build();
        tangle.put(t3.hash, t3);

        // t4
        TransactionBuilder tb4 = new TransactionBuilder();
        tb4.trunkHash = t2.hash;
        tb4.branchHash = t2.hash;
        Transaction t4 = tb4.build();
        tangle.put(t4.hash, t4);

        // t5
        TransactionBuilder tb5 = new TransactionBuilder();
        tb5.trunkHash = t2.hash;
        tb5.branchHash = t2.hash;
        Transaction t5 = tb5.build();
        tangle.put(t5.hash, t5);

        TangleGenerator.continueTangle(tangle,50);

        Interval time1 = tipSelectionTimestampingModule.getTimestampInterval(tipSelectionTimestampingModule.beginTimestampCalculation(t3.hash, t1.hash), tangle);
        Interval time2 = tipSelectionTimestampingModule.getTimestampInterval(tipSelectionTimestampingModule.beginTimestampCalculation(t4.hash, t1.hash), tangle);

        Assert.assertTrue(time1.getLowerbound() <= t3.attachmentTimestamp && t3.attachmentTimestamp <= time1.getUpperbound());
        Assert.assertTrue(time2.getLowerbound() <= t4.attachmentTimestamp && t4.attachmentTimestamp <= time2.getUpperbound());

    }

}
