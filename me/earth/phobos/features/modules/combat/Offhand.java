// 
// Decompiled by Procyon v0.5.36
// 

package me.earth.phobos.features.modules.combat;

import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import me.earth.phobos.Phobos;
import net.minecraft.item.Item;
import net.minecraft.entity.Entity;
import me.earth.phobos.util.EntityUtil;
import net.minecraft.item.ItemSword;
import java.util.function.ToIntFunction;
import net.minecraft.block.BlockWeb;
import net.minecraft.block.BlockObsidian;
import net.minecraft.inventory.ClickType;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import me.earth.phobos.event.events.PacketEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import me.earth.phobos.features.setting.EnumConverter;
import org.lwjgl.input.Keyboard;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Mouse;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.world.World;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.util.EnumHand;
import me.earth.phobos.event.events.ProcessRightClickBlockEvent;
import me.earth.phobos.features.Feature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import java.util.concurrent.ConcurrentLinkedQueue;
import me.earth.phobos.util.Timer;
import me.earth.phobos.util.InventoryUtil;
import java.util.Queue;
import me.earth.phobos.features.setting.Bind;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.features.modules.Module;

public class Offhand extends Module
{
    public Setting<Type> type;
    public Setting<Boolean> cycle;
    public Setting<Bind> cycleKey;
    public Setting<Bind> offHandGapple;
    public Setting<Float> gappleHealth;
    public Setting<Float> gappleHoleHealth;
    public Setting<Bind> offHandCrystal;
    public Setting<Float> crystalHealth;
    public Setting<Float> crystalHoleHealth;
    public Setting<Float> cTargetDistance;
    public Setting<Bind> obsidian;
    public Setting<Float> obsidianHealth;
    public Setting<Float> obsidianHoleHealth;
    public Setting<Bind> webBind;
    public Setting<Float> webHealth;
    public Setting<Float> webHoleHealth;
    public Setting<Boolean> holeCheck;
    public Setting<Boolean> crystalCheck;
    public Setting<Boolean> gapSwap;
    public Setting<Integer> updates;
    public Setting<Boolean> cycleObby;
    public Setting<Boolean> cycleWebs;
    public Setting<Boolean> crystalToTotem;
    public Setting<Boolean> absorption;
    public Setting<Boolean> autoGapple;
    public Setting<Boolean> onlyWTotem;
    public Setting<Boolean> unDrawTotem;
    public Setting<Boolean> noOffhandGC;
    public Setting<Boolean> returnToCrystal;
    public Setting<Integer> timeout;
    public Setting<Integer> timeout2;
    public Setting<Integer> actions;
    public Setting<NameMode> displayNameChange;
    public Setting<Boolean> guis;
    public Mode mode;
    public Mode oldMode;
    private int oldSlot;
    private boolean swapToTotem;
    private boolean eatingApple;
    private boolean oldSwapToTotem;
    public Mode2 currentMode;
    public int totems;
    public int crystals;
    public int gapples;
    public int obby;
    public int webs;
    public int lastTotemSlot;
    public int lastGappleSlot;
    public int lastCrystalSlot;
    public int lastObbySlot;
    public int lastWebSlot;
    public boolean holdingCrystal;
    public boolean holdingTotem;
    public boolean holdingGapple;
    public boolean holdingObby;
    public boolean holdingWeb;
    public boolean didSwitchThisTick;
    private final Queue<InventoryUtil.Task> taskList;
    private boolean autoGappleSwitch;
    private static Offhand instance;
    private Timer timer;
    private Timer secondTimer;
    private boolean second;
    private boolean switchedForHealthReason;
    
