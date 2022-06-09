package codes.flappy.RCCAssistance;

import codes.flappy.RCCAssistance.command.CommandListener;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.util.logging.Logger;

public class BotLoader extends ListenerAdapter {
    private static final Logger logger = Logger.getLogger("BotLoader");

    public static void main(String[] args) {
        logger.info("Bot is starting...");

        JDABuilder builder = JDABuilder.create(System.getenv("RCC_BOT_TOKEN"), GatewayIntent.GUILD_MESSAGES)
                .addEventListeners(new BotLoader(), new CommandListener());
                //.setActivity(Activity.listening("/help or ping"));

        try {
            builder.build();
        } catch(LoginException ex) {
            logger.severe("LoginException when building bot: "+ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }

        logger.info("Bot is built.");
    }

    public void onReady(@NotNull ReadyEvent e) {
        logger.info("Registering commands...");

        // register guild commands
        // these commands will only be registered in RCC Testing
        // TODO: Change this before adding to main server
        e.getJDA().getGuildById(964965253458837645L).updateCommands().addCommands(
                Commands.slash("help", "Stop it. Get some help."),

                // BOT CONTROLS (ADMIN ONLY)
                Commands.slash("freeze", "Freeze the bot, blocking all commands. (admin only)"),
                Commands.slash("unfreeze", "Unfreeze the bot, unblocking all commands. (admin only)"),
                // END BOT CONTROLS

                // TODO VERIFICATION COMMAND (NOT A COMMAND, USE INTERACTION BUTTON)
                //Commands.slash("verify", "Verify that you understand the rules. Required to access the server."),
                Commands.slash("verify_channel", "Set the verification channel.")
                        .addOption(OptionType.CHANNEL, "channel", "The channel for the button", true),
                // END VERIFICATION COMMAND


                // TODO LOCK COMMAND (MOD ONLY)
                Commands.slash("lock", "Lock a channel.")
                        .addOption(OptionType.CHANNEL, "channel", "The channel to lock", true)
                        .addOption(OptionType.STRING, "reason", "The reason the channel is locked"),


                // REACTION ROLE COMMANDS

                // TODO reaction role creation command (mod only)
                Commands.slash("reaction_role_create", "Create a reaction role.")
                        .addOption(OptionType.STRING, "embed_title", "Set the embed title", true)
                        .addOption(OptionType.STRING, "embed_description", "Set the embed description", true)
                        .addOption(OptionType.CHANNEL, "channel", "The channel to send the embed", true)
                        .addOption(OptionType.MENTIONABLE, "role", "The role to add", true)
                        .addOption(OptionType.STRING, "emoji", "The emoji to react with", true),
                // TODO reaction role deletion command (mod only)
                Commands.slash("reaction_role_delete", "Delete a reaction role.")
                        .addOption(OptionType.MENTIONABLE, "role", "The reaction role to delete (will not delete the role)", true),

                // END REACTION ROLE COMMANDS


                // WARN COMMANDS (ALL MOD ONLY)

                // TODO warn command
                Commands.slash("warn", "Warn a member.")
                        .addOption(OptionType.USER, "member", "The member to warn", true)
                        .addOption(OptionType.STRING, "reason", "The reason you are warning the user"),
                // TODO unwarn command
                Commands.slash("unwarn", "Unwarn a member.")
                        .addOption(OptionType.USER, "member", "The member to unwarn", true),
                // TODO view warns command
                Commands.slash("view_warns", "View the warns of a member, or the last 5 warns.")
                        .addOption(OptionType.USER, "member", "The member to view"),

                // END WARN COMMANDS


                // TICKET COMMANDS

                // TODO ticket command (NOT A COMMAND USE INTERACTION BUTTON)
                //Commands.slash("ticket open", "Open a new ticket to talk with a moderator."),
                // TODO ticket button channel command (mod only)
                Commands.slash("ticket_button_channel", "Set the channel for the ticket open interaction button")
                        .addOption(OptionType.CHANNEL, "channel", "The channel", true),
                // TODO close ticket command (mod only)
                Commands.slash("ticket_close", "Close a ticket with a member.")
                        .addOption(OptionType.STRING, "id", "The ticket id", true),
                // TODO claim ticket (mod only)
                Commands.slash("ticket_claim", "Take ownership of a ticket.")
                        .addOption(OptionType.STRING, "id", "The ticket id", true),
                // TODO reclaim ticket (mod only)
                Commands.slash("ticket_reclaim", "Reset ownership of a ticket")
                        .addOption(OptionType.STRING, "id", "The ticket id", true),
                // TODO add mod to ticket (mod only)
                Commands.slash("ticket_add", "Add a moderator to a ticket you own.")
                        .addOption(OptionType.STRING, "id", "The ticket id", true)
                        .addOption(OptionType.USER, "moderator", "The moderator who you are adding to the ticket", true),
                // TODO remove mod from ticket (mod only)
                Commands.slash("ticket_remove", "Remove a moderator from a ticket you own.")
                        .addOption(OptionType.STRING, "id", "The ticket id", true)
                        .addOption(OptionType.USER, "moderator", "The moderator who you are removing from the ticket", true),

                // END TICKET COMMANDS


                // TODO STATUS COMMANDS (ALL MOD ONLY)

                Commands.slash("status_watching", "Change the bot's status to watching.")
                        .addOption(OptionType.STRING, "content", "What is the bot watching?", true),
                Commands.slash("status_listening", "Change the bot's status to playing.")
                        .addOption(OptionType.STRING, "content", "What is the bot listening to?", true),
                Commands.slash("status_playing", "Change the bot's status to listening.")
                        .addOption(OptionType.STRING, "content", "What is the bot playing?", true)

                // END STATUS COMMANDS

        ).complete();

        logger.info("Commands registered.");
        logger.info("Bot is ready.");
    }
}
