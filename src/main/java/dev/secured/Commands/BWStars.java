package dev.secured.Commands;

import dev.secured.Utils.Config;
import net.hypixel.api.HypixelAPI;
import net.hypixel.api.apache.ApacheHttpClient;
import net.hypixel.api.http.HypixelHttpClient;
import net.hypixel.api.reply.PlayerReply;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import scala.tools.nsc.ScalaDoc;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class BWStars extends CommandBase {

    boolean message;
    int stars;
    String star1;
    List<String> numbers = new ArrayList<String>();
    EnumChatFormatting starColor1 = EnumChatFormatting.GRAY;
    EnumChatFormatting starColor2 = EnumChatFormatting.GRAY;
    EnumChatFormatting starColor3 = EnumChatFormatting.GRAY;
    EnumChatFormatting starColor4 = EnumChatFormatting.GRAY;
    EnumChatFormatting starColor5 = EnumChatFormatting.GRAY;
    EnumChatFormatting starColor6 = EnumChatFormatting.GRAY;
    EnumChatFormatting starColor7 = EnumChatFormatting.GRAY;
    EnumChatFormatting rankColor1 = EnumChatFormatting.GRAY;
    EnumChatFormatting plusColor = EnumChatFormatting.RED;
    EnumChatFormatting rankColor2 = EnumChatFormatting.GRAY;
    String rank1;
    String plus;
    String plusString;
    String prefix;
    String suffix;
    boolean nick;

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        List<String> items = new ArrayList<String>();
        items.add("key");
        items.add("deletekey");
        items.add("stats");
        items.add("team");
        return items;
    }

    @Override
    public String getCommandName() {
        return "bws";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "bws <args>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;

        //BWStars Menu
        if(args.length < 1){
            player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "BWStars Usages:"));
            player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "/bws key [API Key] // Sets and stores API Key"));
            player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "/bws deletekey // Deletes API Key stored"));
            player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "/bws stats [Player] // Detailed stat lookup of a player"));
            player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "/bws team [Team] // Stat lookup via team colour"));
            return;
        }

        //Config delete command
        if(args[0].contains("deletekey")){
            Config.delete();
        }

        //Add API key command
        else if (args[0].contains("key")){
            if(args.length < 2){
                player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: /bws key [API Key]"));
                return;
            }

            String input = args[1];
            if (args[1].length() > 35 && args[1].length() < 37) //Make sure the key is the correct length
                {
                UUID key = UUID.fromString(input);
                HypixelHttpClient client = new ApacheHttpClient(key);
                HypixelAPI hypixelAPI = new HypixelAPI(client);
                try {
                    PlayerReply.Player response = hypixelAPI.getPlayerByName(getRandomString()).get().getPlayer(); //Tests new key
                    Config.setStoredAPIKey(args[1]);
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

        //Stats check command
        else if(args[0].contains("stats")){
            String key1 = String.valueOf(Config.getStoredAPIKey());

            if(key1.contains("empty")){player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED+"[BWStars] No API Key Stored! Use /bws key [API Key]"));return;}
            if(args.length < 2){player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED+"Usage: /bws stats [player]"));return;}
            UUID key = UUID.fromString(key1);
            HypixelHttpClient client = new ApacheHttpClient(key);
            HypixelAPI hypixelAPI = new HypixelAPI(client);
            try {
                //Get response
                PlayerReply.Player response = hypixelAPI.getPlayerByName(args[1]).get().getPlayer();

                //Nick check
                stars = response.getIntProperty("achievements.bedwars_level", -1);
                if (stars == -1){player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED+"[BWStars] This player is a nick!"));return;}


                rank1 = response.getHighestRank();
                plus = response.getSelectedPlusColor();

                //Get rank, plus, star colours
                getColors(rank1, stars, plus);
                numbers.removeAll(numbers);
                getStars(stars);

                //Get stats
                double networklevel = response.getNetworkLevel();
                int winstreak = response.getIntProperty("stats.Bedwars.winstreak", 0);
                double wins = response.getDoubleProperty("stats.Bedwars.wins_bedwars", 0);
                double losses = response.getDoubleProperty("stats.Bedwars.losses_bedwars", 0);
                double kills = response.getDoubleProperty("stats.Bedwars.kills_bedwars", 0);
                double deaths = response.getDoubleProperty("stats.Bedwars.deaths_bedwars", 0);
                double bedsbroken = response.getDoubleProperty("stats.Bedwars.beds_broken_bedwars", 0);
                double bedslost = response.getDoubleProperty("stats.Bedwars.beds_lost_bedwars", 0);
                double finalkills = response.getDoubleProperty("stats.Bedwars.final_kills_bedwars", 0);
                double finaldeaths = response.getDoubleProperty("stats.Bedwars.final_deaths_bedwars", 0);
                double sfinalkills = response.getDoubleProperty("stats.Bedwars.eight_one_final_kills_bedwars", 0);
                double sfinaldeaths = response.getDoubleProperty("stats.Bedwars.eight_one_final_deaths_bedwars", 0);

                double wl = wins/losses;
                double kd = kills/deaths;
                double bbl = bedsbroken/bedslost;
                double fkd = finalkills/finaldeaths;
                double sfkd = sfinalkills/sfinaldeaths;

                EnumChatFormatting green = EnumChatFormatting.GREEN;
                player.addChatComponentMessage(new ChatComponentText("--" + EnumChatFormatting.GRAY + "==" + EnumChatFormatting.DARK_GRAY + "|||||" + EnumChatFormatting.WHITE + " BWStars Stat Lookup " + EnumChatFormatting.DARK_GRAY + "|||||" + EnumChatFormatting.GRAY + "==" + EnumChatFormatting.WHITE + "--"));
                player.addChatComponentMessage(new ChatComponentText(starColor1 + "[" + starColor2 + numbers.get(0) + starColor3 + numbers.get(1) + starColor4 + numbers.get(2) + starColor5 + numbers.get(3) + starColor6 + star1 + starColor7 + "] " + rankColor1 + prefix + plusColor + plusString + rankColor2 + suffix + response.getName() + EnumChatFormatting.WHITE + ":"));
                player.addChatComponentMessage(new ChatComponentText("Network Level: " + green + format2.format(networklevel)));
                player.addChatComponentMessage(new ChatComponentText("Total Winstreak: " + green + winstreak));
                player.addChatComponentMessage(new ChatComponentText("Total Wins: " + green + format2.format(wins)));
                player.addChatComponentMessage(new ChatComponentText("Total Losses: " + green + format2.format(losses)));
                player.addChatComponentMessage(new ChatComponentText("Total Kills: " + green + format2.format(kills)));
                player.addChatComponentMessage(new ChatComponentText("Total Deaths: " + green + format2.format(deaths)));
                player.addChatComponentMessage(new ChatComponentText("Total Beds Broken: " + green + format2.format(bedsbroken)));
                player.addChatComponentMessage(new ChatComponentText("Total Final Kills: " + green + format2.format(finalkills)));
                player.addChatComponentMessage(new ChatComponentText("Total W/L Ratio: " + green + format.format(wl)));
                player.addChatComponentMessage(new ChatComponentText("Total K/D Ratio: " + green + format.format(kd)));
                player.addChatComponentMessage(new ChatComponentText("Total BB/L Ratio: " + green + format.format(bbl)));
                player.addChatComponentMessage(new ChatComponentText("Total FK/D Ratio: " + green + format.format(fkd)));
                player.addChatComponentMessage(new ChatComponentText("Solo FK/D Ratio: " + green + format.format(sfkd)));

            } catch (InterruptedException e) {
                player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED+"[BWStars] API fail [1]"));
            } catch (ExecutionException e) {
                //This error is very common
                player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED+"[BWStars] API fail [2]: On Cooldown"));
            }
        }


        else if(args[0].contains("team")){
            String key1 = String.valueOf(Config.getStoredAPIKey());
            if (key1.contains("empty")) {
                player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "[BWStars] No API Key Stored! Use /bws key [API Key]"));
                return;
            }
            if(args.length < 2){player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED+"Usage: /bws team [team]"));return;}
            UUID key = UUID.fromString(key1);
            HypixelHttpClient client = new ApacheHttpClient(key);
            HypixelAPI hypixelAPI = new HypixelAPI(client);
            message = true;
            Collection<NetworkPlayerInfo> playersC = Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap();
            playersC.forEach((loadedPlayer) -> {
                try {
                    //Get team
                    loadedPlayer.getPlayerTeam().getRegisteredName();
                } catch (NullPointerException e) { return; }
                String playerName = loadedPlayer.getGameProfile().getName();
                String team = loadedPlayer.getPlayerTeam().getRegisteredName();
                String team1 = null;
                String inputteam = args[1].toLowerCase();

                //Make the team "blue" instead of "blue10" or something
                if (team.contains("Blue")) {
                    team1 = "blue";
                } else if (team.contains("Green")) {
                    team1 = "green";
                } else if (team.contains("Gray")) {
                    team1 = "gray";
                } else if (team.contains("Pink")) {
                    team1 = "pink";
                } else if (team.contains("Yellow")) {
                    team1 = "yellow";
                } else if (team.contains("Aqua")) {
                    team1 = "aqua";
                } else if (team.contains("Red")) {
                    team1 = "red";
                } else if (team.contains("White")) {
                    team1 = "white";
                } else {
                    return;
                }
                if (inputteam.equals(team1)) {
                    if (message) {
                        player.addChatComponentMessage(new ChatComponentText("--" + EnumChatFormatting.GRAY + "==" + EnumChatFormatting.DARK_GRAY + "|||||" + EnumChatFormatting.WHITE + " BWStars Team Lookup " + EnumChatFormatting.DARK_GRAY + "|||||" + EnumChatFormatting.GRAY + "==" + EnumChatFormatting.WHITE + "--"));
                        message = false;
                    }
                    try {
                        PlayerReply.Player response = hypixelAPI.getPlayerByName(playerName).get().getPlayer();

                        //Nick Check
                        stars = response.getIntProperty("achievements.bedwars_level", -1);
                        if (stars == -1) {
                            player.addChatComponentMessage(new ChatComponentText(playerName + EnumChatFormatting.YELLOW + EnumChatFormatting.ITALIC + " NICK"));
                            player.addChatComponentMessage(new ChatComponentText(""));
                            return;
                        }

                        double networklevel = response.getNetworkLevel();
                        int winstreak = response.getIntProperty("stats.Bedwars.winstreak", 0);
                        double wins = response.getDoubleProperty("stats.Bedwars.wins_bedwars", 0);
                        double losses = response.getDoubleProperty("stats.Bedwars.losses_bedwars", 0);
                        double kills = response.getDoubleProperty("stats.Bedwars.kills_bedwars", 0);
                        double deaths = response.getDoubleProperty("stats.Bedwars.deaths_bedwars", 0);
                        double finalkills = response.getDoubleProperty("stats.Bedwars.final_kills_bedwars", 0);
                        double finaldeaths = response.getDoubleProperty("stats.Bedwars.final_deaths_bedwars", 0);

                        double wl = wins / losses;
                        double kd = kills / deaths;
                        double fkd = finalkills / finaldeaths;

                        rank1 = response.getHighestRank();
                        plus = response.getSelectedPlusColor();
                        getColors(rank1, stars, plus);
                        numbers.removeAll(numbers);
                        getStars(stars);

                        EnumChatFormatting green = EnumChatFormatting.GREEN;
                        player.addChatComponentMessage(new ChatComponentText(starColor1 + "[" + starColor2 + numbers.get(0) + starColor3 + numbers.get(1) + starColor4 + numbers.get(2) + starColor5 + numbers.get(3) + starColor6 + star1 + starColor7 + "] " + rankColor1 + prefix + plusColor + plusString + rankColor2 + suffix + response.getName() + EnumChatFormatting.WHITE + ":"));
                        player.addChatComponentMessage(new ChatComponentText("Network Level: " + green + format2.format(networklevel)));
                        player.addChatComponentMessage(new ChatComponentText("Total Winstreak: " + green + winstreak));
                        player.addChatComponentMessage(new ChatComponentText("Total Wins: " + green + format2.format(wins)));
                        player.addChatComponentMessage(new ChatComponentText("Total Kills: " + green + format2.format(kills)));
                        player.addChatComponentMessage(new ChatComponentText("Total Final Kills: " + green + format2.format(finalkills)));
                        player.addChatComponentMessage(new ChatComponentText("Total W/L Ratio: " + green + format.format(wl)));
                        player.addChatComponentMessage(new ChatComponentText("Total K/D Ratio: " + green + format.format(kd)));
                        player.addChatComponentMessage(new ChatComponentText("Total FK/D Ratio: " + green + format.format(fkd)));
                        player.addChatComponentMessage(new ChatComponentText(""));
                    } catch (InterruptedException e) {
                        player.addChatComponentMessage(new ChatComponentText(loadedPlayer.getGameProfile().getName() + "|" + EnumChatFormatting.ITALIC +  " API FAIL [1]"));
                    } catch (ExecutionException e) {
                        player.addChatComponentMessage(new ChatComponentText(loadedPlayer.getGameProfile().getName() + "|" + EnumChatFormatting.ITALIC + " API FAIL [2]: On Cooldown"));
                    }

                } else {
                    return;
                }
            });
        } else {
            player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "BWStars Usages:"));
            player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "/bws key [API Key] // Sets and stores API Key"));
            player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "/bws deletekey // Deletes API Key stored"));
            player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "/bws stats [Player] // Detailed stat lookup of a player"));
            player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "/bws team [Team] // Stat lookup via team colour"));
            return;
        }
    }

    //This is for the check when you input your api key for the first time
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

    private static final DecimalFormat format = new DecimalFormat("0.00");
    private static final DecimalFormat format2 = new DecimalFormat("0");

    public EnumChatFormatting getPlus(String plus){
        if(plus.contains("RED")){plusColor = EnumChatFormatting.RED;}
        if(plus.contains("GOLD")){plusColor = EnumChatFormatting.GOLD;}
        if(plus.contains("LIGHT_GREEN")){plusColor = EnumChatFormatting.GREEN;}
        if(plus.contains("YELLOW")){plusColor = EnumChatFormatting.YELLOW;}
        if(plus.contains("PINK")){plusColor = EnumChatFormatting.LIGHT_PURPLE;}
        if(plus.contains("WHITE")){plusColor = EnumChatFormatting.WHITE;}
        if(plus.contains("BLUE")){plusColor = EnumChatFormatting.BLUE;}
        if(plus.contains("DARK_GREEN")){plusColor = EnumChatFormatting.DARK_GREEN;}
        if(plus.contains("DARK_RED")){plusColor = EnumChatFormatting.DARK_RED;}
        if(plus.contains("CYAN")){plusColor = EnumChatFormatting.DARK_AQUA;}
        if(plus.contains("PURPLE")){plusColor = EnumChatFormatting.DARK_PURPLE;}
        if(plus.contains("GRAY")){plusColor = EnumChatFormatting.GRAY;}
        if(plus.contains("BLACK")){plusColor = EnumChatFormatting.BLACK;}
        if(plus.contains("DARK_BLUE")){plusColor = EnumChatFormatting.DARK_BLUE;}
        return plusColor;
    }

    public void getStars(int stars){
        LinkedList<Integer> stack = new LinkedList<Integer>();
        while (stars > 0) {
            stack.push( stars % 10 );
            stars = stars / 10;
        }
        if(stack.size() == 1){
            numbers.add(String.valueOf(stack.pop()));
            numbers.add("");
            numbers.add("");
            numbers.add("");
        }else if(stack.size() == 2){
            numbers.add(String.valueOf(stack.pop()));
            numbers.add(String.valueOf(stack.pop()));
            numbers.add("");
            numbers.add("");
        }else if(stack.size() == 3){
            numbers.add(String.valueOf(stack.pop()));
            numbers.add(String.valueOf(stack.pop()));
            numbers.add(String.valueOf(stack.pop()));
            numbers.add("");
        }else if(stack.size() == 4){
            numbers.add(String.valueOf(stack.pop()));
            numbers.add(String.valueOf(stack.pop()));
            numbers.add(String.valueOf(stack.pop()));
            numbers.add(String.valueOf(stack.pop()));
        }
        return;
    }
    public void getColors(String rank, int star, String plus){
        if(rank.contains("NONE")){
            rankColor1 = EnumChatFormatting.GRAY;
            plusColor = EnumChatFormatting.GRAY;
            rankColor2 = EnumChatFormatting.GRAY;
            plusString = "";
            prefix = "";
            suffix = "";
        } else if (rank.contains("VIP_PLUS")) {
            rankColor1 = EnumChatFormatting.GREEN;
            plusColor = EnumChatFormatting.GOLD;
            rankColor2 = EnumChatFormatting.GREEN;
            plusString = "+";
            prefix = "[VIP";
            suffix = "] ";
        } else if (rank.contains("VIP")) {
            rankColor1 = EnumChatFormatting.GREEN;
            rankColor2 = EnumChatFormatting.GREEN;
            plusString = "";
            prefix = "[VIP";
            suffix = "] ";
        } else if (rank.contains("MVP_PLUS")) {
            rankColor1 = EnumChatFormatting.AQUA;
            rankColor2 = EnumChatFormatting.AQUA;
            plusString = "+";
            prefix = "[MVP";
            suffix = "] ";
            plusColor = getPlus(plus);
        } else if (rank.contains("MVP")) {
            rankColor1 = EnumChatFormatting.AQUA;
            rankColor2 = EnumChatFormatting.AQUA;
            plusString = "";
            prefix = "[MVP";
            suffix = "] ";
        } else if (rank.contains("YOUTUBE")){
            rankColor1 = EnumChatFormatting.RED;
            rankColor2 = EnumChatFormatting.RED;
            plusColor = EnumChatFormatting.WHITE;
            plusString = "YOUTUBE";
            prefix = "[";
            suffix = "] ";
        } else if (rank.contains("GAME_MASTER")){
            rankColor1 = EnumChatFormatting.DARK_GREEN;
            rankColor2 = EnumChatFormatting.DARK_GREEN;
            plusString = "";
            prefix = "[GAMEMASTER";
            suffix = "] ";
        } else if (rank.contains("ADMIN")){
            rankColor1 = EnumChatFormatting.RED;
            rankColor2 = EnumChatFormatting.RED;
            plusString = "";
            prefix = "[ADMIN";
            suffix = "] ";
        } else if (rank.contains("SUPERSTAR")) {
            rankColor1 = EnumChatFormatting.GOLD;
            rankColor2 = EnumChatFormatting.GOLD;
            plusString = "++";
            prefix = "[MVP";
            suffix = "] ";
            plusColor = getPlus(plus);
        }

        if(star < 100){
            starColor1 = EnumChatFormatting.GRAY;
            starColor2 = EnumChatFormatting.GRAY;
            starColor3 = EnumChatFormatting.GRAY;
            starColor4 = EnumChatFormatting.GRAY;
            starColor5 = EnumChatFormatting.GRAY;
            starColor6 = EnumChatFormatting.GRAY;
            starColor7 = EnumChatFormatting.GRAY;
            star1 = "✫";
        } else if (star < 200){
            starColor1 = EnumChatFormatting.WHITE;
            starColor2 = EnumChatFormatting.WHITE;
            starColor3 = EnumChatFormatting.WHITE;
            starColor4 = EnumChatFormatting.WHITE;
            starColor5 = EnumChatFormatting.WHITE;
            starColor6 = EnumChatFormatting.WHITE;
            starColor7 = EnumChatFormatting.WHITE;
            star1 = "✫";
        } else if (star < 300){
            starColor1 = EnumChatFormatting.GOLD;
            starColor2 = EnumChatFormatting.GOLD;
            starColor3 = EnumChatFormatting.GOLD;
            starColor4 = EnumChatFormatting.GOLD;
            starColor5 = EnumChatFormatting.GOLD;
            starColor6 = EnumChatFormatting.GOLD;
            starColor7 = EnumChatFormatting.GOLD;
            star1 = "✫";
        } else if (star < 400){
            starColor1 = EnumChatFormatting.AQUA;
            starColor2 = EnumChatFormatting.AQUA;
            starColor3 = EnumChatFormatting.AQUA;
            starColor4 = EnumChatFormatting.AQUA;
            starColor5 = EnumChatFormatting.AQUA;
            starColor6 = EnumChatFormatting.AQUA;
            starColor7 = EnumChatFormatting.AQUA;
            star1 = "✫";
        } else if (star < 500){
            starColor1 = EnumChatFormatting.DARK_GREEN;
            starColor2 = EnumChatFormatting.DARK_GREEN;
            starColor3 = EnumChatFormatting.DARK_GREEN;
            starColor4 = EnumChatFormatting.DARK_GREEN;
            starColor5 = EnumChatFormatting.DARK_GREEN;
            starColor6 = EnumChatFormatting.DARK_GREEN;
            starColor7 = EnumChatFormatting.DARK_GREEN;
            star1 = "✫";
        } else if (star < 600){
            starColor1 = EnumChatFormatting.DARK_AQUA;
            starColor2 = EnumChatFormatting.DARK_AQUA;
            starColor3 = EnumChatFormatting.DARK_AQUA;
            starColor4 = EnumChatFormatting.DARK_AQUA;
            starColor5 = EnumChatFormatting.DARK_AQUA;
            starColor6 = EnumChatFormatting.DARK_AQUA;
            starColor7 = EnumChatFormatting.DARK_AQUA;
            star1 = "✫";
        } else if (star < 700){
            starColor1 = EnumChatFormatting.DARK_RED;
            starColor2 = EnumChatFormatting.DARK_RED;
            starColor3 = EnumChatFormatting.DARK_RED;
            starColor4 = EnumChatFormatting.DARK_RED;
            starColor5 = EnumChatFormatting.DARK_RED;
            starColor6 = EnumChatFormatting.DARK_RED;
            starColor7 = EnumChatFormatting.DARK_RED;
            star1 = "✫";
        } else if (star < 800){
            starColor1 = EnumChatFormatting.LIGHT_PURPLE;
            starColor2 = EnumChatFormatting.LIGHT_PURPLE;
            starColor3 = EnumChatFormatting.LIGHT_PURPLE;
            starColor4 = EnumChatFormatting.LIGHT_PURPLE;
            starColor5 = EnumChatFormatting.LIGHT_PURPLE;
            starColor6 = EnumChatFormatting.LIGHT_PURPLE;
            starColor7 = EnumChatFormatting.LIGHT_PURPLE;
            star1 = "✫";
        } else if (star < 900){
            starColor1 = EnumChatFormatting.BLUE;
            starColor2 = EnumChatFormatting.BLUE;
            starColor3 = EnumChatFormatting.BLUE;
            starColor4 = EnumChatFormatting.BLUE;
            starColor5 = EnumChatFormatting.BLUE;
            starColor6 = EnumChatFormatting.BLUE;
            starColor7 = EnumChatFormatting.BLUE;
            star1 = "✫";
        } else if (star < 1000){
            starColor1 = EnumChatFormatting.DARK_PURPLE;
            starColor2 = EnumChatFormatting.DARK_PURPLE;
            starColor3 = EnumChatFormatting.DARK_PURPLE;
            starColor4 = EnumChatFormatting.DARK_PURPLE;
            starColor5 = EnumChatFormatting.DARK_PURPLE;
            starColor6 = EnumChatFormatting.DARK_PURPLE;
            starColor7 = EnumChatFormatting.DARK_PURPLE;
            star1 = "✫";
        } else if (star < 1100){
            starColor1 = EnumChatFormatting.RED;
            starColor2 = EnumChatFormatting.GOLD;
            starColor3 = EnumChatFormatting.YELLOW;
            starColor4 = EnumChatFormatting.GREEN;
            starColor5 = EnumChatFormatting.AQUA;
            starColor6 = EnumChatFormatting.LIGHT_PURPLE;
            starColor7 = EnumChatFormatting.DARK_PURPLE;
            star1 = "✫";
        } else if (star < 1200){
            starColor1 = EnumChatFormatting.GRAY;
            starColor2 = EnumChatFormatting.WHITE;
            starColor3 = EnumChatFormatting.WHITE;
            starColor4 = EnumChatFormatting.WHITE;
            starColor5 = EnumChatFormatting.WHITE;
            starColor6 = EnumChatFormatting.GRAY;
            starColor7 = EnumChatFormatting.GRAY;
            star1 = "✪";
        } else if (star < 1300){
            starColor1 = EnumChatFormatting.GRAY;
            starColor2 = EnumChatFormatting.YELLOW;
            starColor3 = EnumChatFormatting.YELLOW;
            starColor4 = EnumChatFormatting.YELLOW;
            starColor5 = EnumChatFormatting.YELLOW;
            starColor6 = EnumChatFormatting.GOLD;
            starColor7 = EnumChatFormatting.GRAY;
            star1 = "✪";
        } else if (star < 1400){
            starColor1 = EnumChatFormatting.GRAY;
            starColor2 = EnumChatFormatting.AQUA;
            starColor3 = EnumChatFormatting.AQUA;
            starColor4 = EnumChatFormatting.AQUA;
            starColor5 = EnumChatFormatting.AQUA;
            starColor6 = EnumChatFormatting.DARK_AQUA;
            starColor7 = EnumChatFormatting.GRAY;
            star1 = "✪";
        } else if (star < 1500){
            starColor1 = EnumChatFormatting.GRAY;
            starColor2 = EnumChatFormatting.GREEN;
            starColor3 = EnumChatFormatting.GREEN;
            starColor4 = EnumChatFormatting.GREEN;
            starColor5 = EnumChatFormatting.GREEN;
            starColor6 = EnumChatFormatting.DARK_GREEN;
            starColor7 = EnumChatFormatting.GRAY;
            star1 = "✪";
        } else if (star < 1600){
            starColor1 = EnumChatFormatting.GRAY;
            starColor2 = EnumChatFormatting.DARK_AQUA;
            starColor3 = EnumChatFormatting.DARK_AQUA;
            starColor4 = EnumChatFormatting.DARK_AQUA;
            starColor5 = EnumChatFormatting.DARK_AQUA;
            starColor6 = EnumChatFormatting.BLUE;
            starColor7 = EnumChatFormatting.GRAY;
            star1 = "✪";
        } else if (star < 1700){
            starColor1 = EnumChatFormatting.GRAY;
            starColor2 = EnumChatFormatting.RED;
            starColor3 = EnumChatFormatting.RED;
            starColor4 = EnumChatFormatting.RED;
            starColor5 = EnumChatFormatting.RED;
            starColor6 = EnumChatFormatting.DARK_RED;
            starColor7 = EnumChatFormatting.GRAY;
            star1 = "✪";
        } else if (star < 1800){
            starColor1 = EnumChatFormatting.GRAY;
            starColor2 = EnumChatFormatting.LIGHT_PURPLE;
            starColor3 = EnumChatFormatting.LIGHT_PURPLE;
            starColor4 = EnumChatFormatting.LIGHT_PURPLE;
            starColor5 = EnumChatFormatting.LIGHT_PURPLE;
            starColor6 = EnumChatFormatting.DARK_PURPLE;
            starColor7 = EnumChatFormatting.GRAY;
            star1 = "✪";
        } else if (star < 1900){
            starColor1 = EnumChatFormatting.GRAY;
            starColor2 = EnumChatFormatting.BLUE;
            starColor3 = EnumChatFormatting.BLUE;
            starColor4 = EnumChatFormatting.BLUE;
            starColor5 = EnumChatFormatting.BLUE;
            starColor6 = EnumChatFormatting.DARK_BLUE;
            starColor7 = EnumChatFormatting.GRAY;
            star1 = "✪";
        } else if (star < 2000){
            starColor1 = EnumChatFormatting.GRAY;
            starColor2 = EnumChatFormatting.DARK_PURPLE;
            starColor3 = EnumChatFormatting.DARK_PURPLE;
            starColor4 = EnumChatFormatting.DARK_PURPLE;
            starColor5 = EnumChatFormatting.DARK_PURPLE;
            starColor6 = EnumChatFormatting.DARK_GRAY;
            starColor7 = EnumChatFormatting.GRAY;
            star1 = "✪";
        } else if (star < 2100){
            starColor1 = EnumChatFormatting.DARK_GRAY;
            starColor2 = EnumChatFormatting.GRAY;
            starColor3 = EnumChatFormatting.WHITE;
            starColor4 = EnumChatFormatting.WHITE;
            starColor5 = EnumChatFormatting.GRAY;
            starColor6 = EnumChatFormatting.GRAY;
            starColor7 = EnumChatFormatting.DARK_GRAY;
            star1 = "✪";
        } else if (star < 2200){
            starColor1 = EnumChatFormatting.WHITE;
            starColor2 = EnumChatFormatting.WHITE;
            starColor3 = EnumChatFormatting.YELLOW;
            starColor4 = EnumChatFormatting.YELLOW;
            starColor5 = EnumChatFormatting.GOLD;
            starColor6 = EnumChatFormatting.GOLD;
            starColor7 = EnumChatFormatting.GOLD;
            star1 = "⚝";
        } else if (star < 2300){
            starColor1 = EnumChatFormatting.GOLD;
            starColor2 = EnumChatFormatting.GOLD;
            starColor3 = EnumChatFormatting.WHITE;
            starColor4 = EnumChatFormatting.WHITE;
            starColor5 = EnumChatFormatting.AQUA;
            starColor6 = EnumChatFormatting.DARK_AQUA;
            starColor7 = EnumChatFormatting.DARK_AQUA;
            star1 = "⚝";
        } else if (star < 2400){
            starColor1 = EnumChatFormatting.DARK_PURPLE;
            starColor2 = EnumChatFormatting.DARK_PURPLE;
            starColor3 = EnumChatFormatting.LIGHT_PURPLE;
            starColor4 = EnumChatFormatting.LIGHT_PURPLE;
            starColor5 = EnumChatFormatting.GOLD;
            starColor6 = EnumChatFormatting.YELLOW;
            starColor7 = EnumChatFormatting.YELLOW;
            star1 = "⚝";
        } else if (star < 2500){
            starColor1 = EnumChatFormatting.AQUA;
            starColor2 = EnumChatFormatting.AQUA;
            starColor3 = EnumChatFormatting.WHITE;
            starColor4 = EnumChatFormatting.WHITE;
            starColor5 = EnumChatFormatting.GRAY;
            starColor6 = EnumChatFormatting.DARK_GRAY;
            starColor7 = EnumChatFormatting.DARK_GRAY;
            star1 = "⚝";
        } else if (star < 2600){
            starColor1 = EnumChatFormatting.WHITE;
            starColor2 = EnumChatFormatting.WHITE;
            starColor3 = EnumChatFormatting.GREEN;
            starColor4 = EnumChatFormatting.GREEN;
            starColor5 = EnumChatFormatting.DARK_GREEN;
            starColor6 = EnumChatFormatting.DARK_GREEN;
            starColor7 = EnumChatFormatting.DARK_GREEN;
            star1 = "⚝";
        } else if (star < 2700){
            starColor1 = EnumChatFormatting.DARK_RED;
            starColor2 = EnumChatFormatting.DARK_RED;
            starColor3 = EnumChatFormatting.RED;
            starColor4 = EnumChatFormatting.RED;
            starColor5 = EnumChatFormatting.LIGHT_PURPLE;
            starColor6 = EnumChatFormatting.LIGHT_PURPLE;
            starColor7 = EnumChatFormatting.DARK_PURPLE;
            star1 = "⚝";
        } else if (star < 2800){
            starColor1 = EnumChatFormatting.YELLOW;
            starColor2 = EnumChatFormatting.YELLOW;
            starColor3 = EnumChatFormatting.WHITE;
            starColor4 = EnumChatFormatting.WHITE;
            starColor5 = EnumChatFormatting.DARK_GRAY;
            starColor6 = EnumChatFormatting.DARK_GRAY;
            starColor7 = EnumChatFormatting.DARK_GRAY;
            star1 = "⚝";
        } else if (star < 2900){
            starColor1 = EnumChatFormatting.GREEN;
            starColor2 = EnumChatFormatting.GREEN;
            starColor3 = EnumChatFormatting.DARK_GREEN;
            starColor4 = EnumChatFormatting.DARK_GREEN;
            starColor5 = EnumChatFormatting.GOLD;
            starColor6 = EnumChatFormatting.GOLD;
            starColor7 = EnumChatFormatting.YELLOW;
            star1 = "⚝";
        } else if (star < 3000){
            starColor1 = EnumChatFormatting.AQUA;
            starColor2 = EnumChatFormatting.AQUA;
            starColor3 = EnumChatFormatting.DARK_AQUA;
            starColor4 = EnumChatFormatting.DARK_AQUA;
            starColor5 = EnumChatFormatting.BLUE;
            starColor6 = EnumChatFormatting.BLUE;
            starColor7 = EnumChatFormatting.BLUE;
            star1 = "⚝";
        } else if (star < 3100){
            starColor1 = EnumChatFormatting.YELLOW;
            starColor2 = EnumChatFormatting.YELLOW;
            starColor3 = EnumChatFormatting.GOLD;
            starColor4 = EnumChatFormatting.GOLD;
            starColor5 = EnumChatFormatting.RED;
            starColor6 = EnumChatFormatting.RED;
            starColor7 = EnumChatFormatting.DARK_RED;
            star1 = "⚝";
        } else if (star > 3099){
            starColor1 = EnumChatFormatting.YELLOW;
            starColor2 = EnumChatFormatting.YELLOW;
            starColor3 = EnumChatFormatting.GOLD;
            starColor4 = EnumChatFormatting.GOLD;
            starColor5 = EnumChatFormatting.RED;
            starColor6 = EnumChatFormatting.RED;
            starColor7 = EnumChatFormatting.DARK_RED;
            star1 = "⚝";
        }

        return;
    }
}
