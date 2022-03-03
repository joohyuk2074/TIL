package chapter01;

import java.util.Scanner;

public class Main02_answer {

    public static String solution(String str) {
        StringBuilder answer = new StringBuilder();

        for (char x : str.toCharArray()) {
            if (Character.isLowerCase(x)) {
                answer.append(Character.toUpperCase(x));
            } else {
                answer.append(Character.toLowerCase(x));
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