    public Offhand() {
        super("Offhand", "Allows you to switch up your Offhand.", Category.COMBAT, true, false, false);
        this.type = (Setting<Type>)this.register(new Setting("Mode", (T)Type.NEW));
        this.cycle = (Setting<Boolean>)this.register(new Setting("Cycle", (T)false, v -> this.type.getValue() == Type.OLD));
        this.cycleKey = (Setting<Bind>)this.register(new Setting("Key", (T)new Bind(-1), v -> this.cycle.getValue() && this.type.getValue() == Type.OLD));
        this.offHandGapple = (Setting<Bind>)this.register(new Setting("Gapple", (T)new Bind(-1)));
        this.gappleHealth = (Setting<Float>)this.register(new Setting("G-Health", (T)13.0f, (T)0.1f, (T)36.0f));
        this.gappleHoleHealth = (Setting<Float>)this.register(new Setting("G-H-Health", (T)3.5f, (T)0.1f, (T)36.0f));
        this.offHandCrystal = (Setting<Bind>)this.register(new Setting("Crystal", (T)new Bind(-1)));
        this.crystalHealth = (Setting<Float>)this.register(new Setting("C-Health", (T)13.0f, (T)0.1f, (T)36.0f));
        this.crystalHoleHealth = (Setting<Float>)this.register(new Setting("C-H-Health", (T)3.5f, (T)0.1f, (T)36.0f));
        this.cTargetDistance = (Setting<Float>)this.register(new Setting("C-Distance", (T)10.0f, (T)1.0f, (T)20.0f));
        this.obsidian = (Setting<Bind>)this.register(new Setting("Obsidian", (T)new Bind(-1)));
        this.obsidianHealth = (Setting<Float>)this.register(new Setting("O-Health", (T)13.0f, (T)0.1f, (T)36.0f));
        this.obsidianHoleHealth = (Setting<Float>)this.register(new Setting("O-H-Health", (T)8.0f, (T)0.1f, (T)36.0f));
        this.webBind = (Setting<Bind>)this.register(new Setting("Webs", (T)new Bind(-1)));
        this.webHealth = (Setting<Float>)this.register(new Setting("W-Health", (T)13.0f, (T)0.1f, (T)36.0f));
        this.webHoleHealth = (Setting<Float>)this.register(new Setting("W-H-Health", (T)8.0f, (T)0.1f, (T)36.0f));
        this.holeCheck = (Setting<Boolean>)this.register(new Setting("Hole-Check", (T)true));
        this.crystalCheck = (Setting<Boolean>)this.register(new Setting("Crystal-Check", (T)false));
        this.gapSwap = (Setting<Boolean>)this.register(new Setting("Gap-Swap", (T)true));
        this.updates = (Setting<Integer>)this.register(new Setting("Updates", (T)1, (T)1, (T)2));
        this.cycleObby = (Setting<Boolean>)this.register(new Setting("CycleObby", (T)false, v -> this.type.getValue() == Type.OLD));
        this.cycleWebs = (Setting<Boolean>)this.register(new Setting("CycleWebs", (T)false, v -> this.type.getValue() == Type.OLD));
        this.crystalToTotem = (Setting<Boolean>)this.register(new Setting("Crystal-Totem", (T)true, v -> this.type.getValue() == Type.OLD));
        this.absorption = (Setting<Boolean>)this.register(new Setting("Absorption", (T)false, v -> this.type.getValue() == Type.OLD));
        this.autoGapple = (Setting<Boolean>)this.register(new Setting("AutoGapple", (T)false, v -> this.type.getValue() == Type.OLD));
        this.onlyWTotem = (Setting<Boolean>)this.register(new Setting("OnlyWTotem", (T)true, v -> this.autoGapple.getValue() && this.type.getValue() == Type.OLD));
        this.unDrawTotem = (Setting<Boolean>)this.register(new Setting("DrawTotems", (T)true, v -> this.type.getValue() == Type.OLD));
        this.noOffhandGC = (Setting<Boolean>)this.register(new Setting("NoOGC", (T)false));
        this.returnToCrystal = (Setting<Boolean>)this.register(new Setting("RecoverySwitch", (T)false));
        this.timeout = (Setting<Integer>)this.register(new Setting("Timeout", (T)50, (T)0, (T)500));
        this.timeout2 = (Setting<Integer>)this.register(new Setting("Timeout2", (T)50, (T)0, (T)500));
        this.actions = (Setting<Integer>)this.register(new Setting("Actions", (T)4, (T)1, (T)4, v -> this.type.getValue() == Type.OLD));
        this.displayNameChange = (Setting<NameMode>)this.register(new Setting("Name", (T)NameMode.TOTEM, v -> this.type.getValue() == Type.OLD));
        this.guis = (Setting<Boolean>)this.register(new Setting("Guis", (T)false));
        this.mode = Mode.CRYSTALS;
        this.oldMode = Mode.CRYSTALS;
        this.oldSlot = -1;
        this.swapToTotem = false;
        this.eatingApple = false;
        this.oldSwapToTotem = false;
        this.currentMode = Mode2.TOTEMS;
        this.totems = 0;
        this.crystals = 0;
        this.gapples = 0;
        this.obby = 0;
        this.webs = 0;
        this.lastTotemSlot = -1;
        this.lastGappleSlot = -1;
        this.lastCrystalSlot = -1;
        this.lastObbySlot = -1;
        this.lastWebSlot = -1;
        this.holdingCrystal = false;
        this.holdingTotem = false;
        this.holdingGapple = false;
        this.holdingObby = false;
        this.holdingWeb = false;
        this.didSwitchThisTick = false;
        this.taskList = new ConcurrentLinkedQueue<InventoryUtil.Task>();
        this.autoGappleSwitch = false;
        this.timer = new Timer();
        this.secondTimer = new Timer();
        this.second = false;
        this.switchedForHealthReason = false;
        Offhand.instance = this;
    }
    
    public static Offhand getInstance() {
        if (Offhand.instance == null) {
            Offhand.instance = new Offhand();
        }
        return Offhand.instance;
    }
    
