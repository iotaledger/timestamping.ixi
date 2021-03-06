package org.iota.ict.ixi;

import com.sun.javaws.exceptions.InvalidArgumentException;
import org.iota.ict.ixi.model.Interval;
import org.iota.ict.ixi.util.TangleGenerator;
import org.iota.ict.ixi.util.TipSelectionTimestampingTestTemplate;
import org.iota.ict.model.transaction.Transaction;
import org.iota.ict.model.transaction.TransactionBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class GetTipSelectionTimestampIntervalTest extends TipSelectionTimestampingTestTemplate {

    @Test
    public void testChain() throws InvalidArgumentException {

        List<String> tips = new ArrayList(tangle.getTips());

        // t1
        TransactionBuilder tb1 = new TransactionBuilder();
        tb1.trunkHash = tips.get(0);
        tb1.branchHash = tips.get(1);
        tb1.attachmentTimestampLowerBound = 1;
        tb1.attachmentTimestamp = 2;
        tb1.attachmentTimestampUpperBound = 3;
        Transaction t1 = tb1.build();
        tangle.add(t1);

        // t2
        TransactionBuilder tb2 = new TransactionBuilder();
        tb2.trunkHash = t1.hash;
        tb2.branchHash = t1.hash;
        tb2.attachmentTimestampLowerBound = 4;
        tb2.attachmentTimestamp = 5;
        tb2.attachmentTimestampUpperBound = 6;
        Transaction t2 = tb2.build();
        tangle.add(t2);

        // t3
        TransactionBuilder tb3 = new TransactionBuilder();
        tb3.trunkHash = t2.hash;
        tb3.branchHash = t2.hash;
        tb3.attachmentTimestampLowerBound = 7;
        tb3.attachmentTimestamp = 8;
        tb3.attachmentTimestampUpperBound = 9;
        Transaction t3 = tb3.build();
        tangle.add(t3);

        // t4
        TransactionBuilder tb4 = new TransactionBuilder();
        tb4.trunkHash = t3.hash;
        tb4.branchHash = t3.hash;
        tb4.attachmentTimestampLowerBound = 10;
        tb4.attachmentTimestamp = 11;
        tb4.attachmentTimestampUpperBound = 12;
        Transaction t4 = tb4.build();
        tangle.add(t4);

        // t5
        TransactionBuilder tb5 = new TransactionBuilder();
        tb5.trunkHash = t4.hash;
        tb5.branchHash = t4.hash;
        tb5.attachmentTimestampLowerBound = 13;
        tb5.attachmentTimestamp = 14;
        tb5.attachmentTimestampUpperBound = 15;
        Transaction t5 = tb5.build();
        tangle.add(t5);

        Interval time = tipSelectionTimestampingModule.getTimestampInterval(tipSelectionTimestampingModule.beginTimestampCalculation(t3.hash, t1.hash), tangle);

        Assert.assertTrue(time.getLowerbound() == t2.attachmentTimestampLowerBound);
        Assert.assertTrue(time.getUpperbound() == t4.attachmentTimestampUpperBound);

    }

    @Test
    public void testTangle() throws InvalidArgumentException {

        List<String> tips = new ArrayList(tangle.getTips());

        // t1
        TransactionBuilder tb1 = new TransactionBuilder();
        tb1.trunkHash = tips.get(0);
        tb1.branchHash = tips.get(1);
        tb1.attachmentTimestampLowerBound = 1;
        tb1.attachmentTimestamp = 2;
        tb1.attachmentTimestampUpperBound = 3;
        Transaction t1 = tb1.build();
        tangle.add(t1);

        // t2
        TransactionBuilder tb2 = new TransactionBuilder();
        tb2.trunkHash = t1.hash;
        tb2.branchHash = t1.hash;
        tb2.attachmentTimestampLowerBound = 4;
        tb2.attachmentTimestamp = 5;
        tb2.attachmentTimestampUpperBound = 6;
        Transaction t2 = tb2.build();
        tangle.add(t2);

        // t3 - shall not walk trough this transaction
        TransactionBuilder tb3 = new TransactionBuilder();
        tb3.trunkHash = t2.hash;
        tb3.branchHash = t2.hash;
        tb3.attachmentTimestampLowerBound = 7;
        tb3.attachmentTimestamp = 8;
        tb3.attachmentTimestampUpperBound = 9;
        Transaction t3 = tb3.build();
        tangle.add(t3);

        // t4
        TransactionBuilder tb4 = new TransactionBuilder();
        tb4.trunkHash = t2.hash;
        tb4.branchHash = t2.hash;
        tb4.attachmentTimestampLowerBound = 10;
        tb4.attachmentTimestamp = 11;
        tb4.attachmentTimestampUpperBound = 12;
        Transaction t4 = tb4.build();
        tangle.add(t4);

        // t5
        TransactionBuilder tb5 = new TransactionBuilder();
        tb5.trunkHash = t4.hash;
        tb5.branchHash = t4.hash;
        tb5.attachmentTimestampLowerBound = 13;
        tb5.attachmentTimestamp = 14;
        tb5.attachmentTimestampUpperBound = 15;
        Transaction t5 = tb5.build();
        tangle.add(t5);

        Interval time1 = tipSelectionTimestampingModule.getTimestampInterval(tipSelectionTimestampingModule.beginTimestampCalculation(t2.hash, t1.hash), tangle);
        Interval time2 = tipSelectionTimestampingModule.getTimestampInterval(tipSelectionTimestampingModule.beginTimestampCalculation(t4.hash, t1.hash), tangle);

        Assert.assertTrue(time1.getLowerbound() == t1.attachmentTimestampLowerBound);
        Assert.assertTrue(time1.getUpperbound() == t4.attachmentTimestampUpperBound);

        Assert.assertTrue(time2.getLowerbound() == t2.attachmentTimestampLowerBound);
        Assert.assertTrue(time2.getUpperbound() == t5.attachmentTimestampUpperBound);

    }

    @Test
    public void startWalkAtGenesisWithFixedTips() throws InvalidArgumentException {

        List<Transaction> tips = new ArrayList(tangle.getTips());

        // find most weighted path
        LinkedHashSet<String> path = tipSelectionTimestampingModule.getPath(genesis, tangle);

        // get tip
        String tip = new ArrayList<>(path).get(path.size() -1);

        // t1
        TransactionBuilder tb1 = new TransactionBuilder();
        tb1.trunkHash = tip;
        tb1.branchHash = tip;
        tb1.attachmentTimestampLowerBound = System.currentTimeMillis();
        tb1.attachmentTimestamp = System.currentTimeMillis();
        tb1.attachmentTimestampUpperBound = System.currentTimeMillis();
        Transaction t1 = tb1.build();
        tangle.add(t1);

        // t2
        TransactionBuilder tb2 = new TransactionBuilder();
        tb2.trunkHash = t1.hash;
        tb2.branchHash = t1.hash;
        Transaction t2 = tb2.build();
        tangle.add(t2);

        // t3
        TransactionBuilder tb3 = new TransactionBuilder();
        tb3.trunkHash = t2.hash;
        tb3.branchHash = t2.hash;
        tb3.attachmentTimestampLowerBound = System.currentTimeMillis();
        tb3.attachmentTimestamp = System.currentTimeMillis();
        tb3.attachmentTimestampUpperBound = System.currentTimeMillis();
        Transaction t3 = tb3.build();
        tangle.add(t3);

        Interval time = tipSelectionTimestampingModule.getTimestampInterval(tipSelectionTimestampingModule.beginTimestampCalculation(t2.hash, genesis), tangle);

        Assert.assertTrue(time.getLowerbound() == t1.attachmentTimestampLowerBound);
        Assert.assertTrue(time.getUpperbound() == t3.attachmentTimestampUpperBound);

    }

    @Test
    public void startWalkAtGenesisWithRandomTips() throws InvalidArgumentException {

        String[] tips = TangleGenerator.getTransactionsToApprove(tangle);

        // t1
        TransactionBuilder tb1 = new TransactionBuilder();
        tb1.trunkHash = tips[0];
        tb1.branchHash = tips[1];
        tb1.attachmentTimestampLowerBound = System.currentTimeMillis();
        tb1.attachmentTimestamp = System.currentTimeMillis();
        tb1.attachmentTimestampUpperBound = System.currentTimeMillis();
        Transaction t1 = tb1.build();
        tangle.add(t1);

        TangleGenerator.continueTangle(tangle, 100);

        Interval time = tipSelectionTimestampingModule.getTimestampInterval(tipSelectionTimestampingModule.beginTimestampCalculation(t1.hash, genesis), tangle);

        Assert.assertTrue(time.getLowerbound() <= t1.attachmentTimestampLowerBound);
        Assert.assertTrue(time.getUpperbound() >= t1.attachmentTimestampUpperBound);

    }

    @Test
    public void startWalkAtSingleTransaction() throws InvalidArgumentException {

        // t1
        TransactionBuilder tb1 = new TransactionBuilder();
        tb1.attachmentTimestampLowerBound = System.currentTimeMillis();
        tb1.attachmentTimestamp = System.currentTimeMillis();
        tb1.attachmentTimestampUpperBound = System.currentTimeMillis();
        Transaction t1 = tb1.build();
        tangle.add(t1);

        Interval time = tipSelectionTimestampingModule.getTimestampInterval(tipSelectionTimestampingModule.beginTimestampCalculation(t1.hash, t1.hash), tangle);

        Assert.assertTrue(time.getLowerbound() == 0);
        Assert.assertTrue(time.getUpperbound() == Long.MAX_VALUE);

    }

}
