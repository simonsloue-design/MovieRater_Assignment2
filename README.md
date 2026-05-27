# MovieRater - PI6 Resit Assignment 2

Student: Loue-Mae Simons  
Course: PI6 - Introduction to Software Engineering  
Assignment: Resit Assignment 2 - MovieRater  

## Project overview

MovieRater is a Java command-line application that manages movie viewing habit data using a SQLite database.  
The database is based on the assignment ERD with three tables:

- `User`
- `Movie`
- `ViewingHabit`

The program uses JDBC to connect Java with SQLite. SQL queries are used for the calculations, such as the mean age, the total minutes watched, and the number of users who watched more than one movie.

## Folder structure

```text
MovieRater_Assignment2/
├── data/
│   ├── movie_habits.csv
│   └── movierater.db
├── src/main/java/
│   ├── Main.java
│   ├── DatabaseManager.java
│   ├── DatabaseInitializer.java
│   ├── MovieRaterService.java
│   └── MovieRaterApp.java
├── pom.xml
└── README.md
```

## Database structure

### User

| Column | Type | Description |
|---|---|---|
| UserID | INTEGER PRIMARY KEY | Unique user identifier |
| Age | INTEGER | Age of the user |
| Email | TEXT | Added through menu option 9 |

### Movie

| Column | Type | Description |
|---|---|---|
| MovieID | INTEGER PRIMARY KEY | Unique movie identifier |
| Title | TEXT | Movie title |
| ReleaseYear | INTEGER | Year of release |
| Director | TEXT | Movie director |
| Genre | TEXT | Movie genre |

### ViewingHabit

| Column | Type | Description |
|---|---|---|
| UserID | INTEGER FOREIGN KEY | References User |
| MovieID | INTEGER FOREIGN KEY | References Movie |
| MinutesWatched | INTEGER | Number of minutes watched |

The primary key of `ViewingHabit` is the combination of `UserID` and `MovieID`.

## Functionalities

The application provides the required assignment functionalities:

1. Add a user to the database.
2. Provide all viewing habit data for a certain user.
3. Change the title of a movie in the database.
4. Delete a record/row from the `ViewingHabit` table.
5. Provide the mean age of the users.
6. Provide the total number of users that have watched minutes from a specific movie.
7. Provide the total number of minutes watched by all users.
8. Provide the total number of users that have watched more than one movie.
9. Add a column to the `User` table named `Email` with TEXT data.

Extra helper options are also included to show all movies, show all users, and reset the database from the CSV file.

## How to run the application

### Option 1: Run with Maven

Open the project folder in the terminal and run:

```bash
mvn compile
mvn exec:java -Dexec.mainClass=Main
```

If the second command does not work because the exec plugin is not installed automatically, use Option 2.

### Option 2: Run in Visual Studio Code

1. Open the whole `MovieRater_Assignment2` folder in Visual Studio Code.
2. Make sure the **Extension Pack for Java** is installed.
3. Make sure Maven support is enabled in VS Code.
4. Open `Main.java`.
5. Click **Run** above the `main` method.

VS Code will use `pom.xml` to download the SQLite JDBC dependency.

## Important note about SQLite JDBC

This project uses the `sqlite-jdbc` Maven dependency:

```xml
<dependency>
    <groupId>org.xerial</groupId>
    <artifactId>sqlite-jdbc</artifactId>
    <version>3.46.1.0</version>
</dependency>
```

This dependency is necessary because Java does not include SQLite support by default.

## Example test steps

After starting the program, try the following checks:

1. Choose option `10` to show all movies.
2. Choose option `11` to show all users.
3. Choose option `2` and enter `1` to show viewing habit data for user 1.
4. Choose option `5` to calculate the mean age of all users.
5. Choose option `6` and enter a MovieID to count users for a specific movie.
6. Choose option `7` to calculate the total watched minutes.
7. Choose option `8` to count users who watched more than one movie.
8. Choose option `9` to add the Email column.
9. Choose option `1` to add a new user.
10. Choose option `4` to delete a viewing habit row.

## Notes

- The SQLite database is already included in `data/movierater.db`.
- The program can also reset and refill the database from `data/movie_habits.csv`.
- SQL is used for the required calculations instead of Java loops.
