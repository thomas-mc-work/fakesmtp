package org.tmcw.fakesmtp;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

public class FolderHelper {

    public static List<Path> listFiles(final Path path) throws IOException {
        final List<Path> fileList = new LinkedList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            for (Path entry : stream) {
                if (Files.isRegularFile(entry)) {
                    fileList.add(entry);
                }
            }
        }

        return fileList;
    }
}
