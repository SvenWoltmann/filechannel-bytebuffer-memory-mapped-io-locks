package eu.happycoders.filechannel.demo;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;

public class ReadDemo1 {

    public static void main(String[] args) throws IOException {
        Path path = Path.of("read-demo.bin"); // Run CreateReadDemo to create this file
        try (FileChannel channel = FileChannel.open(path, StandardOpenOption.READ)) {
            ByteBuffer buffer = ByteBuffer.allocate(1024);

            int bytesRead;
            while ((bytesRead = channel.read(buffer)) != -1) {
                System.out.printf("bytes read from file: %d%n", bytesRead);
                if (bytesRead > 0) {
                    System.out.printf("  first byte: %d, last byte: %d%n",
                            buffer.get(0), buffer.get(bytesRead - 1));
                }
                buffer.rewind();
            }
        }
    }

}
