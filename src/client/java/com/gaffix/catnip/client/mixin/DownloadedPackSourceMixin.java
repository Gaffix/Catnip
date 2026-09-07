package com.gaffix.catnip.client.mixin;

import net.minecraft.client.resources.server.DownloadedPackSource;
import net.minecraft.client.resources.server.PackReloadConfig;
import net.minecraft.client.resources.server.ServerPackManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

@Mixin(DownloadedPackSource.class)
public abstract class DownloadedPackSourceMixin {
    @Unique private List<Path> catnip$activePackPaths;
    @Unique private List<Path> catnip$pendingPackPaths;

    @Inject(method = "startReload", at = @At("HEAD"), cancellable = true)
    private void catnip$skipDuplicateReload(PackReloadConfig.Callbacks callbacks, CallbackInfo ci) {
        List<Path> requestedPaths = callbacks.packsToLoad().stream()
                .map(PackReloadConfig.IdAndPath::path)
                .map(Path::toAbsolutePath)
                .toList();
        if (requestedPaths.equals(catnip$activePackPaths)) {
            // Complete through Minecraft's own callback so configuration tasks
            // and server feedback finish exactly as they do after a real reload.
            callbacks.onSuccess();
            ci.cancel();
            return;
        }
        catnip$pendingPackPaths = requestedPaths;
    }

    @Inject(method = "onReloadSuccess", at = @At("TAIL"))
    private void catnip$rememberLoadedPack(CallbackInfo ci) {
        if (catnip$pendingPackPaths != null) {
            catnip$activePackPaths = catnip$pendingPackPaths;
            catnip$pendingPackPaths = null;
        }
    }

    @Inject(method = "popPack", at = @At("HEAD"), cancellable = true)
    private void catnip$keepDownloadedPack(UUID id, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "popAll", at = @At("HEAD"), cancellable = true)
    private void catnip$keepAllDownloadedPacks(CallbackInfo ci) {
        ci.cancel();
    }

    @Redirect(
            method = "cleanupAfterDisconnect",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/resources/server/ServerPackManager;popAll()V"
            )
    )
    private void catnip$keepDownloadedPacksAfterDisconnect(ServerPackManager manager) {
        // Intentionally do nothing: the active downloaded pack remains in the
        // repository until Minecraft closes or a replacement pack is pushed.
    }
}
