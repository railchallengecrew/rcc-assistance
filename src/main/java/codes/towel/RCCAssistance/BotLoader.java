package codes.towel.RCCAssistance;

import codes.towel.RCCAssistance.command.CommandExecutor;
import codes.towel.RCCAssistance.command.CommandMapper;
import codes.towel.RCCAssistance.command.LegacyCommandListener;
import codes.towel.RCCAssistance.command.freeze.FreezeCommandsExecutor;
import codes.towel.RCCAssistance.command.lock.LockCommandsExecutor;
import codes.towel.RCCAssistance.command.lock.LockListener;
import codes.towel.RCCAssistance.command.status.StatusCommandsExecutor;
import codes.towel.RCCAssistance.command.verification.VerificationCommandsExecutor;
import codes.towel.RCCAssistance.command.verification.VerificationListener;
import codes.towel.RCCAssistance.data.StorageUtils;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.NotNull;
import sun.misc.Signal;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Logger;

public class BotLoader extends ListenerAdapter {
    private static final Logger logger = Logger.getLogger("BotLoader");

    private static JDA jda;

    private static final HashMap<String, CommandExecutor> commandMappings = new HashMap<>() {{
        put("freeze", new FreezeCommandsExecutor());
        put("unfreeze", new FreezeCommandsExecutor());

        put("verify_channel", new VerificationCommandsExecutor());
        put("verify_role", new VerificationCommandsExecutor());

        put("status_playing", new StatusCommandsExecutor());
        put("status_listening", new StatusCommandsExecutor());
        put("status_watching", new StatusCommandsExecutor());

        put("lock", new LockCommandsExecutor());
        put("unlock", new LockCommandsExecutor());
    }};


    private static void setupStorage() {
        if (System.getenv("RCC_DB_HOSTNAME")!=null) {
            logger.info("Storage mode set to REMOTE because RCC_DB_HOSTNAME is set in the system environment.");

            // if the required env variables are not set
            if (System.getenv("RCC_DB_USERNAME")==null) {
                logger.severe("RCC_DB_USERNAME is NOT set! Cannot connect to MongoDB without a username.");
                System.exit(1);
            } if (System.getenv("RCC_DB_PASSWORD")==null) {
                logger.severe("RCC_DB_PASSWORD is NOT set! Cannot connect to MongoDB without a password.");
                System.exit(1);
            } if (System.getenv("RCC_DB_NAME")==null) {
                logger.severe("RCC_DB_NAME is NOT set! Cannot connect to MongoDB without a DB name.");
                System.exit(1);
            }


            logger.info("Connecting to "+System.getenv("RCC_DB_HOSTNAME")+" as user "+System.getenv("RCC_DB_USERNAME")+"...");

            try {
                MongoClient dbClient = new MongoClient(new MongoClientURI(System.getenv("RCC_DB_HOSTNAME")));
                StorageUtils.setupDB(dbClient);
            }
            catch(UnknownHostException ex) {
                logger.severe("Could not connect MongoDB: unknown host!");
                logger.severe("UnknownHostException: "+ex.getMessage());
                ex.printStackTrace();
                System.exit(1);
            } finally {
                logger.info("DB storage set up OK!");
            }

        } else {
            logger.info("Storage mode set to LOCAL.");
            if (System.getenv("RCC_LOCAL_FILE")==null) {
                logger.severe("Could not use local storage because RCC_LOCAL_FILE is NOT set!");
                System.exit(1);
            } else {
                try {
                    StorageUtils.setupLocal(new File(System.getenv("RCC_LOCAL_FILE")));
                } catch(IOException ex) {
                    logger.severe("IOException while setting up local storage: "+ex.getMessage());
                    ex.printStackTrace();
                    System.exit(1);
                } finally {
                    logger.info("Local storage set up OK!");
                }
            }
        }
    }

