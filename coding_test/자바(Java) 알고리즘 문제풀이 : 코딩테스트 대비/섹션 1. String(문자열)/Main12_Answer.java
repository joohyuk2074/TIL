import java.util.Scanner;

public class Main12_Answer {

    public static String solution(int n, String s) {
        StringBuilder answer = new StringBuilder();

        for (int i = 0; i < n; i++) {
            String tmp = s.substring(0, 7).replace('#', '1').replace('*', '0');
            int num = Integer.parseInt(tmp, 2);
            answer.append((char) num);
            s = s.substring(7);
        }

        return answer.toString();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int num = scanner.nextInt();
        scanner.nextLine();
        String encryptedStr = scanner.nextLine();
        System.out.println(solution(num, encryptedStr));
    }
}
