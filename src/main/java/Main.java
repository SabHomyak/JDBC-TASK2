import java.sql.*;

public class Main {
    private static Connection connection = null;


    public static void main(String[] args) throws SQLException {
        try {
            connection = ConnectionDB.getConnection();
            createDB();
            createTableProducts();
            createTableCustomers();
            createTableOrders();
            addNewProduct("Phone", 20.35, "Very good phone!");
            addNewProduct("Notebook", 20.35, "Very good notebook!");
            addNewCustomer("Vasya", "+380952794815", "vasya@ukr.net");
            addNewCustomer("Petya", "+380132794815", "vasya@ukr.net");
            addNewOrder(1, 2, new Date(System.currentTimeMillis()));
            addNewOrder(1, 1, new Date(System.currentTimeMillis()));
            addNewOrder(2, 1, new Date(System.currentTimeMillis()));
            System.out.println(showTable("Customers"));
            System.out.println("**************************");
            System.out.println(showTable("Products"));
            System.out.println("**************************");
            System.out.println(showTable("orders"));
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connection.close();
        }
    }

    public static void createDB() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("drop DATABASE IF EXISTS my_db");
            statement.execute("create DATABASE IF NOT EXISTS my_db");
            statement.execute("use my_db");
        }
    }

    public static void createTableProducts() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("create table IF NOT EXISTS Products(\n" +
                    "\tidProduct int not null auto_increment primary key,\n" +
                    "    name varchar(50) not null,\n" +
                    "    price double not null,\n" +
                    "    description text\n" +
                    ");");
        }
    }

    public static void createTableCustomers() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("create table IF NOT EXISTS Customers(\n" +
                    "\tid int not null auto_increment primary key,\n" +
                    "    name varchar(50) not null,\n" +
                    "    phone varchar(13) not null,\n" +
                    "    email varchar(100)\n" +
                    ");");
        }
    }

    public static void createTableOrders() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("create table IF NOT EXISTS Orders(\n" +
                    "\tcustomerID int not null,\n" +
                    "    productID int not null ,\n" +
                    "    date DATETIME not null,\n" +
                    "    foreign key (customerID) references customers(id),\n" +
                    "    foreign key (productID) references products(idProduct)\n" +
                    "    );");
        }
    }

    public static void addNewProduct(String nameProduct, double price, String description) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("insert into products (name,price,description) " +
                "values (?,?,{d?});")) {
            statement.setString(1, nameProduct);
            statement.setDouble(2, price);
            statement.setString(3, description);
            statement.execute();
        }
    }

    public static void addNewCustomer(String name, String phone, String email) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("insert into customers (name,phone,email) " +
                "values (?,?,?);")) {
            statement.setString(1, name);
            statement.setString(2, phone);
            statement.setString(3, email);
            statement.execute();
        }
    }

    public static void addNewOrder(int customerID, int productID, Date date) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("insert into orders (customerID,productID,date) " +
                "values (?,?,?);")) {
            statement.setInt(1, customerID);
            statement.setInt(2, productID);
            statement.setDate(3, date);
            statement.execute();
        }
    }

    public static String showTable(String table) throws SQLException {
        String sqlQuery = "SELECT * FROM " + table + ";";
        if (table.equalsIgnoreCase("orders")) {
            sqlQuery = "SELECT Customers.name as customer_name, products.name as product_name, date FROM orders\n" +
                    "inner join customers on orders.customerID = customers.id\n" +
                    "inner join products on orders.productID = products.idProduct;";
        }
        StringBuilder sb = new StringBuilder();
        try (PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            ResultSet resultSet = statement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            sb.append("Command: " + (statement.toString().substring(43)) + System.lineSeparator());
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                sb.append("|" + metaData.getColumnLabel(i) + "| ");
            }
            sb.append(System.lineSeparator());
            sb.append("-----------------------------------------------------");
            sb.append(System.lineSeparator());
            while (resultSet.next()) {
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    sb.append("|" + resultSet.getString(i) + "| ");
                }
                sb.append(System.lineSeparator());

            }
            return sb.toString();
        }
    }
}
