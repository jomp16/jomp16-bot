package tk.jomp16.utils;

import com.google.common.base.CharMatcher;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    /**
     * Tokenize IRC raw input into it's components, keeping the
     * 'sender' and 'message' fields intact.
     *
     * @param input A string in the format [:]item [item] ... [:item [item] ...]
     * @return List of strings.
     * @author PircBotX team
     */
    public static List<String> tokenizeLine(String input) {
        List<String> stringParts = new ArrayList<>();
        if (input == null || input.length() == 0) {
            return stringParts;
        }

        // Heavily optimized version string split by space with all characters after :
        // added as a single entry. Under benchmarks, its faster than StringTokenizer,
        // String.split, toCharArray, and charAt
        String trimmedInput = CharMatcher.WHITESPACE.trimFrom(input);
        int pos = 0, end;
        while ((end = trimmedInput.indexOf(' ', pos)) >= 0) {
            stringParts.add(trimmedInput.substring(pos, end));
            pos = end + 1;
            if (trimmedInput.charAt(pos) == ':') {
                stringParts.add(trimmedInput.substring(pos + 1));
                return stringParts;
            }
        }

        // No more spaces, add last part of line
        stringParts.add(trimmedInput.substring(pos));
        return stringParts;
    }


    public static String getRamUsage() {
        return humanReadableByteCount(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory(), true);
    }

    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
}
