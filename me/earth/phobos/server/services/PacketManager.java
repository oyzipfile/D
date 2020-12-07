// 
// Decompiled by Procyon v0.5.36
// 

package me.earth.phobos.server.services;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import me.earth.phobos.event.events.PacketEvent;
import net.minecraft.network.Packet;

public class PacketManager
{
    public void handleClientPacket(final Packet<?> packet) {
    }
    
    public void sendPacketToClient(final Packet<?> packet) {
    }
    
    public void controlPacket(final Packet<?> packet) {
    }
    
    @SubscribeEvent
    public void onPacketReceive(final PacketEvent.Receive event) {
        this.sendPacketToClient(event.getPacket());
    }
    
    @SubscribeEvent
    public void onPacketSend(final PacketEvent.Send event) {
        this.controlPacket(event.getPacket());
    }
}
