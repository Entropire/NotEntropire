package me.entropire.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseContext
{
    private String path;
    public Connection con;
    public DataBaseContext(String path)
    {
        this.path = path;
        con = CreateConnection();
    }

    public Connection CreateConnection()
    {
        try
        {
            Class.forName("org.sqlite.JDBC");
            return  DriverManager.getConnection("jdbc:sqlite:" + path);
        }
        catch (SQLException | ClassNotFoundException e)
        {
            System.out.println("Failed to connect to the dataBase: " + e.getMessage());
            return null;
        }
    }
}