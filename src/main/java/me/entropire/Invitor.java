package me.entropire;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class Invitor
{
    public static void sendDMWithButtons(User user)
    {
        user.openPrivateChannel().queue(channel ->
        {
            channel.sendMessage("Here are some buttons:")
                    .setActionRow(
                            Button.primary("Accept", "Accept"),
                            Button.secondary("Reject", "Reject")
                    ).queue();
        });
    }
}
