package me.entropire;

import me.entropire.commands.Interaction;
import me.entropire.commands.TeamCommands;
import me.entropire.database.DataBaseContext;
import me.entropire.database.TeamDatabase;
import me.entropire.database.UserDatabase;
import me.entropire.objects.Invite;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main
{
    public static TeamDatabase teamDatabase;
    public static UserDatabase userDatabase;
    public static final Map<String, Invite> invites = new HashMap<>();

    public static JDA jda;

    public static Guild guild;

    public static void main(String[] args)
    {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            long currentTime = System.currentTimeMillis();
            invites.entrySet().removeIf(entry -> entry.getValue().expireDate() < currentTime);
        }, 0, 5, TimeUnit.MINUTES);

        try
        {
            String projectDir = System.getProperty("user.dir");
            Files.createDirectories(Path.of(projectDir + File.separator + "data"));
            DataBaseContext dataBaseContext = new DataBaseContext(projectDir + File.separator + "data" + File.separator + "NotEntropire.db");
            teamDatabase = new TeamDatabase(dataBaseContext);
            userDatabase = new UserDatabase(dataBaseContext);

            jda = JDABuilder.createDefault("")
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
                                                .addOption(OptionType.STRING, "name", "The name of the team", true),
                                        new SubcommandData("delete", "Delete your team"),
                                        new SubcommandData("leave", "Leave your team"),
                                        new SubcommandData("info", "Info your team"),
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