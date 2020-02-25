package eu.happycoders.filechannel.performance.tests;

import eu.happycoders.filechannel.performance.TestParams;
import eu.happycoders.filechannel.performance.util.MemoryMappedBufferHelper;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

public class MappedByteBufferTest extends AbstractTest {

    public MappedByteBufferTest(TestParams testParams) {
        super(testParams, "MappedByteArray / memory-mapped file");
    }

    @Override
    protected boolean testShouldBeExecuted() {
        // Memory-mapped file allowed only up to Integer.MAX_VALUE
        return testParams.fileSize() <= Integer.MAX_VALUE;
    }

    @Override
    void runTestInternal(Path path) throws IOException {
        try (var file = new RandomAccessFile(path.toFile(), "rw");
             var channel = file.getChannel()) {

            var mappedByteBuffer = channel.map(
                    FileChannel.MapMode.READ_WRITE, 0, testParams.fileSize());

            int numBuffersToWrite = (int) (testParams.fileSize() / testParams.bufferSize());
            for (int i = 0; i < numBuffersToWrite; i++) {
                if (testParams.shouldSetRandomPosition())
                    mappedByteBuffer.position((int) testParams.randomPosition());

                mappedByteBuffer.put(bytes);
            }

            if (testParams.isFlush()) channel.force(false);

            // MappedByteBuffer is not closeable and we need some reflection magic to clean it
            try {
                MemoryMappedBufferHelper.unsafeUnmap(mappedByteBuffer);
            } catch (ReflectiveOperationException e) {
                throw new Error(e);
            }
        }
    }

}
