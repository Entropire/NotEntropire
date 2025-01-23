package me.entropire.database;

import net.dv8tion.jda.api.entities.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class UserDatabase
{
    private DataBaseContext dataBaseContext;

    public UserDatabase(DataBaseContext dataBaseContext)
    {
        this.dataBaseContext = dataBaseContext;

        try (Statement statement = dataBaseContext.con.createStatement())
        {
            statement.execute("""
            CREATE TABLE IF NOT EXISTS Users (
                id TEXT PRIMARY KEY,
                name TEXT NOT NULL,
                teamName STRING DEFAULT NULL
            )
            """);
        }
        catch (Exception e)
        {
            System.out.println("Failed to create/load players table in database: " + e.getMessage());
        }
    }

    public void addUser(User user)
    {
        try (PreparedStatement preparedStatement = dataBaseContext.con.prepareStatement("INSERT INTO Users (id, name) VALUES (?, ?)"))
        {
            preparedStatement.setString(1, user.getId());
            preparedStatement.setString(2, user.getName());
            preparedStatement.execute();
        }
        catch (Exception e)
        {
            System.out.println("Failed to add user to users table: " + e.getMessage());
        }
    }

    public boolean userExists(User user)
    {
        try(PreparedStatement preparedStatement = dataBaseContext.con.prepareStatement("SELECT * FROM Users WHERE id = ?"))
        {
            preparedStatement.setString(1, user.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }
        catch (Exception e)
        {
            System.out.println( "Failed to get user out of users table with user name: " + e.getMessage());
        }
        return false;
    }

    public String getTeamName(User user)
    {
        try (PreparedStatement preparedStatement = dataBaseContext.con.prepareStatement("SELECT teamName FROM Users WHERE id = ?"))
        {
            preparedStatement.setString(1, user.getId());
            try (ResultSet resultSet = preparedStatement.executeQuery())
            {
                if (resultSet.next())
                {
                   return resultSet.getString("teamName");
                }
            }
        }
        catch (Exception e)
        {
            System.out.println( "Failed to get teamId out of users table: " + e.getMessage());
        }
        return null;
    }

    public void updateUserTeam(String userId, String teamName)
    {
        try(PreparedStatement preparedStatement = dataBaseContext.con.prepareStatement("UPDATE Users SET teamName = ? WHERE id = ?"))
        {
            preparedStatement.setString(1, teamName);
            preparedStatement.setString(2, userId);
            preparedStatement.executeUpdate();
        }
        catch (Exception e)
        {
            System.out.println("Failed to update teamId in users table with userId: " + e.getMessage());
        }
    }

    public void updateUserTeamWithUserName(String userName, String teamName)
    {
        try(PreparedStatement preparedStatement = dataBaseContext.con.prepareStatement("UPDATE Users SET teamName = ? WHERE name = ?"))
        {
            preparedStatement.setString(1, teamName);
            preparedStatement.setString(2, userName);
            preparedStatement.executeUpdate();
        }
        catch (Exception e)
        {
            System.out.println("Failed to update teamId in users table with userName: " + e.getMessage());
        }
    }

    public boolean hasTeam(User user)
    {
        try (PreparedStatement preparedStatement = dataBaseContext.con.prepareStatement("SELECT teamName FROM Users WHERE id = ?"))
        {
            preparedStatement.setString(1, user.getId());
            try (ResultSet resultSet = preparedStatement.executeQuery())
            {
                if (resultSet.next())
                {
                    String teamName = resultSet.getString("teamName");
                    return teamName != null;
                }
            }
        }
        catch (Exception e)
        {
            System.out.println("Failed check if players has a team in users table: " + e.getMessage());
        }
        return false;
    }
}
