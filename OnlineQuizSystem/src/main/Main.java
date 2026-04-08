import service.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        AuthService auth = new AuthService();
        QuizService quiz = new QuizService();
        AdminService admin = new AdminService();

        System.out.println("1. Login\n2. Register");
        int choice = sc.nextInt();
        sc.nextLine();

        if (choice == 2) {
            System.out.print("Username: ");
            String user = sc.nextLine();
            System.out.print("Password: ");
            String pass = sc.nextLine();
            auth.register(user, pass);
            System.out.println("Registered Successfully!");
        }

        System.out.print("Login Username: ");
        String user = sc.nextLine();
        System.out.print("Password: ");
        String pass = sc.nextLine();

        String role = auth.login(user, pass);

        if (role == null) {
            System.out.println("Invalid Login!");
            return;
        }

        if (role.equals("admin")) {
            System.out.println("Admin Panel");
            // admin.addSampleQuestion();
        } else {
            System.out.println("1. Quiz Mode\n2. Study Mode");
            int mode = sc.nextInt();
            sc.nextLine();

            System.out.print("Choose difficulty (easy/medium/hard): ");
            String diff = sc.nextLine();

            quiz.startQuiz(diff);
        }
    }
}
