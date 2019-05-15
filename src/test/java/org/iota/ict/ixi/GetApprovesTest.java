package org.iota.ict.ixi;

import org.iota.ict.ixi.model.Tangle;
import org.iota.ict.ixi.util.AbstractModuleTestTemplate;
import org.iota.ict.ixi.util.TangleGenerator;
import org.iota.ict.model.transaction.Transaction;
import org.iota.ict.model.transaction.TransactionBuilder;
import org.junit.Assert;
import org.junit.Test;

public class GetApprovesTest extends AbstractModuleTestTemplate {

    @Test
    public void testFindApproves() {

        String[] tips = TangleGenerator.getTransactionsToApprove(tangle);

        String trunk = tips[0];
        String branch = tips[1];

        TransactionBuilder builder = new TransactionBuilder();
        builder.trunkHash = trunk;
        builder.branchHash = branch;
        Transaction transaction = builder.build();

        tangle.add(transaction);

        String[] approves = Tangle.getApproves(transaction.hash, tangle);

        Assert.assertEquals(trunk, approves[0]);
        Assert.assertEquals(branch, approves[1]);

    }

}
