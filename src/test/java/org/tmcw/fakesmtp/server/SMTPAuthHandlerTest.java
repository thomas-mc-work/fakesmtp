package org.tmcw.fakesmtp.server;

import static org.junit.Assert.*;
import org.junit.Test;

public class SMTPAuthHandlerTest {

    public SMTPAuthHandlerTest() {
    }

    @Test
    public void testAuth() {
        final SMTPAuthHandler instance = new SMTPAuthHandler();

        assertEquals(instance.auth("anything"), SMTPAuthHandler.PROMPT_USERNAME);
        assertEquals(instance.auth("anything"), SMTPAuthHandler.PROMPT_PASSWORD);
        assertNull(instance.auth("anything"));
    }

    @Test
    public void testGetIdentity() {
        final SMTPAuthHandler instance = new SMTPAuthHandler();
        assertEquals(instance.getIdentity(), SMTPAuthHandler.USER_IDENTITY);
    }

}
