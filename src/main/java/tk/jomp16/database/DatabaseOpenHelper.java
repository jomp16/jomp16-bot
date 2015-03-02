/*
 * Copyright © 2015 jomp16 <joseoliviopedrosa@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

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
