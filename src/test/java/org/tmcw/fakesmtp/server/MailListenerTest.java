package org.tmcw.fakesmtp.server;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import javax.mail.internet.MimeMessage;
import static org.junit.Assert.*;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import org.tmcw.fakesmtp.spi.MailHandler;
import org.tmcw.fakesmtp.spi.MailHandlerException;

public class MailListenerTest {

    @Test
    public void testAcceptEmptyTrue() {
        final String from = "john@selfhosted.org";
        final String recipient = "frank@startmail.com";

        final MailListener instance = new MailListener(new ArrayList<MailHandler>(), new ArrayList<String>());

        boolean expResult = true;
        boolean result = instance.accept(from, recipient);
        assertEquals(result, expResult);
    }

    @Test
    public void testAcceptTrue() {
        final String from = "john@selfhosted.org";
        final String recipient = "frank@startmail.com";

        final MailListener instance = new MailListener(new ArrayList<MailHandler>(), Arrays.asList("startmail.com"));

        boolean expResult = true;
        boolean result = instance.accept(from, recipient);
        assertEquals(result, expResult);
    }

    @Test
    public void testAcceptFalse() {
        final String from = "john@selfhosted.org";
        final String recipient = "frank@endmail.com";

        final MailListener instance = new MailListener(new ArrayList<MailHandler>(), Arrays.asList("startmail.com"));

        boolean expResult = false;
        boolean result = instance.accept(from, recipient);
        assertEquals(result, expResult);
    }

    @Test
    public void testDeliver() throws MailHandlerException {
        final String from = "sender@localhost";
        final String recipient = "receiver@localhost";
        final String rawMessage = "hello";
        final InputStream data = new ByteArrayInputStream(rawMessage.getBytes(StandardCharsets.UTF_8));

        final MailHandler handlerMock = mock(MailHandler.class);
        final MailListener instance = new MailListener(Arrays.asList(handlerMock), new ArrayList<String>());
        instance.deliver(from, recipient, data);

        verify(handlerMock).handle(eq(from), eq(recipient), eq(rawMessage), ArgumentMatchers.any(MimeMessage.class));
    }

}
