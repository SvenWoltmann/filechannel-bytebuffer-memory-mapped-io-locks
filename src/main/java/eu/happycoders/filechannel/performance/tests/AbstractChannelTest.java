package eu.happycoders.filechannel.performance.tests;

import eu.happycoders.filechannel.performance.TestParams;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public abstract class AbstractChannelTest extends AbstractTest {

    final ByteBuffer buffer;

    AbstractChannelTest(TestParams testParams, String displayName, boolean directByteBuffer) {
        super(testParams, displayName);
        buffer = createBuffer(directByteBuffer);
    }

    private ByteBuffer createBuffer(boolean directByteBuffer){
        if (directByteBuffer) {
            var buffer = ByteBuffer.allocateDirect(testParams.bufferSize());
            buffer.put(bytes);
            return buffer;
        } else {
            return ByteBuffer.wrap(bytes);
        }
    }

    void writeToChannel(ByteBuffer buffer, FileChannel channel) throws IOException {
        int numBuffersToWrite = (int) (testParams.fileSize() / testParams.bufferSize());
        for (int i = 0; i < numBuffersToWrite; i++) {
            if (testParams.shouldSetRandomPosition())
                channel.position(testParams.randomPosition());

            while (buffer.hasRemaining())
                channel.write(buffer);

            buffer.flip();
        }

        if (testParams.isFlush()) channel.force(false);
    }


}