    public void onItemFinish(final ItemStack stack, final EntityLivingBase base) {
        if (this.noOffhandGC.getValue() && base.equals((Object)Offhand.mc.player) && stack.getItem() == Offhand.mc.player.getHeldItemOffhand().getItem()) {
            this.secondTimer.reset();
            this.second = true;
        }
    }
    
    @Override
    public void onTick() {
        if (Feature.nullCheck() || this.updates.getValue() == 1) {
            return;
        }
        this.doOffhand();
    }
    
    @SubscribeEvent
    public void onUpdateWalkingPlayer(final ProcessRightClickBlockEvent event) {
        if (this.noOffhandGC.getValue() && event.hand == EnumHand.MAIN_HAND && event.stack.getItem() == Items.END_CRYSTAL && Offhand.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE && Offhand.mc.objectMouseOver != null && event.pos == Offhand.mc.objectMouseOver.getBlockPos()) {
            event.setCanceled(true);
            Offhand.mc.player.setActiveHand(EnumHand.OFF_HAND);
            Offhand.mc.playerController.processRightClick((EntityPlayer)Offhand.mc.player, (World)Offhand.mc.world, EnumHand.OFF_HAND);
        }
    }
    
    @Override
    public void onUpdate() {
        if (this.noOffhandGC.getValue()) {
            if (this.timer.passedMs(this.timeout.getValue())) {
                if (Offhand.mc.player != null && Offhand.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE && Offhand.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL && Mouse.isButtonDown(1)) {
                    Offhand.mc.player.setActiveHand(EnumHand.OFF_HAND);
                    Offhand.mc.gameSettings.keyBindUseItem.pressed = Mouse.isButtonDown(1);
                }
            }
            else if (Offhand.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE && Offhand.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL) {
                Offhand.mc.gameSettings.keyBindUseItem.pressed = false;
            }
        }
        if (Feature.nullCheck() || this.updates.getValue() == 2) {
            return;
        }
        this.doOffhand();
        if (this.secondTimer.passedMs(this.timeout2.getValue()) && this.second) {
            this.second = false;
            this.timer.reset();
        }
    }
    
    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    public void onKeyInput(final InputEvent.KeyInputEvent event) {
        if (Keyboard.getEventKeyState()) {
            if (this.type.getValue() == Type.NEW) {
                if (this.offHandCrystal.getValue().getKey() == Keyboard.getEventKey()) {
                    if (this.mode == Mode.CRYSTALS) {
                        this.setSwapToTotem(!this.isSwapToTotem());
                    }
                    else {
                        this.setSwapToTotem(false);
                    }
                    this.setMode(Mode.CRYSTALS);
                }
                if (this.offHandGapple.getValue().getKey() == Keyboard.getEventKey()) {
                    if (this.mode == Mode.GAPPLES) {
                        this.setSwapToTotem(!this.isSwapToTotem());
                    }
                    else {
                        this.setSwapToTotem(false);
                    }
                    this.setMode(Mode.GAPPLES);
                }
                if (this.obsidian.getValue().getKey() == Keyboard.getEventKey()) {
                    if (this.mode == Mode.OBSIDIAN) {
                        this.setSwapToTotem(!this.isSwapToTotem());
                    }
                    else {
                        this.setSwapToTotem(false);
                    }
                    this.setMode(Mode.OBSIDIAN);
                }
                if (this.webBind.getValue().getKey() == Keyboard.getEventKey()) {
                    if (this.mode == Mode.WEBS) {
                        this.setSwapToTotem(!this.isSwapToTotem());
                    }
                    else {
                        this.setSwapToTotem(false);
                    }
                    this.setMode(Mode.WEBS);
                }
            }
            else if (this.cycle.getValue()) {
                if (this.cycleKey.getValue().getKey() == Keyboard.getEventKey()) {
                    Mode2 newMode = (Mode2)EnumConverter.increaseEnum(this.currentMode);
                    if ((newMode == Mode2.OBSIDIAN && !this.cycleObby.getValue()) || (newMode == Mode2.WEBS && !this.cycleWebs.getValue())) {
                        newMode = Mode2.TOTEMS;
                    }
                    this.setMode(newMode);
                }
            }
            else {
                if (this.offHandCrystal.getValue().getKey() == Keyboard.getEventKey()) {
                    this.setMode(Mode2.CRYSTALS);
                }
                if (this.offHandGapple.getValue().getKey() == Keyboard.getEventKey()) {
                    this.setMode(Mode2.GAPPLES);
                }
                if (this.obsidian.getValue().getKey() == Keyboard.getEventKey()) {
                    this.setMode(Mode2.OBSIDIAN);
                }
                if (this.webBind.getValue().getKey() == Keyboard.getEventKey()) {
                    this.setMode(Mode2.WEBS);
                }
            }
        }
    }
    
