package me.entropire;

import me.entropire.commands.Interaction;
import me.entropire.commands.TeamCommands;
import me.entropire.database.DataBaseContext;
import me.entropire.database.TeamDatabase;
import me.entropire.database.UserDatabase;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main
{
    public static TeamDatabase teamDatabase;
    public static UserDatabase userDatabase;
    public static JDA jda;

    public static Guild guild;

    public static void main(String[] args)
    {
        System.out.println("Hello World!");

        try
        {
            String projectDir = System.getProperty("user.dir");
            Files.createDirectories(Path.of(projectDir + File.separator + "data"));
            DataBaseContext dataBaseContext = new DataBaseContext(projectDir + File.separator + "data" + File.separator + "NotEntropire.db");
            teamDatabase = new TeamDatabase(dataBaseContext);
            userDatabase = new UserDatabase(dataBaseContext);

            jda = JDABuilder.createDefault("MTMxNjA0ODc3NDU5ODEwMzA5MA.G57cUE.DLSTCNR4VvsM8JGRAVHC4L6PYsz-ymSYTzgUHU")
                    .addEventListeners(new TeamCommands())
                    .addEventListeners(new Interaction())
                    .build();

            jda.awaitReady();
            System.out.println("Bot is ready!");

            guild = jda.getGuildById("1316045787557269647");
            if (guild != null)
            {
                System.out.println("Guild found!");
                guild.upsertCommand(
                        Commands.slash("team", "Manage your team")
                                .addSubcommands(
                                        new SubcommandData("create", "Create a new team")
                                                .addOption(OptionType.STRING, "name", "The name of the team", true)
                                                .addOption(OptionType.USER, "teammate1", "Your first team mate", false)
                                                .addOption(OptionType.USER, "teammate2", "Your second team mate", false),
                                        new SubcommandData("delete", "Delete your team"),
                                        new SubcommandData("leave", "Leave your team"),
                                        new SubcommandData("invite", "Invite player to your team")
                                                .addOption(OptionType.USER, "participent", "Participent you want to invite", true)
                                )
                ).queue(
                        success -> System.out.println("Command added successfully!"),
                        failure -> {
                            System.err.println("Failed to add command: " + failure.getMessage());
                            failure.printStackTrace();
                        }
                );

            }
            else
            {
                System.err.println("Guild not found!");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}