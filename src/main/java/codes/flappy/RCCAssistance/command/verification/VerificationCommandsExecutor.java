package codes.flappy.RCCAssistance.command.verification;

import codes.flappy.RCCAssistance.ResponseEmbedBuilder;
import codes.flappy.RCCAssistance.command.CommandExecutor;
import codes.flappy.RCCAssistance.command.UnknownCommandException;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class VerificationCommandsExecutor extends CommandExecutor {
    public void onSlashCommand(SlashCommandInteractionEvent e) throws UnknownCommandException {
        //e.deferReply().setEphemeral(true).queue();

        if (e.getMember().hasPermission(Permission.ADMINISTRATOR)) {
            switch (e.getName()) {
                case "verify_channel" -> {
                    String ebTitle = "Verification";
                    String ebDesc = "To confirm you understand the rules, press the button below to gain access to the rest of the server.";
                    String actMsg = "Verify";

                    if (e.getOption("title")!=null) ebTitle = e.getOption("title").getAsString();
                    if (e.getOption("description")!=null) ebDesc = e.getOption("description").getAsString();
                    if (e.getOption("button_msg")!=null) actMsg = e.getOption("button_msg").getAsString();

                    e.getOption("channel").getAsTextChannel().sendMessageEmbeds(ResponseEmbedBuilder.responseEmbedBuilder()
                            .setTitle(ebTitle).setDescription(ebDesc).build())
                            .setActionRows(ActionRow.of(Button.success("verify", actMsg)))
                            .queue();

                    e.replyEmbeds(ResponseEmbedBuilder.successResponseEmbedBuilder()
                            .setDescription("I've added the verification button.").build()).setEphemeral(true).complete();

                }
                case "verify_role" -> {
                    VerificationListener.setVerifRoleId(Long.parseLong(e.getOption("role").getAsRole().getId()));
                    e.replyEmbeds(ResponseEmbedBuilder.successResponseEmbedBuilder().setDescription("I've added the verification role.").build()).setEphemeral(true).queue();
                }
                default -> throw new UnknownCommandException("Unknown command in " + getClass().toString());
            }
        } else {
            e.replyEmbeds(ResponseEmbedBuilder.permErrorResponseEmbedBuilder()
                    .setFooter("Lacking: ADMINISTRATOR").build()).setEphemeral(true).queue();
        }
    }
}