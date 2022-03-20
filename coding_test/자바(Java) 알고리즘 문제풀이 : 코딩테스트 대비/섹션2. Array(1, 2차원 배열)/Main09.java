import java.util.Scanner;

public class Main09 {

    public static int solution(int n, int[][] arr) {
        int answer = 0;

        // 각 행의 합 MAX
        for (int i = 0; i < n; i++) {
            int rowSum = 0;
            for (int j = 0; j < n; j++) {
                rowSum += arr[i][j];
            }
            answer = Math.max(answer, rowSum);
        }

        // 각 열의 합 MAX
        for (int i = 0; i < n; i++) {
            int columnSum = 0;
            for (int j = 0; j < n; j++) {
                columnSum += arr[j][i];
            }
            answer = Math.max(answer, columnSum);
        }

        // 대각선 의 합 MAX
        int startRow = 0;
        int startColumn = 0;
        int diagonalSum = 0;
        for (int i = 0; i < n; i++) {
            diagonalSum += arr[startRow][startColumn];
            startRow++;
            startColumn++;
        }
        answer = Math.max(answer, diagonalSum);

        int endRow = 0;
        int endColumn = n - 1;
        diagonalSum = 0;
        for (int i = 0; i < n; i++) {
            diagonalSum += arr[endRow][endColumn];
            endRow++;
            endColumn--;
        }
        answer = Math.max(answer, diagonalSum);

        return answer;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int[][] arr = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                arr[i][j] = scanner.nextInt();
            }
        }
        System.out.println(solution(n, arr));
    }
}
