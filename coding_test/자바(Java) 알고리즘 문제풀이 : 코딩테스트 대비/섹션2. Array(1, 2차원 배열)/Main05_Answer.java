import java.util.Scanner;

public class Main06 {

    public static int solution(int num) {
        int answer = 0;

        int[] arr = new int[num + 1];
        for (int i = 2; i <= num; i++) {
            if (arr[i] == 0) {
                answer++;
                for (int j = i; j <= num; j = j + i) {
                    arr[j] = 1;
                }
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
