import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Main06 {

    public static String solution(String str) {
        StringBuilder answer = new StringBuilder();

        Set<Character> set = new HashSet<>();
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (!set.contains(c)) {
                set.add(c);
                answer.append(c);
            }
        }

        return answer.toString();
    }

    public static void main(String[] args) {
        Scanner kb = new Scanner(System.in);
        String input = kb.nextLine();
        System.out.println(solution(input));
    }
}
