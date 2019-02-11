package org.iota.ict.ixi;

import org.iota.ict.ixi.util.ModuleTestTemplate;
import org.iota.ict.ixi.util.TangleGenerator;
import org.iota.ict.model.Transaction;
import org.iota.ict.model.TransactionBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class ApprovesTest extends ModuleTestTemplate {

    @Test
    public void testFindApproves() {

        List<Transaction> tips = TangleGenerator.findTips(tangle);

        String trunk = tips.get(0).hash;
        String branch = tips.get(1).hash;

        TransactionBuilder builder = new TransactionBuilder();
        builder.trunkHash = trunk;
        builder.branchHash = branch;
        Transaction transaction = builder.build();

        tangle.put(transaction.hash, transaction);

        String[] approves = timestampingModule.getApproves(transaction.hash, tangle);

        Assert.assertEquals(trunk, approves[0]);
        Assert.assertEquals(branch, approves[1]);

    }

}
