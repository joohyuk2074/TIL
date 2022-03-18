import java.util.Scanner;

public class Main07 {

    public static int solution(int n, int[] arr) {
        int answer = 0;

        int acc = 0;
        for (int i = 0; i < n; i++) {
            if (arr[i] == 1) {
               acc++;
            } else {
                acc = 0;
            }
            answer += acc;
        }

        return answer;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) {
            arr[i] = scanner.nextInt();
        }

        System.out.println(solution(n, arr));
    }
}
