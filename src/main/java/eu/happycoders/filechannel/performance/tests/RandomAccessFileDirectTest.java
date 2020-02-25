package eu.happycoders.filechannel.performance.tests;

import eu.happycoders.filechannel.performance.TestParams;

import java.nio.ByteBuffer;

public class RandomAccessFileDirectTest extends AbstractRandomAccessFileTest {

    public RandomAccessFileDirectTest(TestParams testParams) {
        super(testParams, "RandomAccessFile with direct byte buffer", true);
    }


}
