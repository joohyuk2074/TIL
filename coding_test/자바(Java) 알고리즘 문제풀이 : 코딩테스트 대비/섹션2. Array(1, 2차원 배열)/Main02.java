import java.util.Scanner;

public class Main {

    public static int solution(int n, int[] arr) {
        int max = arr[0];
        int num = 1;

        for (int i = 1; i < n; i++) {
            if (max < arr[i]) {
                num++;
                max = arr[i];
            }
        }

        return num;
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
