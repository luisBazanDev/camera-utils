package de.maxhenkel.camerautils;

import de.maxhenkel.camerautils.network.SetDetachCameraPositionPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class CameraUtilsMain implements ModInitializer {

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.clientboundPlay().register(SetDetachCameraPositionPayload.TYPE, SetDetachCameraPositionPayload.CODEC);
        ServerCommands.register();
    }
}
