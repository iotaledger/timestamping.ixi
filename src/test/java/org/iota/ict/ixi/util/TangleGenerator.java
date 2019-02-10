package org.iota.ict.ixi.util;

import org.iota.ict.model.Transaction;
import org.iota.ict.model.TransactionBuilder;

import java.util.*;

public class TangleGenerator {

    public static Map<String, Transaction> generate(int size) {

        LinkedHashMap<String, Transaction> tangle = new LinkedHashMap<>();

        Transaction genesis = new TransactionBuilder().build();
        tangle.put(genesis.hash, genesis);

        for(int i = 0; i < size; i++) {

            TransactionBuilder builder = new TransactionBuilder();
            String[] tips = getTransactionsToApprove(tangle);
            builder.trunkHash = tips[0];
            builder.branchHash = tips[1];
            Transaction transaction = builder.build();
            tangle.put(transaction.hash, transaction);

        }

        return tangle;

    }

    public static String[] getTransactionsToApprove(Map<String, Transaction> tangle) {

        List<Transaction> tips = findTips(tangle);

        Set<String> candidates = new HashSet<>();
        for(Transaction t: tips) {
            candidates.add(t.hash);
            candidates.add(t.trunkHash());
            candidates.add(t.branchHash());
        }

        candidates.remove("999999999999999999999999999999999999999999999999999999999999999999999999999999999");

        String[] ret = new String[2];

        List<String> list = new ArrayList(candidates);
        Collections.shuffle(list) ;

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

    public static List<Transaction> findTips(Map<String, Transaction> tangle) {

        List<Transaction> tips = new ArrayList<>();

        for(Transaction x: tangle.values()) {

            boolean isTip = true;
            for(Transaction y: tangle.values()) {

                if(x == y)
                    continue;

                if(y.trunkHash().equals(x.hash) || y.branchHash().equals(x.hash)) {
                    isTip = false;
                    break;
                }

            }

            if(isTip)
                tips.add(x);

        }

        return tips;

    }

}
