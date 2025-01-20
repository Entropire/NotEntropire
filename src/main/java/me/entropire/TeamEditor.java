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
            event.reply("The name " + teamName + " is already in use by another faction!").setEphemeral(true).queue();
            return;
        }

        Team team = new Team(0, teamName, event.getUser().getId());
        Main.teamDatabase.addTeam(team);
        team = Main.teamDatabase.getTeamDataByName(teamName);

        Main.guild.createRole()
                .setName(teamName)
                .setColor(Color.GRAY)
                .setMentionable(false)
                .setHoisted(false)
                .setPermissions()
                .complete();

        //Role role = event.getGuild().getRolesByName(teamName, true).stream().findFirst().orElse(null);

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("New team: " + teamName)
                .setDescription("You have created a new team!");

        event.replyEmbeds(embed.build()).setEphemeral(true).queue();
    }

    public static void deleteTeam(SlashCommandInteractionEvent event)
    {
        if(!Main.userDatabase.hasTeam(event.getUser()))
        {
            event.reply("You must be in a team to preform this action!").setEphemeral(true).queue();
            return;
        }

        int teamId = Main.userDatabase.getTeamId(event.getUser());
        Team team = Main.teamDatabase.getTeamDataById(teamId);

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
            Main.userDatabase.updateUserTeam(member, 0);
        }
        Main.teamDatabase.deleteTeam(teamId);

        Role role = event.getGuild().getRolesByName(team.getName(), true).stream().findFirst().orElse(null);
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

        int teamId = Main.userDatabase.getTeamId(event.getUser());
        Team team = Main.teamDatabase.getTeamDataById(teamId);
        if(team.getOwner().equals(event.getUser().getId()))
        {
            event.reply("You can not preform this action as owner of the team!").setEphemeral(true).queue();
            return;
        }

        Main.teamDatabase.updateFactionMembers(teamId, event.getUser().getId(), false);
        Main.userDatabase.updateUserTeam(event.getId(), 0);

        Role role = event.getGuild().getRolesByName(team.getName(), true).stream().findFirst().orElse(null);
        Main.guild.removeRoleFromMember(event.getUser(), role);

        event.reply("You have left your team").setEphemeral(true).queue();
    }
}
