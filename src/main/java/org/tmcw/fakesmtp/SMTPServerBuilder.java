package org.tmcw.fakesmtp;

import org.tmcw.fakesmtp.server.MailListener;
import org.tmcw.fakesmtp.server.SMTPAuthHandlerFactory;
import org.tmcw.fakesmtp.spi.MailHandler;
import java.net.InetAddress;
import java.util.List;
import org.subethamail.smtp.helper.SimpleMessageListenerAdapter;
import org.subethamail.smtp.server.SMTPServer;

public class SMTPServerBuilder {

    public SMTPServer build(final List<MailHandler> mailHandlerList, final List<String> relayDomains, final int port,
            final InetAddress bindAddress) {

        final SMTPServer smtpServer = new SMTPServer(
                new SimpleMessageListenerAdapter(new MailListener(mailHandlerList, relayDomains)),
                new SMTPAuthHandlerFactory());
        smtpServer.setBindAddress(bindAddress);
        smtpServer.setPort(port);
        smtpServer.setDisableReceivedHeaders(true);

        return smtpServer;
    }
}
