package eu.happycoders.filechannel.performance.util;

import java.util.*;

public class TestStats {

    private final String name;
    private final long numBytes;
    private final List<Long> times = new ArrayList<>();

    public TestStats(String name, long numBytes) {
        this.name = name;
        this.numBytes = numBytes;
    }

   public void addTime(long millis) {
        times.add(millis);
    }

    @Override
    public String toString() {
        int size = times.size();
        if (size == 0) return "not measured";

        double avg = getAverage(times);
        double med = getMedian();

        double megaBytes = numBytes >> 20;

        return String.format("%-56s : avg = %7.2f ms (= %6.1f MB/s); med = %7.2f ms (= %6.1f MB/s)",
                name,
                avg / 1_000_000.0,
                megaBytes / avg * 1_000_000_000.0,
                med / 1_000_000.0,
                megaBytes / med * 1_000_000_000.0);
    }

    double getAverage(List<Long> times) {
        long sum = 0;
        for (Long time : times)
            sum += time;
        return (double) sum / times.size();
    }

    double getMedian() {
        Collections.sort(times);
        int size = times.size();
        int middle = size / 2;
        return size % 2 == 1
                ? times.get(middle)
                : (times.get(middle - 1) + times.get(middle)) / 2.0;
    }

}

