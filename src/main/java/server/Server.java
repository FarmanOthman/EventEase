package server;


class Server {
    public static void main(String[] args) {
        // [AuthenticationService.authenticate] is statick so we do not need to create obejct from it
        
        boolean isAuthenticated = AuthenticationService.authenticate("a@a.com", "password123"); // return a boolean value
    

        // if true then we will show admin dashboward
        if (isAuthenticated) {
            System.out.println("Login successful! Redirecting to Dashboard...");
            System.out.println("GUI"); // Show Admin Dashboard
        } else {
            System.out.println("Invalid email or password.");
        }
        // testttttttttttt
        // test by abdullah
    }
}

