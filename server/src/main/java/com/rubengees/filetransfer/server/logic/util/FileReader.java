package com.rubengees.filetransfer.server.logic.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

/**
 * Todo: Describe class
 *
 * @author Ruben Gees
 */
public class FileReader {

    public static String readFile(Path path) throws IOException {
        return Files.lines(path).collect(Collectors.joining());
    }

}