    private static void setupBot() {
        JDABuilder builder = JDABuilder.create(System.getenv("RCC_BOT_TOKEN"), GatewayIntent.GUILD_MESSAGES)
                .disableCache(CacheFlag.ACTIVITY)
                .disableCache(CacheFlag.VOICE_STATE)
                .disableCache(CacheFlag.EMOTE)
                .disableCache(CacheFlag.CLIENT_STATUS)
                .disableCache(CacheFlag.ONLINE_STATUS)
                .addEventListeners(new BotLoader(), new LegacyCommandListener(), new CommandMapper(commandMappings), new VerificationListener(), new LockListener());

        try {
            builder.build();
        } catch(LoginException ex) {
            logger.severe("LoginException when building bot: "+ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }

        logger.info("Bot is built.");
    }

    public static void main(String[] args) {
        Signal.handle(new Signal("INT"), signal -> {
            logger.info("Shutting down...");
            if (jda != null) {
                jda.shutdownNow();
            }
            logger.info("Goodbye!");
            System.exit(0);
        });

        logger.info("Setting up storage...");
        setupStorage();
        logger.info("Storage is ready.\nSetting up bot...");
        setupBot();
        logger.info("Bot is awaiting connection...");
    }

    public void onReady(@NotNull ReadyEvent e) {
        jda = e.getJDA();

        logger.info("Registering commands...");

        e.getJDA().updateCommands().addCommands(
                Commands.slash("help", "Stop it. Get some help."),

                // BOT CONTROLS (ADMIN ONLY)
                Commands.slash("freeze", "Freeze the bot, blocking all commands. (admin only)"),
                Commands.slash("unfreeze", "Unfreeze the bot, unblocking all commands. (admin only)"),
                // END BOT CONTROLS

                //Commands.slash("verify", "Verify that you understand the rules. Required to access the server."),
                Commands.slash("verify_channel", "Set the verification channel.")
                        .addOption(OptionType.CHANNEL, "channel", "The channel for the button", true)
                        .addOption(OptionType.STRING, "title", "The title for the embed")
                        .addOption(OptionType.STRING, "description", "The description for the embed")
                        .addOption(OptionType.STRING, "button_msg", "The text on the action button"),
                Commands.slash("verify_role", "Set the verification role.")
                        .addOption(OptionType.MENTIONABLE, "role", "The role to be given on verification", true),
                // END VERIFICATION COMMAND

                Commands.slash("lock", "Lock a channel.")
                        .addOption(OptionType.CHANNEL, "channel", "The channel to lock", true),
                Commands.slash("unlock", "Unlock a channel.")
                        .addOption(OptionType.CHANNEL, "channel", "The channel to unlock", true),


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


                // STATUS COMMANDS (ALL MOD ONLY)

                Commands.slash("status_watching", "Change the bot's status to watching.")
                        .addOption(OptionType.STRING, "content", "What is the bot watching?", true),
                Commands.slash("status_listening", "Change the bot's status to listening.")
                        .addOption(OptionType.STRING, "content", "What is the bot listening to?", true),
                Commands.slash("status_playing", "Change the bot's status to playing.")
                        .addOption(OptionType.STRING, "content", "What is the bot playing?", true)

                // END STATUS COMMANDS

        ).complete();

        logger.info("Commands registered.");

        if (StorageUtils.getObject("StatusCommandsExecutor", "Activity", "ActivityContent") != null
            && StorageUtils.getObject("StatusCommandsExecutor", "Activity", "ActivityType") != null) {
            try {
                switch ((Integer) StorageUtils.getObject("StatusCommandsExecutor", "Activity", "ActivityType")) {
                    case 1 -> {
                        e.getJDA().getPresence().setActivity(Activity.watching(Objects.requireNonNull(StorageUtils.getObject("StatusCommandsExecutor", "Activity", "ActivityContent")).toString()));
                        logger.info("Loaded Activity from storage.");
                    }
                    case 2 -> {
                        e.getJDA().getPresence().setActivity(Activity.listening(Objects.requireNonNull(StorageUtils.getObject("StatusCommandsExecutor", "Activity", "ActivityContent")).toString()));
                        logger.info("Loaded Activity from storage.");
                    }
                    case 3 -> {
                        e.getJDA().getPresence().setActivity(Activity.playing(Objects.requireNonNull(StorageUtils.getObject("StatusCommandsExecutor", "Activity", "ActivityContent")).toString()));
                        logger.info("Loaded Activity from storage.");
                    }
                }
            } catch(NullPointerException ex) {
                logger.warning("Failed to load Activity from storage. (NullPointerException)");
                ex.printStackTrace();
            }
        }

        logger.info("Bot is ready.");
    }
}
