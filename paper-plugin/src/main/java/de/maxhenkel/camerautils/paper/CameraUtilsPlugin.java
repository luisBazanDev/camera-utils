package de.maxhenkel.camerautils.paper;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class CameraUtilsPlugin extends JavaPlugin {

    public static final String CHANNEL = "camerautils:set_detach_camera";

    private static CameraUtilsPlugin instance;

    @Override
    public void onEnable() {
        instance = this;
        getServer().getMessenger().registerOutgoingPluginChannel(this, CHANNEL);
        DetachCamCommand.register(this);
        getLogger().info("CameraUtils plugin enabled");
    }

    @Override
    public void onDisable() {
        getServer().getMessenger().unregisterOutgoingPluginChannel(this, CHANNEL);
        instance = null;
        getLogger().info("CameraUtils plugin disabled");
    }

    public static CameraUtilsPlugin getInstance() {
        return instance;
    }

    public static void sendDetach(Player player, double x, double y, double z, float pitch, float yaw) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeDouble(x);
        out.writeDouble(y);
        out.writeDouble(z);
        out.writeFloat(pitch);
        out.writeFloat(yaw);
        player.sendPluginMessage(getInstance(), CHANNEL, out.toByteArray());
    }
}
