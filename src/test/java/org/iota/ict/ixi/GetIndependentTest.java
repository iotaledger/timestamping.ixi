package org.iota.ict.ixi;

import org.iota.ict.ixi.model.Tangle;
import org.iota.ict.ixi.util.AbstractModuleTestTemplate;
import org.iota.ict.ixi.util.Generator;
import org.iota.ict.model.Transaction;
import org.iota.ict.model.TransactionBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

public class GetIndependentTest extends AbstractModuleTestTemplate {

    @Test
    public void getIndependentTest() {

        tangle = new Tangle();

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

        // t4
        TransactionBuilder tb4 = new TransactionBuilder();
        tb4.trunkHash = t3.hash;
        tb4.branchHash = t2.hash;
        Transaction t4 = tb4.build();
        tangle.add(t4);

        // add 5 independent tx
        for(int i = 0; i < 5; i++) {
            TransactionBuilder tb = new TransactionBuilder();
            tb.trunkHash = Generator.getRandomHash();
            tb.branchHash = Generator.getRandomHash();
            Transaction t = tb.build();
            tangle.add(t);
        }

        Set<String> past = AbstractTimestampingModule.getPast(t2.hash, tangle);
        Set<String> future = AbstractTimestampingModule.getFuture(t2.hash, tangle);
        Set<String> independent = AbstractTimestampingModule.getIndependent(past, future, tangle);

        Assert.assertEquals(6, independent.size());
        Assert.assertEquals(false, independent.contains(genesis.hash));
        Assert.assertEquals(false, independent.contains(t1.hash));
        Assert.assertEquals(true, independent.contains(t2.hash));
        Assert.assertEquals(false, independent.contains(t3.hash));
        Assert.assertEquals(false, independent.contains(t4.hash));

    }

}
