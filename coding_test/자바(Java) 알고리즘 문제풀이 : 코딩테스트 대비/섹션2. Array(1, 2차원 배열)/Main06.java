import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main07 {

    public static int[] map = new int[100001];

    public static List<Integer> solution(int num, String[] nums) {
        List<Integer> answer = new ArrayList<>();

        StringBuilder sb;
        for (int i = 0; i < num; i++) {
            sb = new StringBuilder(nums[i]);
            String s = sb.reverse().toString();
            int integer = Integer.parseInt(s);
            if (map[integer] == 0) {
                answer.add(integer);
            }
        }

        return answer;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int num = scanner.nextInt();
        scanner.nextLine();
        String[] nums = new String[num];
        for (int i = 0; i < num; i++) {
            nums[i] = scanner.next();
        }

        map[0] = 1;
        map[1] = 1;
        for (int i = 2; i * i <= 100000; i++) {
            if (map[i] == 0) {
                for (int j = i + i; j <= 100000; j = j + i) {
                    map[j] = 1;
                }
            }
        }

        List<Integer> solution = solution(num, nums);
        for (int s : solution) {
            System.out.print(s + " ");
        }
    }
}
