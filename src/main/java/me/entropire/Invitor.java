package me.entropire;

import me.entropire.objects.Invite;
import me.entropire.objects.Team;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.concurrent.TimeUnit;

public class Invitor
{
    public static void inviteUserToTeam(SlashCommandInteractionEvent event, User invitedUser)
    {
        if(!Main.userDatabase.hasTeam(event.getUser()))
        {
            event.reply("You must be in a team to preform this action!").setEphemeral(true).queue();
            return;
        }

        if(Main.userDatabase.hasTeam(invitedUser))
        {
            event.reply("This user is already part of a team!").setEphemeral(true).queue();
            return;
        }

        String teamName = Main.userDatabase.getTeamName(event.getUser());

        invitedUser.openPrivateChannel().queue(channel -> {
            channel.sendMessage(event.getUser().getName() + " has invited you to there team!")
                    .setActionRow(
                            Button.primary("Accept", "Accept"),
                            Button.secondary("Decline", "Decline")
                    ).queue(message -> {

                        Invite invite = new Invite(event.getUser(), invitedUser, teamName, System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5));
                        Main.invites.put(message.getId(), invite);

                        message.editMessageComponents().queueAfter(5, TimeUnit.MINUTES);
                        message.editMessage(event.getUser().getName() + " has invited you to their team! (This invitation has expired)").queueAfter(5, TimeUnit.MINUTES);
                    });
        },
                failure -> {
                    // Failed to open the private channel (likely due to DMs being disabled or the user blocking the bot)
                    if (failure instanceof ErrorResponseException) {
                        ErrorResponseException exception = (ErrorResponseException) failure;
                        if (exception.getErrorCode() == 50007) {
                            event.reply("Could not send a direct message to " + invitedUser.getName() + ". They may have DMs disabled or have blocked the bot.").setEphemeral(true).queue();
                        } else {
                            event.reply("An unexpected error occurred while trying to send the invitation to " + invitedUser.getName() + ".").setEphemeral(true).queue();
                        }
                    } else {
                        event.reply("An unexpected error occurred while trying to send the invitation to " + invitedUser.getName() + ".").setEphemeral(true).queue();
                    }
                }
        );
            event.reply("Invitation send to " + invitedUser.getName()).setEphemeral(true).queue();
    }

    public static void Accept(ButtonInteractionEvent event)
    {
        if(Main.userDatabase.hasTeam(event.getUser()))
        {
            event.getUser().openPrivateChannel().queue(channel ->
                channel.sendMessage("You are already in a team!").queue());
            return;
        }

        if(!Main.invites.containsKey(event.getMessageId()))
        {
            event.getUser().openPrivateChannel().queue(channel ->
                    channel.sendMessage("You dont have a open invitation!").queue());
            return;
        }

        Invite invite = Main.invites.get(event.getMessageId());
        Team team = Main.teamDatabase.getTeamDataByName(invite.teamName());
        if(team == null)
        {
            event.getUser().openPrivateChannel().queue(channel ->
                    channel.sendMessage("This team does not exist!").queue());
            return;
        }

        if(team.getMembers().size() >= 3)
        {
            event.getUser().openPrivateChannel().queue(channel ->
                    channel.sendMessage("This team has reached there max size!").queue());
            return;
        }

        Main.teamDatabase.updateTeamMembers(team.getName(), event.getUser().getName(), true);
        Main.userDatabase.updateUserTeam(event.getUser().getId(), team.getName());
        Role role = Main.guild.getRoleById(team.getRoleId());
        if(role != null)
        {
            Main.guild.addRoleToMember(event.getUser(), role).queue();
        }
        event.getUser().openPrivateChannel().queue(channel ->
                channel.sendMessage("You have join the team!").queue());

        System.out.println("User " + event.getUser().getName() + " has joined the team " + team.getName());

        Main.invites.remove(event.getMessageId());
    }
}
