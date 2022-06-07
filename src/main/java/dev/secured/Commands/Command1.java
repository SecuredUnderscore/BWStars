package dev.secured.Commands;

import dev.secured.Utils.Config;
import net.hypixel.api.HypixelAPI;
import net.hypixel.api.apache.ApacheHttpClient;
import net.hypixel.api.http.HypixelHttpClient;
import net.hypixel.api.reply.PlayerReply;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class Command1 extends CommandBase {
    @Override
    public String getCommandName(){
        return "bwssetkey";
    }

    @Override
    public String getCommandUsage(ICommandSender sender){
        return "bwssetkey <args>";
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) { return true; }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos){ return null; };

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException{
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if(args.length < 1){
            player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: /bwssetkey [API Key]"));
            return;
        }

        String input = args[0];
        if (args[0].length() > 35 && args[0].length() < 37){
            UUID key = UUID.fromString(input);
            HypixelHttpClient client = new ApacheHttpClient(key);
            HypixelAPI hypixelAPI = new HypixelAPI(client);
            try {
                PlayerReply.Player response = hypixelAPI.getPlayerByName(getRandomString()).get().getPlayer();
                System.out.println(response);
                Config.setStoredAPIKey(args[0]);
                player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.GREEN + "[BWStars] Successfully set key!"));
            } catch (InterruptedException e) {
                player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "[BWStars] Error setting key."));
                System.out.println(e);
            } catch (ExecutionException e) {
                player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "[BWStars] Error setting key."));
                System.out.println(e);
            }
        } else {
            player.addChatComponentMessage(new ChatComponentText("Please enter a valid API key"));
        }
    }

    protected String getRandomString() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder string = new StringBuilder();
        Random rnd = new Random();
        while (string.length() < 12) { // length of the random string.
            int index = (int) (rnd.nextFloat() * chars.length());
            string.append(chars.charAt(index));
        }
        String random = string.toString();
        return random;
    }
}
