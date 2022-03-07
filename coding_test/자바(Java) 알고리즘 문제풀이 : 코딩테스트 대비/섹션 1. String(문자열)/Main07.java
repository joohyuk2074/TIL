import java.util.Scanner;
import java.util.Stack;

public class Main07 {

    public static String solution(String input) {
        Stack<Character> reverse = new Stack<>();
        char[] chars = input.toCharArray();
        for (char c : chars) {
            reverse.push(Character.toLowerCase(c));
        }

        StringBuilder stringBuilder = new StringBuilder();
        while (!reverse.isEmpty()) {
            stringBuilder.append(reverse.pop());
        }

        if (input.toLowerCase().equals(stringBuilder.toString())) {
            return "YES";
        } else {
            return "NO";
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        System.out.println(solution(input));
    }
}
