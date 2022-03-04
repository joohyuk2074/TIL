import java.util.Scanner;

public class Main03_Answer_01 {

    public static String solution(String input) {
        String answer = "";
        int m = Integer.MIN_VALUE;
        String[] s = input.split(" ");
        for (String x : s) {
            int len = x.length();
            if (len > m) {
                m = len;
                answer = x;
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