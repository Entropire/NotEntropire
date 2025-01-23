package me.entropire.database;

import me.entropire.objects.Team;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;

public class TeamDatabase
{
    private DataBaseContext dataBaseContext;

    public TeamDatabase(DataBaseContext dataBaseContext)
    {
        this.dataBaseContext = dataBaseContext;

        try (Statement statement = dataBaseContext.con.createStatement())
        {
            statement.execute("""
                CREATE TABLE IF NOT EXISTS Teams (
                    name TEXT PRIMARY KEY NOT NULL,
                    roleId TEXT NOT NULL,
                    owner TEXT NOT NULL,
                    members TEXT NOT NULL
                )
                """);
        }
        catch (Exception e)
        {
            System.out.println("Failed to create/load Teams table in database: " + e.getMessage());
        }
    }

    public void addTeam(Team team)
    {
        try (PreparedStatement preparedStatement = dataBaseContext.con.prepareStatement("INSERT INTO Teams (name, roleId, owner, members) VALUES (?, ?, ?, ?)"))
        {
            preparedStatement.setString(1, team.getName());
            preparedStatement.setString(2, team.getRoleId());
            preparedStatement.setString(3, team.getOwner());
            preparedStatement.setString(4, String.join(",", team.getMembers()));
            preparedStatement.execute();
        }
        catch (Exception e)
        {
            System.out.println("Failed to add faction to the factions table: " + e.getMessage());
        }
    }

    public boolean teamExistsByName(String teamName)
    {
        try(PreparedStatement preparedStatement = dataBaseContext.con.prepareStatement("SELECT * FROM Teams WHERE name = ?"))
        {
            preparedStatement.setString(1, teamName);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }
        catch (Exception e)
        {
            System.out.println("Failed te retrieve objects where name equals (factionName) in Teams table: " + e.getMessage());
            return true;
        }
    }

    public Team getTeamDataByName(String teamName)
    {
        try(PreparedStatement preparedStatement = dataBaseContext.con.prepareStatement("SELECT * FROM Teams WHERE name = ?"))
        {
            preparedStatement.setString(1, teamName);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next())
            {
                String name = resultSet.getString("name");
                String roleId = resultSet.getString("roleId");
                String owner = resultSet.getString("owner");

                String membersString = resultSet.getString("members");
                ArrayList<String> membersList = new ArrayList<>(Arrays.asList(membersString.split(",")));

                return new Team(name, roleId, owner, membersList);
            }
        }
        catch (Exception e)
        {
            System.out.println("Failed to get faction data out factions table with faction name: " + e.getMessage());
        }
        return null;
    }

    public void updateTeamMembers(String teamName, String member, Boolean add)
    {
        ArrayList<String> membersList;
        try(PreparedStatement preparedStatement = dataBaseContext.con.prepareStatement("SELECT members FROM Teams WHERE name = ?"))
        {
            preparedStatement.setString(1, String.valueOf(teamName));
            ResultSet resultSet = preparedStatement.executeQuery();
            membersList = new ArrayList<>(Arrays.asList(resultSet.getString("members").split(",")));

            if(add)
            {
                membersList.add(member);
            }
            else
            {
                membersList.remove(member);
            }

            try(PreparedStatement preparedStatement2 = dataBaseContext.con.prepareStatement("UPDATE Teams SET members = ? WHERE name = ?"))
            {
                preparedStatement2.setString(1, String.join(",", membersList));
                preparedStatement2.setString(2, String.valueOf(teamName));
                preparedStatement2.executeUpdate();
            }
            catch (Exception e)
            {
                System.out.println("Failed to update members of faction in factions table: " + e.getMessage());
            }
        }
        catch (Exception e)
        {
            System.out.println("Failed to get members of faction out factions table: " + e.getMessage());
        }
    }

    public void deleteTeam(String teamName)
    {
        try(PreparedStatement preparedStatement = dataBaseContext.con.prepareStatement("DELETE FROM Teams WHERE name = ?"))
        {
            preparedStatement.setString(1, String.valueOf(teamName));
            preparedStatement.executeUpdate();
        }
        catch (Exception e)
        {
            System.out.println("Failed to delete faction out of faction table: " + e.getMessage());
        }
    }
}
