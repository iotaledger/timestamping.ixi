package org.iota.ict.ixi;

import org.iota.ict.ixi.util.ModuleTestTemplate;
import org.iota.ict.ixi.util.TangleGenerator;
import org.iota.ict.model.Transaction;
import org.iota.ict.model.TransactionBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class GetPastTest extends ModuleTestTemplate {

    @Test
    public void testTraverseApproved() {

        tangle = new HashMap<>();

        // genesis
        Transaction genesis = new TransactionBuilder().build();
        tangle.put(genesis.hash, genesis);

        // t1
        TransactionBuilder tb1 = new TransactionBuilder();
        tb1.trunkHash = genesis.hash;
        tb1.branchHash = genesis.hash;
        Transaction t1 = tb1.build();
        tangle.put(t1.hash, t1);

        // t2
        TransactionBuilder tb2 = new TransactionBuilder();
        tb2.trunkHash = genesis.hash;
        tb2.branchHash = t1.hash;
        Transaction t2 = tb2.build();
        tangle.put(t2.hash, t2);

        // t3
        TransactionBuilder tb3 = new TransactionBuilder();
        tb3.trunkHash = t1.hash;
        tb3.branchHash = t2.hash;
        Transaction t3 = tb3.build();
        tangle.put(t3.hash, t3);

        // random tx
        TransactionBuilder tb4 = new TransactionBuilder();
        tb4.trunkHash = TangleGenerator.getRandomHash();
        tb4.branchHash = TangleGenerator.getRandomHash();
        Transaction t4 = tb4.build();
        tangle.put(t4.hash, t4);

        // random tx
        TransactionBuilder tb5 = new TransactionBuilder();
        tb5.trunkHash = TangleGenerator.getRandomHash();
        tb5.branchHash = TangleGenerator.getRandomHash();
        Transaction t5 = tb5.build();
        tangle.put(t5.hash, t5);


        Set<String> confirmed = timestampingModule.getPast(t3.hash, tangle);
        confirmed.remove("999999999999999999999999999999999999999999999999999999999999999999999999999999999");

        Assert.assertEquals(3, confirmed.size());
        Assert.assertEquals(true, confirmed.contains(genesis.hash));
        Assert.assertEquals(true, confirmed.contains(t1.hash));
        Assert.assertEquals(true, confirmed.contains(t2.hash));

        Assert.assertEquals(false, confirmed.contains(t4.hash));
        Assert.assertEquals(false, confirmed.contains(t5.hash));

    }

}
