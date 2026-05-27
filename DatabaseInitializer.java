import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {
    private final DatabaseManager databaseManager;

    public DatabaseInitializer(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void createTables() throws SQLException {
        String createUserTable = """
            CREATE TABLE IF NOT EXISTS "User" (
                UserID INTEGER PRIMARY KEY,
                Age INTEGER NOT NULL
            );
            """;

        String createMovieTable = """
            CREATE TABLE IF NOT EXISTS Movie (
                MovieID INTEGER PRIMARY KEY,
                Title TEXT NOT NULL,
                ReleaseYear INTEGER NOT NULL,
                Director TEXT NOT NULL,
                Genre TEXT NOT NULL
            );
            """;

        String createViewingHabitTable = """
            CREATE TABLE IF NOT EXISTS ViewingHabit (
                UserID INTEGER NOT NULL,
                MovieID INTEGER NOT NULL,
                MinutesWatched INTEGER NOT NULL,
                PRIMARY KEY (UserID, MovieID),
                FOREIGN KEY (UserID) REFERENCES "User"(UserID) ON DELETE CASCADE,
                FOREIGN KEY (MovieID) REFERENCES Movie(MovieID) ON DELETE CASCADE
            );
            """;

        try (Statement statement = databaseManager.getConnection().createStatement()) {
            statement.execute(createUserTable);
            statement.execute(createMovieTable);
            statement.execute(createViewingHabitTable);
        }
    }

    public boolean isDatabaseEmpty() throws SQLException {
        String sql = "SELECT COUNT(*) FROM ViewingHabit;";
        try (Statement statement = databaseManager.getConnection().createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            return resultSet.next() && resultSet.getInt(1) == 0;
        }
    }

    public void resetDatabase(String csvPath) throws SQLException, IOException {
        try (Statement statement = databaseManager.getConnection().createStatement()) {
            statement.execute("DROP TABLE IF EXISTS ViewingHabit;");
            statement.execute("DROP TABLE IF EXISTS Movie;");
            statement.execute("DROP TABLE IF EXISTS \"User\";");
        }
        createTables();
        importFromCsv(csvPath);
    }

    public void importFromCsv(String csvPath) throws SQLException, IOException {
        Connection connection = databaseManager.getConnection();

        String insertUser = "INSERT OR IGNORE INTO \"User\" (UserID, Age) VALUES (?, ?);";
        String insertMovie = "INSERT OR IGNORE INTO Movie (MovieID, Title, ReleaseYear, Director, Genre) VALUES (?, ?, ?, ?, ?);";
        String insertViewingHabit = "INSERT OR IGNORE INTO ViewingHabit (UserID, MovieID, MinutesWatched) VALUES (?, ?, ?);";

        try (BufferedReader reader = new BufferedReader(new FileReader(csvPath));
             PreparedStatement userStatement = connection.prepareStatement(insertUser);
             PreparedStatement movieStatement = connection.prepareStatement(insertMovie);
             PreparedStatement habitStatement = connection.prepareStatement(insertViewingHabit)) {

            String line = reader.readLine(); // skip header

            while ((line = reader.readLine()) != null) {
                String[] values = parseCsvLine(line);
                if (values.length < 8) {
                    continue;
                }

                int userId = Integer.parseInt(values[0].trim());
                int age = Integer.parseInt(values[1].trim());
                int movieId = Integer.parseInt(values[2].trim());
                String title = values[3].trim();
                int releaseYear = Integer.parseInt(values[4].trim());
                String director = values[5].trim();
                String genre = values[6].trim();
                int minutesWatched = Integer.parseInt(values[7].trim());

                userStatement.setInt(1, userId);
                userStatement.setInt(2, age);
                userStatement.executeUpdate();

                movieStatement.setInt(1, movieId);
                movieStatement.setString(2, title);
                movieStatement.setInt(3, releaseYear);
                movieStatement.setString(4, director);
                movieStatement.setString(5, genre);
                movieStatement.executeUpdate();

                habitStatement.setInt(1, userId);
                habitStatement.setInt(2, movieId);
                habitStatement.setInt(3, minutesWatched);
                habitStatement.executeUpdate();
            }
        }
    }

    private String[] parseCsvLine(String line) {
        // Simple CSV parser that also handles quoted values.
        java.util.ArrayList<String> values = new java.util.ArrayList<>();
        StringBuilder currentValue = new StringBuilder();
        boolean insideQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);

            if (character == '"') {
                insideQuotes = !insideQuotes;
            } else if (character == ',' && !insideQuotes) {
                values.add(currentValue.toString());
                currentValue.setLength(0);
            } else {
                currentValue.append(character);
            }
        }

        values.add(currentValue.toString());
        return values.toArray(new String[0]);
    }
}
