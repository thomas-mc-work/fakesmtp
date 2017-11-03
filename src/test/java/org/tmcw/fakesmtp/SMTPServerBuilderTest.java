package org.tmcw.fakesmtp;

import org.tmcw.fakesmtp.spi.MailHandler;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Test;
import org.subethamail.smtp.server.SMTPServer;

public class SMTPServerBuilderTest {

    final SMTPServerBuilder instance = new SMTPServerBuilder();

    @Test
    public void testBuild() throws UnknownHostException {
        final List<MailHandler> mailHandlerList = Collections.emptyList();
        final List<String> relayDomains = Arrays.asList("evil.com");
        final int port = 345;
        final InetAddress bindAddress = InetAddress.getByName("ddg.gg");

        final SMTPServer result = instance.build(mailHandlerList, relayDomains, port, bindAddress);

        assertEquals(result.getBindAddress(), bindAddress);
        assertEquals(result.getPort(), port);
    }

}
