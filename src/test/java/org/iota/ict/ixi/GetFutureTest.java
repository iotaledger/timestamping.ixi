package org.iota.ict.ixi;

import org.iota.ict.ixi.util.AbstractModuleTestTemplate;
import org.iota.ict.model.Transaction;
import org.iota.ict.model.TransactionBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

public class GetFutureTest extends AbstractModuleTestTemplate {

    @Test
    public void testGetAllDirectApprovers() {

        // genesis
        Transaction genesis = new TransactionBuilder().build();
        tangle.addTransaction(genesis);

        // t1
        TransactionBuilder tb1 = new TransactionBuilder();
        tb1.trunkHash = genesis.hash;
        tb1.branchHash = genesis.hash;
        Transaction t1 = tb1.build();
        tangle.addTransaction(t1);

        // t2
        TransactionBuilder tb2 = new TransactionBuilder();
        tb2.trunkHash = t1.hash;
        tb2.branchHash = t1.hash;
        Transaction t2 = tb2.build();
        tangle.addTransaction(t2);

        // t3
        TransactionBuilder tb3 = new TransactionBuilder();
        tb3.trunkHash = t1.hash;
        tb3.branchHash = t1.hash;
        Transaction t3 = tb3.build();
        tangle.addTransaction(t3);

        // t4
        TransactionBuilder tb4 = new TransactionBuilder();
        tb4.trunkHash = t1.hash;;
        tb4.branchHash = t1.hash;
        Transaction t4 = tb4.build();
        tangle.addTransaction(t4);

        Set<String> future = AbstractTimestampingModule.getFuture(genesis.hash, AbstractTimestampingModule.getPast(genesis.hash, tangle), tangle);

        Assert.assertEquals(4, future.size());
        Assert.assertEquals(true, future.contains(t1.hash));
        Assert.assertEquals(true, future.contains(t2.hash));
        Assert.assertEquals(true, future.contains(t3.hash));
        Assert.assertEquals(true, future.contains(t4.hash));

    }

    @Test
    public void testGetAllDirectAndIndirectApprovers() {

        // genesis
        Transaction genesis = new TransactionBuilder().build();
        tangle.addTransaction(genesis);

        // t1
        TransactionBuilder tb1 = new TransactionBuilder();
        tb1.trunkHash = genesis.hash;
        tb1.branchHash = genesis.hash;
        Transaction t1 = tb1.build();
        tangle.addTransaction(t1);

        // t2
        TransactionBuilder tb2 = new TransactionBuilder();
        tb2.trunkHash = genesis.hash;
        tb2.branchHash = t1.hash;
        Transaction t2 = tb2.build();
        tangle.addTransaction(t2);

        // t3
        TransactionBuilder tb3 = new TransactionBuilder();
        tb3.trunkHash = t1.hash;
        tb3.branchHash = t2.hash;
        Transaction t3 = tb3.build();
        tangle.addTransaction(t3);

        // t4
        TransactionBuilder tb4 = new TransactionBuilder();
        tb4.trunkHash = t3.hash;
        tb4.branchHash = t2.hash;
        Transaction t4 = tb4.build();
        tangle.addTransaction(t4);

        Set<String> future = AbstractTimestampingModule.getFuture(genesis.hash, AbstractTimestampingModule.getPast(genesis.hash, tangle), tangle);

        Assert.assertEquals(4, future.size());
        Assert.assertEquals(true, future.contains(t1.hash));
        Assert.assertEquals(true, future.contains(t2.hash));
        Assert.assertEquals(true, future.contains(t3.hash));
        Assert.assertEquals(true, future.contains(t4.hash));

    }

    @Test
    public void testTip() {

        Transaction tip = new TransactionBuilder().build();
        tangle.addTransaction(tip);

        Set<String> future = AbstractTimestampingModule.getFuture(tip.hash, AbstractTimestampingModule.getPast(tip.hash, tangle), tangle);
        Assert.assertEquals(0, future.size());

    }

}
