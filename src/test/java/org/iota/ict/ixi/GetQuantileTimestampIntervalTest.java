package org.iota.ict.ixi;

import com.sun.javaws.exceptions.InvalidArgumentException;
import org.iota.ict.ixi.model.Interval;
import org.iota.ict.ixi.util.QuantileTimestampingTestTemplate;
import org.iota.ict.ixi.util.TangleGenerator;
import org.iota.ict.model.transaction.Transaction;
import org.iota.ict.model.transaction.TransactionBuilder;
import org.junit.Assert;
import org.junit.Test;

public class GetQuantileTimestampIntervalTest extends QuantileTimestampingTestTemplate {

    @Test
    public void getTimestampIntervalTest() throws InvalidArgumentException {

        String[] tips = TangleGenerator.getTransactionsToApprove(tangle);

        // t1
        TransactionBuilder tb1 = new TransactionBuilder();
        tb1.trunkHash = tips[0];
        tb1.branchHash = tips[1];
        tb1.attachmentTimestampLowerBound = System.currentTimeMillis();
        tb1.attachmentTimestamp = System.currentTimeMillis();
        tb1.attachmentTimestampUpperBound = System.currentTimeMillis();
        Transaction t1 = tb1.build();
        tangle.add(t1);

        TangleGenerator.continueTangle(tangle,50);

        Interval time = quantileTimestampingModule.getTimestampInterval(quantileTimestampingModule.beginTimestampCalculation(t1.hash, 0.3), tangle);

        Assert.assertTrue(time.getLowerbound() <= t1.attachmentTimestampLowerBound);
        Assert.assertTrue(time.getUpperbound() >= t1.attachmentTimestampUpperBound);

    }

}
