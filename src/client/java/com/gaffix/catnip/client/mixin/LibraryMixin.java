package com.gaffix.catnip.client.mixin;

import com.mojang.blaze3d.audio.Library;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Library.class)
public abstract class LibraryMixin {
    @ModifyConstant(method = "init", constant = @Constant(intValue = 255))
    private int catnip$raiseStaticChannelCap(int vanillaCap) {
        // Keep the device-reported hardware limit, but remove Minecraft's lower
        // software ceiling. Requesting an arbitrary source count can break audio
        // initialization on some OpenAL drivers.
        return Integer.MAX_VALUE;
    }
}
