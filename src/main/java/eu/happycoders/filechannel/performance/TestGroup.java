package eu.happycoders.filechannel.performance;

import eu.happycoders.filechannel.performance.tests.*;

import java.io.IOException;
import java.util.List;

public class TestGroup {

    private final TestParams testParams;
    private final int repetitions;

    TestGroup(TestParams testParams, int repetitions) {
        this.testParams = testParams;
        this.repetitions = repetitions;
    }

    void run() throws IOException, ReflectiveOperationException {
        System.out.printf("Results for %d tests à %d MB with bufferSize = %d KB, writeMode = %s, flush = %b:%n",
                repetitions, testParams.fileSize() >> 20, testParams.bufferSize() >> 10, testParams.writeMode(), testParams.isFlush());

        var tests = List.of(
                new MappedByteBufferTest(testParams),
                new FileChannelDirectTest(testParams),
                new FileChannelHeapTest(testParams),
                new RandomAccessFileDirectTest(testParams),
                new RandomAccessFileHeapTest(testParams),
                new FileChannelViaOutputStreamDirectTest(testParams),
                new FileChannelViaOutputStreamHeapTest(testParams),
                new FileOutputStreamTest(testParams));

        for (int i = 0; i < repetitions; i++) {
            for (var test : tests) {
                test.runTest(i);
            }
        }

        tests.forEach(AbstractTest::printStats);
        System.out.println();
    }

}
