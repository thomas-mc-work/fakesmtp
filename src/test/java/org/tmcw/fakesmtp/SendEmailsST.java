package org.tmcw.fakesmtp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.commons.mail.SimpleEmail;
import static org.junit.Assert.assertEquals;
import org.junit.Ignore;
import org.junit.Test;

public final class SendEmailsST {

    public static final String SMTP_HOST = System.getProperty("smtp.host", "localhost");
    public static final int SMTP_PORT = Integer.parseInt(System.getProperty("smtp.port", "2525"));
    public static final Path OUTPUT_PATH = Paths.get(System.getProperty("output.path", "output"));

    @Test
    @Ignore
    public void sendSimpleTestEmail() throws Exception {
        final Email email = simpleEmail();
        email.send();

        assertEquals(OUTPUT_PATH.toFile().listFiles().length, 1);

        final MimeMessage message = readAndRemoveMessageFile();
        assertEquals(message.getSubject(), email.getSubject());
    }

    @Test
    public void sendEmailWithAttachment() throws Exception {
        // Create the attachment
        final EmailAttachment attachment = new EmailAttachment();
        attachment.setPath("src/main/resources/icon.gif");
        attachment.setDisposition(EmailAttachment.ATTACHMENT);
        attachment.setDescription("Image file");
        attachment.setName("icon.gif");

        // Create the email message
        final MultiPartEmail email = new MultiPartEmail();
        email.setHostName(SMTP_HOST);
        email.setSmtpPort(SMTP_PORT);
        email.addTo("jdoe@somewhere.org", "John Doe");
        email.setFrom("me@example.org", "Me");
        email.setSubject("File attachment");
        email.setMsg("This email contains an enclosed file.");

        // Add the attachment
        email.attach(attachment);

        // Send the email
        email.send();

        assertEquals(OUTPUT_PATH.toFile().listFiles().length, 1);

        final MimeMessage message = readAndRemoveMessageFile();
        assertEquals(message.getSubject(), email.getSubject());

        final MimeMultipart mpContent = (MimeMultipart) message.getContent();
        assertEquals(mpContent.getCount(), 2);
        assertEquals(mpContent.getBodyPart(1).getFileName(), attachment.getName());
        assertEquals(mpContent.getBodyPart(1).getDescription(), attachment.getDescription());
        assertEquals(mpContent.getBodyPart(1).getSize(), 2578);
    }

    @Test
    public void sendHTMLFormattedEmail() throws Exception {
        // Create the email message
        final HtmlEmail email = new HtmlEmail();
        email.setHostName(SMTP_HOST);
        email.setSmtpPort(SMTP_PORT);
        email.addTo("jdoe@somewhere.org", "John Doe");
        email.setFrom("me@example.org", "Me");
        email.setSubject("HTML email");

        // Set the HTML message
        email.setHtmlMsg("<html><body>This is an <b>HTML</b> email.<br /><br /></body></html>");

        // Set the alternative message
        email.setTextMsg("Your email client does not support HTML messages");

        email.send();

        assertEquals(OUTPUT_PATH.toFile().listFiles().length, 1);

        final MimeMessage message = readAndRemoveMessageFile();
        assertEquals(message.getSubject(), email.getSubject());

        final MimeMultipart mpContent = (MimeMultipart) message.getContent();
        assertEquals(mpContent.getCount(), 1);
        assertEquals(mpContent.getBodyPart(0).getSize(), 401);
    }

    @Test
    public void sendEmailWithBase64Subject() throws Exception {
        final Email email = simpleEmail();
        // 🚅 Boston railway deals - while they last!
        email.setSubject("=?UTF-8?B?8J+ahSBCb3N0b24gcmFpbHdheSBkZWFscyAtIHdoaWxlIHRoZXkgbGFzdCE==?=");
        email.send();

        assertEquals(OUTPUT_PATH.toFile().listFiles().length, 1);

        final MimeMessage message = readAndRemoveMessageFile();
        assertEquals(message.getSubject(), "🚅 Boston railway deals - while they last!");
    }

    @Test
    public void sendEmailToManyRecipientsWithTwoHeaders() throws Exception {
        final Email email = simpleEmail();
        email.addTo("test2@example.com");
        email.addHeader("Foo", "Bar");
        email.addHeader("Foo2", "Bar2");
        email.send();

        assertEquals(OUTPUT_PATH.toFile().listFiles().length, 2);

        final MimeMessage message1 = readAndRemoveMessageFile();
        final MimeMessage message2 = readAndRemoveMessageFile();
        assertEquals(message1.getSubject(), email.getSubject());

        final Address[] recipientsMsg1 = message1.getRecipients(Message.RecipientType.TO);
        assertEquals(recipientsMsg1.length, 2);
        assertEquals(recipientsMsg1[0].toString(), "foo@bar.com");
        assertEquals(recipientsMsg1[1].toString(), "test2@example.com");
    }

    @Test
    public void sendEmailWithDots() throws Exception {
        final Email email = simpleEmail();
        email.setDebug(true);
        email.setMsg(".\n.");
        email.send();

        assertEquals(OUTPUT_PATH.toFile().listFiles().length, 1);

        final MimeMessage message = readAndRemoveMessageFile();
        assertEquals(message.getSubject(), email.getSubject());
        assertEquals(message.getContent().toString(), ".\r\n.\r\n");
    }

    private Email simpleEmail() throws EmailException {
        final Email email = new SimpleEmail();
        email.setHostName(SMTP_HOST);
        email.setSmtpPort(SMTP_PORT);
        email.setStartTLSEnabled(true);
        email.setFrom("user@startmail.com");
        email.setSubject("Simple email");
        email.setMsg("This is a simple plain text #üöä# email :-)");
        email.addTo("foo@bar.com");

        return email;
    }

    private MimeMessage readAndRemoveMessageFile() throws IOException, MessagingException {
        final File firstFile = OUTPUT_PATH.toFile().listFiles()[0];

        try (InputStream inputStream = Files.newInputStream(firstFile.toPath())) {
            final MimeMessage mimeMessage = new MimeMessage(
                    Session.getInstance(new Properties()), inputStream);
            Files.delete(firstFile.toPath());
            return mimeMessage;
        }
    }
}
