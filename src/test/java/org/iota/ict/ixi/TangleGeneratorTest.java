package org.iota.ict.ixi;

import org.iota.ict.ixi.util.AbstractModuleTestTemplate;
import org.iota.ict.ixi.util.TangleGenerator;
import org.iota.ict.model.Transaction;
import org.iota.ict.model.TransactionBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class TangleGeneratorTest extends AbstractModuleTestTemplate {

    @Test
    public void testFindTips() {

        Map<String, Transaction> tangle = new HashMap<>();

        Assert.assertEquals(0, TangleGenerator.findTips(tangle).size());

        Transaction genesis = new TransactionBuilder().build();
        tangle.put(genesis.hash, genesis);

        Assert.assertEquals(1, TangleGenerator.findTips(tangle).size());

        TransactionBuilder tb1 = new TransactionBuilder();
        tb1.trunkHash = genesis.hash;
        tb1.branchHash = genesis.hash;
        Transaction t1 = tb1.build();
        tangle.put(t1.hash, t1);

        Assert.assertEquals(1, TangleGenerator.findTips(tangle).size());

        TransactionBuilder tb2 = new TransactionBuilder();
        tb2.trunkHash = genesis.hash;
        tb2.branchHash = t1.hash;
        Transaction t2 = tb2.build();
        tangle.put(t2.hash, t2);

        Assert.assertEquals(1, TangleGenerator.findTips(tangle).size());

        TransactionBuilder tb3 = new TransactionBuilder();
        tb3.trunkHash = genesis.hash;
        tb3.branchHash = t1.hash;
        Transaction t3 = tb3.build();
        tangle.put(t3.hash, t3);

        Assert.assertEquals(2, TangleGenerator.findTips(tangle).size());

        TransactionBuilder tb4 = new TransactionBuilder();
        tb4.trunkHash = t1.hash;
        tb4.branchHash = t2.hash;
        Transaction t4 = tb4.build();
        tangle.put(t4.hash, t4);

        Assert.assertEquals(2, TangleGenerator.findTips(tangle).size());

        TransactionBuilder tb5 = new TransactionBuilder();
        tb5.trunkHash = t4.hash;
        tb5.branchHash = t3.hash;
        Transaction t5 = tb5.build();
        tangle.put(t5.hash, t5);

        Assert.assertEquals(1, TangleGenerator.findTips(tangle).size());

    }

}
