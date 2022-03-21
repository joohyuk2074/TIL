
import java.util.Scanner;

public class Main10 {

    public static int[] dx = {0, -1, 0, 1};
    public static int[] dy = {-1, 0, 1, 0};

    public static int solution(int n, int[][] arr) {
        int answer = 0;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                boolean flag = true;
                for (int k = 0; k < 4; k++) {
                    int row = i + dx[k];
                    int column = j + dy[k];
                    if (row >= 0 && row < n && column >= 0 && column < n) {
                        if (arr[i][j] <= arr[row][column]) {
                            flag = false;
                            break;
                        }
                    }
                }
                if (flag) {
                    answer++;
                }
            }
        }

        return answer;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int[][] arr = new int[n][n];
        System.out.println(solution(n, arr));
    }
}
