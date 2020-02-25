package eu.happycoders.filechannel.performance;

import eu.happycoders.filechannel.performance.tests.AbstractTest;

import java.io.IOException;
import java.nio.file.*;

public class TestMain {

    private static final long FILE_SIZE_MIN = 1 << 20; // 1 MB
    private static final long FILE_SIZE_MAX = 1 << 30; // 1 GB

    private static final int BUFFER_SIZE = 1 << 12; // 4 KB

    private static final int REPETITIONS_PER_TEST = 32;

    // Don't write more than 4 GB per test (so if the file size is 1 GB, repeat only 4 times instead of 32 times)
    private static final long MAX_BYTES_WRITTEN = 4 * (1L << 30);

    private static final AbstractTest.WriteMode WRITE_MODE = AbstractTest.WriteMode.SEQUENTIAL;

    private static final boolean FLUSH = false;

    private static final Path TMP_DIR = Path.of(System.getProperty("java.io.tmpdir"));
    private static final Path WORK_DIR = TMP_DIR.resolve("nio");

    static {
        try {
            if (!Files.exists(WORK_DIR))
                Files.createDirectory(WORK_DIR);
        } catch (IOException e) {
            throw new Error("Cannot create work dir", e);
        }
    }

    public static void main(String[] args) throws IOException, ReflectiveOperationException {
        for (long fileSize = FILE_SIZE_MIN; fileSize <= FILE_SIZE_MAX; fileSize <<= 1) {
            var testParams = new TestParams(WORK_DIR, fileSize, BUFFER_SIZE, WRITE_MODE, FLUSH);
            int repetitions = (int) Math.min(REPETITIONS_PER_TEST, MAX_BYTES_WRITTEN / fileSize);
            new TestGroup(testParams, repetitions).run();
        }
    }

}
