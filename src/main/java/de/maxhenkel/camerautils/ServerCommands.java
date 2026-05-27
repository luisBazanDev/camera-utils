package de.maxhenkel.camerautils;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.maxhenkel.camerautils.network.SetDetachCameraPositionPayload;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.NameAndId;
import net.minecraft.server.players.PlayerList;

public class ServerCommands {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(Commands.literal("detachcam")
                    .requires(source -> true) // TODO: Fix permission check for 26.1.2
                    .then(Commands.literal("set")
                            .then(Commands.argument("x", DoubleArgumentType.doubleArg())
                                    .then(Commands.argument("y", DoubleArgumentType.doubleArg())
                                            .then(Commands.argument("z", DoubleArgumentType.doubleArg())
                                                    .then(Commands.argument("pitch", FloatArgumentType.floatArg(-90F, 90F))
                                                            .then(Commands.argument("yaw", FloatArgumentType.floatArg(-360F, 360F))
                                                                    .executes(ServerCommands::setAll)
                                                            )
                                                    )
                                            )
                                    )
                            )
                            .then(Commands.argument("player", EntityArgument.player())
                                    .then(Commands.argument("x", DoubleArgumentType.doubleArg())
                                            .then(Commands.argument("y", DoubleArgumentType.doubleArg())
                                                    .then(Commands.argument("z", DoubleArgumentType.doubleArg())
                                                            .then(Commands.argument("pitch", FloatArgumentType.floatArg(-90F, 90F))
                                                                    .then(Commands.argument("yaw", FloatArgumentType.floatArg(-360F, 360F))
                                                                            .executes(ServerCommands::setSingle)
                                                                    )
                                                            )
                                                    )
                                            )
                                    )
                            )
                    )
                    .then(Commands.literal("setall")
                            .executes(ServerCommands::setAllFromSource)
                    )
            );
        });
    }

    private static int setAll(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        double x = DoubleArgumentType.getDouble(context, "x");
        double y = DoubleArgumentType.getDouble(context, "y");
        double z = DoubleArgumentType.getDouble(context, "z");
        float pitch = FloatArgumentType.getFloat(context, "pitch");
        float yaw = FloatArgumentType.getFloat(context, "yaw");

        for (ServerPlayer player : context.getSource().getServer().getPlayerList().getPlayers()) {
            SetDetachCameraPositionPayload.send(player, x, y, z, pitch, yaw);
        }

        context.getSource().sendSuccess(
                () -> Component.translatable("command.camerautils.detachcam.set.all", x, y, z, pitch, yaw),
                true
        );
        return Command.SINGLE_SUCCESS;
    }

    private static int setSingle(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        double x = DoubleArgumentType.getDouble(context, "x");
        double y = DoubleArgumentType.getDouble(context, "y");
        double z = DoubleArgumentType.getDouble(context, "z");
        float pitch = FloatArgumentType.getFloat(context, "pitch");
        float yaw = FloatArgumentType.getFloat(context, "yaw");

        SetDetachCameraPositionPayload.send(player, x, y, z, pitch, yaw);

        context.getSource().sendSuccess(
                () -> Component.translatable("command.camerautils.detachcam.set.single", player.getDisplayName().getString(), x, y, z, pitch, yaw),
                true
        );
        return Command.SINGLE_SUCCESS;
    }

    private static int setAllFromSource(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerPlayer executor = source.getPlayerOrException();

        double x = executor.getX();
        double y = executor.getY();
        double z = executor.getZ();
        float pitch = executor.getXRot();
        float yaw = executor.getYRot();

        for (ServerPlayer player : source.getServer().getPlayerList().getPlayers()) {
            SetDetachCameraPositionPayload.send(player, x, y, z, pitch, yaw);
        }

        context.getSource().sendSuccess(
                () -> Component.translatable("command.camerautils.detachcam.setall", x, y, z, pitch, yaw),
                true
        );
        return Command.SINGLE_SUCCESS;
    }
}
