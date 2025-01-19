package me.entropire.objects;

import java.util.ArrayList;

public class Team
{
    private int id;
    private String name;
    private String owner;
    private ArrayList<String> members;

    public Team(int id, String name, String owner, ArrayList<String> members)
    {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.members = members;
    }

    public int getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
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
