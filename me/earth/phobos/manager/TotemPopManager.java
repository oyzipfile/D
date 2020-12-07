// 
// Decompiled by Procyon v0.5.36
// 

package me.earth.phobos.manager;

import java.util.function.BiFunction;
import me.earth.phobos.Phobos;
import java.util.Iterator;
import me.earth.phobos.features.command.Command;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;
import net.minecraft.entity.player.EntityPlayer;
import java.util.Map;
import me.earth.phobos.features.modules.client.Notifications;
import me.earth.phobos.features.Feature;

public class TotemPopManager extends Feature
{
    private Notifications notifications;
    private Map<EntityPlayer, Integer> poplist;
    private Set<EntityPlayer> toAnnounce;
    
    public TotemPopManager() {
        this.poplist = new ConcurrentHashMap<EntityPlayer, Integer>();
        this.toAnnounce = new HashSet<EntityPlayer>();
    }
    
    public void onUpdate() {
        if (this.notifications.totemAnnounce.passedMs(this.notifications.delay.getValue()) && this.notifications.isOn() && this.notifications.totemPops.getValue()) {
            for (final EntityPlayer player : this.toAnnounce) {
                if (player == null) {
                    continue;
                }
                Command.sendMessage("§c" + player.getName() + " popped " + "§a" + this.getTotemPops(player) + "§c" + " Totem" + ((this.getTotemPops(player) == 1) ? "" : "s") + ".", this.notifications.totemNoti.getValue());
                this.toAnnounce.remove(player);
                this.notifications.totemAnnounce.reset();
                break;
            }
        }
    }
    
    public void onLogout() {
        this.onOwnLogout(this.notifications.clearOnLogout.getValue());
    }
    
    public void init() {
        this.notifications = Phobos.moduleManager.getModuleByClass(Notifications.class);
    }
    
    public void onTotemPop(final EntityPlayer player) {
        this.popTotem(player);
        if (!player.equals((Object)TotemPopManager.mc.player)) {
            this.toAnnounce.add(player);
            this.notifications.totemAnnounce.reset();
        }
    }
    
    public void onDeath(final EntityPlayer player) {
        if (this.getTotemPops(player) != 0 && !player.equals((Object)TotemPopManager.mc.player) && this.notifications.isOn() && this.notifications.totemPops.getValue()) {
            Command.sendMessage("§c" + player.getName() + " died after popping " + "§a" + this.getTotemPops(player) + "§c" + " Totem" + ((this.getTotemPops(player) == 1) ? "" : "s") + ".", this.notifications.totemNoti.getValue());
            this.toAnnounce.remove(player);
        }
        this.resetPops(player);
    }
    
    public void onLogout(final EntityPlayer player, final boolean clearOnLogout) {
        if (clearOnLogout) {
            this.resetPops(player);
        }
    }
    
    public void onOwnLogout(final boolean clearOnLogout) {
        if (clearOnLogout) {
            this.clearList();
        }
    }
    
    public void clearList() {
        this.poplist = new ConcurrentHashMap<EntityPlayer, Integer>();
    }
    
    public void resetPops(final EntityPlayer player) {
        this.setTotemPops(player, 0);
    }
    
    public void popTotem(final EntityPlayer player) {
        this.poplist.merge(player, 1, Integer::sum);
    }
    
    public void setTotemPops(final EntityPlayer player, final int amount) {
        this.poplist.put(player, amount);
    }
    
    public int getTotemPops(final EntityPlayer player) {
        final Integer pops = this.poplist.get(player);
        if (pops == null) {
            return 0;
        }
        return pops;
    }
    
    public String getTotemPopString(final EntityPlayer player) {
        return "§f" + ((this.getTotemPops(player) <= 0) ? "" : ("-" + this.getTotemPops(player) + " "));
    }
}
