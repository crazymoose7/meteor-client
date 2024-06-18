/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.mixin;

import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Supplier;

@Mixin(Registry.class)
public abstract class RegistriesMixin {
    @Redirect(method = "create(Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/registry/MutableRegistry;Lnet/minecraft/registry/Registries$Initializer;)Lnet/minecraft/registry/MutableRegistry;", at = @At(value = "INVOKE", target = "Lnet/minecraft/Bootstrap;ensureBootstrapped(Ljava/util/function/Supplier;)V"))
    private static void ignoreBootstrap(Supplier<String> callerGetter) {
        // nothing
    }
}
