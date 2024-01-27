package org.tmcw.fakesmtp;

import java.net.UnknownHostException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import org.mockito.MockitoAnnotations;
import org.subethamail.smtp.server.SMTPServer;

public class SMTPServerHandlerTest {

    @Mock
    SMTPServer smtpServer;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testStartServer() throws UnknownHostException {
        final SMTPServerHandler instance = new SMTPServerHandler(smtpServer);
        instance.startServer();

        verify(smtpServer).start();
    }

    @Test
    public void testStopServer() {
        final SMTPServerHandler instance = new SMTPServerHandler(smtpServer);
        instance.stopServer();

        verify(smtpServer).stop();
    }

}
