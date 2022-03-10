import java.util.Scanner;

public class Main11_Answer {

    public static String solution(String input) {
        StringBuilder answer = new StringBuilder();

        input = input + " ";
        int cnt = 1;
        for (int i = 0; i < input.length() - 1; i++) {
            if (input.charAt(i) == input.charAt(i + 1)) {
                cnt++;
            } else {
                answer.append(input.charAt(i));
                if (cnt > 1) {
                    answer.append(cnt);
                    cnt = 1;
                }
            }
        }

        return answer.toString();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        System.out.println(solution(input));
    }
}
