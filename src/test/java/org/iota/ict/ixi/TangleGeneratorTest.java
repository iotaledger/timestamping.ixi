package org.iota.ict.ixi;

import org.iota.ict.ixi.model.Tangle;
import org.iota.ict.ixi.util.AbstractModuleTestTemplate;
import org.iota.ict.ixi.util.TangleGenerator;
import org.iota.ict.model.Transaction;
import org.iota.ict.model.TransactionBuilder;
import org.junit.Assert;
import org.junit.Test;

public class TangleGeneratorTest extends AbstractModuleTestTemplate {

    @Test
    public void testFindTips() {

        Tangle tangle = new Tangle();

        Assert.assertEquals(0, TangleGenerator.findTips(tangle).size());

        Transaction genesis = new TransactionBuilder().build();
        tangle.addTransaction(genesis);

        Assert.assertEquals(1, TangleGenerator.findTips(tangle).size());

        TransactionBuilder tb1 = new TransactionBuilder();
        tb1.trunkHash = genesis.hash;
        tb1.branchHash = genesis.hash;
        Transaction t1 = tb1.build();
        tangle.addTransaction(t1);

        Assert.assertEquals(1, TangleGenerator.findTips(tangle).size());

        TransactionBuilder tb2 = new TransactionBuilder();
        tb2.trunkHash = genesis.hash;
        tb2.branchHash = t1.hash;
        Transaction t2 = tb2.build();
        tangle.addTransaction(t2);

        Assert.assertEquals(1, TangleGenerator.findTips(tangle).size());

        TransactionBuilder tb3 = new TransactionBuilder();
        tb3.trunkHash = genesis.hash;
        tb3.branchHash = t1.hash;
        Transaction t3 = tb3.build();
        tangle.addTransaction(t3);

        Assert.assertEquals(2, TangleGenerator.findTips(tangle).size());

        TransactionBuilder tb4 = new TransactionBuilder();
        tb4.trunkHash = t1.hash;
        tb4.branchHash = t2.hash;
        Transaction t4 = tb4.build();
        tangle.addTransaction(t4);

        Assert.assertEquals(2, TangleGenerator.findTips(tangle).size());

        TransactionBuilder tb5 = new TransactionBuilder();
        tb5.trunkHash = t4.hash;
        tb5.branchHash = t3.hash;
        Transaction t5 = tb5.build();
        tangle.addTransaction(t5);

        Assert.assertEquals(1, TangleGenerator.findTips(tangle).size());

    }

}
