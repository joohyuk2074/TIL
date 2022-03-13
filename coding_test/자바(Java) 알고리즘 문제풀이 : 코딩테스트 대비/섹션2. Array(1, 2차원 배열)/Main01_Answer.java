package chapter02;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main01_Answer {

    public static List<Integer> solution(int n, int[] arr) {
        ArrayList<Integer> answer = new ArrayList<>();
        answer.add(arr[0]);
        for (int i = 1; i < n; i++) {
            if (arr[i] > arr[i - 1]) {
                answer.add(arr[i]);
            }
        }

        return answer;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int num = scanner.nextInt();
        scanner.nextLine();

        int[] nums = new int[num];
        for (int i = 0; i < num; i++) {
            nums[i] = scanner.nextInt();
        }

        for (int x : solution(num, nums)) {
            System.out.print(x + " ");
        }
    }
}
