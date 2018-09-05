package ru.innopolis.stc12.sourceparser;

import com.sun.xml.internal.ws.util.ByteArrayBuffer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            long start = System.currentTimeMillis();
            FileInputStream fileInputStream = new FileInputStream("D://Projects//java/file.txt");
            FileChannel fileChannel = fileInputStream.getChannel();
            MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());

            //Scanner scanner = new Scanner(fileInputStream);

            List<String> list = new ArrayList<>();
            ByteArrayBuffer buffer = new ByteArrayBuffer();
            while (mappedByteBuffer.hasRemaining()) {
                byte symbol = mappedByteBuffer.get();
                buffer.write(symbol);
                if (symbol == '.') {
                    list.add(buffer.toString());
                    buffer.reset();
                }
            }
            System.out.println(System.currentTimeMillis() - start);
            int counter = 0;
            for (String text : list) {
                System.out.println(text);
                counter++;
                if (counter > 100) break;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
