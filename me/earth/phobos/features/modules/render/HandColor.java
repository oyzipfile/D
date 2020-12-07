// 
// Decompiled by Procyon v0.5.36
// 

package me.earth.phobos.features.modules.render;

import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.features.modules.Module;

public class HandColor extends Module
{
    public static HandColor INSTANCE;
    public Setting<Boolean> colorSync;
    public Setting<Boolean> rainbow;
    public Setting<Integer> saturation;
    public Setting<Integer> brightness;
    public Setting<Integer> speed;
    public Setting<Integer> red;
    public Setting<Integer> green;
    public Setting<Integer> blue;
    public Setting<Integer> alpha;
    
    public HandColor() {
        super("HandColor", "Changes the color of your hands", Category.RENDER, false, false, false);
        this.colorSync = (Setting<Boolean>)this.register(new Setting("Sync", (T)false));
        this.rainbow = (Setting<Boolean>)this.register(new Setting("Rainbow", (T)false));
        this.saturation = (Setting<Integer>)this.register(new Setting("Saturation", (T)50, (T)0, (T)100, v -> this.rainbow.getValue()));
        this.brightness = (Setting<Integer>)this.register(new Setting("Brightness", (T)100, (T)0, (T)100, v -> this.rainbow.getValue()));
        this.speed = (Setting<Integer>)this.register(new Setting("Speed", (T)40, (T)1, (T)100, v -> this.rainbow.getValue()));
        this.red = (Setting<Integer>)this.register(new Setting("Red", (T)0, (T)0, (T)255, v -> !this.rainbow.getValue()));
        this.green = (Setting<Integer>)this.register(new Setting("Green", (T)255, (T)0, (T)255, v -> !this.rainbow.getValue()));
        this.blue = (Setting<Integer>)this.register(new Setting("Blue", (T)0, (T)0, (T)255, v -> !this.rainbow.getValue()));
        this.alpha = (Setting<Integer>)this.register(new Setting("Alpha", (T)255, (T)0, (T)255));
        HandColor.INSTANCE = this;
    }
}
