package eu.happycoders.filechannel.performance;

import eu.happycoders.filechannel.performance.tests.AbstractTest;
import eu.happycoders.filechannel.performance.tests.AbstractTest.WriteMode;
import eu.happycoders.filechannel.performance.tests.FileChannelDirectTest;
import eu.happycoders.filechannel.performance.tests.FileChannelHeapTest;
import eu.happycoders.filechannel.performance.tests.FileChannelViaOutputStreamDirectTest;
import eu.happycoders.filechannel.performance.tests.FileChannelViaOutputStreamHeapTest;
import eu.happycoders.filechannel.performance.tests.FileOutputStreamTest;
import eu.happycoders.filechannel.performance.tests.MappedByteBufferTest;
import eu.happycoders.filechannel.performance.tests.RandomAccessFileDirectTest;
import eu.happycoders.filechannel.performance.tests.RandomAccessFileHeapTest;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.reflect.Field;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * The write-performance grid of the FileChannel article: every write method of this repository,
 * for one write mode, a range of buffer sizes and a range of file sizes.
 *
 * <p>TestMain runs one buffer size and one write mode per start, set as constants; the 2020
 * results came from editing them between runs. This ladder runs the same tests, in the same order
 * and with the same repetition rule as TestMain and TestGroup - 32 repetitions, at most a given
 * amount of data per test and file size - over the whole grid in one JVM, and keeps every single
 * time. The times are those AbstractTest measures itself (only runTestInternal(), not the deletion
 * of the file before and after); they are read from its TestStats by reflection, because TestStats
 * only prints average and median.
 *
 * <p>The results of 2026-10-18 in results/2026-10-18 come from this class.
 *
 * <p>Two deviations from 2020:
 *
 * <ul>
 *   <li>at most 1 GB per test and file size instead of 4 GB - every written byte reaches the SSD,
 *       and 4 GB would have meant about 2.4 TB for the grid; the 1 GB file is written once per
 *       test instead of four times;
 *   <li>a ninth test, MemorySegmentTest, maps the file with FileChannel.map(..., Arena) and copies
 *       the buffer in with MemorySegment.copy() - the way the article recommends since Java 22. It
 *       runs right after MappedByteBufferTest and is built like it.
 * </ul>
 *
 * <p>Usage: java -cp <classes> eu.happycoders.filechannel.performance.FileChannelWriteLadder
 * <workDir> SEQUENTIAL|RANDOM <minBufferKB> <maxBufferKB> <minFileMB> <maxFileMB> [repetitions]
 * [maxMbPerTest] [warmups]
 *
 * <p>Requires Java 22 or later (FileChannel.map() with an Arena).
 *
 * <p>warmups (default 0) runs the whole first group - smallest buffer, smallest file - that many
 * times before the measurement and throws the times away. The grid needs none: it runs all buffer
 * sizes in one JVM. A cross-check that starts a fresh JVM per buffer size does: in the 1 MB column
 * of the cross-check of 2026-10-18 the JIT had not finished compiling the write loops, and the
 * memory-mapped tests came out 20 to 27 % slower than in the warm grid.
 *
 * <p>Output on stdout, one line per measurement: mode, file MB, buffer KB, test, repetition, ms
 * (tab-separated). Progress goes to stderr.
 */
public class FileChannelWriteLadder {

