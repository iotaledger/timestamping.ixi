package org.iota.ict.ixi.util;

import org.iota.ict.Ict;
import org.iota.ict.model.Transaction;
import org.iota.ict.utils.properties.EditableProperties;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import java.util.Map;

public abstract class AbstractModuleTestTemplate {

    protected static Ict ict;

    protected Map<String, Transaction> tangle;
    protected String genesis;

    @Before
    public void initializeTangle() {
        tangle = TangleGenerator.createTangle(50);
        genesis = tangle.keySet().iterator().next();
    }

    @BeforeClass
    public static void setUp() {
        EditableProperties properties = new EditableProperties().host("localhost").port(1337).minForwardDelay(0).maxForwardDelay(10).guiEnabled(false);
        ict = new Ict(properties.toFinal());
    }

    @AfterClass
    public static void tearDown() {
        ict.terminate();
    }

}
