// 
// Decompiled by Procyon v0.5.36
// 

package me.earth.phobos.features.modules.player;

import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemTool;
import net.minecraft.entity.EntityLivingBase;
import me.earth.phobos.util.EntityUtil;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.EnumFacing;
import net.minecraft.init.Blocks;
import net.minecraft.entity.Entity;
import me.earth.phobos.Phobos;
import net.minecraft.world.World;
import net.minecraft.network.play.client.CPacketUseEntity;
import me.earth.phobos.event.events.PacketEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.item.ItemBlock;
import me.earth.phobos.features.Feature;
import net.minecraftforge.event.world.BlockEvent;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.features.modules.Module;

public class BlockTweaks extends Module
{
    public Setting<Boolean> autoTool;
    public Setting<Boolean> autoWeapon;
    public Setting<Boolean> noFriendAttack;
    public Setting<Boolean> noBlock;
    public Setting<Boolean> noGhost;
    public Setting<Boolean> destroy;
    private static BlockTweaks INSTANCE;
    private int lastHotbarSlot;
    private int currentTargetSlot;
    private boolean switched;
    
    public BlockTweaks() {
        super("BlockTweaks", "Some tweaks for blocks.", Category.PLAYER, true, false, false);
        this.autoTool = (Setting<Boolean>)this.register(new Setting("AutoTool", (T)false));
        this.autoWeapon = (Setting<Boolean>)this.register(new Setting("AutoWeapon", (T)false));
        this.noFriendAttack = (Setting<Boolean>)this.register(new Setting("NoFriendAttack", (T)false));
        this.noBlock = (Setting<Boolean>)this.register(new Setting("NoHitboxBlock", (T)true));
        this.noGhost = (Setting<Boolean>)this.register(new Setting("NoGlitchBlocks", (T)false));
        this.destroy = (Setting<Boolean>)this.register(new Setting("Destroy", (T)false, v -> this.noGhost.getValue()));
        this.lastHotbarSlot = -1;
        this.currentTargetSlot = -1;
        this.switched = false;
        this.setInstance();
    }
    
    private void setInstance() {
        BlockTweaks.INSTANCE = this;
    }
    
    public static BlockTweaks getINSTANCE() {
        if (BlockTweaks.INSTANCE == null) {
            BlockTweaks.INSTANCE = new BlockTweaks();
        }
        return BlockTweaks.INSTANCE;
    }
    
    @Override
    public void onDisable() {
        if (this.switched) {
            this.equip(this.lastHotbarSlot, false);
        }
        this.lastHotbarSlot = -1;
        this.currentTargetSlot = -1;
    }
    
    @SubscribeEvent
    public void onBreak(final BlockEvent.BreakEvent event) {
        if (Feature.fullNullCheck() || !this.noGhost.getValue() || !this.destroy.getValue()) {
            return;
        }
        if (!(BlockTweaks.mc.player.getHeldItemMainhand().getItem() instanceof ItemBlock)) {
            final BlockPos pos = BlockTweaks.mc.player.getPosition();
            this.removeGlitchBlocks(pos);
        }
    }
    
    @SubscribeEvent
    public void onBlockInteract(final PlayerInteractEvent.LeftClickBlock event) {
        if (this.autoTool.getValue() && (Speedmine.getInstance().mode.getValue() != Speedmine.Mode.PACKET || Speedmine.getInstance().isOff() || !Speedmine.getInstance().tweaks.getValue()) && !Feature.fullNullCheck() && event.getPos() != null) {
            this.equipBestTool(BlockTweaks.mc.world.getBlockState(event.getPos()));
        }
    }
    
    @SubscribeEvent
    public void onAttack(final AttackEntityEvent event) {
        if (this.autoWeapon.getValue() && !Feature.fullNullCheck() && event.getTarget() != null) {
            this.equipBestWeapon(event.getTarget());
        }
    }
    
    @SubscribeEvent
    public void onPacketSend(final PacketEvent.Send event) {
        if (fullNullCheck()) {
            return;
        }
        if (this.noFriendAttack.getValue() && event.getPacket() instanceof CPacketUseEntity) {
            final CPacketUseEntity packet = event.getPacket();
            final Entity entity = packet.getEntityFromWorld((World)BlockTweaks.mc.world);
            if (entity != null && Phobos.friendManager.isFriend(entity.getName())) {
                event.setCanceled(true);
            }
        }
    }
    
    @Override
    public void onUpdate() {
        if (!Feature.fullNullCheck()) {
            if (BlockTweaks.mc.player.inventory.currentItem != this.lastHotbarSlot && BlockTweaks.mc.player.inventory.currentItem != this.currentTargetSlot) {
                this.lastHotbarSlot = BlockTweaks.mc.player.inventory.currentItem;
            }
            if (!BlockTweaks.mc.gameSettings.keyBindAttack.isKeyDown() && this.switched) {
                this.equip(this.lastHotbarSlot, false);
            }
        }
    }
    
    private void removeGlitchBlocks(final BlockPos pos) {
        for (int dx = -4; dx <= 4; ++dx) {
            for (int dy = -4; dy <= 4; ++dy) {
                for (int dz = -4; dz <= 4; ++dz) {
                    final BlockPos blockPos = new BlockPos(pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz);
                    if (BlockTweaks.mc.world.getBlockState(blockPos).getBlock().equals(Blocks.AIR)) {
                        BlockTweaks.mc.playerController.processRightClickBlock(BlockTweaks.mc.player, BlockTweaks.mc.world, blockPos, EnumFacing.DOWN, new Vec3d(0.5, 0.5, 0.5), EnumHand.MAIN_HAND);
                    }
                }
            }
        }
    }
    
