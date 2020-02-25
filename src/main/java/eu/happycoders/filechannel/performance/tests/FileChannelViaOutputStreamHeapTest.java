package eu.happycoders.filechannel.performance.tests;

import eu.happycoders.filechannel.performance.TestParams;

import java.nio.ByteBuffer;

public class FileChannelViaOutputStreamHeapTest extends AbstractFileChannelViaOutputStreamTest {

    public FileChannelViaOutputStreamHeapTest(TestParams testParams) {
        super(testParams, "FileChannel via FileOutputStream", false);
    }

    ByteBuffer createBuffer() {
        return ByteBuffer.wrap(bytes);
    }

}
