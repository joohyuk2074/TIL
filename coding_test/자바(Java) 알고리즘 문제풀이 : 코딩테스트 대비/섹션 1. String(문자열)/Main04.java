import java.util.Scanner;
import java.util.Stack;

public class Main04 {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int wordCount = scanner.nextInt();
        scanner.nextLine();

        for (int i=0; i<wordCount; i++) {
            String input = scanner.nextLine();
            Stack<Character> wordStack = new Stack<>();
            for (char c : input.toCharArray()) {
                wordStack.push(c);
            }

            StringBuilder sb = new StringBuilder();
            while(!wordStack.empty()) {
                sb.append(wordStack.pop());
            }

            System.out.println(sb);
        }
    }
}