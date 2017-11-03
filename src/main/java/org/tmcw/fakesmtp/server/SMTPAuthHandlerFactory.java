package org.tmcw.fakesmtp.server;

import java.util.Arrays;
import java.util.List;
import org.subethamail.smtp.AuthenticationHandler;
import org.subethamail.smtp.AuthenticationHandlerFactory;

/**
 * The factory interface for creating authentication handlers.
 */
public final class SMTPAuthHandlerFactory implements AuthenticationHandlerFactory {

    private static final String LOGIN_MECHANISM = "LOGIN";

    @Override
    public AuthenticationHandler create() {
        return new SMTPAuthHandler();
    }

    @Override
    public List<String> getAuthenticationMechanisms() {
        return Arrays.asList(SMTPAuthHandlerFactory.LOGIN_MECHANISM);
    }
}
