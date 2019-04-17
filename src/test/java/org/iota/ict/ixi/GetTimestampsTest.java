package org.iota.ict.ixi;

import org.iota.ict.ixi.model.Tangle;
import org.iota.ict.ixi.model.TimestampType;
import org.iota.ict.ixi.util.AbstractModuleTestTemplate;
import org.iota.ict.model.transaction.Transaction;
import org.iota.ict.model.transaction.TransactionBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class GetTimestampsTest extends AbstractModuleTestTemplate {

    @Test
    public void testGetTimestamps() {

        List<Long> timestamps = new ArrayList<>();
        tangle = new Tangle();

        // genesis
        Transaction genesis = new TransactionBuilder().build();
        tangle.add(genesis);
        timestamps.add(genesis.attachmentTimestamp);

        // t1
        TransactionBuilder tb1 = new TransactionBuilder();
        tb1.trunkHash = genesis.hash;
        tb1.branchHash = genesis.hash;
        tb1.attachmentTimestampLowerBound = 1;
        tb1.attachmentTimestampUpperBound = 3;
        Transaction t1 = tb1.build();
        timestamps.add(t1.attachmentTimestamp);
        tangle.add(t1);

        // t2
        TransactionBuilder tb2 = new TransactionBuilder();
        tb2.trunkHash = genesis.hash;
        tb2.branchHash = t1.hash;
        tb2.attachmentTimestampLowerBound = 3;
        tb2.attachmentTimestampUpperBound = 5;
        Transaction t2 = tb2.build();
        timestamps.add(t2.attachmentTimestamp);
        tangle.add(t2);

        // t3
        TransactionBuilder tb3 = new TransactionBuilder();
        tb3.trunkHash = t1.hash;
        tb3.branchHash = t2.hash;
        Transaction t3 = tb3.build();
        tangle.add(t3);

        Set<String> set = AbstractTimestampingProcedure.getPast(t3.hash, tangle);

        List<Long> lowerbounds = AbstractTimestampingProcedure.getTimestamps(set, TimestampType.ATTACHMENT_TIMESTAMP_LOWERBOUND, tangle);
        Assert.assertEquals(3, lowerbounds.size());
        Assert.assertEquals(true, lowerbounds.contains(0l));
        Assert.assertEquals(true, lowerbounds.contains(1l));
        Assert.assertEquals(true, lowerbounds.contains(3l));

        List<Long> normal = AbstractTimestampingProcedure.getTimestamps(set, TimestampType.ATTACHMENT_TIMESTAMP, tangle);
        Assert.assertEquals(3, normal.size());
        Assert.assertEquals(true, normal.contains(timestamps.get(0)));
        Assert.assertEquals(true, normal.contains(timestamps.get(1)));

        List<Long> upperbounds = AbstractTimestampingProcedure.getTimestamps(set, TimestampType.ATTACHMENT_TIMESTAMP_UPPERBOUND, tangle);
        Assert.assertEquals(3, upperbounds.size());
        Assert.assertEquals(true, upperbounds.contains(0l));
        Assert.assertEquals(true, upperbounds.contains(3l));
        Assert.assertEquals(true, upperbounds.contains(5l));

    }

}
