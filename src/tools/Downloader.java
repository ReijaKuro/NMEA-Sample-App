package tools;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

public class Downloader {


    public static void download(String address) {
        // greift auf nmea zu und legt den speicher ort fest
        try (BufferedInputStream inputStream = new BufferedInputStream(new URL(address).openStream());
             FileOutputStream fileOS = new FileOutputStream("D:/Uni/sample.nmea")) {
            byte data[] = new byte[1024];
            int byteContent;
            while ((byteContent = inputStream.read(data, 0, 1024)) != -1) {
                fileOS.write(data, 0, byteContent);
            }
        } catch (IOException e) {
        }
    }

}
