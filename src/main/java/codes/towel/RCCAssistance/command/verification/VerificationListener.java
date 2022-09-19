package codes.towel.RCCAssistance.command.verification;

import codes.towel.RCCAssistance.ResponseEmbedBuilder;
import codes.towel.RCCAssistance.data.StorageUtils;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.logging.Logger;

public class VerificationListener extends ListenerAdapter {
    private static Long verifRoleId = 0L;
    private static boolean verifRoleSetByEnv = false;
    public static boolean getVerifRoleSetByEnv() { return verifRoleSetByEnv; }

    protected static void setVerifRoleId(long l) {
        VerificationListener.verifRoleId = l;
        StorageUtils.setObject("VerificationListener", "Verification", "VerifRoleId", VerificationListener.verifRoleId);
    }

    public VerificationListener() {
        // first check environment variables
        if (System.getenv("VERIFY_ROLE_ID") != null) {
            verifRoleSetByEnv = true;
            verifRoleId = Long.parseLong(System.getenv("VERIFY_ROLE_ID"));
            Logger.getLogger("VerificationListener").info("Verification role set by environment");
        } else {
            verifRoleSetByEnv = false;
            // try to load values from verification.dat
            Logger.getLogger("VerificationListener").info("\n"+ StorageUtils.getObject("VerificationListener", "Verification", "VerifRoleId"));
            VerificationListener.verifRoleId = (Long) StorageUtils.getObject("VerificationListener", "Verification", "VerifRoleId");
        }

        Logger.getLogger(VerificationListener.class.getName()).info("Verification role id is set to "+verifRoleId);
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent e) {
        if (e.getComponentId().equals("verify")) {

            try {
                Objects.requireNonNull(e.getMember()).getGuild().addRoleToMember(
                        e.getMember(),
                        Objects.requireNonNull(
                                Objects.requireNonNull(
                                        e.getGuild()
                                ).getRoleById(verifRoleId)
                        )
                ).complete();
                e.replyEmbeds(ResponseEmbedBuilder.successResponseEmbedBuilder()
                        .setDescription(e.getMember().getAsMention()+", I gave you access to the rest of the server.").build()).setEphemeral(true).queue();
            } catch(NullPointerException ex) {
                e.replyEmbeds(ResponseEmbedBuilder.errorResponseEmbedBuilder()
                        .setDescription(e.getMember().getAsMention()+", I couldn't add you to the role.\nIf you are a server admin, please make sure that your verification role is configured.\n\n`Verification Role ID: "+verifRoleId+"`")
                        .setFooter("NullPointerException@VerifyListener:onButtonInteraction").build()).queue();
            } catch(HierarchyException ex) {
                e.replyEmbeds(ResponseEmbedBuilder.errorResponseEmbedBuilder()
                        .setDescription(e.getMember().getAsMention()+", I couldn't add you to the role because I can't modify your user as you have higher permissions than me!")
                        .setFooter("HierarchyException@VerifyListener:onButtonInteraction").build()).queue();
            }
        }
    }
}
