package codes.flappy.RCCAssistance.command.verification;

import codes.flappy.RCCAssistance.ResponseEmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Objects;
import java.util.logging.Logger;

public class VerificationListener extends ListenerAdapter {
    private static long verifRoleId = 0L;

    protected static void setVerifRoleId(long l) {
        verifRoleId = l;
        try(DataOutputStream dout = new DataOutputStream(new FileOutputStream("verification.dat"))) {
            dout.writeLong(l);
        } catch(FileNotFoundException ex) {
            Logger.getLogger("VerificationListener").warning("Could not write to verification.dat file (FileNotFoundException): "+ex.getMessage());
        } catch(IOException ex) {
            Logger.getLogger("VerificationListener").warning("Could not write to verification.dat file (IOException): "+ex.getMessage());
        }
    }

    public VerificationListener() {
        // try to load values from verification.dat
        try(DataInputStream in = new DataInputStream(new FileInputStream("verification.dat"))) {
            verifRoleId = in.readLong();
            Logger.getLogger("VerificationListener").info("Read long from verification.dat: "+in.readLong());
        } catch(FileNotFoundException ignored) {}
        catch(IOException ex) {
            Logger.getLogger("VerificationListener").warning("Could not load verification.dat file: " + ex.getMessage());
        }
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
