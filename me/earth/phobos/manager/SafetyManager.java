// 
// Decompiled by Procyon v0.5.36
// 

package me.earth.phobos.manager;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.util.Iterator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import me.earth.phobos.util.BlockUtil;
import me.earth.phobos.util.DamageUtil;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.Entity;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.features.modules.client.Managers;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.ScheduledExecutorService;
import me.earth.phobos.util.Timer;
import me.earth.phobos.features.Feature;

public class SafetyManager extends Feature implements Runnable
{
    private final Timer syncTimer;
    private ScheduledExecutorService service;
    private final AtomicBoolean SAFE;
    
    public SafetyManager() {
        this.syncTimer = new Timer();
        this.SAFE = new AtomicBoolean(false);
    }
    
    @Override
    public void run() {
        if (!Feature.fullNullCheck()) {
            boolean safe = true;
            final EntityPlayer closest = Managers.getInstance().safety.getValue() ? EntityUtil.getClosestEnemy(18.0) : null;
            if (Managers.getInstance().safety.getValue() && closest == null) {
                this.SAFE.set(true);
                return;
            }
            for (final Entity crystal : SafetyManager.mc.world.loadedEntityList) {
                if (crystal instanceof EntityEnderCrystal && DamageUtil.calculateDamage(crystal, (Entity)SafetyManager.mc.player) > 4.0 && (closest == null || closest.getDistanceSq(crystal) < 40.0)) {
                    safe = false;
                    break;
                }
            }
            if (safe) {
                for (final BlockPos pos : BlockUtil.possiblePlacePositions(4.0f, false, Managers.getInstance().oneDot15.getValue())) {
                    if (DamageUtil.calculateDamage(pos, (Entity)SafetyManager.mc.player) > 4.0 && (closest == null || closest.getDistanceSq(pos) < 40.0)) {
                        safe = false;
                        break;
                    }
                }
            }
            this.SAFE.set(safe);
        }
    }
    
    public void onUpdate() {
        this.run();
    }
    
    public String getSafetyString() {
        if (this.SAFE.get()) {
            return "§aSecure";
        }
        return "§cUnsafe";
    }
    
    public boolean isSafe() {
        return this.SAFE.get();
    }
    
    public ScheduledExecutorService getService() {
        final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(this, 0L, Managers.getInstance().safetyCheck.getValue(), TimeUnit.MILLISECONDS);
        return service;
    }
}
