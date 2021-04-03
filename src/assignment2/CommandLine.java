package assignment2;

import java.util.*;

public class CommandLine {
    public static Map<String, String> parse(String[] args) {
        Map<String, String> data = new TreeMap<>();

        String key = null;
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            if (arg.startsWith("-")) {
                if (key != null) {
                    data.put(key, "");
                }
                key = arg.substring(1).toLowerCase();
            } else if (key != null) {
                data.put(key, arg);
                key = null;
            }
        }
        if (key != null) {
            data.put(key, "");
        }

        return data;
    }
}
