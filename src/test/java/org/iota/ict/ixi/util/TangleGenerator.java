package org.iota.ict.ixi.util;

import org.iota.ict.ixi.model.Tangle;
import org.iota.ict.model.Transaction;
import org.iota.ict.model.TransactionBuilder;

import java.util.*;

public class TangleGenerator {

    public static Tangle createTangle(int size) {

        Tangle tangle = new Tangle();

        Transaction genesis = new TransactionBuilder().build();
        tangle.add(genesis);

        continueTangle(tangle, size);

        return tangle;

    }

    public static void continueTangle(Tangle tangle, int size) {

        for(int i = 0; i < size; i++) {

            TransactionBuilder builder = new TransactionBuilder();
            String[] tips = getTransactionsToApprove(tangle);
            builder.trunkHash = tips[0];
            builder.branchHash = tips[1];
            builder.attachmentTimestampLowerBound = System.currentTimeMillis();
            builder.attachmentTimestamp = System.currentTimeMillis();
            builder.attachmentTimestampUpperBound = System.currentTimeMillis();
            Transaction transaction = builder.build();
            tangle.add(transaction);

        }

    }

    public static String[] getTransactionsToApprove(Tangle tangle) {

        Set<String> candidates = getAttachmentCandidates(tangle);

        String[] ret = new String[2];

        List<String> list = new ArrayList(candidates);
        Collections.shuffle(list);

        String tip1 = list.get(0);
        list.remove(tip1);

        String tip2;
        if(list.size() == 0)
            tip2 = tip1;
        else
            tip2 = list.get(0);

        ret[0] = tip1;
        ret[1] = tip2;

        return ret;

    }

    public static Set<String> getAttachmentCandidates(Tangle tangle) {

        Set<String> tips = tangle.getTips();

        Set<String> candidates = new HashSet<>();
        for(String hash: tips) {
            Transaction t = tangle.getTransactions().get(hash);
            candidates.add(t.hash);
            candidates.add(t.trunkHash());
            candidates.add(t.branchHash());
        }

        candidates.remove("999999999999999999999999999999999999999999999999999999999999999999999999999999999");

        return candidates;
    }

}
