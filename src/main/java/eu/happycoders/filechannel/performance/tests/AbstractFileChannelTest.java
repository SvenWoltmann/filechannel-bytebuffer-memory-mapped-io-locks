package eu.happycoders.filechannel.performance.tests;

import eu.happycoders.filechannel.performance.TestParams;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.*;

public abstract class AbstractFileChannelTest extends AbstractChannelTest {

    public AbstractFileChannelTest(TestParams testParams, String displayName, boolean directByteBuffer) {
        super(testParams, displayName, directByteBuffer);
    }

    @Override
    void runTestInternal(Path path) throws IOException {
        try (var channel = FileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
            writeToChannel(buffer, channel);
        }
    }

}
