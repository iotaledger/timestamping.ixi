package org.iota.ict.ixi.util;

import org.iota.ict.Ict;
import org.iota.ict.ixi.DefaultTimestampingModule;
import org.iota.ict.ixi.util.TangleGenerator;
import org.iota.ict.model.Transaction;
import org.iota.ict.utils.properties.EditableProperties;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import java.util.Map;

public class ModuleTestTemplate {

    private static Ict ict;

    protected Map<String, Transaction> tangle;
    protected static DefaultTimestampingModule timestampingModule;

    @Before
    public void initializeTangle() {
        tangle = TangleGenerator.createTangle(15);
    }

    @BeforeClass
    public static void setUp() {
        EditableProperties properties = new EditableProperties().host("localhost").port(1337).minForwardDelay(0).maxForwardDelay(10).guiEnabled(false);
        ict = new Ict(properties.toFinal());
        timestampingModule = new DefaultTimestampingModule(ict);
    }

    @AfterClass
    public static void tearDown() {
        ict.terminate();
    }

}
