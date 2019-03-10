package org.iota.ict.ixi;

import org.iota.ict.ixi.util.AbstractModuleTestTemplate;
import org.iota.ict.model.Transaction;
import org.iota.ict.model.TransactionBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GetApproversTest extends AbstractModuleTestTemplate {

    @Test
    public void getApproversTest() {

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

        // t3 - the first which is referencing t2
        TransactionBuilder tb3 = new TransactionBuilder();
        tb3.trunkHash = t2.hash;
        tb3.branchHash = t2.hash;
        Transaction t3 = tb3.build();
        tangle.add(t3);

        // t4
        TransactionBuilder tb4 = new TransactionBuilder();
        tb4.trunkHash = t3.hash;
        tb4.branchHash = t1.hash;
        Transaction t4 = tb4.build();
        tangle.add(t4);

        // t5 - the second which is referencing t2
        TransactionBuilder tb5 = new TransactionBuilder();
        tb5.trunkHash = t4.hash;
        tb5.branchHash = t2.hash;
        Transaction t5 = tb5.build();
        tangle.add(t5);

        Set<String> past = AbstractTimestampingProcedure.getPast(t2.hash, tangle);
        Set<String> future = AbstractTimestampingProcedure.getFuture(t2.hash, tangle);

        Set<String> approvers = AbstractTimestampingProcedure.getApprovers(t2.hash, tangle);

        Assert.assertEquals(2, approvers.size());
        Assert.assertEquals(true, approvers.contains(t3.hash));
        Assert.assertEquals(true, approvers.contains(t5.hash));

    }

}
