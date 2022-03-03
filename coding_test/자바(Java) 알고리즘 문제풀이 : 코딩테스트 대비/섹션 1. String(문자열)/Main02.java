package chapter01;

import java.util.Scanner;

public class Main02 {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();

        StringBuilder stringBuilder = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (65 <= c && c <= 90) {
                stringBuilder.append(Character.toChars(c + 32));
            } else {
                stringBuilder.append(Character.toChars(c - 32));
            }
        }

        System.out.println(stringBuilder);
    }
}
