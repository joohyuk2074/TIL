import java.util.Scanner;

public class Main07_Answer_01 {

    public static String solution(String input) {
        String answer = "YES";
        input = input.toUpperCase();
        int len = input.length();
        for (int i = 0; i < len / 2; i++) {
            if (input.charAt(i) != input.charAt(len - 1 - i)) {
                return "NO";
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
