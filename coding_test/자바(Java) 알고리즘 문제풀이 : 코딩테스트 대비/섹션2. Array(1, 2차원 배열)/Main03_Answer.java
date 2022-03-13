import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main03_Answer {

    public static List<String> solution(int n, int[] a, int[] b) {
        List<String> answer = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            if (a[i] == b[i]) {
                answer.add("D");
            } else if (a[i] == 1 && b[i] == 3) {
                answer.add("A");
            } else if (a[i] == 2 && b[i] == 1) {
                answer.add("A");
            } else if (a[i] == 3 && b[i] == 2) {
                answer.add("A");
            } else {
                answer.add("B");
            }
        }

        return answer;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        int[] a = new int[n];
        int[] b = new int[n];
        for (int i = 0; i < n; i++) {
            a[i] = scanner.nextInt();
        }
        for (int i = 0; i < n; i++) {
            b[i] = scanner.nextInt();
        }
        List<String> results = solution(n, a, b);
        results.forEach(System.out::println);
    }
}
