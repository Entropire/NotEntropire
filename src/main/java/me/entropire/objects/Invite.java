package me.entropire.objects;

import net.dv8tion.jda.api.entities.User;

public record Invite(User sender, User receiver, String teamName, long expireDate){}