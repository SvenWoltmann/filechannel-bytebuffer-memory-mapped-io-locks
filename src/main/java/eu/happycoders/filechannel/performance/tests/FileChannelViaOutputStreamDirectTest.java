package eu.happycoders.filechannel.performance.tests;

import eu.happycoders.filechannel.performance.TestParams;

public class FileChannelViaOutputStreamDirectTest extends AbstractFileChannelViaOutputStreamTest {

    public FileChannelViaOutputStreamDirectTest(TestParams testParams) {
        super(testParams, "FileChannel via FileOutputStream with direct byte buffer", true);
    }

}
