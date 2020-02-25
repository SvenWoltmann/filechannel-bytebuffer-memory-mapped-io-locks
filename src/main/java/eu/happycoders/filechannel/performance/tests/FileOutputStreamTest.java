package eu.happycoders.filechannel.performance.tests;

import eu.happycoders.filechannel.performance.TestParams;

import java.io.*;
import java.nio.file.Path;

public class FileOutputStreamTest extends AbstractTest {

    public FileOutputStreamTest(TestParams testParams) {
        super(testParams, "FileOutputStream with BufferedOutputStream");
    }

    @Override
    protected boolean testShouldBeExecuted() {
        // FileOutputStream only suitable for sequential write test
        return testParams.writeMode() == WriteMode.SEQUENTIAL;
    }

    @Override
    void runTestInternal(Path path) throws IOException {
        if (testParams.writeMode() == WriteMode.RANDOM) {
            throw new IllegalStateException("FileOutputStream allows only sequential write");
        }

        try (var out = new FileOutputStream(path.toFile());
             var buf = new BufferedOutputStream(out)) {
            int numBuffersToWrite = (int) (testParams.fileSize() / testParams.bufferSize());

            for (int i = 0; i < numBuffersToWrite; i++)
                buf.write(bytes);

            if (testParams.isFlush()) {
                buf.flush();
                out.flush();
                out.getFD().sync();
            }
        }
    }

}
