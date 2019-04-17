package org.iota.ict.ixi;

import org.iota.ict.ixi.util.AbstractModuleTestTemplate;
import org.iota.ict.ixi.util.Generator;
import org.iota.ict.model.transaction.Transaction;
import org.iota.ict.model.transaction.TransactionBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

public class GetPastTest extends AbstractModuleTestTemplate {

    @Test
    public void testTraverseApproved() {

        // genesis
        Transaction genesis = new TransactionBuilder().build();
        tangle.add(genesis);

        // t1
        TransactionBuilder tb1 = new TransactionBuilder();
        tb1.trunkHash = genesis.hash;
        tb1.branchHash = genesis.hash;
        Transaction t1 = tb1.build();
        tangle.add(t1);

        // t2
        TransactionBuilder tb2 = new TransactionBuilder();
        tb2.trunkHash = genesis.hash;
        tb2.branchHash = t1.hash;
        Transaction t2 = tb2.build();
        tangle.add(t2);

        // t3
        TransactionBuilder tb3 = new TransactionBuilder();
        tb3.trunkHash = t1.hash;
        tb3.branchHash = t2.hash;
        Transaction t3 = tb3.build();
        tangle.add(t3);

        // random tx
        TransactionBuilder tb4 = new TransactionBuilder();
        tb4.trunkHash = Generator.getRandomHash();
        tb4.branchHash = Generator.getRandomHash();
        Transaction t4 = tb4.build();
        tangle.add(t4);

        // random tx
        TransactionBuilder tb5 = new TransactionBuilder();
        tb5.trunkHash = Generator.getRandomHash();
        tb5.branchHash = Generator.getRandomHash();
        Transaction t5 = tb5.build();
        tangle.add(t5);


        Set<String> confirmed = AbstractTimestampingProcedure.getPast(t3.hash, tangle);
        confirmed.remove("999999999999999999999999999999999999999999999999999999999999999999999999999999999");

        Assert.assertEquals(3, confirmed.size());
        Assert.assertEquals(true, confirmed.contains(genesis.hash));
        Assert.assertEquals(true, confirmed.contains(t1.hash));
        Assert.assertEquals(true, confirmed.contains(t2.hash));

        Assert.assertEquals(false, confirmed.contains(t4.hash));
        Assert.assertEquals(false, confirmed.contains(t5.hash));

    }

}
