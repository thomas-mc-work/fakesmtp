package org.tmcw.fakesmtp.spi;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.mail.internet.MimeMessage;

/**
 * Saves incoming email in file system.
 */
public final class SaveToFilesystem implements MailHandler {

    static final String FILE_EXTENSION = ".eml";

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss.SSS");

    private final Path outputPath;

    public SaveToFilesystem(final Path outputPath) {
        this.outputPath = outputPath;
    }

    @Override
    public void handle(
            final String from, final String recipient, final String rawMessage, final MimeMessage mimeMessage)
            throws MailHandlerException {

        synchronized (this) {
            saveEmailToFile(rawMessage);
        }
    }

    /**
     * Saves the content of the email passed in parameters in a file.
     *
     * @param rawContent the content of the email to be saved.
     * @return the path of the created file.
     */
    private void saveEmailToFile(final String rawContent) throws MailHandlerException {
        final Path filePath = createValidFilePath(sdf.format(new Date()));

        try {
            Files.write(filePath, rawContent.getBytes(StandardCharsets.UTF_8));
        } catch (IOException ex) {
            throw new MailHandlerException(ex);
        }
    }

    private Path createValidFilePath(final String fileNameBase) {
        int i = 0;
        Path filePath = null;

        do {
            final String counterPart = i > 0 ? "_" + i : "";
            final String fileName = fileNameBase + counterPart + FILE_EXTENSION;
            filePath = outputPath.resolve(fileName);
            i++;
        } while (Files.exists(filePath));

        return filePath;
    }
}
