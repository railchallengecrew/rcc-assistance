package codes.flappy.RCCAssistance.command;

import codes.flappy.RCCAssistance.ResponseEmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Map;

public class CommandMapper extends ListenerAdapter {
    private Map<String, CommandExecutor> mappings;

    private static boolean frozen = false;
    public static void setFrozen(boolean b) { frozen=b; }

    public CommandMapper(Map<String, CommandExecutor> mappings) {
        this.mappings = mappings;
    }

    public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
        if (e.getName().equals("help")) {
            e.replyEmbeds(ResponseEmbedBuilder.responseEmbedBuilder()
                    .setTitle("Need some help?")
                    .setDescription("Take a look at http://rcc-assistance.docs.towel.codes").build()).setEphemeral(true).queue();
            return;
        }

        if (mappings.containsKey(e.getName())) {
            try {
                mappings.get(e.getName()).onSlashCommand(e);
            } catch(UnknownCommandException ex) {
                e.replyEmbeds(ResponseEmbedBuilder.errorResponseEmbedBuilder()
                        .setDescription("""
                                The command couldn't be handled correctly. Please report this bug as an issue at www.github.com/railchallengecrew/rcc-assistance

                                `No further information available.`""")
                        .setFooter("UnknownCommandException: " + ex.getMessage()).build()).queue();
            }
        } else {
            e.replyEmbeds(ResponseEmbedBuilder.errorResponseEmbedBuilder()
                    .setTitle("Command Not Available")
                    .setDescription("This command has not yet been implemented.").build()).queue();
        }
    }
}
