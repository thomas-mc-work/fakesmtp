package org.tmcw.fakesmtp.server;

import static org.junit.Assert.*;
import org.junit.Test;

public class SMTPAuthHandlerTest {

    public SMTPAuthHandlerTest() {
    }

    @Test
    public void testAuth() {
        final SMTPAuthHandler instance = new SMTPAuthHandler();

        assertEquals(SMTPAuthHandler.PROMPT_USERNAME, instance.auth("anything"));
        assertEquals(SMTPAuthHandler.PROMPT_PASSWORD, instance.auth("anything"));
        assertNull(instance.auth("anything"));
    }

    @Test
    public void testGetIdentity() {
        final SMTPAuthHandler instance = new SMTPAuthHandler();
        assertEquals(SMTPAuthHandler.USER_IDENTITY, instance.getIdentity());
    }

}
