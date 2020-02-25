package eu.happycoders.filechannel.performance;

import eu.happycoders.filechannel.performance.tests.AbstractTest;

import java.nio.file.Path;
import java.util.concurrent.ThreadLocalRandom;

public class TestParams {

    private final Path workDir;
    private final long fileSize;
    private final int bufferSize;
    private final AbstractTest.WriteMode writeMode;
    private final boolean flush;

    public TestParams(Path workDir, long fileSize, int bufferSize, AbstractTest.WriteMode writeMode, boolean flush) {
        this.workDir = workDir;
        this.fileSize = fileSize;
        this.bufferSize = bufferSize;
        this.writeMode = writeMode;
        this.flush = flush;
    }

    public Path workDir() {
        return workDir;
    }

    public long fileSize() {
        return fileSize;
    }

    public int bufferSize() {
        return bufferSize;
    }

    public AbstractTest.WriteMode writeMode() {
        return writeMode;
    }

    public boolean isFlush() {
        return flush;
    }

    public boolean shouldSetRandomPosition() {
        return writeMode == AbstractTest.WriteMode.RANDOM && fileSize != bufferSize;
    }

    public long randomPosition() {
        return ThreadLocalRandom.current().nextLong(fileSize - bufferSize);
    }

}
