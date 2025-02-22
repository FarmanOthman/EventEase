package server;
import ui.LoginView;

public class Server {
    public static void main(String[] args) {
        LoginView loginView = new LoginView();
        loginView.show();

        // Wait for the user to successfully log in
        while (!loginView.isLoginSuccessful()) {
            try {
                Thread.sleep(500); // Check login status every 500ms
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Hide login window once successful
        loginView.hide();
        System.out.println("User logged in successfully! Proceeding...");
    }
}
