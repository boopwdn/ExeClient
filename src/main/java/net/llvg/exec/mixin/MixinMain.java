package net.llvg.exec.mixin;

import net.llvg.exec.ExeClient;
import net.minecraft.client.main.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin (Main.class)
public class MixinMain {
        @Inject (method = "main", at = @At ("HEAD"), remap = false)
        private static void startExeClient(String[] strings, CallbackInfo ci) {
                ExeClient.initialize();
                Class<? extends ExeClient> clazz = ExeClient.INSTANCE.getClass();
                ExeClient.logger.info("ExeClient initialization succeed! Instance(class={}, classloader={})", clazz, clazz.getClassLoader());
        }
}
