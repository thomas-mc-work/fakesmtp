package org.tmcw.fakesmtp.server;

import org.tmcw.fakesmtp.spi.MailHandler;
import org.tmcw.fakesmtp.spi.MailHandlerException;
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

        assertEquals(handler1.from, from);
        assertEquals(handler1.recipient, recipient);
        assertEquals(handler1.rawMessage, rawMessage);
        assertEquals(handler1.mimeMessage.getFrom().length, 1);
        assertEquals(handler1.mimeMessage.getFrom()[0].toString(), "someone@example.com");
        final Address[] recipients = handler1.mimeMessage.getRecipients(Message.RecipientType.TO);
        assertEquals(recipients.length, 1);
        assertEquals(recipients[0].toString(), "yourfriend@gmail.com");
        assertEquals(handler1.mimeMessage.getSubject(), "This is the subject");
        assertEquals(handler1.mimeMessage.getContent().toString(), "This is the message body and contains the message\n");
        assertEquals(handler1.mimeMessage.getMessageID(), "<20171102105403.dOb0cwKYw%someone@example.com>");
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
