package org.iota.ict.ixi;

import org.iota.ict.ixi.model.Interval;
import org.iota.ict.ixi.util.AbstractModuleTestTemplate;
import org.iota.ict.ixi.util.DefaultModuleTestTemplate;
import org.iota.ict.ixi.util.QuantileTimestampingTestTemplate;
import org.iota.ict.ixi.util.TangleGenerator;
import org.iota.ict.model.Transaction;
import org.iota.ict.model.TransactionBuilder;
import org.junit.Test;

import java.util.List;

public class GetTimestampIntervalTest extends QuantileTimestampingTestTemplate {

    @Test
    public void getTimestampIntervalTest() {

        tangle = TangleGenerator.createTangle(50);

        List<Transaction> tips = TangleGenerator.findTips(tangle);

        // t1
        TransactionBuilder tb1 = new TransactionBuilder();
        tb1.trunkHash = tips.get(0).hash;
        tb1.branchHash = tips.get(1).hash;
        Transaction t1 = tb1.build();
        tangle.put(t1.hash, t1);

        // t2
        TransactionBuilder tb2 = new TransactionBuilder();
        tb2.trunkHash = t1.hash;
        tb2.branchHash = t1.hash;
        Transaction t2 = tb2.build();
        tangle.put(t2.hash, t2);

        // t3
        TransactionBuilder tb3 = new TransactionBuilder();
        tb3.trunkHash = t2.hash;
        tb3.branchHash = t2.hash;
        Transaction t3 = tb3.build();
        tangle.put(t3.hash, t3);

        TangleGenerator.continueTangle(tangle,100);

        Interval time1 = quantileTimestampingModule.getTimestampInterval(quantileTimestampingModule.beginTimestampCalculation(t1.hash, 0.3), tangle);
        Interval time2 = quantileTimestampingModule.getTimestampInterval(quantileTimestampingModule.beginTimestampCalculation(t2.hash, 0.3), tangle);
        Interval time3 = quantileTimestampingModule.getTimestampInterval(quantileTimestampingModule.beginTimestampCalculation(t3.hash, 0.3), tangle);

    }

}
