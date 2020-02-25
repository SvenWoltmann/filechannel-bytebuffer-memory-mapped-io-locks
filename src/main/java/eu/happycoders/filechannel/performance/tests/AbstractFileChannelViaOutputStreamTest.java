package eu.happycoders.filechannel.performance.tests;

import eu.happycoders.filechannel.performance.TestParams;

import java.io.*;
import java.nio.file.Path;

public abstract class AbstractFileChannelViaOutputStreamTest extends AbstractChannelTest {

    public AbstractFileChannelViaOutputStreamTest(TestParams testParams, String displayName, boolean directByteBuffer) {
        super(testParams, displayName, directByteBuffer);
    }

    @Override
    void runTestInternal(Path path) throws IOException {
        try (var out = new FileOutputStream(path.toFile());
             var channel = out.getChannel()) {
            writeToChannel(buffer, channel);
        }
    }

}
