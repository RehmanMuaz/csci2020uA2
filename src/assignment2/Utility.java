package assignment2;

import java.io.*;

public class Utility {
    public static boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch(Exception ex) {
            return false;
        }
    }

    public static String getStackTrace(final Throwable throwable) {
        final StringWriter stringWriter = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(stringWriter, true);

        throwable.printStackTrace(printWriter);

        return stringWriter.getBuffer().toString();
    }
}
