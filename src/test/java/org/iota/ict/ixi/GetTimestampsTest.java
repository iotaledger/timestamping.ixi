package org.iota.ict.ixi;

import org.iota.ict.ixi.model.TimestampType;
import org.iota.ict.ixi.util.ModuleTestTemplate;
import org.iota.ict.model.Transaction;
import org.iota.ict.model.TransactionBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class GetTimestampsTest extends ModuleTestTemplate {

    @Test
    public void testGetTimestamps() {

        List<Long> timestamps = new ArrayList<>();
        tangle = new HashMap<>();

        // genesis
        Transaction genesis = new TransactionBuilder().build();
        tangle.put(genesis.hash, genesis);
        timestamps.add(genesis.attachmentTimestamp);

        // t1
        TransactionBuilder tb1 = new TransactionBuilder();
        tb1.trunkHash = genesis.hash;
        tb1.branchHash = genesis.hash;
        tb1.attachmentTimestampLowerBound = 1;
        tb1.attachmentTimestampUpperBound = 3;
        Transaction t1 = tb1.build();
        timestamps.add(t1.attachmentTimestamp);
        tangle.put(t1.hash, t1);

        // t2
        TransactionBuilder tb2 = new TransactionBuilder();
        tb2.trunkHash = genesis.hash;
        tb2.branchHash = t1.hash;
        tb2.attachmentTimestampLowerBound = 3;
        tb2.attachmentTimestampUpperBound = 5;
        Transaction t2 = tb2.build();
        timestamps.add(t2.attachmentTimestamp);
        tangle.put(t2.hash, t2);

        // t3
        TransactionBuilder tb3 = new TransactionBuilder();
        tb3.trunkHash = t1.hash;
        tb3.branchHash = t2.hash;
        Transaction t3 = tb3.build();
        tangle.put(t3.hash, t3);

        Set<String> set = timestampingModule.getPast(t3.hash, tangle);

        List<Long> lowerbounds = timestampingModule.getTimestamps(set, TimestampType.ATTACHMENT_TIMESTAMP_LOWERBOUND, tangle);
        Assert.assertEquals(3, lowerbounds.size());
        Assert.assertEquals(true, lowerbounds.contains(0l));
        Assert.assertEquals(true, lowerbounds.contains(1l));
        Assert.assertEquals(true, lowerbounds.contains(3l));

        List<Long> normal = timestampingModule.getTimestamps(set, TimestampType.ATTACHMENT_TIMESTAMP, tangle);
        Assert.assertEquals(3, normal.size());
        Assert.assertEquals(true, normal.contains(timestamps.get(0)));
        Assert.assertEquals(true, normal.contains(timestamps.get(1)));

        List<Long> upperbounds = timestampingModule.getTimestamps(set, TimestampType.ATTACHMENT_TIMESTAMP_UPPERBOUND, tangle);
        Assert.assertEquals(3, upperbounds.size());
        Assert.assertEquals(true, upperbounds.contains(0l));
        Assert.assertEquals(true, upperbounds.contains(3l));
        Assert.assertEquals(true, upperbounds.contains(5l));

    }

}