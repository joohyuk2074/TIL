import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main04 {

    public static List<Integer> solution(int n) {
        List<Integer> result = new ArrayList<>();

        result.add(1);
        result.add(1);
        for (int i = 2; i < n; i++) {
            int num1 = result.get(i - 2);
            int num2 = result.get(i - 1);
            result.add(num1 + num2);
        }

        return result;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        List<Integer> results = solution(n);
        results.forEach(num -> System.out.print(num + " "));
    }
}
