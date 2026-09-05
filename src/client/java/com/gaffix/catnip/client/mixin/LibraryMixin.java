package com.gaffix.catnip.client.mixin;

import com.mojang.blaze3d.audio.Library;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.nio.IntBuffer;

@Mixin(Library.class)
public abstract class LibraryMixin {
    private static final int CATNIP_CHANNEL_LIMIT = 1024;
    private static final int ALC_MONO_SOURCES = 0x1010;

    @ModifyConstant(method = "createAttributes", constant = @Constant(intValue = 11))
    private int catnip$makeRoomForChannelRequest(int originalSize) {
        return originalSize + 2;
    }

    @Redirect(
            method = "createAttributes",
            at = @At(value = "INVOKE", target = "Ljava/nio/IntBuffer;flip()Ljava/nio/IntBuffer;")
    )
    private IntBuffer catnip$requestMoreChannels(IntBuffer attributes) {
        attributes.put(ALC_MONO_SOURCES).put(CATNIP_CHANNEL_LIMIT);
        return attributes.flip();
    }

    @ModifyConstant(method = "init", constant = @Constant(intValue = 255))
    private int catnip$raiseStaticChannelCap(int vanillaCap) {
        return CATNIP_CHANNEL_LIMIT;
    }
}
