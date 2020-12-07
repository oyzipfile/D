// 
// Decompiled by Procyon v0.5.36
// 

package me.earth.phobos.mixin;

import java.util.Map;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.launch.MixinBootstrap;
import me.earth.phobos.Phobos;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

public class PhobosMixinLoader implements IFMLLoadingPlugin
{
    private static boolean isObfuscatedEnvironment;
    
    public PhobosMixinLoader() {
        Phobos.LOGGER.info("Phobos mixins initialized");
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.phobos.json");
        MixinEnvironment.getDefaultEnvironment().setObfuscationContext("searge");
        Phobos.LOGGER.info(MixinEnvironment.getDefaultEnvironment().getObfuscationContext());
    }
    
    public String[] getASMTransformerClass() {
        return new String[0];
    }
    
    public String getModContainerClass() {
        return null;
    }
    
    public String getSetupClass() {
        return null;
    }
    
    public void injectData(final Map<String, Object> data) {
        PhobosMixinLoader.isObfuscatedEnvironment = data.get("runtimeDeobfuscationEnabled");
    }
    
    public String getAccessTransformerClass() {
        return null;
    }
    
    static {
        PhobosMixinLoader.isObfuscatedEnvironment = false;
    }
}