    private void equipBestTool(final IBlockState blockState) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: istore_2        /* bestSlot */
        //     2: dconst_0       
        //     3: dstore_3        /* max */
        //     4: iconst_0       
        //     5: istore          i
        //     7: iload           i
        //     9: bipush          9
        //    11: if_icmpge       114
        //    14: getstatic       me/earth/phobos/features/modules/player/BlockTweaks.mc:Lnet/minecraft/client/Minecraft;
        //    17: getfield        net/minecraft/client/Minecraft.player:Lnet/minecraft/client/entity/EntityPlayerSP;
        //    20: getfield        net/minecraft/client/entity/EntityPlayerSP.inventory:Lnet/minecraft/entity/player/InventoryPlayer;
        //    23: iload           i
        //    25: invokevirtual   net/minecraft/entity/player/InventoryPlayer.getStackInSlot:(I)Lnet/minecraft/item/ItemStack;
        //    28: astore          stack
        //    30: aload           stack
        //    32: getfield        net/minecraft/item/ItemStack.isEmpty:Z
        //    35: ifeq            41
        //    38: goto            108
        //    41: aload           stack
        //    43: aload_1         /* blockState */
        //    44: invokevirtual   net/minecraft/item/ItemStack.getDestroySpeed:(Lnet/minecraft/block/state/IBlockState;)F
        //    47: fstore          speed
        //    49: fload           speed
        //    51: fconst_1       
        //    52: fcmpl          
        //    53: ifle            108
        //    56: fload           speed
        //    58: f2d            
        //    59: getstatic       net/minecraft/init/Enchantments.EFFICIENCY:Lnet/minecraft/enchantment/Enchantment;
        //    62: aload           stack
        //    64: invokestatic    net/minecraft/enchantment/EnchantmentHelper.getEnchantmentLevel:(Lnet/minecraft/enchantment/Enchantment;Lnet/minecraft/item/ItemStack;)I
        //    67: istore          8
        //    69: iload           8
        //    71: ifle            88
        //    74: iload           eff
        //    76: i2d            
        //    77: ldc2_w          2.0
        //    80: invokestatic    java/lang/Math.pow:(DD)D
        //    83: dconst_1       
        //    84: dadd           
        //    85: goto            89
        //    88: dconst_0       
        //    89: dadd           
        //    90: d2f            
        //    91: fstore          speed
        //    93: fload           speed
        //    95: f2d            
        //    96: dload_3         /* max */
        //    97: dcmpl          
        //    98: ifle            108
        //   101: fload           speed
        //   103: f2d            
        //   104: dstore_3        /* max */
        //   105: iload           i
        //   107: istore_2        /* bestSlot */
        //   108: iinc            i, 1
        //   111: goto            7
        //   114: aload_0         /* this */
        //   115: iload_2         /* bestSlot */
        //   116: iconst_1       
        //   117: invokespecial   me/earth/phobos/features/modules/player/BlockTweaks.equip:(IZ)V
        //   120: return         
        //    StackMapTable: 00 06 FE 00 07 01 03 01 FC 00 21 07 00 AD FF 00 2E 00 08 07 00 02 07 01 42 01 03 01 07 00 AD 02 01 00 01 03 FF 00 00 00 08 07 00 02 07 01 42 01 03 01 07 00 AD 02 01 00 02 03 03 F9 00 12 FA 00 05
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        //     at com.strobel.decompiler.ast.AstBuilder.convertLocalVariables(AstBuilder.java:2895)
        //     at com.strobel.decompiler.ast.AstBuilder.performStackAnalysis(AstBuilder.java:2445)
        //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:108)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:211)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:782)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:675)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:552)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:519)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:161)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:150)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:125)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:330)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:251)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:126)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void equipBestWeapon(final Entity entity) {
        int bestSlot = -1;
        double maxDamage = 0.0;
        EnumCreatureAttribute creatureAttribute = EnumCreatureAttribute.UNDEFINED;
        if (EntityUtil.isLiving(entity)) {
            final EntityLivingBase base = (EntityLivingBase)entity;
            creatureAttribute = base.getCreatureAttribute();
        }
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = BlockTweaks.mc.player.inventory.getStackInSlot(i);
            if (!stack.isEmpty) {
                if (stack.getItem() instanceof ItemTool) {
                    final double damage = ((ItemTool)stack.getItem()).attackDamage + (double)EnchantmentHelper.getModifierForCreature(stack, creatureAttribute);
                    if (damage > maxDamage) {
                        maxDamage = damage;
                        bestSlot = i;
                    }
                }
                else if (stack.getItem() instanceof ItemSword) {
                    final double damage = ((ItemSword)stack.getItem()).getAttackDamage() + (double)EnchantmentHelper.getModifierForCreature(stack, creatureAttribute);
                    if (damage > maxDamage) {
                        maxDamage = damage;
                        bestSlot = i;
                    }
                }
            }
        }
        this.equip(bestSlot, true);
    }
    
    private void equip(final int slot, final boolean equipTool) {
        if (slot != -1) {
            if (slot != BlockTweaks.mc.player.inventory.currentItem) {
                this.lastHotbarSlot = BlockTweaks.mc.player.inventory.currentItem;
            }
            this.currentTargetSlot = slot;
            BlockTweaks.mc.player.inventory.currentItem = slot;
            BlockTweaks.mc.playerController.syncCurrentPlayItem();
            this.switched = equipTool;
        }
    }
    
    static {
        BlockTweaks.INSTANCE = new BlockTweaks();
    }
}
