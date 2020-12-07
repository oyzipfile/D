// 
// Decompiled by Procyon v0.5.36
// 

package me.earth.phobos.server.services;

import net.minecraft.network.play.client.CPacketChatMessage;

public class InputManager
{
    public boolean onCPacketChat(final CPacketChatMessage packet) {
        boolean result = true;
        if (packet.getMessage().startsWith("@Server")) {
            result = false;
            packet.getMessage().replace("@Server", "");
        }
        return result;
    }
}
