package dev.secured;

import dev.secured.Commands.Command1;
import dev.secured.Commands.Command2;
import dev.secured.Commands.Command3;
import dev.secured.Commands.Command4;
import dev.secured.Keybinding.Keybind1;
import dev.secured.Utils.Config;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.lwjgl.input.Keyboard;

@Mod(modid = "tm", name = "Test Mod", version = "0.01")
public class BWStars {

    public static KeyBinding keybinding1 = new KeyBinding("Mass lobby lookup", Keyboard.KEY_M, "BWStars");

    @Mod.EventHandler
    public void Init(FMLInitializationEvent event){
        Config.create();
        ClientRegistry.registerKeyBinding(keybinding1);
        FMLCommonHandler.instance().bus().register(new Keybind1());
        ClientCommandHandler.instance.registerCommand(new Command1());
        ClientCommandHandler.instance.registerCommand(new Command2());
        ClientCommandHandler.instance.registerCommand(new Command3());
        ClientCommandHandler.instance.registerCommand(new Command4());
    }

    @Mod.EventHandler
    public void preInit(final FMLPreInitializationEvent event){}

    @Mod.EventHandler
    public void postInit(final FMLPostInitializationEvent event){}

}
