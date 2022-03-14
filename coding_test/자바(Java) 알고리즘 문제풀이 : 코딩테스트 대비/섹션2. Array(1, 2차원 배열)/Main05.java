import java.util.Scanner;

public class Main05 {

    public static int solution(int num) {
        int answer = 0;

        boolean[] arr = new boolean[num + 1];

        for (int i = 2; i * i <= num; i++) {
            for (int j = i + i; j <= num; j += i) {
                arr[j] = true;
            }
        }

        for (int i = 2; i <= num; i++) {
            if (!arr[i]) {
                answer++;
            }
        }

        return answer;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int num = scanner.nextInt();
        System.out.println(solution(num));
    }
}
