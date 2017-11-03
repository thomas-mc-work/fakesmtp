package org.tmcw.fakesmtp.spi;

import java.io.IOException;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.slf4j.LoggerFactory;

/**
 * Execute a custom CLI command provided by the user.
 */
public class CommandLineHandler implements MailHandler {

    private final String cliCommand;

    public CommandLineHandler(final String cliCommand) {
        this.cliCommand = cliCommand;
    }

    @Override
    public void handle(
            final String from, final String recipient, final String rawMessage, final MimeMessage mimeMessage)
            throws MailHandlerException {

        LoggerFactory.getLogger(getClass()).debug("executing custom cli command: {}", cliCommand);

        try {
            final ProcessBuilder pb = new ProcessBuilder(cliCommand);
            pb.environment().put("MAIL_FROM", from);
            pb.environment().put("MAIL_TO", recipient);
            pb.environment().put("MAIL_SUBJECT", mimeMessage.getSubject());
            pb.environment().put("MAIL_BODY", mimeMessage.getContent().toString());

            pb.start();
        } catch (IOException | MessagingException ex) {
            throw new MailHandlerException(ex);
        }
    }

}
