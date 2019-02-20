package org.iota.ict.ixi;

import org.iota.ict.ixi.util.DefaultModuleTestTemplate;
import org.iota.ict.ixi.util.QuantileTimestampingTestTemplate;
import org.iota.ict.model.Transaction;
import org.iota.ict.model.TransactionBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

public class IsReferencingTest extends DefaultModuleTestTemplate {

    @Test
    public void isReferencingTest() {

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

        Assert.assertEquals(true, timestampingModule.isReferencing(t3.hash, genesis.hash, tangle));

    }

}
