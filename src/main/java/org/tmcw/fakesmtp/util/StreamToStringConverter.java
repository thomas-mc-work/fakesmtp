package org.tmcw.fakesmtp.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class StreamToStringConverter {

    /**
     * Converts an {@code InputStream} into a {@code String} object.
     * <p>
     * The method will not copy the first 4 lines of the input stream.<br>
     * These 4 lines are SubEtha SMTP additional information.
     * </p>
     *
     * @param is the InputStream to be converted.
     * @return the converted string object, containing data from the InputStream passed in parameters.
     */
    public String convert(final InputStream is) {
        final Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
