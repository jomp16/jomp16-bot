package tk.jomp16.database;

import tk.jomp16.database.sqlite.SqliteDatabase;

import java.sql.SQLException;

public class DatabaseOpenHelper {
    private String databasePath;
    private int databaseVersion;

    public DatabaseOpenHelper(String databasePath, int databaseVersion) {
        this.databasePath = databasePath;
        this.databaseVersion = databaseVersion;
    }

    public void onCreate(SqliteDatabase database) throws SQLException {

    }

    public void onUpgrade(SqliteDatabase database, int oldVersion, int newVersion) throws SQLException {

    }

    public String getDatabasePath() {
        return databasePath;
    }

    public int getDatabaseVersion() {
        return databaseVersion;
    }

    public SqliteDatabase getDatabase() throws Exception {
        return new SqliteDatabase(this);
    }
}
