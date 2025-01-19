package me.entropire.commands;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Interaction extends ListenerAdapter
{
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String buttonId = event.getComponentId(); // Get the ID of the clicked button

        switch (buttonId) {
            case "button_1":
                event.reply("You clicked Button 1!").queue();
                // Add the code you want to execute for Button 1 here
                break;

            case "button_2":
                event.reply("You clicked Button 2!").queue();
                // Add the code you want to execute for Button 2 here
                break;

            default:
                event.reply("Unknown button clicked!").queue();
        }
    }
}
