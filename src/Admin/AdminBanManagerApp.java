package Admin;

import Exceptions.ItemNotFoundException;
import MainClasses.BannedUser;
import Repositories.BannedUserRepository;

import java.util.List;
import java.util.Scanner;

public class AdminBanManagerApp {

    private static final BannedUserRepository repository = BannedUserRepository.getInstance();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   Welcome to the Admin Ban Management System");
        System.out.println("==================================================");

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1":
                        banUser();
                        break;
                    case "2":
                        viewAllBannedUsers();
                        break;
                    case "3":
                        checkIfUserIsBanned();
                        break;
                    case "4":
                        checkUserPermissions();
                        break;
                    case "5":
                        removeBan();
                        break;
                    case "6":
                        running = false;
                        System.out.println("Exiting the application. Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid option. Please enter a number between 1 and 6.");
                }
            } catch (ItemNotFoundException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("An unexpected error occurred: " + e.getMessage());
            }
            System.out.println(); // Print a blank line for readability
        }

        scanner.close();
    }

    private static void printMenu() {
        System.out.println("Please select an action:");
        System.out.println("1. Ban a new user (Create ban record)");
        System.out.println("2. View all banned users");
        System.out.println("3. Check if a specific user is banned");
        System.out.println("4. Check specific permissions of a user (Login/Comment/Post)");
        System.out.println("5. Remove a user's ban");
        System.out.println("6. Exit");
        System.out.print("> ");
    }

    private static void banUser() {
        System.out.print("Enter the ID of the user to ban: ");
        String userId = scanner.nextLine().trim();

        if (repository.isUserBanned(userId)) {
            System.out.println("User is already in the banned list. Use edit functionality to change permissions.");
            return;
        }

        System.out.println("Configure permissions for this user (enter 'true' to allow, 'false' to deny):");

        System.out.print("Allow Login? (true/false): ");
        boolean allowLogin = Boolean.parseBoolean(scanner.nextLine().trim());

        System.out.print("Allow Commenting? (true/false): ");
        boolean allowComment = Boolean.parseBoolean(scanner.nextLine().trim());

        System.out.print("Allow Posting? (true/false): ");
        boolean allowPost = Boolean.parseBoolean(scanner.nextLine().trim());

        repository.create(userId, allowLogin, allowComment, allowPost);
        System.out.println("Successfully created ban record for user: " + userId);
    }

    private static void viewAllBannedUsers() {
        List<BannedUser> bannedUsers = repository.getAllBannedUsers();

        if (bannedUsers.isEmpty()) {
            System.out.println("There are currently no banned users in the system.");
            return;
        }

        System.out.println("--- Banned Users List ---");
        for (BannedUser user : bannedUsers) {
            System.out.printf("User ID: %s | Login: %b | Comment: %b | Post: %b%n",
                    user.getUserId(),
                    user.isUserAllowedToLogin(),
                    user.isUserAllowedToComment(),
                    user.isUserAllowedToPost());
        }
        System.out.println("-------------------------");
    }

    private static void checkIfUserIsBanned() {
        System.out.print("Enter the User ID to check: ");
        String userId = scanner.nextLine().trim();

        boolean isBanned = repository.isUserBanned(userId);
        if (isBanned) {
            System.out.println("User '" + userId + "' IS currently on the ban list.");
        } else {
            System.out.println("User '" + userId + "' is NOT on the ban list.");
        }
    }

    private static void checkUserPermissions() {
        System.out.print("Enter the User ID to check permissions for: ");
        String userId = scanner.nextLine().trim();

        boolean canLogin = repository.isUserAllowedToLogin(userId);
        boolean canComment = repository.isUserAllowedToComment(userId);
        boolean canPost = repository.isUserAllowedToPost(userId);

        System.out.println("--- Permissions for User: " + userId + " ---");
        System.out.println("Allowed to Login:   " + (canLogin ? "YES" : "NO"));
        System.out.println("Allowed to Comment: " + (canComment ? "YES" : "NO"));
        System.out.println("Allowed to Post:    " + (canPost ? "YES" : "NO"));

        if (canLogin && canComment && canPost) {
            System.out.println("(Note: This user has full permissions. They likely do not have a ban record.)");
        }
    }

    private static void removeBan() {
        System.out.print("Enter the User ID to unban: ");
        String userId = scanner.nextLine().trim();

        if (!repository.isUserBanned(userId)) {
            System.out.println("User '" + userId + "' is not on the ban list.");
            return;
        }

        repository.removeBannedUser(userId);
        System.out.println("Successfully removed ban record for user: " + userId);
    }
}