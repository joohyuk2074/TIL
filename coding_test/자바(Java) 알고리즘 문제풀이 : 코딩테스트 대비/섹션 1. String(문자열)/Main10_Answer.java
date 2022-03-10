import java.util.Scanner;

public class Main10_Answer_01 {

    public static int[] solution(String s, char t) {
        int[] answer = new int[s.length()];
        int p = 1000;

        // -> 방향
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == t) {
                p = 0;
                answer[i] = p;
            } else {
                p++;
                answer[i] = p;
            }
        }

        // <- 역순
        p = 1000;
        for (int i = s.length() - 1; i >= 0; i--) {
            if (s.charAt(i) == t) {
                p = 0;
            } else {
                p++;
                answer[i] = Math.min(answer[i], p);
            }
        }

        return answer;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input1 = scanner.next();
        char input2 = scanner.next().charAt(0);
        int[] solution = solution(input1, input2);
        for (int i : solution) {
            System.out.print(i + " ");
        }
    }
}
