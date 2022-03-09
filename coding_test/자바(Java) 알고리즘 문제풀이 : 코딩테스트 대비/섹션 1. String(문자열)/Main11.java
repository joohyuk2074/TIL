import java.util.Scanner;

public class Main11 {

    public static String solution(String input) {
        StringBuilder sb = new StringBuilder();
        char[] chars = input.toCharArray();
        char defaultChar = chars[0];
        sb.append(chars[0]);
        int count = 1;
        for (int i = 1; i < chars.length; i++) {
            if (chars[i] == defaultChar) {
                count++;
            } else {
                if (count > 1) {
                    sb.append(count);
                }
                defaultChar = chars[i];
                count = 1;
                sb.append(chars[i]);
            }
        }
        if (count > 1) {
            sb.append(count);
        }

        return sb.toString();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        System.out.println(solution(input));
    }
}
