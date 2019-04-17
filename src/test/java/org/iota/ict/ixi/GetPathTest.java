package org.iota.ict.ixi;

import org.iota.ict.ixi.model.Tangle;
import org.iota.ict.ixi.util.TangleGenerator;
import org.iota.ict.ixi.util.TipSelectionTimestampingTestTemplate;
import org.iota.ict.model.transaction.Transaction;
import org.iota.ict.model.transaction.TransactionBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GetPathTest extends TipSelectionTimestampingTestTemplate {

    @Test
    public void singleTransactionTest() {

        tangle = new Tangle();

        // t1
        TransactionBuilder tb1 = new TransactionBuilder();
        Transaction t1 = tb1.build();
        tangle.add(t1);

        Set<String> path = tipSelectionTimestampingModule.getPath(t1.hash, tangle);

        Assert.assertTrue(path.size() == 1);
        Assert.assertTrue(path.contains(t1.hash));

    }

    @Test
    public void walkThroughChainTest() {

        // t1
        TransactionBuilder tb1 = new TransactionBuilder();
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
        tb4.trunkHash = t3.hash;
        tb4.branchHash = t3.hash;
        Transaction t4 = tb4.build();
        tangle.add(t4);

        Set<String> path = tipSelectionTimestampingModule.getPath(t1.hash, tangle);

        Assert.assertEquals(4, path.size());
        Assert.assertEquals(true, path.contains(t1.hash));
        Assert.assertEquals(true, path.contains(t2.hash));
        Assert.assertEquals(true, path.contains(t3.hash));
        Assert.assertEquals(true, path.contains(t4.hash));

    }

    @Test
    public void walkThroughSimpleTangleTest() {

        List<String> tips = new ArrayList<>(TangleGenerator.getAttachmentCandidates(tangle));

        // t1
        TransactionBuilder tb1 = new TransactionBuilder();
        tb1.trunkHash = tips.get(0);
        tb1.branchHash = tips.get(1);
        Transaction t1 = tb1.build();
        tangle.add(t1);

        // t2
        TransactionBuilder tb2 = new TransactionBuilder();
        tb2.trunkHash = t1.hash;
        tb2.branchHash = t1.hash;
        Transaction t2 = tb2.build();
        tangle.add(t2);

        // t3 - referencing t2
        TransactionBuilder tb3 = new TransactionBuilder();
        tb3.trunkHash = t2.hash;
        tb3.branchHash = t2.hash;
        Transaction t3 = tb3.build();
        tangle.add(t3);

        // t4 - referencing t2
        TransactionBuilder tb4 = new TransactionBuilder();
        tb4.trunkHash = t2.hash;
        tb4.branchHash = t2.hash;
        Transaction t4 = tb4.build();
        tangle.add(t4);

        // t5 - increase rating of t4
        TransactionBuilder tb5 = new TransactionBuilder();
        tb5.trunkHash = t4.hash;
        tb5.branchHash = t4.hash;
        Transaction t5 = tb5.build();
        tangle.add(t5);

        Set<String> path = tipSelectionTimestampingModule.getPath(t1.hash, tangle);

        Assert.assertEquals(4, path.size());

        // path shall not contain t3
        Assert.assertEquals(false, path.contains(t3.hash));

        Assert.assertEquals(true, path.contains(t1.hash));
        Assert.assertEquals(true, path.contains(t2.hash));
        Assert.assertEquals(true, path.contains(t4.hash));
        Assert.assertEquals(true, path.contains(t5.hash));

    }

    @Test
    public void walkThroughTangleWithMoreBranchesTest() {

        List<String> tips = new ArrayList(tangle.getTips());

        // t1
        TransactionBuilder tb1 = new TransactionBuilder();
        tb1.trunkHash = tips.get(0);
        tb1.branchHash = tips.get(1);
        Transaction t1 = tb1.build();
        tangle.add(t1);

        // t2
        TransactionBuilder tb2 = new TransactionBuilder();
        tb2.trunkHash = t1.hash;
        tb2.branchHash = t1.hash;
        Transaction t2 = tb2.build();
        tangle.add(t2);

        // t3 - referencing t2
        TransactionBuilder tb3 = new TransactionBuilder();
        tb3.trunkHash = t2.hash;
        tb3.branchHash = t2.hash;
        Transaction t3 = tb3.build();
        tangle.add(t3);

        // t4 - referencing t2
        TransactionBuilder tb4 = new TransactionBuilder();
        tb4.trunkHash = t2.hash;
        tb4.branchHash = t2.hash;
        Transaction t4 = tb4.build();
        tangle.add(t4);

        // t5 - increase rating of t4
        TransactionBuilder tb5 = new TransactionBuilder();
        tb5.trunkHash = t4.hash;
        tb5.branchHash = t4.hash;
        Transaction t5 = tb5.build();
        tangle.add(t5);

        // t6 - increase rating of t3
        TransactionBuilder tb6 = new TransactionBuilder();
        tb6.trunkHash = t3.hash;
        tb6.branchHash = t3.hash;
        Transaction t6 = tb6.build();
        tangle.add(t6);

        // t7 - increase rating of t3
        TransactionBuilder tb7 = new TransactionBuilder();
        tb7.trunkHash = t3.hash;
        tb7.branchHash = t3.hash;
        Transaction t7 = tb7.build();
        tangle.add(t7);

        // t8 - increase rating of t7
        TransactionBuilder tb8 = new TransactionBuilder();
        tb8.trunkHash = t7.hash;
        tb8.branchHash = t7.hash;
        Transaction t8 = tb8.build();
        tangle.add(t8);

        Set<String> path = tipSelectionTimestampingModule.getPath(t1.hash, tangle);

        Assert.assertEquals(5, path.size());

        Assert.assertEquals(true, path.contains(t1.hash));
        Assert.assertEquals(true, path.contains(t2.hash));
        Assert.assertEquals(true, path.contains(t3.hash));
        Assert.assertEquals(true, path.contains(t7.hash));
        Assert.assertEquals(true, path.contains(t8.hash));

    }

}
