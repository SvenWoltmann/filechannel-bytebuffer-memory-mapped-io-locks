package eu.happycoders.filechannel.demo;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.file.*;
import java.util.concurrent.ThreadLocalRandom;

public class LockDemo {

    public static void main(String[] args) throws IOException {
        Path path = Path.of("lock-demo.bin");

        byte[] bytes = new byte[1000];
        ThreadLocalRandom.current().nextBytes(bytes);

        try (FileChannel channel = FileChannel.open(path,
                StandardOpenOption.CREATE, StandardOpenOption.WRITE);
             FileLock lock = channel.lock();) {
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            channel.write(buffer);
        }
    }

}
