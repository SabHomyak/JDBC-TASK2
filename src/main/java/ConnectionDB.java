import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionDB {
    private static File file = new File("db.properties");
    private static String url;
    private static String user;
    private static String password;

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private static void createDBProperties() {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (FileWriter fw = new FileWriter(file)) {
            fw.write("db.url=jdbc:mysql://localhost:3306/?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC" + System.lineSeparator());
            fw.write("db.user=root" + System.lineSeparator());
            fw.write("db.password=root" + System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        createDBProperties();
        Connection connection = null;
        Properties properties = new Properties();
        try (InputStream is = new FileInputStream(new File("db.properties"))) {
            properties.load(is);

        } catch (IOException e) {
            e.printStackTrace();
        }
        url = properties.getProperty("db.url");
        user = properties.getProperty("db.user");
        password = properties.getProperty("db.password");
        connection = DriverManager.getConnection(url, user, password);
        return connection;
    }
}
