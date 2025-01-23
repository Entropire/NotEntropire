package me.entropire;

import me.entropire.objects.Team;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.util.ArrayList;

public class TeamEditor
{
    public static void createTeam(SlashCommandInteractionEvent event, String teamName)
    {
        if(Main.userDatabase.hasTeam(event.getUser()))
        {
            event.reply("You are already in a team!").setEphemeral(true).queue();
            return;
        }

        teamName = teamName.toLowerCase();

        if(teamName.matches(".*[^a-zA-Z].*"))
        {
            event.reply("special characters are not allowed in an team names!").setEphemeral(true).queue();
            return;
        }

        if(Main.teamDatabase.teamExistsByName(teamName))
        {
            event.reply("The name " + teamName + " is already in use by another team!").setEphemeral(true).queue();
            return;
        }

        Role role = Main.guild.createRole()
                .setName(teamName)
                .setColor(Color.GRAY)
                .setMentionable(false)
                .setHoisted(false)
                .setPermissions()
                .complete();

        ArrayList<String> members = new ArrayList<>();
        members.add(event.getUser().getName());
        Team team = new Team(teamName, role.getId(), event.getUser().getId(), members);

        Main.teamDatabase.addTeam(team);
        Main.userDatabase.updateUserTeam(event.getUser().getId(), teamName);

        Main.guild.addRoleToMember(event.getUser(), role).queue();

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("New team: " + teamName)
                .setDescription("You have created a new team!");

        System.out.println("New team created with the name: " + teamName + ", Owner: " + event.getUser().getName());
        event.replyEmbeds(embed.build()).setEphemeral(true).queue();
    }

    public static void deleteTeam(SlashCommandInteractionEvent event)
    {
        if(!Main.userDatabase.hasTeam(event.getUser()))
        {
            event.reply("You must be in a team to preform this action!").setEphemeral(true).queue();
            return;
        }

        String teamName = Main.userDatabase.getTeamName(event.getUser());
        Team team = Main.teamDatabase.getTeamDataByName(teamName);

        if(team == null)
        {
            event.reply("Your team data could not be found!").setEphemeral(true).queue();
            return;
        }

        if(!team.getOwner().equals(event.getUser().getId()))
        {
            event.reply("You must be the owner of the team to preform this action!").setEphemeral(true).queue();
            return;
        }

        ArrayList<String> members = team.getMembers();
        for (String member : members)
        {
            Main.userDatabase.updateUserTeamWithUserName(member, null);
        }
        Main.teamDatabase.deleteTeam(teamName);

        Role role = Main.guild.getRoleById(team.getRoleId());
        if(role != null)
        {
            role.delete().queue();
        }

        event.reply("You have deleted your Team!").setEphemeral(true).queue();
    }

    public static void leaveTeam(SlashCommandInteractionEvent event)
    {
        if(!Main.userDatabase.hasTeam(event.getUser()))
        {
            event.reply("You must be in a team to preform this action!").setEphemeral(true).queue();
            return;
        }

        String teamName = Main.userDatabase.getTeamName(event.getUser());
        Team team = Main.teamDatabase.getTeamDataByName(teamName);

        if(team.getOwner().equals(event.getUser().getId()))
        {
            event.reply("You can not preform this action as owner of the team!").setEphemeral(true).queue();
            return;
        }

        Main.teamDatabase.updateTeamMembers(teamName, event.getUser().getId(), false);
        Main.userDatabase.updateUserTeam(event.getId(), null);

        Role role = event.getGuild().getRoleById(team.getRoleId());
        if(role != null)
        {
            Main.guild.removeRoleFromMember(event.getUser(), role).queue();
        }

        event.reply("You have left your team").setEphemeral(true).queue();
    }
}
