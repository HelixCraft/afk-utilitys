package com.HelixCraft.afkutility.features;

import com.HelixCraft.afkutility.config.ConfigManager;
import com.HelixCraft.afkutility.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;

import java.util.Random;

public class AntiAfk {
    private static final Random random = new Random();

    // Timers
    private static int jumpTimer = 0;
    private static int swingTimer = 0;
    private static int sneakIntervalTimer = 0;

    private static int messageTimer = 0;
    private static int messageI = 0;
    private static int sneakTimeTimer = 0; // Duration of sneak
    private static int strafeTimer = 0;
    private static boolean direction = false;

    // Spin State
    private static float lastYaw = 0;
    private static boolean wasSpinning = false; // To detect toggle on

    public static void tick(Minecraft client) {
        if (client.player == null || client.level == null)
            return;

        ModConfig.AntiAfk config = ConfigManager.get().antiAfk;

        // Jump
        if (config.jump) {
            if (jumpTimer-- <= 0) {
                if (client.options.keyJump.isDown())
                    client.options.keyJump.setDown(false);
                else {
                    client.options.keyJump.setDown(true);
                    resetJumpTimer(config);
                }
            } else {
                if (client.options.keyJump.isDown() && jumpTimer < (getInterval(config.jumpInterval) - 5)) {
                    client.options.keyJump.setDown(false);
                }
            }
        }

        // Swing
        if (config.swing) {
            if (swingTimer-- <= 0) {
                client.player.swing(InteractionHand.MAIN_HAND);
                resetSwingTimer(config);
            }
        }

        // Sneak
        if (config.sneak) {
            if (sneakIntervalTimer-- <= 0) {
                if (sneakTimeTimer++ < config.sneakTime) {
                    client.options.keyShift.setDown(true);
                } else {
                    client.options.keyShift.setDown(false);
                    resetSneakTimer(config);
                    sneakTimeTimer = 0;
                }
            } else {
                client.options.keyShift.setDown(false);
            }
        }

        // Strafe
        if (config.strafe) {
            if (strafeTimer-- <= 0) {
                client.options.keyLeft.setDown(!direction);
                client.options.keyRight.setDown(direction);
                direction = !direction;
                strafeTimer = 20;
            }
        } else {
            if (strafeTimer > 0) {
                client.options.keyLeft.setDown(false);
                client.options.keyRight.setDown(false);
                strafeTimer = 0;
            }
        }

        // Spin
        if (config.spin) {
            if (!wasSpinning) {
                // Just enabled, sync yaw to current player yaw to avoid snapping
                lastYaw = client.player.getYRot();
                wasSpinning = true;
            }

            lastYaw += config.spinSpeed;
            // Wrap degrees to keep it clean (though setYRot handles it, good practice)
            lastYaw = lastYaw % 360;

            if (config.spinMode == ModConfig.AntiAfk.SpinMode.Client) {
                client.player.setYRot(lastYaw);
            } else {
                // Server Mode: Manual Packet Send
                if (client.getConnection() != null) {
                    // 1.21.4 Constructor: Rot(yRot, xRot, onGround, horizontalCollision)
                    // We use 'false' for horizontalCollision as a safe default.
                    client.getConnection().send(new net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.Rot(
                            lastYaw,
                            (float) config.pitch,
                            client.player.onGround(),
                            false));
                }
            }
        } else {
            wasSpinning = false;
        }

        // Messages
        if (config.sendMessages) {
            if (messageTimer-- <= 0) {
                String msg = config.customMessage;

                if (config.randomMessage || msg.isEmpty()) {
                    if (!config.messages.isEmpty()) {
                        if (config.randomMessage) {
                            messageI = random.nextInt(config.messages.size());
                        } else {
                            if (++messageI >= config.messages.size())
                                messageI = 0;
                        }
                        msg = config.messages.get(messageI);
                    }
                }

                if (!msg.isEmpty() && client.getConnection() != null) {
                    client.getConnection().sendChat(msg);
                }

                // Reset timer: Minutes -> Ticks
                // 1 min = 60s * 20t = 1200 ticks
                messageTimer = (int) (config.messageInterval * 1200);
            }
        }
    }

    public static float getLastYaw() {
        return lastYaw;
    }

    private static void resetJumpTimer(ModConfig.AntiAfk config) {
        jumpTimer = getInterval(config.jumpInterval);
    }

    private static void resetSwingTimer(ModConfig.AntiAfk config) {
        swingTimer = getInterval(config.swingInterval);
    }

    private static void resetSneakTimer(ModConfig.AntiAfk config) {
        sneakIntervalTimer = getInterval(config.sneakInterval);
    }

    private static int getInterval(int baseSeconds) {
        int variation = random.nextInt(5) - 2;
        int seconds = Math.max(1, baseSeconds + variation);
        return seconds * 20;
    }
}
