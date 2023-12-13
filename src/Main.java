import java.util.Optional;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Enter n (or press q to exit): ");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("q")) {
                break;
            }

            try {
                int n = Integer.parseInt(input);
                Computation.compfunc(n);
            } catch (NumberFormatException e) {
                System.out.println("Please enter an integer or 'esc' to exit.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        scanner.close();
    }



}
