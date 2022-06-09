package codes.flappy.RCCAssistance.command;

import codes.flappy.RCCAssistance.ResponseEmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class CommandListener extends ListenerAdapter {
    private static boolean frozen = false;

    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent e) {
        if (frozen && !(e.getName().equals("freeze") || e.getName().equals("unfreeze"))) {
            e.replyEmbeds(ResponseEmbedBuilder.errorResponseEmbedBuilder()
                    .setTitle("Command Not Available")
                    .setDescription("The bot has been frozen by the server admins. All commands are disabled.").build()).queue();
            return;
        }

        switch(e.getName()) {
            case "freeze" -> {
                if (e.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                    frozen = true;
                    e.replyEmbeds(ResponseEmbedBuilder.successResponseEmbedBuilder().setDescription("I set frozen mode on.").build()).queue();
                } else e.replyEmbeds(ResponseEmbedBuilder.permErrorResponseEmbedBuilder()
                        .setFooter("Lacking permission Administrator").build()).queue();
            } case "unfreeze" -> {
                if (e.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                    frozen = false;
                    e.replyEmbeds(ResponseEmbedBuilder.successResponseEmbedBuilder().setDescription("I set frozen mode off.").build()).queue();
                } else {
                    e.replyEmbeds(ResponseEmbedBuilder.permErrorResponseEmbedBuilder()
                            .setFooter("Lacking permission Administrator").build()).queue();
                }
            } case "help" -> {
                e.replyEmbeds(ResponseEmbedBuilder.responseEmbedBuilder()
                        .setTitle("Need some help?")
                        .setDescription("Take a look at http://rcc-assistance.docs.flappy.codes").build()).queue();
            } default -> e.replyEmbeds(ResponseEmbedBuilder.errorResponseEmbedBuilder().
                    setTitle("Command Not Available")
                    .setDescription("This command has not been implemented yet.").build()).queue();
        }
    }

    public void onMessageReceived(MessageReceivedEvent e) {
        if (e.getMessage().getMentionedUsers().contains(e.getJDA().getSelfUser())) {
            e.getMessage().replyEmbeds(ResponseEmbedBuilder.responseEmbedBuilder()
                    .setTitle("Need some help?")
                    .setDescription("Take a look at http://rcc-assistance.docs.flappy.codes").build()).queue();
        }
    }
}
