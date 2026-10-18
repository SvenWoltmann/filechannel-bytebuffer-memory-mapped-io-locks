# Write-speed measurements of 2026-10-18

Every single time behind the performance charts of the article, measured with
[`FileChannelWriteLadder`](../../src/main/java/eu/happycoders/filechannel/performance/FileChannelWriteLadder.java)
on an Apple M5 Pro (arm64, 64 GB RAM, internal SSD, macOS, on AC power) with
OpenJDK 27 (build 27+35-2325).

## Files

Each run consists of three files:

- `<run>.tsv` – one line per measurement: mode, file size in MB, buffer size
  in KB, test, repetition, time in ms (tab-separated)
- `<run>.log` – progress output of the program
- `<run>-run-metadata.txt` – date, JDK, parameters and notes on the run

Two kinds of runs:

- **The grid** – `filechannel-sequential` (buffers 1 KB to 1 MB, files 1 MB to
  1 GB) and `filechannel-random` (files 1 MB to 128 MB), all buffer sizes in
  one JVM, 32 repetitions, at most 1 GB per test and file size.
- **Cross-checks** – the runs ending in `-reverse`: one or a few file sizes,
  buffer sizes from 1 MB down to 1 KB, a fresh JVM per buffer size and a pause
  before each, so the SSD can finish writing back what the previous one left.
  Their metadata files name pause, repetitions and warmups.

The cross-checks exist because in the grid, from the 256 KB buffers on – after
about 450 GB had been written – the single times at 16 and 128 MB fell into a
fast and a slow group: the SSD's write-back, not the buffer size. A cold JVM,
on the other hand, costs the memory-mapped tests in the 1 MB column up to 27 %.

## Which run each chart uses

The charts show the file size divided by the median time per buffer size and
write method.

| Chart | Run |
|---|---|
| Sequential, 1 MB | `filechannel-sequential.tsv` |
| Sequential, 8 MB and 16 MB | `filechannel-sequential-1-16mb-reverse.tsv` |
| Sequential, 128 MB | `filechannel-sequential-128mb-reverse.tsv` |
| Random access, 1 MB | `filechannel-random.tsv` |
| Random access, 8 MB | `filechannel-random-1-8mb-reverse.tsv` |
| Random access, 128 MB | `filechannel-random-128mb-reverse.tsv` |

## Running it yourself

```
javac -d classes $(find src/main/java/eu/happycoders/filechannel/performance -name '*.java')
java -Xmx4g -cp classes eu.happycoders.filechannel.performance.FileChannelWriteLadder \
    /tmp/filechannel SEQUENTIAL 1 1024 1 1024 > filechannel-sequential.tsv
```

`FileChannelWriteLadder` needs Java 22 or later. Every byte the tests write
reaches the SSD, although each file is deleted right after its test: the SSD
write counter rose by 707 GB for the sequential grid and by 356 GB for the
random-access grid.
