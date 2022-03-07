import java.util.Scanner;

public class Main07_Answer_02 {

    public static String solution(String input) {
        String answer = "NO";
        String tmp = new StringBuilder(input).reverse().toString();
        if (!input.equalsIgnoreCase(tmp)) {
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
