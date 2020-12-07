// 
// Decompiled by Procyon v0.5.36
// 

package me.earth.phobos.features.modules.client;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.event.events.ClientEvent;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.features.modules.Module;

public class Server extends Module
{
    private final Setting<Boolean> isServer;
    
    public Server() {
        super("Server", "Manages Phobos`s internal Server", Category.CLIENT, false, false, true);
        this.isServer = (Setting<Boolean>)this.register(new Setting("IsServer", (T)false));
    }
    
    @SubscribeEvent
    public void onSettingChange(final ClientEvent event) {
        if (event.getStage() == 2 && event.getSetting() != null && this.equals(event.getSetting().getFeature()) && event.getSetting().equals(this.isServer)) {
            Command.sendMessage("§cYou shouldnt touch this. I hope you know what you are doing!");
            Command.sendMessage("§cYou shouldnt touch this. I hope you know what you are doing!");
            Command.sendMessage("§cYou shouldnt touch this. I hope you know what you are doing!");
            Command.sendMessage("§cYou shouldnt touch this. I hope you know what you are doing!");
            Command.sendMessage("§cYou shouldnt touch this. I hope you know what you are doing!");
        }
    }
}
