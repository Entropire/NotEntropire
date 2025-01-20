package me.entropire.commands;

import me.entropire.Main;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Interaction extends ListenerAdapter
{
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String buttonId = event.getComponentId();
        if (!event.getComponentId().equals("Accept") && !event.getComponentId().equals("Decline"))
        {
            return;
        }

        event.getMessage().editMessage(event.getMessage().getContentRaw() + (buttonId == "Accept" ? " (accepted)" : " (declined)"))
                .setComponents()
                .queue();

        event.deferEdit().queue();

        switch (buttonId) {
            case "Accept":

                break;

            case "Decline":
                Main.invites.remove(event.getUser());
                break;
        }



    }
}
