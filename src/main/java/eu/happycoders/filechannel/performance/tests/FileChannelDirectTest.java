package eu.happycoders.filechannel.performance.tests;

import eu.happycoders.filechannel.performance.TestParams;

public class FileChannelDirectTest extends AbstractFileChannelTest {

    public FileChannelDirectTest(TestParams testParams) {
        super(testParams, "FileChannel with direct byte buffer", true);
    }


}
