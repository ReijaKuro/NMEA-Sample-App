package gnss;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class SimpleBufferedReader extends FileInputStream {

    public SimpleBufferedReader(File file) throws FileNotFoundException {
        super(file);
    }

    public String readLine(){
        int c;
        String line = "";
        try {
            while ((c = this.read()) != -1) {
                char unsignedByte = (char) c;
                if (unsignedByte == 10) { // break line \n
                    return line;
                }
                line += Character.toString(c);
            }
        } catch (IOException e) {
            return null;
        }
        return null;
    }
}
