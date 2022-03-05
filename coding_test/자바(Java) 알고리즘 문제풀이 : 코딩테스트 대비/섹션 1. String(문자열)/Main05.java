import java.util.Scanner;

public class Main05 {

    public static String solution(String str) {
        char[] chars = str.toCharArray();
        int lt = 0;
        int rt = chars.length - 1;
        while (lt < rt) {
            while (!Character.isAlphabetic(chars[lt])) {
                lt++;
            }
            while (!Character.isAlphabetic(chars[rt])) {
                rt--;
            }
            char temp = chars[lt];
            chars[lt] = chars[rt];
            chars[rt] = temp;
            lt++;
            rt--;
        }
        return String.valueOf(chars);
    }

    public static void main(String[] args) {
        Scanner kb = new Scanner(System.in);
        String input = kb.nextLine();
        System.out.println(solution(input));
    }
}
