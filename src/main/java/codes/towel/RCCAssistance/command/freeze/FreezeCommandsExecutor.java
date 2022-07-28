package codes.towel.RCCAssistance.command.freeze;

import codes.towel.RCCAssistance.ResponseEmbedBuilder;
import codes.towel.RCCAssistance.command.CommandExecutor;
import codes.towel.RCCAssistance.command.CommandMapper;
import codes.towel.RCCAssistance.command.UnknownCommandException;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class FreezeCommandsExecutor extends CommandExecutor {
    public void onSlashCommand(SlashCommandInteractionEvent e) throws UnknownCommandException {
        if (e.getMember().hasPermission(Permission.ADMINISTRATOR)) {
            switch (e.getName()) {
                case "freeze" -> {
                    CommandMapper.setFrozen(true);
                    e.replyEmbeds(ResponseEmbedBuilder.successResponseEmbedBuilder()
                            .setDescription("I've frozen all commands.").build()).setEphemeral(true).queue();
                }
                case "unfreeze" -> {
                    CommandMapper.setFrozen(false);
                    e.replyEmbeds(ResponseEmbedBuilder.successResponseEmbedBuilder()
                            .setDescription("I've unfrozen all commands.").build()).setEphemeral(true).queue();
                }
                default -> throw new UnknownCommandException("Unknown command in " + getClass().toString());
            }
        } else {
            e.replyEmbeds(ResponseEmbedBuilder.permErrorResponseEmbedBuilder()
                    .setFooter("Lacking: ADMINISTRATOR").build()).setEphemeral(true).queue();
        }
    }
}
