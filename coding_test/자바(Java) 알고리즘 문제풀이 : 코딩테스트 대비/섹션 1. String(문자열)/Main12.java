import java.util.Scanner;

public class Main12 {

    public static String solution(String encryptedStr, int num) {
        StringBuilder answer = new StringBuilder();

        String replace = encryptedStr.replace("*", "0")
            .replace("#", "1");

        String[] words = new String[num];
        for (int i = 0; i < num; i++) {
            words[i] = replace.substring(i * 7, i * 7 + 7);
        }

        for (String word : words) {
            int asciiNum = Integer.parseInt(word, 2);
            answer.append((char) asciiNum);
        }

        return answer.toString();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int num = scanner.nextInt();
        scanner.nextLine();
        String encryptedStr = scanner.nextLine();
        System.out.println(solution(encryptedStr, num));
    }
}
