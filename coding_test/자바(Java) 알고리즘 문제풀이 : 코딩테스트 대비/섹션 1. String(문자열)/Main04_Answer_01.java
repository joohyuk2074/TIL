import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

public class Main04_Answer_01 {

    public static ArrayList<String> solution(int n, String[] str) {
        ArrayList<String> answer = new ArrayList<>();

        for(String x : str) {
            String tmp = new StringBuilder(x).reverse().toString();
            answer.add(tmp);
        }

        return answer;
    }

    public static void main(String[] args) {
        Scanner kb = new Scanner(System.in);
        int n = kb.nextInt();
        String[] str = new String[n];
        for (int i=0; i<n; i++) {
            str[i] = kb.next();
        }
        for (String x : solution(n, str)) {
            System.out.println(x);
        }
    }
}