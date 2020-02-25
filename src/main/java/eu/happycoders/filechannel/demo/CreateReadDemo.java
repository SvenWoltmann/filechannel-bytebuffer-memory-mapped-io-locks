package eu.happycoders.filechannel.demo;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.util.concurrent.ThreadLocalRandom;

public class CreateReadDemo {

    public static void main(String[] args) throws IOException {
        Path path = Path.of("read-demo.bin");

        byte[] bytes = new byte[10000];
        ThreadLocalRandom.current().nextBytes(bytes);

        try (FileChannel channel = FileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            channel.write(buffer);
        }
    }

}
