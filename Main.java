import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String databasePath = "data/movierater.db";
        String csvPath = "data/movie_habits.csv";

        try (Scanner scanner = new Scanner(System.in)) {
            DatabaseManager databaseManager = new DatabaseManager(databasePath);
            DatabaseInitializer initializer = new DatabaseInitializer(databaseManager);

            initializer.createTables();
            if (initializer.isDatabaseEmpty()) {
                initializer.importFromCsv(csvPath);
            }

            MovieRaterService service = new MovieRaterService(databaseManager);
            MovieRaterApp app = new MovieRaterApp(scanner, service, initializer, csvPath);
            app.run();

            databaseManager.close();
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        }
    }
}
