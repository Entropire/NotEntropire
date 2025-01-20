package me.entropire;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class Invitor
{
    public static void sendDMWithButtons(SlashCommandInteractionEvent event, User user)
    {
        user.openPrivateChannel().queue(channel -> {
            channel.sendMessage("Here are some buttons:")
                    .setActionRow(
                            Button.primary("Accept", "Accept"),
                            Button.secondary("Reject", "Reject")
                    ).queue(message -> {
                        // Listen for button interactions
                        message.getJDA().addEventListener(new ListenerAdapter() {
                            @Override
                            public void onButtonInteraction(ButtonInteractionEvent event) {
                                // Check if the interaction matches the buttons
                                if (event.getComponentId().equals("Accept") || event.getComponentId().equals("Reject")) {
                                    // Acknowledge the button click
                                    event.deferEdit().queue();

                                    // Remove buttons by editing the message to clear action rows
                                    event.getMessage().editMessage(event.getMessage().getContentRaw()) // Keep the original content
                                            .setComponents() // Clears all components (removes buttons)
                                            .queue();
                                }
                            }
                        });
                    });
        });
            event.reply("Invitation send to " + user.getName()).setEphemeral(true).queue();
    }
}