    @SubscribeEvent
    public void onPacketSend(final PacketEvent.Send event) {
        if (this.noOffhandGC.getValue() && !Feature.fullNullCheck() && Offhand.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE && Offhand.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL && Offhand.mc.gameSettings.keyBindUseItem.isKeyDown()) {
            if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
                final CPacketPlayerTryUseItemOnBlock packet = event.getPacket();
                if (packet.getHand() == EnumHand.MAIN_HAND && !AutoCrystal.placedPos.contains(packet.getPos())) {
                    if (this.timer.passedMs(this.timeout.getValue())) {
                        Offhand.mc.player.setActiveHand(EnumHand.OFF_HAND);
                        Offhand.mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItem(EnumHand.OFF_HAND));
                    }
                    event.setCanceled(true);
                }
            }
            else if (event.getPacket() instanceof CPacketPlayerTryUseItem) {
                final CPacketPlayerTryUseItem packet2 = event.getPacket();
                if (packet2.getHand() == EnumHand.OFF_HAND && !this.timer.passedMs(this.timeout.getValue())) {
                    event.setCanceled(true);
                }
            }
        }
    }
    
    @Override
    public String getDisplayInfo() {
        if (this.type.getValue() == Type.NEW) {
            return String.valueOf(this.getStackSize());
        }
        switch (this.displayNameChange.getValue()) {
            case MODE: {
                return EnumConverter.getProperName(this.currentMode);
            }
            case TOTEM: {
                if (this.currentMode == Mode2.TOTEMS) {
                    return this.totems + "";
                }
                return EnumConverter.getProperName(this.currentMode);
            }
            default: {
                switch (this.currentMode) {
                    case TOTEMS: {
                        return this.totems + "";
                    }
                    case GAPPLES: {
                        return this.gapples + "";
                    }
                    default: {
                        return this.crystals + "";
                    }
                }
                break;
            }
        }
    }
    
    @Override
    public String getDisplayName() {
        if (this.type.getValue() == Type.NEW) {
            if (this.shouldTotem()) {
                return "AutoTotem" + (this.isSwapToTotem() ? "" : ("-" + this.getModeStr()));
            }
            switch (this.mode) {
                case GAPPLES: {
                    return "OffhandGapple";
                }
                case WEBS: {
                    return "OffhandWebs";
                }
                case OBSIDIAN: {
                    return "OffhandObby";
                }
                default: {
                    return "OffhandCrystal";
                }
            }
        }
        else {
            switch (this.displayNameChange.getValue()) {
                case MODE: {
                    return this.displayName.getValue();
                }
                case TOTEM: {
                    if (this.currentMode == Mode2.TOTEMS) {
                        return "AutoTotem";
                    }
                    return this.displayName.getValue();
                }
                default: {
                    switch (this.currentMode) {
                        case TOTEMS: {
                            return "AutoTotem";
                        }
                        case GAPPLES: {
                            return "OffhandGapple";
                        }
                        case WEBS: {
                            return "OffhandWebs";
                        }
                        case OBSIDIAN: {
                            return "OffhandObby";
                        }
                        default: {
                            return "OffhandCrystal";
                        }
                    }
                    break;
                }
            }
        }
    }
    
    public void doOffhand() {
        if (this.type.getValue() == Type.NEW) {
            if (Offhand.mc.currentScreen instanceof GuiContainer && !this.guis.getValue() && !(Offhand.mc.currentScreen instanceof GuiInventory)) {
                return;
            }
            if (this.gapSwap.getValue()) {
                if ((this.getSlot(Mode.GAPPLES) != -1 || Offhand.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE) && Offhand.mc.player.getHeldItemMainhand().getItem() != Items.GOLDEN_APPLE && Offhand.mc.gameSettings.keyBindUseItem.isKeyDown()) {
                    this.setMode(Mode.GAPPLES);
                    this.eatingApple = true;
                    this.swapToTotem = false;
                }
                else if (this.eatingApple) {
                    this.setMode(this.oldMode);
                    this.swapToTotem = this.oldSwapToTotem;
                    this.eatingApple = false;
                }
                else {
                    this.oldMode = this.mode;
                    this.oldSwapToTotem = this.swapToTotem;
                }
            }
            if (!this.shouldTotem()) {
                if (Offhand.mc.player.getHeldItemOffhand() == ItemStack.EMPTY || !this.isItemInOffhand()) {
                    final int slot = (this.getSlot(this.mode) < 9) ? (this.getSlot(this.mode) + 36) : this.getSlot(this.mode);
                    if (this.getSlot(this.mode) != -1) {
                        if (this.oldSlot != -1) {
                            Offhand.mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, (EntityPlayer)Offhand.mc.player);
                            Offhand.mc.playerController.windowClick(0, this.oldSlot, 0, ClickType.PICKUP, (EntityPlayer)Offhand.mc.player);
                        }
                        this.oldSlot = slot;
                        Offhand.mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, (EntityPlayer)Offhand.mc.player);
                        Offhand.mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, (EntityPlayer)Offhand.mc.player);
                        Offhand.mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, (EntityPlayer)Offhand.mc.player);
                    }
                }
            }
            else if (!this.eatingApple && (Offhand.mc.player.getHeldItemOffhand() == ItemStack.EMPTY || Offhand.mc.player.getHeldItemOffhand().getItem() != Items.TOTEM_OF_UNDYING)) {
                final int slot = (this.getTotemSlot() < 9) ? (this.getTotemSlot() + 36) : this.getTotemSlot();
                if (this.getTotemSlot() != -1) {
                    Offhand.mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, (EntityPlayer)Offhand.mc.player);
                    Offhand.mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, (EntityPlayer)Offhand.mc.player);
                    Offhand.mc.playerController.windowClick(0, this.oldSlot, 0, ClickType.PICKUP, (EntityPlayer)Offhand.mc.player);
                    this.oldSlot = -1;
                }
            }
        }
        else {
            if (!this.unDrawTotem.getValue()) {
                this.manageDrawn();
            }
            this.didSwitchThisTick = false;
            this.holdingCrystal = (Offhand.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL);
            this.holdingTotem = (Offhand.mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING);
            this.holdingGapple = (Offhand.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE);
            this.holdingObby = InventoryUtil.isBlock(Offhand.mc.player.getHeldItemOffhand().getItem(), BlockObsidian.class);
            this.holdingWeb = InventoryUtil.isBlock(Offhand.mc.player.getHeldItemOffhand().getItem(), BlockWeb.class);
            this.totems = Offhand.mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::func_190916_E).sum();
            if (this.holdingTotem) {
                this.totems += Offhand.mc.player.inventory.offHandInventory.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::func_190916_E).sum();
            }
            this.crystals = Offhand.mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.END_CRYSTAL).mapToInt(ItemStack::func_190916_E).sum();
            if (this.holdingCrystal) {
                this.crystals += Offhand.mc.player.inventory.offHandInventory.stream().filter(itemStack -> itemStack.getItem() == Items.END_CRYSTAL).mapToInt(ItemStack::func_190916_E).sum();
            }
            this.gapples = Offhand.mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.GOLDEN_APPLE).mapToInt(ItemStack::func_190916_E).sum();
            if (this.holdingGapple) {
                this.gapples += Offhand.mc.player.inventory.offHandInventory.stream().filter(itemStack -> itemStack.getItem() == Items.GOLDEN_APPLE).mapToInt(ItemStack::func_190916_E).sum();
            }
            if (this.currentMode == Mode2.WEBS || this.currentMode == Mode2.OBSIDIAN) {
                this.obby = Offhand.mc.player.inventory.mainInventory.stream().filter(itemStack -> InventoryUtil.isBlock(itemStack.getItem(), BlockObsidian.class)).mapToInt(ItemStack::func_190916_E).sum();
                if (this.holdingObby) {
                    this.obby += Offhand.mc.player.inventory.offHandInventory.stream().filter(itemStack -> InventoryUtil.isBlock(itemStack.getItem(), BlockObsidian.class)).mapToInt(ItemStack::func_190916_E).sum();
                }
                this.webs = Offhand.mc.player.inventory.mainInventory.stream().filter(itemStack -> InventoryUtil.isBlock(itemStack.getItem(), BlockWeb.class)).mapToInt(ItemStack::func_190916_E).sum();
                if (this.holdingWeb) {
                    this.webs += Offhand.mc.player.inventory.offHandInventory.stream().filter(itemStack -> InventoryUtil.isBlock(itemStack.getItem(), BlockWeb.class)).mapToInt(ItemStack::func_190916_E).sum();
                }
            }
            this.doSwitch();
        }
    }
    
    private void manageDrawn() {
        if (this.currentMode == Mode2.TOTEMS && this.drawn.getValue()) {
            this.drawn.setValue(false);
        }
        if (this.currentMode != Mode2.TOTEMS && !this.drawn.getValue()) {
            this.drawn.setValue(true);
        }
    }
    
    public void doSwitch() {
        if (this.autoGapple.getValue()) {
            if (Offhand.mc.gameSettings.keyBindUseItem.isKeyDown()) {
                if (Offhand.mc.player.getHeldItemMainhand().getItem() instanceof ItemSword && (!this.onlyWTotem.getValue() || Offhand.mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING)) {
                    this.setMode(Mode.GAPPLES);
                    this.autoGappleSwitch = true;
                }
            }
            else if (this.autoGappleSwitch) {
                this.setMode(Mode2.TOTEMS);
                this.autoGappleSwitch = false;
            }
        }
        if ((this.currentMode == Mode2.GAPPLES && ((!EntityUtil.isSafe((Entity)Offhand.mc.player) && EntityUtil.getHealth((Entity)Offhand.mc.player, this.absorption.getValue()) <= this.gappleHealth.getValue()) || EntityUtil.getHealth((Entity)Offhand.mc.player, this.absorption.getValue()) <= this.gappleHoleHealth.getValue())) || (this.currentMode == Mode2.CRYSTALS && ((!EntityUtil.isSafe((Entity)Offhand.mc.player) && EntityUtil.getHealth((Entity)Offhand.mc.player, this.absorption.getValue()) <= this.crystalHealth.getValue()) || EntityUtil.getHealth((Entity)Offhand.mc.player, this.absorption.getValue()) <= this.crystalHoleHealth.getValue())) || (this.currentMode == Mode2.OBSIDIAN && ((!EntityUtil.isSafe((Entity)Offhand.mc.player) && EntityUtil.getHealth((Entity)Offhand.mc.player, this.absorption.getValue()) <= this.obsidianHealth.getValue()) || EntityUtil.getHealth((Entity)Offhand.mc.player, this.absorption.getValue()) <= this.obsidianHoleHealth.getValue())) || (this.currentMode == Mode2.WEBS && ((!EntityUtil.isSafe((Entity)Offhand.mc.player) && EntityUtil.getHealth((Entity)Offhand.mc.player, this.absorption.getValue()) <= this.webHealth.getValue()) || EntityUtil.getHealth((Entity)Offhand.mc.player, this.absorption.getValue()) <= this.webHoleHealth.getValue()))) {
            if (this.returnToCrystal.getValue() && this.currentMode == Mode2.CRYSTALS) {
                this.switchedForHealthReason = true;
            }
            this.setMode(Mode2.TOTEMS);
        }
        if (this.switchedForHealthReason && ((EntityUtil.isSafe((Entity)Offhand.mc.player) && EntityUtil.getHealth((Entity)Offhand.mc.player, this.absorption.getValue()) > this.crystalHoleHealth.getValue()) || EntityUtil.getHealth((Entity)Offhand.mc.player, this.absorption.getValue()) > this.crystalHealth.getValue())) {
            this.setMode(Mode2.CRYSTALS);
            this.switchedForHealthReason = false;
        }
        if (Offhand.mc.currentScreen instanceof GuiContainer && !this.guis.getValue() && !(Offhand.mc.currentScreen instanceof GuiInventory)) {
            return;
        }
        final Item currentOffhandItem = Offhand.mc.player.getHeldItemOffhand().getItem();
        switch (this.currentMode) {
            case TOTEMS: {
                if (this.totems > 0 && !this.holdingTotem) {
                    this.lastTotemSlot = InventoryUtil.findItemInventorySlot(Items.TOTEM_OF_UNDYING, false);
                    final int lastSlot = this.getLastSlot(currentOffhandItem, this.lastTotemSlot);
                    this.putItemInOffhand(this.lastTotemSlot, lastSlot);
                    break;
                }
                break;
            }
            case GAPPLES: {
                if (this.gapples > 0 && !this.holdingGapple) {
                    this.lastGappleSlot = InventoryUtil.findItemInventorySlot(Items.GOLDEN_APPLE, false);
                    final int lastSlot = this.getLastSlot(currentOffhandItem, this.lastGappleSlot);
                    this.putItemInOffhand(this.lastGappleSlot, lastSlot);
                    break;
                }
                break;
            }
            case WEBS: {
                if (this.webs > 0 && !this.holdingWeb) {
                    this.lastWebSlot = InventoryUtil.findInventoryBlock(BlockWeb.class, false);
                    final int lastSlot = this.getLastSlot(currentOffhandItem, this.lastWebSlot);
                    this.putItemInOffhand(this.lastWebSlot, lastSlot);
                    break;
                }
                break;
            }
            case OBSIDIAN: {
                if (this.obby > 0 && !this.holdingObby) {
                    this.lastObbySlot = InventoryUtil.findInventoryBlock(BlockObsidian.class, false);
                    final int lastSlot = this.getLastSlot(currentOffhandItem, this.lastObbySlot);
                    this.putItemInOffhand(this.lastObbySlot, lastSlot);
                    break;
                }
                break;
            }
            default: {
                if (this.crystals > 0 && !this.holdingCrystal) {
                    this.lastCrystalSlot = InventoryUtil.findItemInventorySlot(Items.END_CRYSTAL, false);
                    final int lastSlot = this.getLastSlot(currentOffhandItem, this.lastCrystalSlot);
                    this.putItemInOffhand(this.lastCrystalSlot, lastSlot);
                    break;
                }
                break;
            }
        }
        for (int i = 0; i < this.actions.getValue(); ++i) {
            final InventoryUtil.Task task = this.taskList.poll();
            if (task != null) {
                task.run();
                if (task.isSwitching()) {
                    this.didSwitchThisTick = true;
                }
            }
        }
    }
    
    private int getLastSlot(final Item item, final int slotIn) {
        if (item == Items.END_CRYSTAL) {
            return this.lastCrystalSlot;
        }
        if (item == Items.GOLDEN_APPLE) {
            return this.lastGappleSlot;
        }
        if (item == Items.TOTEM_OF_UNDYING) {
            return this.lastTotemSlot;
        }
        if (InventoryUtil.isBlock(item, BlockObsidian.class)) {
            return this.lastObbySlot;
        }
        if (InventoryUtil.isBlock(item, BlockWeb.class)) {
            return this.lastWebSlot;
        }
        if (item == Items.AIR) {
            return -1;
        }
        return slotIn;
    }
    
    private void putItemInOffhand(final int slotIn, final int slotOut) {
        if (slotIn != -1 && this.taskList.isEmpty()) {
            this.taskList.add(new InventoryUtil.Task(slotIn));
            this.taskList.add(new InventoryUtil.Task(45));
            this.taskList.add(new InventoryUtil.Task(slotOut));
            this.taskList.add(new InventoryUtil.Task());
        }
    }
    
    private boolean noNearbyPlayers() {
        return this.mode == Mode.CRYSTALS && Offhand.mc.world.playerEntities.stream().noneMatch(e -> e != Offhand.mc.player && !Phobos.friendManager.isFriend(e) && Offhand.mc.player.getDistance((Entity)e) <= this.cTargetDistance.getValue());
    }
    
    private boolean isItemInOffhand() {
        switch (this.mode) {
            case GAPPLES: {
                return Offhand.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE;
            }
            case CRYSTALS: {
                return Offhand.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL;
            }
            case OBSIDIAN: {
                return Offhand.mc.player.getHeldItemOffhand().getItem() instanceof ItemBlock && ((ItemBlock)Offhand.mc.player.getHeldItemOffhand().getItem()).block == Blocks.OBSIDIAN;
            }
            case WEBS: {
                return Offhand.mc.player.getHeldItemOffhand().getItem() instanceof ItemBlock && ((ItemBlock)Offhand.mc.player.getHeldItemOffhand().getItem()).block == Blocks.WEB;
            }
            default: {
                return false;
            }
        }
    }
    
    private boolean isHeldInMainHand() {
        switch (this.mode) {
            case GAPPLES: {
                return Offhand.mc.player.getHeldItemMainhand().getItem() == Items.GOLDEN_APPLE;
            }
            case CRYSTALS: {
                return Offhand.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL;
            }
            case OBSIDIAN: {
                return Offhand.mc.player.getHeldItemMainhand().getItem() instanceof ItemBlock && ((ItemBlock)Offhand.mc.player.getHeldItemMainhand().getItem()).block == Blocks.OBSIDIAN;
            }
            case WEBS: {
                return Offhand.mc.player.getHeldItemMainhand().getItem() instanceof ItemBlock && ((ItemBlock)Offhand.mc.player.getHeldItemMainhand().getItem()).block == Blocks.WEB;
            }
            default: {
                return false;
            }
        }
    }
    
    private boolean shouldTotem() {
        if (this.isHeldInMainHand() || this.isSwapToTotem()) {
            return true;
        }
        if (this.holeCheck.getValue() && EntityUtil.isInHole((Entity)Offhand.mc.player)) {
            return Offhand.mc.player.getHealth() + Offhand.mc.player.getAbsorptionAmount() <= this.getHoleHealth() || Offhand.mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() == Items.ELYTRA || Offhand.mc.player.fallDistance >= 3.0f || this.noNearbyPlayers() || (this.crystalCheck.getValue() && this.isCrystalsAABBEmpty());
        }
        return Offhand.mc.player.getHealth() + Offhand.mc.player.getAbsorptionAmount() <= this.getHealth() || Offhand.mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() == Items.ELYTRA || Offhand.mc.player.fallDistance >= 3.0f || this.noNearbyPlayers() || (this.crystalCheck.getValue() && this.isCrystalsAABBEmpty());
    }
    
    private boolean isNotEmpty(final BlockPos pos) {
        return Offhand.mc.world.getEntitiesWithinAABBExcludingEntity((Entity)null, new AxisAlignedBB(pos)).stream().anyMatch(e -> e instanceof EntityEnderCrystal);
    }
    
    private float getHealth() {
        switch (this.mode) {
            case CRYSTALS: {
                return this.crystalHealth.getValue();
            }
            case GAPPLES: {
                return this.gappleHealth.getValue();
            }
            case OBSIDIAN: {
                return this.obsidianHealth.getValue();
            }
            default: {
                return this.webHealth.getValue();
            }
        }
    }
    
    private float getHoleHealth() {
        switch (this.mode) {
            case CRYSTALS: {
                return this.crystalHoleHealth.getValue();
            }
            case GAPPLES: {
                return this.gappleHoleHealth.getValue();
            }
            case OBSIDIAN: {
                return this.obsidianHoleHealth.getValue();
            }
            default: {
                return this.webHoleHealth.getValue();
            }
        }
    }
    
    private boolean isCrystalsAABBEmpty() {
        return this.isNotEmpty(Offhand.mc.player.getPosition().add(1, 0, 0)) || this.isNotEmpty(Offhand.mc.player.getPosition().add(-1, 0, 0)) || this.isNotEmpty(Offhand.mc.player.getPosition().add(0, 0, 1)) || this.isNotEmpty(Offhand.mc.player.getPosition().add(0, 0, -1)) || this.isNotEmpty(Offhand.mc.player.getPosition());
    }
    
    int getStackSize() {
        int size = 0;
        if (this.shouldTotem()) {
            for (int i = 45; i > 0; --i) {
                if (Offhand.mc.player.inventory.getStackInSlot(i).getItem() == Items.TOTEM_OF_UNDYING) {
                    size += Offhand.mc.player.inventory.getStackInSlot(i).getCount();
                }
            }
        }
        else if (this.mode == Mode.OBSIDIAN) {
            for (int i = 45; i > 0; --i) {
                if (Offhand.mc.player.inventory.getStackInSlot(i).getItem() instanceof ItemBlock && ((ItemBlock)Offhand.mc.player.inventory.getStackInSlot(i).getItem()).block == Blocks.OBSIDIAN) {
                    size += Offhand.mc.player.inventory.getStackInSlot(i).getCount();
                }
            }
        }
        else if (this.mode == Mode.WEBS) {
            for (int i = 45; i > 0; --i) {
                if (Offhand.mc.player.inventory.getStackInSlot(i).getItem() instanceof ItemBlock && ((ItemBlock)Offhand.mc.player.inventory.getStackInSlot(i).getItem()).block == Blocks.WEB) {
                    size += Offhand.mc.player.inventory.getStackInSlot(i).getCount();
                }
            }
        }
        else {
            for (int i = 45; i > 0; --i) {
                if (Offhand.mc.player.inventory.getStackInSlot(i).getItem() == ((this.mode == Mode.CRYSTALS) ? Items.END_CRYSTAL : Items.GOLDEN_APPLE)) {
                    size += Offhand.mc.player.inventory.getStackInSlot(i).getCount();
                }
            }
        }
        return size;
    }
    
    int getSlot(final Mode m) {
        int slot = -1;
        if (m == Mode.OBSIDIAN) {
            for (int i = 45; i > 0; --i) {
                if (Offhand.mc.player.inventory.getStackInSlot(i).getItem() instanceof ItemBlock && ((ItemBlock)Offhand.mc.player.inventory.getStackInSlot(i).getItem()).block == Blocks.OBSIDIAN) {
                    slot = i;
                    break;
                }
            }
        }
        else if (m == Mode.WEBS) {
            for (int i = 45; i > 0; --i) {
                if (Offhand.mc.player.inventory.getStackInSlot(i).getItem() instanceof ItemBlock && ((ItemBlock)Offhand.mc.player.inventory.getStackInSlot(i).getItem()).block == Blocks.WEB) {
                    slot = i;
                    break;
                }
            }
        }
        else {
            for (int i = 45; i > 0; --i) {
                if (Offhand.mc.player.inventory.getStackInSlot(i).getItem() == ((m == Mode.CRYSTALS) ? Items.END_CRYSTAL : Items.GOLDEN_APPLE)) {
                    slot = i;
                    break;
                }
            }
        }
        return slot;
    }
    
    int getTotemSlot() {
        int totemSlot = -1;
        for (int i = 45; i > 0; --i) {
            if (Offhand.mc.player.inventory.getStackInSlot(i).getItem() == Items.TOTEM_OF_UNDYING) {
                totemSlot = i;
                break;
            }
        }
        return totemSlot;
    }
    
    private String getModeStr() {
        switch (this.mode) {
            case GAPPLES: {
                return "G";
            }
            case WEBS: {
                return "W";
            }
            case OBSIDIAN: {
                return "O";
            }
            default: {
                return "C";
            }
        }
    }
    
    public void setMode(final Mode mode) {
        this.mode = mode;
    }
    
    public void setMode(final Mode2 mode) {
        if (this.currentMode == mode) {
            this.currentMode = Mode2.TOTEMS;
        }
        else if (!this.cycle.getValue() && this.crystalToTotem.getValue() && (this.currentMode == Mode2.CRYSTALS || this.currentMode == Mode2.OBSIDIAN || this.currentMode == Mode2.WEBS) && mode == Mode2.GAPPLES) {
            this.currentMode = Mode2.TOTEMS;
        }
        else {
            this.currentMode = mode;
        }
    }
    
    public boolean isSwapToTotem() {
        return this.swapToTotem;
    }
    
    public void setSwapToTotem(final boolean swapToTotem) {
        this.swapToTotem = swapToTotem;
    }
    
    public enum Mode
    {
        CRYSTALS, 
        GAPPLES, 
        OBSIDIAN, 
        WEBS;
    }
    
    public enum Type
    {
        OLD, 
        NEW;
    }
    
    public enum Mode2
    {
        TOTEMS, 
        GAPPLES, 
        CRYSTALS, 
        OBSIDIAN, 
        WEBS;
    }
    
    public enum NameMode
    {
        MODE, 
        TOTEM, 
        AMOUNT;
    }
}
