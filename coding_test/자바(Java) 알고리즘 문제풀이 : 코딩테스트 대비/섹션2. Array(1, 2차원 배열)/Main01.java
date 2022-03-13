import java.util.Scanner;

public class Main01 {

    public static void solution(int[] nums, int num) {
        StringBuilder answer = new StringBuilder(String.valueOf(nums[0]));

        int currentNum = nums[0];
        for (int i = 1; i < num; i++) {
           if (currentNum < nums[i]) {
               answer.append(" ").append(nums[i]);
           }
           currentNum = nums[i];
        }

        System.out.println(answer);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int num = scanner.nextInt();
        scanner.nextLine();

        int[] nums = new int[num];
        for (int i = 0; i < num; i++) {
            nums[i] = scanner.nextInt();
        }

        solution(nums, num);
    }
}
