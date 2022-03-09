import java.util.Scanner;

public class Main09_Answer_01 {

    public static int solution(String input) {
        int answer = 0;

        for (char x : input.toCharArray()) {
           if (x >= 48 && x <= 57) {
               answer = answer * 10 + (x - 48);
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
