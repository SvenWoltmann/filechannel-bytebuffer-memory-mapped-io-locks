package eu.happycoders.filechannel.demo;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.util.concurrent.ThreadLocalRandom;

public class ReadDemo2 {

    public static void main(String[] args) throws IOException {
        Path path = Path.of("read-demo.bin"); // Run CreateReadDemo to create this file
        try (FileChannel channel = FileChannel.open(path, StandardOpenOption.READ)) {
            ByteBuffer buffer = ByteBuffer.allocate(1024);

            int bytesRead;
            while ((bytesRead = channel.read(buffer)) != -1) {
                System.out.printf("bytes read from file: %d%n", bytesRead);

                long sum = 0;

                buffer.flip();
                int numBytesToRead = ThreadLocalRandom.current().nextInt(buffer.remaining());
                for (int i = 0; i < numBytesToRead; i++) {
                    sum += buffer.get();
                }

                System.out.printf("  bytes read from buffer: %d, sum of bytes: %d%n",
                        numBytesToRead, sum);
                buffer.compact();
            }
        }
    }

}
