package chapter01;

import java.util.Scanner;

public class Main01 {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        String input1 = in.nextLine();
        char input2 = in.next().charAt(0);

        int[] alphabet = new int[128];
        char[] input1Arr = input1.toLowerCase().toCharArray();
        for (char c : input1Arr) {
            alphabet[c] += 1;
        }

        int result = alphabet[Character.toLowerCase(input2)];
        System.out.println(result);
    }
}
