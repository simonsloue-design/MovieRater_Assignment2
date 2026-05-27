import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MovieRaterService {
    private final DatabaseManager databaseManager;

    public MovieRaterService(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void addUser(int userId, int age, String email) throws SQLException {
        boolean emailColumnExists = emailColumnExists();

        if (emailColumnExists) {
            String sql = "INSERT INTO \"User\" (UserID, Age, Email) VALUES (?, ?, ?);";
            try (PreparedStatement statement = databaseManager.getConnection().prepareStatement(sql)) {
                statement.setInt(1, userId);
                statement.setInt(2, age);
                statement.setString(3, email);
                statement.executeUpdate();
            }
        } else {
            String sql = "INSERT INTO \"User\" (UserID, Age) VALUES (?, ?);";
            try (PreparedStatement statement = databaseManager.getConnection().prepareStatement(sql)) {
                statement.setInt(1, userId);
                statement.setInt(2, age);
                statement.executeUpdate();
            }
        }

        System.out.println("User added successfully.");
    }

    public void showViewingDataForUser(int userId) throws SQLException {
        String sql = """
            SELECT u.UserID, u.Age, m.MovieID, m.Title, m.ReleaseYear, m.Director, m.Genre, vh.MinutesWatched
            FROM "User" u
            JOIN ViewingHabit vh ON u.UserID = vh.UserID
            JOIN Movie m ON vh.MovieID = m.MovieID
            WHERE u.UserID = ?
            ORDER BY m.Title;
            """;

        try (PreparedStatement statement = databaseManager.getConnection().prepareStatement(sql)) {
            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                boolean found = false;
                System.out.println("\nViewing data for user " + userId + ":");

                while (resultSet.next()) {
                    found = true;
                    System.out.println(
                            "MovieID: " + resultSet.getInt("MovieID")
                            + " | Title: " + resultSet.getString("Title")
                            + " | Year: " + resultSet.getInt("ReleaseYear")
                            + " | Director: " + resultSet.getString("Director")
                            + " | Genre: " + resultSet.getString("Genre")
                            + " | Minutes watched: " + resultSet.getInt("MinutesWatched")
                    );
                }

                if (!found) {
                    System.out.println("No viewing data found for this user.");
                }
            }
        }
    }

    public void changeMovieTitle(int movieId, String newTitle) throws SQLException {
        String sql = "UPDATE Movie SET Title = ? WHERE MovieID = ?;";

        try (PreparedStatement statement = databaseManager.getConnection().prepareStatement(sql)) {
            statement.setString(1, newTitle);
            statement.setInt(2, movieId);

            int rowsChanged = statement.executeUpdate();
            if (rowsChanged > 0) {
                System.out.println("Movie title updated successfully.");
            } else {
                System.out.println("No movie found with this MovieID.");
            }
        }
    }

    public void deleteViewingHabit(int userId, int movieId) throws SQLException {
        String sql = "DELETE FROM ViewingHabit WHERE UserID = ? AND MovieID = ?;";

        try (PreparedStatement statement = databaseManager.getConnection().prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.setInt(2, movieId);

            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("ViewingHabit record deleted successfully.");
            } else {
                System.out.println("No ViewingHabit record found for this UserID and MovieID.");
            }
        }
    }

    public double getMeanAge() throws SQLException {
        String sql = "SELECT AVG(Age) AS MeanAge FROM \"User\";";
        try (Statement statement = databaseManager.getConnection().createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            return resultSet.next() ? resultSet.getDouble("MeanAge") : 0.0;
        }
    }

    public int getNumberOfUsersForMovie(int movieId) throws SQLException {
        String sql = """
            SELECT COUNT(DISTINCT UserID) AS TotalUsers
            FROM ViewingHabit
            WHERE MovieID = ? AND MinutesWatched > 0;
            """;

        try (PreparedStatement statement = databaseManager.getConnection().prepareStatement(sql)) {
            statement.setInt(1, movieId);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getInt("TotalUsers") : 0;
            }
        }
    }

    public int getTotalMinutesWatched() throws SQLException {
        String sql = "SELECT SUM(MinutesWatched) AS TotalMinutes FROM ViewingHabit;";

        try (Statement statement = databaseManager.getConnection().createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            return resultSet.next() ? resultSet.getInt("TotalMinutes") : 0;
        }
    }

    public int getNumberOfUsersWhoWatchedMoreThanOneMovie() throws SQLException {
        String sql = """
            SELECT COUNT(*) AS TotalUsers
            FROM (
                SELECT UserID
                FROM ViewingHabit
                GROUP BY UserID
                HAVING COUNT(DISTINCT MovieID) > 1
            );
            """;

        try (Statement statement = databaseManager.getConnection().createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            return resultSet.next() ? resultSet.getInt("TotalUsers") : 0;
        }
    }

    public void addEmailColumn() throws SQLException {
        if (emailColumnExists()) {
            System.out.println("The Email column already exists.");
            return;
        }

        String sql = "ALTER TABLE \"User\" ADD COLUMN Email TEXT;";
        try (Statement statement = databaseManager.getConnection().createStatement()) {
            statement.execute(sql);
            System.out.println("Email column added successfully.");
        }
    }

    public boolean emailColumnExists() throws SQLException {
        String sql = "PRAGMA table_info(\"User\");";
        try (Statement statement = databaseManager.getConnection().createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                String columnName = resultSet.getString("name");
                if ("Email".equalsIgnoreCase(columnName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void showAllMovies() throws SQLException {
        String sql = "SELECT MovieID, Title, ReleaseYear, Director, Genre FROM Movie ORDER BY MovieID;";

        try (Statement statement = databaseManager.getConnection().createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            System.out.println("\nMovies in database:");
            while (resultSet.next()) {
                System.out.println(
                        resultSet.getInt("MovieID") + " | "
                        + resultSet.getString("Title") + " | "
                        + resultSet.getInt("ReleaseYear") + " | "
                        + resultSet.getString("Director") + " | "
                        + resultSet.getString("Genre")
                );
            }
        }
    }

    public void showAllUsers() throws SQLException {
        String emailSelect = emailColumnExists() ? ", Email" : "";
        String sql = "SELECT UserID, Age" + emailSelect + " FROM \"User\" ORDER BY UserID;";

        try (Statement statement = databaseManager.getConnection().createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            System.out.println("\nUsers in database:");
            while (resultSet.next()) {
                String output = "UserID: " + resultSet.getInt("UserID")
                        + " | Age: " + resultSet.getInt("Age");

                if (emailColumnExists()) {
                    output += " | Email: " + resultSet.getString("Email");
                }

                System.out.println(output);
            }
        }
    }
}
