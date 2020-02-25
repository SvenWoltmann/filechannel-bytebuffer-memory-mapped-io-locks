package eu.happycoders.filechannel.demo;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.util.concurrent.ThreadLocalRandom;

public class MappedByteBufferDemo {

    public static void main(String[] args) throws IOException {
        Path path = Path.of("mapped-byte-buffer-demo.bin");
        try (FileChannel channel = FileChannel.open(path,
                StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.READ)) {
            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, 256);

            // Write backwards
            for (int pos = 255; pos >= 0; pos--) {
                buffer.put(pos, (byte) pos);
            }

            // Read from random positions
            for (int i = 0; i < 10; i++) {
                int pos = ThreadLocalRandom.current().nextInt((int) channel.size());
                byte b = buffer.get(pos);
                System.out.printf("Byte at position %d: %d%n", pos, b);
            }
        }
        Files.delete(path);
    }

}
