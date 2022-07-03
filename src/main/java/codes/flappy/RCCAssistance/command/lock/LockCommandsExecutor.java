package codes.flappy.RCCAssistance.command.lock;

import codes.flappy.RCCAssistance.ResponseEmbedBuilder;
import codes.flappy.RCCAssistance.command.CommandExecutor;
import codes.flappy.RCCAssistance.command.UnknownCommandException;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import javax.management.openmbean.KeyAlreadyExistsException;

public class LockCommandsExecutor extends CommandExecutor {
    public void onSlashCommand(SlashCommandInteractionEvent e) throws UnknownCommandException {
        switch(e.getName()) {
            case "lock" -> {
                if (e.getMember().hasPermission(Permission.MANAGE_CHANNEL)) {
                    try {
                        LockListener.addLockedChannel(e.getOption("channel").getAsTextChannel());
                    } catch(KeyAlreadyExistsException ex) {
                        e.replyEmbeds(ResponseEmbedBuilder.errorResponseEmbedBuilder()
                                .setTitle("Channel is already locked.")
                                .setDescription("This channel is already locked.")
                                .setFooter("KeyAlreadyExistsException@LockCommandsExecutor:onSlashCommand").build()).setEphemeral(true).queue();
                        return;
                    }

                    e.getOption("channel").getAsTextChannel().sendMessageEmbeds(ResponseEmbedBuilder.responseEmbedBuilder()
                            .setTitle("Channel Locked")
                            .setDescription("This channel has been locked by "+e.getMember().getAsMention()+". Only moderators and admins can talk.").build()).queue();

                    e.replyEmbeds(ResponseEmbedBuilder.successResponseEmbedBuilder()
                                    .setDescription("I've locked the channel "+e.getOption("channel").getAsTextChannel().getAsMention()+".").build())
                            .setEphemeral(true).queue();
                } else {
                    e.replyEmbeds(ResponseEmbedBuilder.permErrorResponseEmbedBuilder()
                            .setFooter("Lacking: MANAGE_CHANNEL").build()).setEphemeral(true).queue();
                }
            } case "unlock" -> {
                LockListener.delLockedChannel(e.getOption("channel").getAsTextChannel());

                e.getOption("channel").getAsTextChannel().sendMessageEmbeds(ResponseEmbedBuilder.responseEmbedBuilder()
                        .setTitle("Channel Unlocked")
                        .setDescription("This channel has been unlocked by "+e.getMember().getAsMention()+".").build()).queue();

                e.replyEmbeds(ResponseEmbedBuilder.successResponseEmbedBuilder()
                        .setDescription("I've unlocked the channel "+e.getOption("channel").getAsTextChannel().getAsMention()+".").build()).setEphemeral(true).queue();
            } default -> throw new UnknownCommandException("Unknown command: " + e.getName());
        }

    }
}
