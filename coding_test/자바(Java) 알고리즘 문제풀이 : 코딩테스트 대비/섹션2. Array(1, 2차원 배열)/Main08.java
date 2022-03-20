
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class Main08 {

    public static List<Integer> solution(int n, int[] arr) {
        List<Integer> answer = new ArrayList<>();

        Integer[] sortedArr = new Integer[n];
        for (int i = 0; i < n; i++) {
            sortedArr[i] = arr[i];
        }
        Arrays.sort(sortedArr, Comparator.reverseOrder());

        for (int i = 0; i < n; i++) {
            int j = 0;
            for (; j < n; j++) {
                if (arr[i] == sortedArr[j]) {
                    answer.add(j + 1);
                    break;
                }
            }
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

        List<Integer> arrayList = solution(n, arr);
        for (int num : arrayList) {
            System.out.print(num + " ");
        }
    }
}
