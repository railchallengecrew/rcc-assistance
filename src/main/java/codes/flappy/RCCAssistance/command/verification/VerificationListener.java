package codes.flappy.RCCAssistance.command.verification;

import codes.flappy.RCCAssistance.ResponseEmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class VerificationListener extends ListenerAdapter {
    private static long verifRoleId = 0L;

    protected static void setVerifRoleId(long l) { verifRoleId = l; }


    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent e) {
        if (e.getComponentId().equals("verify")) {
            e.deferReply().queue();

            try {
                Objects.requireNonNull(e.getMember()).getGuild().addRoleToMember(
                        e.getMember(),
                        Objects.requireNonNull(
                                Objects.requireNonNull(
                                        e.getGuild()
                                ).getRoleById(verifRoleId)
                        )
                ).complete();
                e.getHook().sendMessageEmbeds(ResponseEmbedBuilder.successResponseEmbedBuilder()
                        .setDescription(e.getMember().getAsMention()+", I gave you access to the rest of the server.").build()).setEphemeral(true).queue();
            } catch(NullPointerException ex) {
                e.getHook().sendMessageEmbeds(ResponseEmbedBuilder.errorResponseEmbedBuilder()
                        .setDescription(e.getMember().getAsMention()+", I couldn't add you to the role.\nIf you are a server admin, please make sure that your verification role is configured.\n\n`Verification Role ID: "+verifRoleId+"`")
                        .setFooter("NullPointerException@VerifyListener:onButtonInteraction").build()).queue();
            } catch(HierarchyException ex) {
                e.getHook().sendMessageEmbeds(ResponseEmbedBuilder.errorResponseEmbedBuilder()
                        .setDescription(e.getMember().getAsMention()+", I couldn't add you to the role because I can't modify your user as you have higher permissions than me!")
                        .setFooter("HierarchyException@VerifyListener:onButtonInteraction").build()).queue();
            }
        }
    }
}
