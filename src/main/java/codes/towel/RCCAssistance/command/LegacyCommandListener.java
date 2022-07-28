package codes.towel.RCCAssistance.command;

import codes.towel.RCCAssistance.ResponseEmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class LegacyCommandListener extends ListenerAdapter {
    public void onMessageReceived(MessageReceivedEvent e) {
        if (e.getMessage().getMentionedUsers().contains(e.getJDA().getSelfUser())) {
            e.getMessage().replyEmbeds(ResponseEmbedBuilder.responseEmbedBuilder()
                    .setTitle("Need some help?")
                    .setDescription("Take a look at http://rcc-assistance.docs.flappy.codes").build()).queue();
        }
    }
}
