package me.entropire;

import me.entropire.objects.Invite;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Invitor
{
    public static void inviteUserToTeam(SlashCommandInteractionEvent event, User user)
    {
        if(!Main.userDatabase.hasTeam(event.getUser()))
        {
            event.reply("You must be in a team to preform this action!").setEphemeral(true).queue();
            return;
        }

        if(Main.userDatabase.hasTeam(user))
        {
            event.reply("This user is already part of a team!").setEphemeral(true).queue();
            return;
        }

        int teamId = Main.userDatabase.getTeamId(event.getUser());

        user.openPrivateChannel().queue(channel -> {
            channel.sendMessage(event.getUser().getName() + " has invited you to there team!")
                    .setActionRow(
                            Button.primary("Accept", "Accept"),
                            Button.secondary("Decline", "Decline")
                    ).queue(message -> {

                        Invite invite = new Invite(event.getUser(), user, teamId, System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5));
                        Main.invites.put(message.getId(), invite);

                        message.editMessage(event.getUser().getName() + " has invited you to their team! (This invitation has expired)")
                                .setActionRow()
                                .queueAfter(5, TimeUnit.MINUTES);
                    });
        });
            event.reply("Invitation send to " + user.getName()).setEphemeral(true).queue();
    }

    public void Accept(SlashCommandInteractionEvent event)
    {
        if(Main.userDatabase.hasTeam(event.getUser()))
        {
            event.reply("You are already in a team!").setEphemeral(true).queue();
            return;
        }

        if(Main.invites.containsKey(event.))
        {

        }
    }
}
