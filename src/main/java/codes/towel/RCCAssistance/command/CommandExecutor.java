package codes.towel.RCCAssistance.command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public abstract class CommandExecutor {
    public abstract void onSlashCommand(SlashCommandInteractionEvent e) throws UnknownCommandException;
}
