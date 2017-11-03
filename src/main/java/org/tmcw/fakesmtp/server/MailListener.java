package org.tmcw.fakesmtp.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Properties;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import org.slf4j.LoggerFactory;
import org.subethamail.smtp.helper.SimpleMessageListener;
import org.tmcw.fakesmtp.spi.MailHandler;
import org.tmcw.fakesmtp.spi.MailHandlerException;
import org.tmcw.fakesmtp.spi.SaveToFilesystem;
import org.tmcw.fakesmtp.util.StreamToStringConverter;

/**
 * Listens to incoming emails and redirects them to the {@code SaveToFilesystem} object.
 */
public final class MailListener implements SimpleMessageListener {

    private final Collection<MailHandler> mailHandlerList;

    private final Collection<String> relayDomains;

    StreamToStringConverter streamToStringConverter = new StreamToStringConverter();

    public MailListener(final Collection<MailHandler> mailHandlerList, final Collection<String> relayDomains) {
        this.mailHandlerList = mailHandlerList;
        this.relayDomains = relayDomains;
    }

    /**
     * Accepts all kind of email <i>(always return true)</i>.
     * <p>
     * Called once for every RCPT TO during a SMTP exchange.<br>
     * Each accepted recipient will result in a separate deliver() call later.
     * </p>
     *
     * @param from the user who send the email.
     * @param recipient the recipient of the email.
     * @return always return {@code true}
     */
    @Override
    public boolean accept(final String from, final String recipient) {
        if (recipientIsAllowed(recipient)) {
            return true;
        } else {
            LoggerFactory.getLogger(getClass()).info(
                    "✕ rejecting mail to '{}' due to unmatched relay domain", recipient);
            return false;
        }
    }

    /**
     * Receives emails and forwards them to the {@link SaveToFilesystem} object.
     */
    @Override
    public void deliver(final String from, final String recipient, final InputStream data) {
        LoggerFactory.getLogger(getClass()).info("✓ Incoming mail: {} ⇒ {}", from, recipient);

        try {
            final String rawMessage = streamToStringConverter.convert(data);

            final MimeMessage mimeMessage = new MimeMessage(
                    Session.getInstance(new Properties()),
                    new ByteArrayInputStream(rawMessage.getBytes(StandardCharsets.UTF_8)));

            processHandlers(from, recipient, rawMessage, mimeMessage);
        } catch (IOException ex) {
            LoggerFactory.getLogger(getClass()).error("failed to read email content stream", ex);
        } catch (MessagingException ex) {
            LoggerFactory.getLogger(getClass()).error("failed to decode email content stream", ex);
        }
    }

    private void processHandlers(
            final String from, final String recipient, final String rawMessage, final MimeMessage mimeMessage) {

        for (final MailHandler mailHandler : mailHandlerList) {
            try {
                mailHandler.handle(from, recipient, rawMessage, mimeMessage);
            } catch (MailHandlerException ex) {
                LoggerFactory.getLogger(getClass()).error(
                        "✕ mail handler '" + mailHandler.getClass().getSimpleName() + "' failed to execute: "
                        + ex.getMessage());
            }
        }
    }

    private boolean recipientIsAllowed(final String recipient) {
        if (relayDomains.isEmpty()) {
            return true;
        } else {
            for (final String domain : relayDomains) {
                if (recipient.endsWith(domain)) {
                    return true;
                }
            }

            return false;
        }
    }

}
