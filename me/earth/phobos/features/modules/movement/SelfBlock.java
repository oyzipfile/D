// 
// Decompiled by Procyon v0.5.36
// 

package me.earth.phobos.features.modules.movement;

import me.earth.phobos.util.BlockUtil;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.Vec3d;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.util.InventoryUtil;
import net.minecraft.block.BlockObsidian;
import me.earth.phobos.util.Timer;
import net.minecraft.util.math.BlockPos;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.features.modules.Module;

public class SelfBlock extends Module
{
    private final Setting<Boolean> packet;
    private final Setting<Integer> delay;
    private BlockPos startPos;
    private int obbySlot;
    private final Timer timer;
    
    public SelfBlock() {
        super("SelfBlock", "Lags you back to block yourself", Category.MOVEMENT, false, false, false);
        this.packet = (Setting<Boolean>)this.register(new Setting("Packet", (T)true));
        this.delay = (Setting<Integer>)this.register(new Setting("Delay", (T)50, (T)0, (T)250));
        this.timer = new Timer();
    }
    
    @Override
    public void onEnable() {
        if (fullNullCheck()) {
            this.disable();
            return;
        }
        this.timer.reset();
        this.obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
        if (this.obbySlot == -1) {
            if (!InventoryUtil.isBlock(SelfBlock.mc.player.getHeldItemOffhand().getItem(), BlockObsidian.class)) {
                Command.sendMessage("§cOut of obby.");
                this.disable();
                return;
            }
            this.obbySlot = -2;
        }
        this.startPos = new BlockPos(SelfBlock.mc.player.getPositionVector());
        SelfBlock.mc.player.jump();
    }
    
    @Override
    public void onTick() {
        if (fullNullCheck()) {
            return;
        }
        if (this.timer.passedMs(this.delay.getValue())) {
            SelfBlock.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Rotation(SelfBlock.mc.player.rotationYaw, 90.0f, false));
            SelfBlock.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Rotation(SelfBlock.mc.player.rotationYaw, 90.0f, false));
            final int lastHotbarSlot = SelfBlock.mc.player.inventory.currentItem;
            InventoryUtil.switchToHotbarSlot(this.obbySlot, false);
            BlockUtil.rightClickBlock(this.startPos.down(), new Vec3d((Vec3i)this.startPos.down()).add(0.5, 0.5, 0.5), (this.obbySlot == -2) ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, EnumFacing.UP, this.packet.getValue());
            if (this.obbySlot != -2) {
                InventoryUtil.switchToHotbarSlot(lastHotbarSlot, false);
            }
            SelfBlock.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Rotation(SelfBlock.mc.player.rotationYaw, 90.0f, false));
            SelfBlock.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Rotation(SelfBlock.mc.player.rotationYaw, 90.0f, false));
            SelfBlock.mc.player.connection.sendPacket((Packet)new CPacketPlayer.PositionRotation((double)this.startPos.getX(), (double)this.startPos.getY(), (double)this.startPos.getZ(), SelfBlock.mc.player.rotationYaw, 90.0f, true));
            SelfBlock.mc.player.connection.sendPacket((Packet)new CPacketPlayer.PositionRotation((double)this.startPos.getX(), (double)this.startPos.getY(), (double)this.startPos.getZ(), SelfBlock.mc.player.rotationYaw, 90.0f, true));
            SelfBlock.mc.player.connection.sendPacket((Packet)new CPacketPlayer.PositionRotation((double)this.startPos.getX(), (double)this.startPos.getY(), (double)this.startPos.getZ(), SelfBlock.mc.player.rotationYaw, 90.0f, true));
            SelfBlock.mc.player.connection.sendPacket((Packet)new CPacketPlayer.PositionRotation((double)this.startPos.getX(), (double)this.startPos.getY(), (double)this.startPos.getZ(), SelfBlock.mc.player.rotationYaw, 90.0f, true));
            SelfBlock.mc.player.connection.sendPacket((Packet)new CPacketPlayer.PositionRotation((double)this.startPos.getX(), (double)this.startPos.getY(), (double)this.startPos.getZ(), SelfBlock.mc.player.rotationYaw, 90.0f, true));
            this.disable();
        }
    }
}
