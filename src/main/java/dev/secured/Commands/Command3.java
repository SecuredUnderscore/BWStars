package dev.secured.Commands;

import dev.secured.Utils.Config;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;

import java.util.List;

public class Command3 extends CommandBase {
    @Override
    public String getCommandName() { return "bwsdeletekey"; }

    @Override
    public String getCommandUsage(ICommandSender sender) { return "bwsdeletekey"; }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) { return true; }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos){ return null; };

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException { Config.delete(); }
}
