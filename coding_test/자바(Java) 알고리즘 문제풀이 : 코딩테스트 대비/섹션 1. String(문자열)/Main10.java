import java.util.Scanner;

public class Main10 {

    public static void solution(String text, char c) {
        char[] chars = text.toCharArray();
        for (int pivot = 0; pivot < chars.length; pivot++) {
            // 왼쪽
            boolean leftFind = false;
            int leftDistance = 0;
            for (int j = pivot; j >= 0; j--) {
                if (chars[j] == c) {
                    leftFind = true;
                    break;
                }
                leftDistance++;
            }

            // 오른쪽
            boolean rightFind = false;
            int rightDistance = 0;
            for (int k = pivot; k < chars.length; k++) {
               if (chars[k] == c) {
                   rightFind = true;
                   break;
               }
               rightDistance++;
            }

            if (!leftFind && rightFind) {
                System.out.print(rightDistance);
            } else if(leftFind && !rightFind) {
                System.out.print(leftDistance);
            } else {
                System.out.print(Math.min(rightDistance, leftDistance));
            }
            System.out.print(" ");
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input1 = scanner.next();
        char input2 = scanner.next().charAt(0);
        solution(input1, input2);
    }
}
