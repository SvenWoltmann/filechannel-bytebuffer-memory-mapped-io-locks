package eu.happycoders.filechannel.demo;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.*;

public class SizeDemo2 {

    public static void main(String[] args) throws IOException {
        Path path = Path.of("1g-demo.bin");
        try (FileChannel channel = FileChannel.open(path, StandardOpenOption.WRITE)) {
            channel.truncate(1 << 10);
        }
    }

}
