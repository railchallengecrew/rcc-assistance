package codes.towel.RCCAssistance.command.status;

import codes.towel.RCCAssistance.ResponseEmbedBuilder;
import codes.towel.RCCAssistance.command.CommandExecutor;
import codes.towel.RCCAssistance.command.UnknownCommandException;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.Objects;
import java.util.logging.Logger;

public class StatusCommandsExecutor extends CommandExecutor {
    public void onSlashCommand(SlashCommandInteractionEvent e) throws UnknownCommandException {
        if (e.getMember().hasPermission(Permission.MANAGE_SERVER)) {
            try {
                switch (e.getName()) {
                    case "status_watching" -> {
                        e.getJDA().getPresence().setActivity(Activity.watching(Objects.requireNonNull(e.getOption("content")).getAsString()));
                    }
                    case "status_listening" -> {
                        e.getJDA().getPresence().setActivity(Activity.listening(Objects.requireNonNull(e.getOption("content")).getAsString()));
                    }
                    case "status_playing" -> {
                        e.getJDA().getPresence().setActivity(Activity.playing(Objects.requireNonNull(e.getOption("content")).getAsString()));
                    }
                    default -> {
                        throw new UnknownCommandException("Unknown command in " + getClass().toString());
                    }

                }
            } catch(NullPointerException ex) {
                Logger.getLogger("StatusCommandExecutor").warning(ex.getMessage());
                ex.printStackTrace();
                e.replyEmbeds(ResponseEmbedBuilder.errorResponseEmbedBuilder()
                        .setDescription("Try running the command again, or report a bug at www.github.com/railchallengecrew/rcc-assistance")
                        .setFooter("NullPointerException@StatusCommandExecutor:onSlashCommand").build()).queue();
            }

            e.replyEmbeds(ResponseEmbedBuilder.successResponseEmbedBuilder()
                    .setDescription("My status has been changed.").build()).queue();
        } else {
            e.replyEmbeds(ResponseEmbedBuilder.permErrorResponseEmbedBuilder()
                    .setFooter("Lacking: MANAGE_SERVER").build()).setEphemeral(true).queue();
        }
    }
}
