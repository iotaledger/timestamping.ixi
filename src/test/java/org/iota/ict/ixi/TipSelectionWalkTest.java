package org.iota.ict.ixi;

import org.iota.ict.ixi.util.TangleGenerator;
import org.iota.ict.ixi.util.TipSelectionTimestampingTestTemplate;
import org.iota.ict.model.Transaction;
import org.iota.ict.model.TransactionBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Set;

public class TipSelectionWalkTest extends TipSelectionTimestampingTestTemplate {

    @Test
    public void walkThroughChainTest() {

        tangle = TangleGenerator.createTangle(50);

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
        tb4.trunkHash = t3.hash;
        tb4.branchHash = t3.hash;
        Transaction t4 = tb4.build();
        tangle.put(t4.hash, t4);

        Set<String> path = tipSelectionTimestampingModule.getPath(t1.hash, tangle);

        Assert.assertEquals(3, path.size());
        Assert.assertEquals(true, path.contains(t2.hash));
        Assert.assertEquals(true, path.contains(t3.hash));
        Assert.assertEquals(true, path.contains(t4.hash));

    }

    @Test
    public void walkThroughTangleTest() {

        tangle = TangleGenerator.createTangle(50);

        List<Transaction> tips = TangleGenerator.findTips(tangle);

        // t1 - genesis
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

        // t3 - referencing t2
        TransactionBuilder tb3 = new TransactionBuilder();
        tb3.trunkHash = t2.hash;
        tb3.branchHash = t2.hash;
        Transaction t3 = tb3.build();
        tangle.put(t3.hash, t3);

        // t4 - referencing t2
        TransactionBuilder tb4 = new TransactionBuilder();
        tb4.trunkHash = t2.hash;
        tb4.branchHash = t2.hash;
        Transaction t4 = tb4.build();
        tangle.put(t4.hash, t4);

        // t5 - increase rating of t4
        TransactionBuilder tb5 = new TransactionBuilder();
        tb5.trunkHash = t4.hash;
        tb5.branchHash = t4.hash;
        Transaction t5 = tb5.build();
        tangle.put(t5.hash, t5);

        Set<String> path = tipSelectionTimestampingModule.getPath(t1.hash, tangle);

        Assert.assertEquals(3, path.size());
        // path must not contain t2
        Assert.assertEquals(true, path.contains(t2.hash));
        Assert.assertEquals(true, path.contains(t4.hash));
        Assert.assertEquals(true, path.contains(t5.hash));

    }

}
