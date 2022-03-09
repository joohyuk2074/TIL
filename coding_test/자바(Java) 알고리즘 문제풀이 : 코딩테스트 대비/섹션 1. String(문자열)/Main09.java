import java.util.Scanner;

public class Main09 {

    public static String solution(String input) {
        String answer = "";

        String replacedInput = input.replaceAll("[^0-9]", "");

        if (replacedInput.length() == 1 && replacedInput.charAt(0) == '0') {
            return "0";
        }

        int startIndex = 0;
        while (startIndex < replacedInput.length() - 1) {
            if (replacedInput.charAt(startIndex) == '0' && replacedInput.charAt(startIndex + 1) != '0') {
                startIndex += 1;
                break;
            } else if (replacedInput.charAt(startIndex) == '0') {
                startIndex++;
            } else {
                break;
            }
        }

        answer = replacedInput.substring(startIndex);
        return answer;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        System.out.println(solution(input));
    }
}
