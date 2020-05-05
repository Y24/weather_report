package cn.org.y24.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class StringMatcher {
    public static String getDivByClassname(BufferedReader reader, String classname) throws IOException {
        Pattern divStartPattern = Pattern.compile("\\s*<div class=\"" + classname + "\">\\s*");
        Pattern divEndPattern = Pattern.compile("\\s*</div>\\s*");
        String line;
        StringBuilder result = new StringBuilder();
        do {
            line = reader.readLine();
            if (divStartPattern.matcher(line).matches()) {
                result.append(line);
                break;
            }
        } while (true);
        do {
            line = reader.readLine();
            if (divEndPattern.matcher(line).matches()) {
                result.append(line);
                break;
            }
            result.append(line);
        } while (true);
        return result.toString();
    }

    public static String match(String str, Pattern pattern, Pattern groupPattern) {
        Matcher matcher0 = pattern.matcher(str);
        if (!matcher0.find()) return "";
        Matcher matcher1 = groupPattern.matcher(matcher0.group());
        if (!matcher1.matches()) return "";
        return matcher1.group(2);
    }
}
