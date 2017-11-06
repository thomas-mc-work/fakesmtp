package org.tmcw.fakesmtp.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.tmcw.fakesmtp.spi.MailHandler;
import org.tmcw.fakesmtp.spi.MailHandlerException;

public class MailListenerIT {

    @Test
    public void testDeliver() throws MessagingException, IOException, MessagingException {
        final String from = "jones@myhost.org";
        final String recipient = "hightower@yourhost.net";
        final Path exampleMailPath = Paths.get("src/test/resources/example-mail.eml");
        final String rawMessage = new String(Files.readAllBytes(exampleMailPath), StandardCharsets.UTF_8);
        final InputStream data = new ByteArrayInputStream(rawMessage.getBytes(StandardCharsets.UTF_8));

        final SimpleMailHandler handler1 = new SimpleMailHandler();
        final SimpleMailHandler handler2 = new SimpleMailHandler();
        final List<MailHandler> mailHandlerList = new ArrayList<>();
        mailHandlerList.add(handler1);
        mailHandlerList.add(handler2);

        final MailListener instance = new MailListener(mailHandlerList, new ArrayList<String>());
        instance.deliver(from, recipient, data);

        verifyHandler(handler1, from, recipient, rawMessage);
        verifyHandler(handler2, from, recipient, rawMessage);
    }

    private void verifyHandler(
            final SimpleMailHandler handler1, final String from, final String recipient, final String rawMessage)
            throws IOException, MessagingException {

        assertEquals(from, handler1.from);
        assertEquals(recipient, handler1.recipient);
        assertEquals(rawMessage, handler1.rawMessage);
        assertEquals(1, handler1.mimeMessage.getFrom().length);
        assertEquals("someone@example.com", handler1.mimeMessage.getFrom()[0].toString());
        final Address[] recipients = handler1.mimeMessage.getRecipients(Message.RecipientType.TO);
        assertEquals(1, recipients.length);
        assertEquals("yourfriend@gmail.com", recipients[0].toString());
        assertEquals("This is the subject", handler1.mimeMessage.getSubject());
        assertEquals("This is the message body and contains the message\n", handler1.mimeMessage.getContent().toString());
        assertEquals("<20171102105403.dOb0cwKYw%someone@example.com>", handler1.mimeMessage.getMessageID());
    }

    class SimpleMailHandler implements MailHandler {

        public String from;
        public String recipient;
        public String rawMessage;
        public MimeMessage mimeMessage;

        @Override
        public void handle(String from, String recipient, String rawMessage, MimeMessage mimeMessage)
                throws MailHandlerException {

            this.from = from;
            this.recipient = recipient;
            this.rawMessage = rawMessage;
            this.mimeMessage = mimeMessage;
        }
    }
}
