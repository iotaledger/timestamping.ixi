package org.iota.ict.ixi;

import org.iota.ict.ixi.util.TangleGenerator;
import org.iota.ict.ixi.util.TipSelectionTimestampingTestTemplate;
import org.iota.ict.model.Transaction;
import org.iota.ict.model.TransactionBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class CalculateRatingsTest extends TipSelectionTimestampingTestTemplate {

    @Test
    public void testCalculateChainRating() {

        String[] tips = TangleGenerator.getTransactionsToApprove(tangle);

        // t1
        TransactionBuilder tb1 = new TransactionBuilder();
        tb1.trunkHash = tips[0];
        tb1.branchHash = tips[1];
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
        Transaction t3 = tb3.build();
        tangle.add(t3);

        Map<String, Integer> chainRatings = tipSelectionTimestampingModule.calculateRatings(genesis, tangle);

        Assert.assertEquals(3, chainRatings.get(t1.hash).intValue());
        Assert.assertEquals(2, chainRatings.get(t2.hash).intValue());
        Assert.assertEquals(1, chainRatings.get(t3.hash).intValue());

    }

    @Test
    public void testCalculateTipRating() {

        String[] tips = TangleGenerator.getTransactionsToApprove(tangle);

        // t1
        TransactionBuilder tb1 = new TransactionBuilder();
        tb1.trunkHash = tips[0];
        tb1.branchHash = tips[1];
        Transaction t1 = tb1.build();
        tangle.add(t1);

        Map<String, Integer> ratings = tipSelectionTimestampingModule.calculateRatings(genesis, tangle);

        Assert.assertEquals(1, ratings.get(t1.hash).intValue());

    }

    @Test
    public void testCalculateTangleRating() {

        String[] tips = TangleGenerator.getTransactionsToApprove(tangle);

        // t1
        TransactionBuilder tb1 = new TransactionBuilder();
        tb1.trunkHash = tips[0];
        tb1.branchHash = tips[1];
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
        Transaction t3 = tb3.build();
        tangle.add(t3);

        // t4
        TransactionBuilder tb4 = new TransactionBuilder();
        tb4.trunkHash = t2.hash;
        tb4.branchHash = t2.hash;
        Transaction t4 = tb4.build();
        tangle.add(t4);

        // t5
        TransactionBuilder tb5 = new TransactionBuilder();
        tb5.trunkHash = t1.hash;
        tb5.branchHash = t2.hash;
        Transaction t5 = tb5.build();
        tangle.add(t5);

        Map<String, Integer> ratings = tipSelectionTimestampingModule.calculateRatings(genesis, tangle);

        Assert.assertEquals(5, ratings.get(t1.hash).intValue());
        Assert.assertEquals(4, ratings.get(t2.hash).intValue());
        Assert.assertEquals(1, ratings.get(t3.hash).intValue());

    }

}