import java.util.Scanner;

public class Main02_Answer {

    public static int solution(int n, int[] arr) {
        int answer = 1;
        int max = arr[0];
        for (int i = 1; i < n; i++) {
            if (arr[i] > max) {
                answer++;
                max = arr[i];
            }
        }
        return answer;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();

        int[] arr = new int[n];
        for (int i = 0; i < n; i++) {
            arr[i] = scanner.nextInt();
        }

        System.out.println(solution(n, arr));
    }
}
