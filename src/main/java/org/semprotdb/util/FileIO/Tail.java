package org.semprotdb.util.FileIO;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.input.ReversedLinesFileReader;

public class Tail {

    public static List<String> readLast(String file, int numLastLineToRead) throws IOException {
        List<String> result = new ArrayList<>();

        ReversedLinesFileReader reader = new ReversedLinesFileReader(new File(file), StandardCharsets.UTF_8);

        String line = "";
        while ((line = reader.readLine()) != null && result.size() < numLastLineToRead) {
            result.add(line);
        }
        return result;
    }
}
