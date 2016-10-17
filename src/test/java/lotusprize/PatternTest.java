package lotusprize;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class PatternTest {

    public static void main(String[] args) {
        String regex = "(?dog)";
        String input = "dog dog";
        try {
            Pattern p = Pattern.compile(regex);
            Matcher matcher = p.matcher(input);
            boolean found = false;

            while (matcher.find()) {
                System.out.format("检索到匹配文本\"%s\"从位置 %d 到 %d。%n",
                        matcher.group(), matcher.start(), matcher.end());
                found = true;
            }
            if (!found) {
                System.out.println("没有检索到匹配的文本。");
            }
            System.out
                    .println("===============================================");
            matcher = p.matcher(input);
            System.out.println(matcher.groupCount());
            while (matcher.find()) {
                for (int i = 1; i <= matcher.groupCount(); i++) {
                    System.out.println(matcher.group(i) + ":from "
                            + matcher.start(i) + " to " + matcher.end(i));
                }
            }
        } catch (PatternSyntaxException pse) {
            System.out.println("The pattern in question is:" + pse.getPattern());
            System.out.println("The description is:"+ pse.getDescription());
            System.out.println("The message is:"+ pse.getMessage());
            System.out.println("The index is:" + pse.getIndex());
            System.exit(0);
        }

    }

}
