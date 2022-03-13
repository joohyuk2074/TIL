import java.util.Scanner;

public class Main04_Answer_02 {

    public static void solution(int n) {
        int a = 1;
        int b = 1;
        int c;
        System.out.println(a + " " + b + " ");
        for (int i = 2; i < n; i++) {
            c = a + b;
            System.out.print(c + " ");
            a = b;
            b = c;
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        solution(n);
    }
}
