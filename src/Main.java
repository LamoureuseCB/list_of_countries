import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    static Scanner scanner = new Scanner(System.in);
    public static void main(String[] args) throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "postgres";
        String password = "postgres";

        Connection connection = DriverManager.getConnection(url, user, password);
//       createContinent(connection);
//       createCountryName(connection);
//       updateCountry(connection);
        getPopulation(connection);
    }

    // метод для задачи с первого скриншота
// (нужно создать континент, страну, переименовать/обновить страну)
    static void createContinent(Connection connection) throws SQLException {
        String sql = "insert into continents(id, name) values (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            System.out.println("Введите id континента:");
            int continentId = Integer.parseInt(scanner.nextLine());
            System.out.println("Введите имя континента:");
            String createdContinentName = scanner.nextLine();
            statement.setInt(1, continentId);
            statement.setString(2, createdContinentName);
            statement.executeUpdate();
            System.out.println("Континент  успешно добавлен");
        }
    }


    static void createCountryName(Connection connection) throws SQLException {
        String sql = "insert into countries(id, name, continent_id) values (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            System.out.println("Введите id страны:");
            int countryId = Integer.parseInt(scanner.nextLine());
            System.out.println("Введите имя страны:");
            String createdCountryName = scanner.nextLine();
            System.out.println("Введите id континента:");
            int createdContinentNameId = scanner.nextInt();
            statement.setInt(1, countryId);
            statement.setString(2, createdCountryName);
            statement.setInt(3, createdContinentNameId);
            statement.executeUpdate();
            System.out.println("Страна  успешно добавлена");
        }
    }


    static void updateCountry(Connection connection) throws SQLException {
        String sql = "update countries set name = ? where continent_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            System.out.println("Введите id континента:");
            int continentId = Integer.parseInt(scanner.nextLine());

            String oldName = "select name from countries where countries.continent_id = ?";
            try (PreparedStatement statement2 = connection.prepareStatement(oldName)) {
                statement2.setInt(1, continentId);
                ResultSet rs = statement2.executeQuery();
                if (rs.next()) {
                    String oldCountryName = rs.getString("name");
                    System.out.println("Старое имя страны: " + oldCountryName);
                } else {
                    System.out.println("Страна с таким id не найдена");
                    return;
                }
            }

            System.out.println("Введите новое имя страны: ");
            String countryNewName = scanner.nextLine();
            statement.setString(1, countryNewName);
            statement.setInt(2, continentId);
            statement.executeUpdate();
            System.out.println("Имя страны  успешно обновлено");
        }
    }


    // метод для задачи со второго и третьего скриншота
    // (ввод нескольких стран в строку запроса и  вывод количества населения по городам этих стран)
    static void getPopulation(Connection connection) throws SQLException {
        System.out.println("Введите название стран (через запятую и пробел): ");
        String countryName = scanner.nextLine();
        String[] countryNames = countryName.split(",");

        StringBuilder sql = new StringBuilder();
        sql.append("select cities.id, cities.name, cities.population from cities join countries on countries.id = cities.country_id where countries.name in (");
        for (int i = 0; i < countryNames.length; i++) {
            if (i > 0) {
                sql.append(", ");
            }
            sql.append("?");
        }
        sql.append(")");
        try (PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < countryNames.length; i++) {
                statement.setString(i + 1, countryNames[i]);
            }

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    int citiesId = rs.getInt("id");
                    String name = rs.getString("name");
                    int population = rs.getInt("population");
                    System.out.printf("%d. %s [%d]%n", citiesId, name, population);
                }
            }
        }
    }
}