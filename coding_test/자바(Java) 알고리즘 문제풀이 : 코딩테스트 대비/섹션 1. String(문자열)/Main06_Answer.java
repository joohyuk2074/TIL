import java.util.Scanner;

public class Main06_Answer {

    public static String solution(String str) {
        String answer = "";

        for (int i = 0; i < str.length(); i++) {
            if (str.indexOf(str.charAt(i)) == i) {
                answer += str.charAt(i);
            }
        }

        return answer;
    }

    public static void main(String[] args) {
        Scanner kb = new Scanner(System.in);
        String input = kb.nextLine();
        System.out.println(solution(input));
    }
}
