package org.tmcw.fakesmtp.spi;

import javax.mail.internet.MimeMessage;

public interface MailHandler {

    /**
     * Handle an incoming mail.
     *
     * You can Decode the raw content with this:
     *
     * final InputStream decodedInputStream = MimeUtility.decode(rawIs, "quoted-printable");
     *
     * @param from the user who send the email.
     * @param recipient the recipient of the email.
     * @param rawMessage the raw e-mail content.
     * @param mimeMessage the message object
     * @throws org.tmcw.fakesmtp.spi.MailHandlerException The Handler is allowed to fail
     */
    void handle(final String from, final String recipient, final String rawMessage, final MimeMessage mimeMessage)
            throws MailHandlerException;

}
