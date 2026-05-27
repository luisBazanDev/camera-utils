package de.maxhenkel.camerautils.network;

import de.maxhenkel.camerautils.CameraUtils;
import de.maxhenkel.camerautils.config.ClientConfig;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

public class SetDetachCameraPositionPayload implements CustomPacketPayload {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(CameraUtils.MODID, "set_detach_camera");
    public static final Type<SetDetachCameraPositionPayload> TYPE = new Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, SetDetachCameraPositionPayload> CODEC = StreamCodec.of(
            SetDetachCameraPositionPayload::write,
            SetDetachCameraPositionPayload::read
    );

    private final double x;
    private final double y;
    private final double z;
    private final float pitch;
    private final float yaw;

    public SetDetachCameraPositionPayload(double x, double y, double z, float pitch, float yaw) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    private static SetDetachCameraPositionPayload read(RegistryFriendlyByteBuf buf) {
        return new SetDetachCameraPositionPayload(buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readFloat(), buf.readFloat());
    }

    private static void write(RegistryFriendlyByteBuf buf, SetDetachCameraPositionPayload payload) {
        buf.writeDouble(payload.x);
        buf.writeDouble(payload.y);
        buf.writeDouble(payload.z);
        buf.writeFloat(payload.pitch);
        buf.writeFloat(payload.yaw);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void registerClientReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(TYPE, (payload, context) -> {
            context.client().execute(() -> {
                ClientConfig.detached = true;
                ClientConfig.x = payload.x;
                ClientConfig.y = payload.y;
                ClientConfig.z = payload.z;
                ClientConfig.xRot = payload.pitch;
                ClientConfig.yRot = payload.yaw;
            });
        });
    }

    public static void send(ServerPlayer player, double x, double y, double z, float pitch, float yaw) {
        ServerPlayNetworking.send(player, new SetDetachCameraPositionPayload(x, y, z, pitch, yaw));
    }
}
