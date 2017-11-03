package org.tmcw.fakesmtp.spi;

public class MailHandlerException extends Exception {

    public MailHandlerException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public MailHandlerException(final Throwable cause) {
        super(cause);
    }

    public MailHandlerException(final String msg) {
        super(msg);
    }
}
