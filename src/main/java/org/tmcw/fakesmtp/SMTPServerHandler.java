package org.tmcw.fakesmtp;

import java.net.BindException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.smtp.server.SMTPServer;

/**
 * Starts and stops the SMTP server.
 */
public class SMTPServerHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(SMTPServerHandler.class);

    private final SMTPServer smtpServer;

    public SMTPServerHandler(final SMTPServer smtpServer) {
        this.smtpServer = smtpServer;
    }

    /**
     * Starts the server on the port and address specified in parameters.
     *
     * @throws IllegalArgumentException when port is out of range.
     */
    public void startServer() {
        LOGGER.debug("Start listening on {}:{}", smtpServer.getBindAddress(), smtpServer.getPort());
        try {
            smtpServer.start();
        } catch (RuntimeException ex) {
            final Throwable causeEx = ex.getCause();

            if (BindException.class.isInstance(causeEx)) {
                LOGGER.error("Port {}: {}", smtpServer.getPort(), ex.getMessage());
            } else {
                LOGGER.error("Server startup failed due to an unexpected error!", ex);
            }
        }
    }

    /**
     * Stops the server.
     */
    public void stopServer() {
        LOGGER.debug("Stopping server");
        smtpServer.stop();
    }
}
