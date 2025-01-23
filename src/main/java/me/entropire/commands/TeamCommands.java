package me.entropire.commands;

import me.entropire.Invitor;
import me.entropire.Main;
import me.entropire.TeamEditor;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class TeamCommands extends ListenerAdapter
{
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (!event.getName().equalsIgnoreCase("team")) return;

        if(!Main.userDatabase.userExists(event.getUser()))
        {
            Main.userDatabase.addUser(event.getUser());
        }

        String subcommand = event.getSubcommandName();
        if (subcommand == null) {
            event.reply("No subcommand provided!").setEphemeral(true).queue();
            return;
        }

        System.out.println("Command executed: " + event.getUser().getName() + " : " + event.getSubcommandName());

        switch (subcommand) {
            case "create":
                String teamName = event.getOption("name") != null ? event.getOption("name").getAsString() : "Unnamed Team";
                TeamEditor.createTeam(event, teamName);
                break;
            case "delete":
                TeamEditor.deleteTeam(event);
                break;
            case "leave":
                TeamEditor.leaveTeam(event);
                break;
            case "invite":
                User invitedUser = event.getOption("participent").getAsUser();
                if(invitedUser != event.getUser() && !invitedUser.isBot())
                {
                    Invitor.inviteUserToTeam(event, invitedUser);
                }
                break;
            default:
                event.reply("Unknown subcommand!").setEphemeral(true).queue();
                break;
        }
    }
}
