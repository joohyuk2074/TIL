import java.util.Scanner;

public class Main09_Answer_02 {

    public static int solution(String input) {
        StringBuilder answer = new StringBuilder();

        for (char x : input.toCharArray()) {
            if (Character.isDigit(x)) {
                answer.append(x);
            }
        }

        return Integer.parseInt(answer.toString());
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        System.out.println(solution(input));
    }
}