  public static void main(String[] args) throws Exception {
    Path workDir = Path.of(args[0]);
    WriteMode mode = WriteMode.valueOf(args[1]);
    int minBufferKb = Integer.parseInt(args[2]);
    int maxBufferKb = Integer.parseInt(args[3]);
    long minFileMb = Long.parseLong(args[4]);
    long maxFileMb = Long.parseLong(args[5]);
    int repetitionsPerTest = args.length > 6 ? Integer.parseInt(args[6]) : 32;
    long maxBytesWritten = (args.length > 7 ? Long.parseLong(args[7]) : 1024) << 20;
    int warmups = args.length > 8 ? Integer.parseInt(args[8]) : 0;
    Files.createDirectories(workDir);

    if (warmups > 0) {
      var params = new TestParams(workDir, minFileMb << 20, minBufferKb << 10, mode, false);
      var memorySegmentTest = new MemorySegmentTest(params);
      List<AbstractTest> tests = tests(params);
      for (int i = 0; i < warmups; i++) {
        for (AbstractTest test : tests) {
          test.runTest(i);
          if (test instanceof MappedByteBufferTest) memorySegmentTest.runTest(i);
        }
      }
      System.err.printf(Locale.ROOT, "%d warmup repetitions done%n", warmups);
    }

    Field statsField = AbstractTest.class.getDeclaredField("stats");
    statsField.setAccessible(true);

    System.out.println("# mode\tfile_mb\tbuffer_kb\ttest\trepetition\tms");
    for (int bufferKb = minBufferKb; bufferKb <= maxBufferKb; bufferKb <<= 1) {
      for (long fileMb = minFileMb; fileMb <= maxFileMb; fileMb <<= 1) {
        long fileSize = fileMb << 20;
        long start = System.nanoTime();
        var params = new TestParams(workDir, fileSize, bufferKb << 10, mode, false);
        int repetitions = (int) Math.max(1, Math.min(repetitionsPerTest, maxBytesWritten / fileSize));

        // TestGroup.run(), without the printing, with the MemorySegment test after the
        // MappedByteBuffer test
        var memorySegmentTest = new MemorySegmentTest(params);
        List<AbstractTest> tests = tests(params);
        for (int i = 0; i < repetitions; i++) {
          for (AbstractTest test : tests) {
            test.runTest(i);
            if (test instanceof MappedByteBufferTest) memorySegmentTest.runTest(i);
          }
        }

        print(mode, fileMb, bufferKb, "MemorySegmentTest", memorySegmentTest.times);
        for (AbstractTest test : tests) {
          print(mode, fileMb, bufferKb, test.getClass().getSimpleName(), times(statsField.get(test)));
        }
        System.out.flush();
        System.err.printf(Locale.ROOT, "%s buffer %d KB, file %d MB: %d repetitions, %.1f s%n",
            mode, bufferKb, fileMb, repetitions, (System.nanoTime() - start) / 1e9);
      }
    }
  }

  private static List<AbstractTest> tests(TestParams params) {
    return List.of(
        new MappedByteBufferTest(params),
        new FileChannelDirectTest(params),
        new FileChannelHeapTest(params),
        new RandomAccessFileDirectTest(params),
        new RandomAccessFileHeapTest(params),
        new FileChannelViaOutputStreamDirectTest(params),
        new FileChannelViaOutputStreamHeapTest(params),
        new FileOutputStreamTest(params));
  }

  private static void print(WriteMode mode, long fileMb, int bufferKb, String test, List<Long> times) {
    for (int i = 0; i < times.size(); i++) {
      System.out.printf(Locale.ROOT, "%s\t%d\t%d\t%s\t%d\t%.3f%n", mode, fileMb, bufferKb, test,
          i + 1, times.get(i) / 1_000_000.0);
    }
  }

  @SuppressWarnings("unchecked")
  private static List<Long> times(Object testStats) throws ReflectiveOperationException {
    Field timesField = testStats.getClass().getDeclaredField("times");
    timesField.setAccessible(true);
    return (List<Long>) timesField.get(testStats);
  }

  /**
   * The counterpart of the repository's MappedByteBufferTest with the FFM API: the same file
   * opening, the same buffer of 'A's, the same write loop; the mapping lives in a confined arena,
   * whose close() unmaps the file - in place of MappedByteBufferTest's reflective call of
   * sun.misc.Unsafe.invokeCleaner(). Timed like AbstractTest.runTest(): without deleting the file
   * before and after.
   */
  private static final class MemorySegmentTest {
    private final TestParams params;
    private final MemorySegment source;
    final List<Long> times = new ArrayList<>();

    MemorySegmentTest(TestParams params) {
      this.params = params;
      byte[] bytes = new byte[params.bufferSize()];
      Arrays.fill(bytes, (byte) 65);
      this.source = MemorySegment.ofArray(bytes);
    }

    void runTest(int testNo) throws IOException {
      Path path = params.workDir().resolve("data-MemorySegmentTest-" + testNo + ".txt");
      Files.deleteIfExists(path);
      long time = System.nanoTime();
      runTestInternal(path);
      times.add(System.nanoTime() - time);
      Files.deleteIfExists(path);
    }

    private void runTestInternal(Path path) throws IOException {
      try (var file = new RandomAccessFile(path.toFile(), "rw");
          var channel = file.getChannel();
          var arena = Arena.ofConfined()) {
        MemorySegment segment =
            channel.map(FileChannel.MapMode.READ_WRITE, 0, params.fileSize(), arena);
        long numBuffersToWrite = params.fileSize() / params.bufferSize();
        long offset = 0;
        for (long i = 0; i < numBuffersToWrite; i++) {
          if (params.shouldSetRandomPosition()) offset = params.randomPosition();
          MemorySegment.copy(source, 0, segment, offset, params.bufferSize());
          offset += params.bufferSize();
        }
        if (params.isFlush()) segment.force();
      }
    }
  }
}
