package dev.secured.Keybinding;

import dev.secured.BWStars;
import dev.secured.Utils.Config;
import net.hypixel.api.HypixelAPI;
import net.hypixel.api.apache.ApacheHttpClient;
import net.hypixel.api.http.HypixelHttpClient;
import net.hypixel.api.reply.PlayerReply;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class Keybind1 {

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
    EnumChatFormatting wsColor = EnumChatFormatting.GREEN;
    EnumChatFormatting fkdColor = EnumChatFormatting.GREEN;
    EnumChatFormatting kdColor = EnumChatFormatting.GREEN;
    EnumChatFormatting wlColor = EnumChatFormatting.GREEN;

    String rank1;
    String plus;
    String plusString;
    String prefix;
    String suffix;
    boolean fail;

    @SubscribeEvent
    public void Keybind1(InputEvent.KeyInputEvent event) throws ExecutionException, InterruptedException {
        if(BWStars.keybinding1.isKeyDown()){
            EntityPlayer player = Minecraft.getMinecraft().thePlayer;
            String key1 = String.valueOf(Config.getStoredAPIKey());
            if (key1.contains("empty")) {
                player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "[BWStars] No API Key Stored! Use /bwssetkey [apikey]"));
                return;
            }
            UUID key = UUID.fromString(key1);
            HypixelHttpClient client = new ApacheHttpClient(key);
            HypixelAPI hypixelAPI = new HypixelAPI(client);
            player.addChatComponentMessage(new ChatComponentText("--" + EnumChatFormatting.GRAY + "==" + EnumChatFormatting.DARK_GRAY + "|||||" + EnumChatFormatting.WHITE + " BWStars Mass Lookup (WS, FK/D, K/D, W/L) " + EnumChatFormatting.DARK_GRAY + "|||||" + EnumChatFormatting.GRAY + "==" + EnumChatFormatting.WHITE + "--"));
            Collection<NetworkPlayerInfo> playersC= Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap();
            playersC.forEach((loadedPlayer) -> {
                fail = false;
                String loadedPlayerName = loadedPlayer.getGameProfile().getName();
                try {
                    PlayerReply.Player response = hypixelAPI.getPlayerByName(loadedPlayerName).get().getPlayer();
                    stars = response.getIntProperty("achievements.bedwars_level", -1);
                    if (stars == -1) {
                        player.addChatComponentMessage(new ChatComponentText(loadedPlayerName + EnumChatFormatting.YELLOW + EnumChatFormatting.ITALIC + " NICK"));
                        return;
                    }

                    int winstreak = response.getIntProperty("stats.Bedwars.winstreak", 0);
                    double wins = response.getDoubleProperty("stats.Bedwars.wins_bedwars", 0);
                    double losses = response.getDoubleProperty("stats.Bedwars.losses_bedwars", 0);
                    double kills = response.getDoubleProperty("stats.Bedwars.kills_bedwars", 0);
                    double deaths = response.getDoubleProperty("stats.Bedwars.deaths_bedwars", 0);
                    double finalkills = response.getDoubleProperty("stats.Bedwars.final_kills_bedwars", 0);
                    double finaldeaths = response.getDoubleProperty("stats.Bedwars.final_deaths_bedwars", 0);
                    String party = response.getStringProperty("channel", "").toLowerCase();

                    double wl = wins / losses;
                    double kd = kills / deaths;
                    double fkd = finalkills / finaldeaths;

                    if (party.contains("party")){
                        party = "PARTY";
                    } else {
                        party = "";
                    }
                    rank1 = response.getHighestRank();
                    plus = response.getSelectedPlusColor();
                    getColors(rank1, stars, plus, winstreak, fkd, kd, wl);
                    numbers.removeAll(numbers);
                    getStars(stars);
                    player.addChatComponentMessage(new ChatComponentText(starColor1 + "[" + starColor2 + numbers.get(0) + starColor3 + numbers.get(1) + starColor4 + numbers.get(2) + starColor5 + numbers.get(3) + starColor6 + star1 + starColor7 + "] " + rankColor1 + prefix + plusColor + plusString + rankColor2 + suffix + response.getName() + EnumChatFormatting.WHITE + ": " + wsColor + winstreak + EnumChatFormatting.GRAY + EnumChatFormatting.BOLD + " / " + EnumChatFormatting.RESET + fkdColor + format.format(fkd) + EnumChatFormatting.GRAY + EnumChatFormatting.BOLD + " / " + EnumChatFormatting.RESET + kdColor + format.format(kd) + EnumChatFormatting.GRAY + EnumChatFormatting.BOLD + " / " + EnumChatFormatting.RESET + wlColor + format.format(wl) + " " + EnumChatFormatting.BLUE + EnumChatFormatting.ITALIC + party));
                } catch (InterruptedException e) {
                    player.addChatComponentMessage(new ChatComponentText(loadedPlayerName + "|" + EnumChatFormatting.ITALIC + " API FAIL [1]"));
                } catch (ExecutionException e) {
                    player.addChatComponentMessage(new ChatComponentText(loadedPlayerName + "|" + EnumChatFormatting.ITALIC + " API FAIL [2]: On Cooldown"));
                }
            });
        }
    }

    private static final DecimalFormat format = new DecimalFormat("0.00");

    public EnumChatFormatting getPlus(String plus) {
        if (plus.contains("RED")) {
            plusColor = EnumChatFormatting.RED;
        }
        if (plus.contains("GOLD")) {
            plusColor = EnumChatFormatting.GOLD;
        }
        if (plus.contains("GREEN")) {
            plusColor = EnumChatFormatting.GREEN;
        }
        if (plus.contains("YELLOW")) {
            plusColor = EnumChatFormatting.YELLOW;
        }
        if (plus.contains("LIGHT_PURPLE")) {
            plusColor = EnumChatFormatting.LIGHT_PURPLE;
        }
        if (plus.contains("WHITE")) {
            plusColor = EnumChatFormatting.WHITE;
        }
        if (plus.contains("BLUE")) {
            plusColor = EnumChatFormatting.BLUE;
        }
        if (plus.contains("DARK_GREEN")) {
            plusColor = EnumChatFormatting.DARK_GREEN;
        }
        if (plus.contains("DARK_RED")) {
            plusColor = EnumChatFormatting.DARK_RED;
        }
        if (plus.contains("CYAN")) {
            plusColor = EnumChatFormatting.DARK_AQUA;
        }
        if (plus.contains("PURPLE")) {
            plusColor = EnumChatFormatting.DARK_PURPLE;
        }
        if (plus.contains("GRAY")) {
            plusColor = EnumChatFormatting.GRAY;
        }
        if (plus.contains("BLACK")) {
            plusColor = EnumChatFormatting.BLACK;
        }
        if (plus.contains("DARK_BLUE")) {
            plusColor = EnumChatFormatting.DARK_BLUE;
        }
        return plusColor;
    }

    public void getStars(int stars) {
        LinkedList<Integer> stack = new LinkedList<Integer>();
        while (stars > 0) {
            stack.push(stars % 10);
            stars = stars / 10;
        }
        if (stack.size() == 1) {
            numbers.add(String.valueOf(stack.pop()));
            numbers.add("");
            numbers.add("");
            numbers.add("");
        } else if (stack.size() == 2) {
            numbers.add(String.valueOf(stack.pop()));
            numbers.add(String.valueOf(stack.pop()));
            numbers.add("");
            numbers.add("");
        } else if (stack.size() == 3) {
            numbers.add(String.valueOf(stack.pop()));
            numbers.add(String.valueOf(stack.pop()));
            numbers.add(String.valueOf(stack.pop()));
            numbers.add("");
        } else if (stack.size() == 4) {
            numbers.add(String.valueOf(stack.pop()));
            numbers.add(String.valueOf(stack.pop()));
            numbers.add(String.valueOf(stack.pop()));
            numbers.add(String.valueOf(stack.pop()));
        }
        return;
    }

    public void getColors(String rank, int star, String plus, int ws, double fkd, double kd, double wl) {
        if (rank.contains("NONE")) {
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
        } else if (rank.contains("YOUTUBE")) {
            rankColor1 = EnumChatFormatting.RED;
            rankColor2 = EnumChatFormatting.RED;
            plusColor = EnumChatFormatting.WHITE;
            plusString = "YOUTUBE";
            prefix = "[";
            suffix = "] ";
        } else if (rank.contains("GAME_MASTER")) {
            rankColor1 = EnumChatFormatting.DARK_GREEN;
            rankColor2 = EnumChatFormatting.DARK_GREEN;
            plusString = "";
            prefix = "[GAMEMASTER";
            suffix = "] ";
        } else if (rank.contains("ADMIN")) {
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

        if (star < 100) {
            starColor1 = EnumChatFormatting.GRAY;
            starColor2 = EnumChatFormatting.GRAY;
            starColor3 = EnumChatFormatting.GRAY;
            starColor4 = EnumChatFormatting.GRAY;
            starColor5 = EnumChatFormatting.GRAY;
            starColor6 = EnumChatFormatting.GRAY;
            starColor7 = EnumChatFormatting.GRAY;
            star1 = "✫";
        } else if (star < 200) {
            starColor1 = EnumChatFormatting.WHITE;
            starColor2 = EnumChatFormatting.WHITE;
            starColor3 = EnumChatFormatting.WHITE;
            starColor4 = EnumChatFormatting.WHITE;
            starColor5 = EnumChatFormatting.WHITE;
            starColor6 = EnumChatFormatting.WHITE;
            starColor7 = EnumChatFormatting.WHITE;
            star1 = "✫";
        } else if (star < 300) {
            starColor1 = EnumChatFormatting.GOLD;
            starColor2 = EnumChatFormatting.GOLD;
            starColor3 = EnumChatFormatting.GOLD;
            starColor4 = EnumChatFormatting.GOLD;
            starColor5 = EnumChatFormatting.GOLD;
            starColor6 = EnumChatFormatting.GOLD;
            starColor7 = EnumChatFormatting.GOLD;
            star1 = "✫";
        } else if (star < 400) {
            starColor1 = EnumChatFormatting.AQUA;
            starColor2 = EnumChatFormatting.AQUA;
            starColor3 = EnumChatFormatting.AQUA;
            starColor4 = EnumChatFormatting.AQUA;
            starColor5 = EnumChatFormatting.AQUA;
            starColor6 = EnumChatFormatting.AQUA;
            starColor7 = EnumChatFormatting.AQUA;
            star1 = "✫";
        } else if (star < 500) {
            starColor1 = EnumChatFormatting.DARK_GREEN;
            starColor2 = EnumChatFormatting.DARK_GREEN;
            starColor3 = EnumChatFormatting.DARK_GREEN;
            starColor4 = EnumChatFormatting.DARK_GREEN;
            starColor5 = EnumChatFormatting.DARK_GREEN;
            starColor6 = EnumChatFormatting.DARK_GREEN;
            starColor7 = EnumChatFormatting.DARK_GREEN;
            star1 = "✫";
        } else if (star < 600) {
            starColor1 = EnumChatFormatting.DARK_AQUA;
            starColor2 = EnumChatFormatting.DARK_AQUA;
            starColor3 = EnumChatFormatting.DARK_AQUA;
            starColor4 = EnumChatFormatting.DARK_AQUA;
            starColor5 = EnumChatFormatting.DARK_AQUA;
            starColor6 = EnumChatFormatting.DARK_AQUA;
            starColor7 = EnumChatFormatting.DARK_AQUA;
            star1 = "✫";
        } else if (star < 700) {
            starColor1 = EnumChatFormatting.DARK_RED;
            starColor2 = EnumChatFormatting.DARK_RED;
            starColor3 = EnumChatFormatting.DARK_RED;
            starColor4 = EnumChatFormatting.DARK_RED;
            starColor5 = EnumChatFormatting.DARK_RED;
            starColor6 = EnumChatFormatting.DARK_RED;
            starColor7 = EnumChatFormatting.DARK_RED;
            star1 = "✫";
        } else if (star < 800) {
            starColor1 = EnumChatFormatting.LIGHT_PURPLE;
            starColor2 = EnumChatFormatting.LIGHT_PURPLE;
            starColor3 = EnumChatFormatting.LIGHT_PURPLE;
            starColor4 = EnumChatFormatting.LIGHT_PURPLE;
            starColor5 = EnumChatFormatting.LIGHT_PURPLE;
            starColor6 = EnumChatFormatting.LIGHT_PURPLE;
            starColor7 = EnumChatFormatting.LIGHT_PURPLE;
            star1 = "✫";
        } else if (star < 900) {
            starColor1 = EnumChatFormatting.BLUE;
            starColor2 = EnumChatFormatting.BLUE;
            starColor3 = EnumChatFormatting.BLUE;
            starColor4 = EnumChatFormatting.BLUE;
            starColor5 = EnumChatFormatting.BLUE;
            starColor6 = EnumChatFormatting.BLUE;
            starColor7 = EnumChatFormatting.BLUE;
            star1 = "✫";
        } else if (star < 1000) {
            starColor1 = EnumChatFormatting.DARK_PURPLE;
            starColor2 = EnumChatFormatting.DARK_PURPLE;
            starColor3 = EnumChatFormatting.DARK_PURPLE;
            starColor4 = EnumChatFormatting.DARK_PURPLE;
            starColor5 = EnumChatFormatting.DARK_PURPLE;
            starColor6 = EnumChatFormatting.DARK_PURPLE;
            starColor7 = EnumChatFormatting.DARK_PURPLE;
            star1 = "✫";
        } else if (star < 1100) {
            starColor1 = EnumChatFormatting.RED;
            starColor2 = EnumChatFormatting.GOLD;
            starColor3 = EnumChatFormatting.YELLOW;
            starColor4 = EnumChatFormatting.GREEN;
            starColor5 = EnumChatFormatting.AQUA;
            starColor6 = EnumChatFormatting.LIGHT_PURPLE;
            starColor7 = EnumChatFormatting.DARK_PURPLE;
            star1 = "✫";
        } else if (star < 1200) {
            starColor1 = EnumChatFormatting.GRAY;
            starColor2 = EnumChatFormatting.WHITE;
            starColor3 = EnumChatFormatting.WHITE;
            starColor4 = EnumChatFormatting.WHITE;
            starColor5 = EnumChatFormatting.WHITE;
            starColor6 = EnumChatFormatting.GRAY;
            starColor7 = EnumChatFormatting.GRAY;
            star1 = "✪";
        } else if (star < 1300) {
            starColor1 = EnumChatFormatting.GRAY;
            starColor2 = EnumChatFormatting.YELLOW;
            starColor3 = EnumChatFormatting.YELLOW;
            starColor4 = EnumChatFormatting.YELLOW;
            starColor5 = EnumChatFormatting.YELLOW;
            starColor6 = EnumChatFormatting.GOLD;
            starColor7 = EnumChatFormatting.GRAY;
            star1 = "✪";
        } else if (star < 1400) {
            starColor1 = EnumChatFormatting.GRAY;
            starColor2 = EnumChatFormatting.AQUA;
            starColor3 = EnumChatFormatting.AQUA;
            starColor4 = EnumChatFormatting.AQUA;
            starColor5 = EnumChatFormatting.AQUA;
            starColor6 = EnumChatFormatting.DARK_AQUA;
            starColor7 = EnumChatFormatting.GRAY;
            star1 = "✪";
        } else if (star < 1500) {
            starColor1 = EnumChatFormatting.GRAY;
            starColor2 = EnumChatFormatting.GREEN;
            starColor3 = EnumChatFormatting.GREEN;
            starColor4 = EnumChatFormatting.GREEN;
            starColor5 = EnumChatFormatting.GREEN;
            starColor6 = EnumChatFormatting.DARK_GREEN;
            starColor7 = EnumChatFormatting.GRAY;
            star1 = "✪";
        } else if (star < 1600) {
            starColor1 = EnumChatFormatting.GRAY;
            starColor2 = EnumChatFormatting.DARK_AQUA;
            starColor3 = EnumChatFormatting.DARK_AQUA;
            starColor4 = EnumChatFormatting.DARK_AQUA;
            starColor5 = EnumChatFormatting.DARK_AQUA;
            starColor6 = EnumChatFormatting.BLUE;
            starColor7 = EnumChatFormatting.GRAY;
            star1 = "✪";
        } else if (star < 1700) {
            starColor1 = EnumChatFormatting.GRAY;
            starColor2 = EnumChatFormatting.RED;
            starColor3 = EnumChatFormatting.RED;
            starColor4 = EnumChatFormatting.RED;
            starColor5 = EnumChatFormatting.RED;
            starColor6 = EnumChatFormatting.DARK_RED;
            starColor7 = EnumChatFormatting.GRAY;
            star1 = "✪";
        } else if (star < 1800) {
            starColor1 = EnumChatFormatting.GRAY;
            starColor2 = EnumChatFormatting.LIGHT_PURPLE;
            starColor3 = EnumChatFormatting.LIGHT_PURPLE;
            starColor4 = EnumChatFormatting.LIGHT_PURPLE;
            starColor5 = EnumChatFormatting.LIGHT_PURPLE;
            starColor6 = EnumChatFormatting.DARK_PURPLE;
            starColor7 = EnumChatFormatting.GRAY;
            star1 = "✪";
        } else if (star < 1900) {
            starColor1 = EnumChatFormatting.GRAY;
            starColor2 = EnumChatFormatting.BLUE;
            starColor3 = EnumChatFormatting.BLUE;
            starColor4 = EnumChatFormatting.BLUE;
            starColor5 = EnumChatFormatting.BLUE;
            starColor6 = EnumChatFormatting.DARK_BLUE;
            starColor7 = EnumChatFormatting.GRAY;
            star1 = "✪";
        } else if (star < 2000) {
            starColor1 = EnumChatFormatting.GRAY;
            starColor2 = EnumChatFormatting.DARK_PURPLE;
            starColor3 = EnumChatFormatting.DARK_PURPLE;
            starColor4 = EnumChatFormatting.DARK_PURPLE;
            starColor5 = EnumChatFormatting.DARK_PURPLE;
            starColor6 = EnumChatFormatting.DARK_GRAY;
            starColor7 = EnumChatFormatting.GRAY;
            star1 = "✪";
        } else if (star < 2100) {
            starColor1 = EnumChatFormatting.DARK_GRAY;
            starColor2 = EnumChatFormatting.GRAY;
            starColor3 = EnumChatFormatting.WHITE;
            starColor4 = EnumChatFormatting.WHITE;
            starColor5 = EnumChatFormatting.GRAY;
            starColor6 = EnumChatFormatting.GRAY;
            starColor7 = EnumChatFormatting.DARK_GRAY;
            star1 = "✪";
        } else if (star < 2200) {
            starColor1 = EnumChatFormatting.WHITE;
            starColor2 = EnumChatFormatting.WHITE;
            starColor3 = EnumChatFormatting.YELLOW;
            starColor4 = EnumChatFormatting.YELLOW;
            starColor5 = EnumChatFormatting.GOLD;
            starColor6 = EnumChatFormatting.GOLD;
            starColor7 = EnumChatFormatting.GOLD;
            star1 = "⚝";
        } else if (star < 2300) {
            starColor1 = EnumChatFormatting.GOLD;
            starColor2 = EnumChatFormatting.GOLD;
            starColor3 = EnumChatFormatting.WHITE;
            starColor4 = EnumChatFormatting.WHITE;
            starColor5 = EnumChatFormatting.AQUA;
            starColor6 = EnumChatFormatting.DARK_AQUA;
            starColor7 = EnumChatFormatting.DARK_AQUA;
            star1 = "⚝";
        } else if (star < 2400) {
            starColor1 = EnumChatFormatting.DARK_PURPLE;
            starColor2 = EnumChatFormatting.DARK_PURPLE;
            starColor3 = EnumChatFormatting.LIGHT_PURPLE;
            starColor4 = EnumChatFormatting.LIGHT_PURPLE;
            starColor5 = EnumChatFormatting.GOLD;
            starColor6 = EnumChatFormatting.YELLOW;
            starColor7 = EnumChatFormatting.YELLOW;
            star1 = "⚝";
        } else if (star < 2500) {
            starColor1 = EnumChatFormatting.AQUA;
            starColor2 = EnumChatFormatting.AQUA;
            starColor3 = EnumChatFormatting.WHITE;
            starColor4 = EnumChatFormatting.WHITE;
            starColor5 = EnumChatFormatting.GRAY;
            starColor6 = EnumChatFormatting.DARK_GRAY;
            starColor7 = EnumChatFormatting.DARK_GRAY;
            star1 = "⚝";
        } else if (star < 2600) {
            starColor1 = EnumChatFormatting.WHITE;
            starColor2 = EnumChatFormatting.WHITE;
            starColor3 = EnumChatFormatting.GREEN;
            starColor4 = EnumChatFormatting.GREEN;
            starColor5 = EnumChatFormatting.DARK_GREEN;
            starColor6 = EnumChatFormatting.DARK_GREEN;
            starColor7 = EnumChatFormatting.DARK_GREEN;
            star1 = "⚝";
        } else if (star < 2700) {
            starColor1 = EnumChatFormatting.DARK_RED;
            starColor2 = EnumChatFormatting.DARK_RED;
            starColor3 = EnumChatFormatting.RED;
            starColor4 = EnumChatFormatting.RED;
            starColor5 = EnumChatFormatting.LIGHT_PURPLE;
            starColor6 = EnumChatFormatting.LIGHT_PURPLE;
            starColor7 = EnumChatFormatting.DARK_PURPLE;
            star1 = "⚝";
        } else if (star < 2800) {
            starColor1 = EnumChatFormatting.YELLOW;
            starColor2 = EnumChatFormatting.YELLOW;
            starColor3 = EnumChatFormatting.WHITE;
            starColor4 = EnumChatFormatting.WHITE;
            starColor5 = EnumChatFormatting.DARK_GRAY;
            starColor6 = EnumChatFormatting.DARK_GRAY;
            starColor7 = EnumChatFormatting.DARK_GRAY;
            star1 = "⚝";
        } else if (star < 2900) {
            starColor1 = EnumChatFormatting.GREEN;
            starColor2 = EnumChatFormatting.GREEN;
            starColor3 = EnumChatFormatting.DARK_GREEN;
            starColor4 = EnumChatFormatting.DARK_GREEN;
            starColor5 = EnumChatFormatting.GOLD;
            starColor6 = EnumChatFormatting.GOLD;
            starColor7 = EnumChatFormatting.YELLOW;
            star1 = "⚝";
        } else if (star < 3000) {
            starColor1 = EnumChatFormatting.AQUA;
            starColor2 = EnumChatFormatting.AQUA;
            starColor3 = EnumChatFormatting.DARK_AQUA;
            starColor4 = EnumChatFormatting.DARK_AQUA;
            starColor5 = EnumChatFormatting.BLUE;
            starColor6 = EnumChatFormatting.BLUE;
            starColor7 = EnumChatFormatting.BLUE;
            star1 = "⚝";
        } else if (star < 3100) {
            starColor1 = EnumChatFormatting.YELLOW;
            starColor2 = EnumChatFormatting.YELLOW;
            starColor3 = EnumChatFormatting.GOLD;
            starColor4 = EnumChatFormatting.GOLD;
            starColor5 = EnumChatFormatting.RED;
            starColor6 = EnumChatFormatting.RED;
            starColor7 = EnumChatFormatting.DARK_RED;
            star1 = "⚝";
        } else if (star > 3099) {
            starColor1 = EnumChatFormatting.YELLOW;
            starColor2 = EnumChatFormatting.YELLOW;
            starColor3 = EnumChatFormatting.GOLD;
            starColor4 = EnumChatFormatting.GOLD;
            starColor5 = EnumChatFormatting.RED;
            starColor6 = EnumChatFormatting.RED;
            starColor7 = EnumChatFormatting.DARK_RED;
            star1 = "⚝";
        }

        if (ws < 1){
            wsColor = EnumChatFormatting.GREEN;
        } else if (ws < 2){
            wsColor = EnumChatFormatting.GOLD;
        } else if (ws < 10){
            wsColor = EnumChatFormatting.DARK_RED;
        } else if (ws > 9){
            wsColor = EnumChatFormatting.DARK_PURPLE;
        }

        if (fkd < 1){
            fkdColor = EnumChatFormatting.GREEN;
        } else if (fkd < 2){
            fkdColor = EnumChatFormatting.GOLD;
        } else if (fkd < 5){
            fkdColor = EnumChatFormatting.DARK_RED;
        } else if (fkd > 4){
            fkdColor = EnumChatFormatting.DARK_PURPLE;
        }

        if (kd < 0.6){
            kdColor = EnumChatFormatting.GREEN;
        } else if (kd < 2){
            kdColor = EnumChatFormatting.GOLD;
        } else if (wl < 4){
            kdColor = EnumChatFormatting.DARK_RED;
        } else if (wl >= 4){
            kdColor = EnumChatFormatting.DARK_PURPLE;
        }

        if (wl < 0.5){
            wlColor = EnumChatFormatting.GREEN;
        } else if (wl < 1){
            wlColor = EnumChatFormatting.GOLD;
        } else if (wl < 4){
            wlColor = EnumChatFormatting.DARK_RED;
        } else if (wl >= 4){
            wlColor = EnumChatFormatting.DARK_PURPLE;
        }

        return;
    }
}

