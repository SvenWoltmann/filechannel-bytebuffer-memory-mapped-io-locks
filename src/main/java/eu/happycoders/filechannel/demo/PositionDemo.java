package eu.happycoders.filechannel.demo;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.util.concurrent.ThreadLocalRandom;

public class PositionDemo {

    public static void main(String[] args) throws IOException {
        Path path = Path.of("position-demo.bin");
        try (FileChannel channel = FileChannel.open(path,
                StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.READ)) {

            ByteBuffer buffer = ByteBuffer.allocate(1);

            // Write backwards
            for (int pos = 255; pos >= 0; pos--) {
                buffer.put((byte) pos);
                buffer.flip();
                channel.position(pos);
                while (buffer.remaining() > 0) {
                    channel.write(buffer);
                }
                buffer.compact();
            }

            // Read from random positions
            for (int i = 0; i < 10; i++) {
                long pos = ThreadLocalRandom.current().nextLong(channel.size());
                channel.position(pos);
                channel.read(buffer);
                buffer.flip();
                byte b = buffer.get();
                System.out.printf("Byte at position %d: %d%n", pos, b);
                buffer.compact();
            }
        }
    }

}
