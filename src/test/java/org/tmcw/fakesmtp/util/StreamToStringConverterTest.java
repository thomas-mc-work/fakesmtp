package org.tmcw.fakesmtp.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import static org.junit.Assert.*;
import org.junit.Test;

public class StreamToStringConverterTest {

    final StreamToStringConverter instance = new StreamToStringConverter();

    @Test
    public void testConvert() throws Exception {
        final String input = "hêllo ju";
        final InputStream is = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));

        final String result = instance.convert(is);

        assertEquals(input, result);
    }

}
