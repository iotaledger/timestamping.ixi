package org.iota.ict.ixi.util;

import org.iota.ict.ixi.AbstractTimestampingModule;
import org.iota.ict.ixi.Ixi;
import org.iota.ict.ixi.model.Interval;
import org.iota.ict.model.Transaction;

import java.util.Map;

public class DefaultModuleTestTemplate extends AbstractModuleTestTemplate {

    protected DefaultTimestampingModule timestampingModule = new DefaultTimestampingModule(ict);

    public class DefaultTimestampingModule extends AbstractTimestampingModule {

        public DefaultTimestampingModule(Ixi ixi) {
            super(ixi);
        }

        @Override
        public String beginTimestampCalculation(String txToInspect, Object... args) {
            return null;
        }

        @Override
        public Interval getTimestampInterval(String hash, Map<String, Transaction> tangle) {
            return null;
        }

        @Override
        public double getTimestampConfidence(String identifier) {
            return 0;
        }

    }

}
