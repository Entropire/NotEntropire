package me.entropire.objects;

import java.util.ArrayList;

public class Team
{
    private String name;
    private String roleId;
    private String owner;
    private ArrayList<String> members;

    public Team(String name, String roleId, String owner, ArrayList<String> members)
    {
        this.name = name;
        this.roleId = roleId;
        this.owner = owner;
        this.members = members;
    }

    public String getName()
    {
        return name;
    }

    public String getRoleId()
    {
        return roleId;
    }

    public String getOwner()
    {
        return owner;
    }

    public ArrayList<String> getMembers()
    {
        return members;
    }
}
