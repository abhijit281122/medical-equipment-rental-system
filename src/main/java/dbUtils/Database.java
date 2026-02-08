package dbUtils;

import lombok.Getter;
import org.flywaydb.core.Flyway;
import org.sqlite.SQLiteDataSource;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

@Getter
public class Database {

    private static Database instance;
    private final Connection connection;

    // App directory (C:\Users\<user>\EquipmentRentalSystem)
    private static final String APP_DIR =
            System.getProperty("user.home") + File.separator + "EquipmentRentalSystem";

    // Full DB path
    private static final String DB_URL =
            "jdbc:sqlite:" + APP_DIR + File.separator + "myDb.db";

    private Database() {
        try {
            createAppDirectory();

            SQLiteDataSource dataSource = new SQLiteDataSource();
            dataSource.setUrl(DB_URL);

            // Flyway migration
            Flyway flyway = Flyway.configure()
                    .dataSource(dataSource)
                    .locations("classpath:db/migration")
                    .baselineOnMigrate(false)
                    .load();

            flyway.migrate();

            this.connection = dataSource.getConnection();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Database initialization failed", e);
        }
    }

    private void createAppDirectory() {
        File dir = new File(APP_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public static synchronized Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
