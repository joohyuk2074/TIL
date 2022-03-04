import java.util.Scanner;

public class Main03 {

    public static String solution(String input) {
        String answer = "";
        String[] words = input.split(" ");
        for (String word : words) {
            if (answer.length() < word.length()) {
                answer = word;
            }
        }
        return answer;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        System.out.println(solution(input));
    }
}