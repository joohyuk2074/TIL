import java.util.Scanner;

public class Main08 {

    public static String solution(String input) {
        String answer = "NO";

        String replacedInput = input.replaceAll("[^a-zA-Z]", "");
        String reverseReplacedInput = new StringBuilder(replacedInput).reverse().toString();

        if (replacedInput.equalsIgnoreCase(reverseReplacedInput)) {
            answer = "YES";
        }

        return answer;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        System.out.println(solution(input));
    }
}
