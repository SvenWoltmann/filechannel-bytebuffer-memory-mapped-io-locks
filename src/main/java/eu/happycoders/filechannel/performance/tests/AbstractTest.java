package eu.happycoders.filechannel.performance.tests;

import eu.happycoders.filechannel.performance.TestParams;
import eu.happycoders.filechannel.performance.util.TestStats;

import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;

public abstract class AbstractTest {

    public enum WriteMode {SEQUENTIAL, RANDOM}

    final TestParams testParams;
    final String displayName;
    final TestStats stats;
    final byte[] bytes;

    AbstractTest(TestParams testParams, String displayName) {
        this.testParams = testParams;
        this.displayName = displayName;
        this.stats = new TestStats(displayName, testParams.fileSize());
        this.bytes = createBytes();
    }

    public void runTest(int testNo) throws IOException {
        if (!testShouldBeExecuted())
            return;

        Path path = testParams.workDir().resolve("data-" + this.getClass().getSimpleName() + "-" + testNo + ".txt");
        Files.deleteIfExists(path);

        long time = System.nanoTime();

        runTestInternal(path);

        time = System.nanoTime() - time;
        stats.addTime(time);

        Files.deleteIfExists(path);
    }

    boolean testShouldBeExecuted() {
        return true;
    }

    abstract void runTestInternal(Path path) throws IOException;

    private byte[] createBytes() {
        byte[] bytes = new byte[testParams.bufferSize()];
        Arrays.fill(bytes, (byte) 65);
        return bytes;
    }

    public void printStats() {
        System.out.println(stats);
    }

}
