package me.entropire.commands;

import me.entropire.Invitor;
import me.entropire.Main;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Interaction extends ListenerAdapter
{
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if(!Main.userDatabase.userExists(event.getUser()))
        {
            Main.userDatabase.addUser(event.getUser());
        }

        String buttonId = event.getComponentId();
        if (!event.getComponentId().equals("Accept") && !event.getComponentId().equals("Decline"))
        {
            return;
        }

        event.getMessage().editMessage(event.getMessage().getContentRaw() + (buttonId.equalsIgnoreCase("accept") ? " (accepted)" : " (declined)")).queue();
        event.getMessage().editMessageComponents().queue();

        event.deferEdit().queue();

        switch (buttonId)
        {
            case "Accept":
                Invitor.Accept(event);
                break;

            case "Decline":
                Main.invites.remove(event.getUser());
                break;
        }
    }
}
