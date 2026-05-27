import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

public class MovieRaterApp {
    private final Scanner scanner;
    private final MovieRaterService service;
    private final DatabaseInitializer initializer;
    private final String csvPath;

    public MovieRaterApp(Scanner scanner, MovieRaterService service, DatabaseInitializer initializer, String csvPath) {
        this.scanner = scanner;
        this.service = service;
        this.initializer = initializer;
        this.csvPath = csvPath;
    }

    public void run() {
        boolean running = true;

        while (running) {
            printMenu();
            int choice = readInt("Choose an option: ");

            try {
                switch (choice) {
                    case 1 -> addUser();
                    case 2 -> showViewingDataForUser();
                    case 3 -> changeMovieTitle();
                    case 4 -> deleteViewingHabit();
                    case 5 -> showMeanAge();
                    case 6 -> showNumberOfUsersForMovie();
                    case 7 -> showTotalMinutesWatched();
                    case 8 -> showUsersWhoWatchedMoreThanOneMovie();
                    case 9 -> service.addEmailColumn();
                    case 10 -> service.showAllMovies();
                    case 11 -> service.showAllUsers();
                    case 12 -> resetDatabase();
                    case 0 -> {
                        running = false;
                        System.out.println("Goodbye.");
                    }
                    default -> System.out.println("Invalid option.");
                }
            } catch (SQLException e) {
                System.out.println("Database action failed: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("File action failed: " + e.getMessage());
            }

            System.out.println();
        }
    }

    private void printMenu() {
        System.out.println("--- MovieRater Menu ---");
        System.out.println("1. Add a user to the database");
        System.out.println("2. Show all viewing habit data for a user");
        System.out.println("3. Change the title of a movie");
        System.out.println("4. Delete a record from ViewingHabit");
        System.out.println("5. Show mean age of users");
        System.out.println("6. Show number of users who watched a specific movie");
        System.out.println("7. Show total number of minutes watched by all users");
        System.out.println("8. Show number of users who watched more than one movie");
        System.out.println("9. Add Email column to User table");
        System.out.println("10. Show all movies");
        System.out.println("11. Show all users");
        System.out.println("12. Reset database from movie_habits.csv");
        System.out.println("0. Exit");
    }

    private void addUser() throws SQLException {
        int userId = readInt("Enter new UserID: ");
        int age = readInt("Enter age: ");
        String email = "";

        if (service.emailColumnExists()) {
            System.out.print("Enter email: ");
            email = scanner.nextLine();
        }

        service.addUser(userId, age, email);
    }

    private void showViewingDataForUser() throws SQLException {
        int userId = readInt("Enter UserID: ");
        service.showViewingDataForUser(userId);
    }

    private void changeMovieTitle() throws SQLException {
        service.showAllMovies();
        int movieId = readInt("Enter MovieID: ");
        System.out.print("Enter new title: ");
        String newTitle = scanner.nextLine();
        service.changeMovieTitle(movieId, newTitle);
    }

    private void deleteViewingHabit() throws SQLException {
        int userId = readInt("Enter UserID of the record: ");
        int movieId = readInt("Enter MovieID of the record: ");
        service.deleteViewingHabit(userId, movieId);
    }

    private void showMeanAge() throws SQLException {
        double meanAge = service.getMeanAge();
        System.out.printf("Mean age of users: %.2f%n", meanAge);
    }

    private void showNumberOfUsersForMovie() throws SQLException {
        service.showAllMovies();
        int movieId = readInt("Enter MovieID: ");
        int totalUsers = service.getNumberOfUsersForMovie(movieId);
        System.out.println("Number of users who watched this movie: " + totalUsers);
    }

    private void showTotalMinutesWatched() throws SQLException {
        int totalMinutes = service.getTotalMinutesWatched();
        System.out.println("Total minutes watched by all users: " + totalMinutes);
    }

    private void showUsersWhoWatchedMoreThanOneMovie() throws SQLException {
        int totalUsers = service.getNumberOfUsersWhoWatchedMoreThanOneMovie();
        System.out.println("Number of users who watched more than one movie: " + totalUsers);
    }

    private void resetDatabase() throws SQLException, IOException {
        System.out.print("This will reset all data. Type YES to continue: ");
        String confirmation = scanner.nextLine();

        if ("YES".equalsIgnoreCase(confirmation)) {
            initializer.resetDatabase(csvPath);
            System.out.println("Database reset from CSV.");
        } else {
            System.out.println("Reset cancelled.");
        }
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}
