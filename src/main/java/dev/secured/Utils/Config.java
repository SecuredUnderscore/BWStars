package dev.secured.Utils;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.common.Loader;

import java.io.*;

public class Config {
    public static File configFile = new File(Loader.instance().getConfigDir(), "bwstars.cfg");

    public static String getValueFromConfig(String value) {
        //example:
        //getValueFromConfig("api-key") returns qergijoiwequjfboqwiuefnweiofnwe

        String data = null;
        try {
            BufferedReader file = new BufferedReader(new FileReader(configFile));
            String line;

            while ((line = file.readLine()) != null) {
                if (line.contains(value)) {
                    data = line.replace(value + ": ", "");
                    file.close();
                    return data;
                }
            }

            file.close();
        } catch (Exception e) {
            System.out.println("Problem writing file.");
        }

        return data;
    }

    public static void setValueFromConfig(String value, String data) {
        //example:
        //setValueFromConfig("api-key", "qergijoiwequjfboqwiuefnweiofnwe")

        try {
            // input the (modified) file content to the StringBuffer "input"
            BufferedReader file = new BufferedReader(new FileReader(configFile));
            StringBuffer inputBuffer = new StringBuffer();
            String line;

            while ((line = file.readLine()) != null) {
                if (line.contains(value)) {
                    line = value + ": " + data;
                }
                inputBuffer.append(line);
                inputBuffer.append('\n');
            }
            file.close();

            // write the new string with the replaced line OVER the same file
            FileOutputStream fileOut = new FileOutputStream(configFile);
            fileOut.write(inputBuffer.toString().getBytes());
            fileOut.close();
        } catch (Exception e) {
            System.out.println("Problem writing file.");
        }
    }

    public static void delete() {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        //deletes the config file
        if (configFile.delete()) {
            player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.GREEN + "[BWStars] Config file has been succesfully deleted."));
            System.out.println("API key has been succesfully deleted.");
        } else {
            player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "[BWStars] Error! Config file couldn't be deleted!"));
            System.out.println("Error! API key couldn't be deleted!");
        }
        Config.create();
    }

    public static void create() {
        try {
            if (configFile.createNewFile()) {
                FileWriter w = new FileWriter(configFile, true);
                w.write("# BWStars v" + "0.01" + " Config" + System.lineSeparator());
                w.write(System.lineSeparator());
                w.write("# Hypixel API Key" + System.lineSeparator());
                w.write("api-key: empty" + System.lineSeparator());
                w.close();

                System.out.println("new config has been created (good)");
            } else {
                System.out.println("config already exists (good)");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getStoredAPIKey() {
        return getValueFromConfig("api-key");
    }

    public static void setStoredAPIKey(String key) {
        setValueFromConfig("api-key", key);
    }

}
