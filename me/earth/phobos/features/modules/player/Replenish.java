// 
// Decompiled by Procyon v0.5.36
// 

package me.earth.phobos.features.modules.player;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.Iterator;
import net.minecraft.init.Items;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.gui.inventory.GuiContainer;
import me.earth.phobos.features.modules.combat.Auto32k;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentHashMap;
import me.earth.phobos.util.InventoryUtil;
import java.util.Queue;
import net.minecraft.item.ItemStack;
import java.util.Map;
import me.earth.phobos.util.Timer;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.features.modules.Module;

public class Replenish extends Module
{
    private final Setting<Integer> threshold;
    private final Setting<Integer> replenishments;
    private final Setting<Integer> updates;
    private final Setting<Integer> actions;
    private final Setting<Boolean> pauseInv;
    private final Setting<Boolean> putBack;
    private final Timer timer;
    private final Timer replenishTimer;
    private Map<Integer, ItemStack> hotbar;
    private final Queue<InventoryUtil.Task> taskList;
    
    public Replenish() {
        super("Replenish", "Replenishes your hotbar", Category.PLAYER, false, false, false);
        this.threshold = (Setting<Integer>)this.register(new Setting("Threshold", (T)0, (T)0, (T)63));
        this.replenishments = (Setting<Integer>)this.register(new Setting("RUpdates", (T)0, (T)0, (T)1000));
        this.updates = (Setting<Integer>)this.register(new Setting("HBUpdates", (T)100, (T)0, (T)1000));
        this.actions = (Setting<Integer>)this.register(new Setting("Actions", (T)2, (T)1, (T)30));
        this.pauseInv = (Setting<Boolean>)this.register(new Setting("PauseInv", (T)true));
        this.putBack = (Setting<Boolean>)this.register(new Setting("PutBack", (T)true));
        this.timer = new Timer();
        this.replenishTimer = new Timer();
        this.hotbar = new ConcurrentHashMap<Integer, ItemStack>();
        this.taskList = new ConcurrentLinkedQueue<InventoryUtil.Task>();
    }
    
    @Override
    public void onUpdate() {
        if (Auto32k.getInstance().isOn() && (!Auto32k.getInstance().autoSwitch.getValue() || Auto32k.getInstance().switching)) {
            return;
        }
        if (Replenish.mc.currentScreen instanceof GuiContainer && (!(Replenish.mc.currentScreen instanceof GuiInventory) || this.pauseInv.getValue())) {
            return;
        }
        if (this.timer.passedMs(this.updates.getValue())) {
            this.mapHotbar();
        }
        if (this.replenishTimer.passedMs(this.replenishments.getValue())) {
            for (int i = 0; i < this.actions.getValue(); ++i) {
                final InventoryUtil.Task task = this.taskList.poll();
                if (task != null) {
                    task.run();
                }
            }
            this.replenishTimer.reset();
        }
    }
    
    @Override
    public void onDisable() {
        this.hotbar.clear();
    }
    
    @Override
    public void onLogout() {
        this.onDisable();
    }
    
    private void mapHotbar() {
        final Map<Integer, ItemStack> map = new ConcurrentHashMap<Integer, ItemStack>();
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = Replenish.mc.player.inventory.getStackInSlot(i);
            map.put(i, stack);
        }
        if (this.hotbar.isEmpty()) {
            this.hotbar = map;
            return;
        }
        final Map<Integer, Integer> fromTo = new ConcurrentHashMap<Integer, Integer>();
        for (final Map.Entry<Integer, ItemStack> hotbarItem : map.entrySet()) {
            final ItemStack stack2 = hotbarItem.getValue();
            final Integer slotKey = hotbarItem.getKey();
            if (slotKey != null && stack2 != null && (stack2.isEmpty || stack2.getItem() == Items.AIR || (stack2.stackSize <= this.threshold.getValue() && stack2.stackSize < stack2.getMaxStackSize()))) {
                ItemStack previousStack = hotbarItem.getValue();
                if (stack2.isEmpty || stack2.getItem() != Items.AIR) {
                    previousStack = this.hotbar.get(slotKey);
                }
                if (previousStack == null || previousStack.isEmpty || previousStack.getItem() == Items.AIR) {
                    continue;
                }
                final int replenishSlot = this.getReplenishSlot(previousStack);
                if (replenishSlot == -1) {
                    continue;
                }
                fromTo.put(replenishSlot, InventoryUtil.convertHotbarToInv(slotKey));
            }
        }
        if (!fromTo.isEmpty()) {
            for (final Map.Entry<Integer, Integer> slotMove : fromTo.entrySet()) {
                this.taskList.add(new InventoryUtil.Task(slotMove.getKey()));
                this.taskList.add(new InventoryUtil.Task(slotMove.getValue()));
                this.taskList.add(new InventoryUtil.Task(slotMove.getKey()));
                this.taskList.add(new InventoryUtil.Task());
            }
        }
        this.hotbar = map;
    }
    
    private int getReplenishSlot(final ItemStack stack) {
        final AtomicInteger slot = new AtomicInteger();
        slot.set(-1);
        for (final Map.Entry<Integer, ItemStack> entry : InventoryUtil.getInventoryAndHotbarSlots().entrySet()) {
            if (entry.getKey() < 36 && InventoryUtil.areStacksCompatible(stack, entry.getValue())) {
                slot.set(entry.getKey());
                return slot.get();
            }
        }
        return slot.get();
    }
}
