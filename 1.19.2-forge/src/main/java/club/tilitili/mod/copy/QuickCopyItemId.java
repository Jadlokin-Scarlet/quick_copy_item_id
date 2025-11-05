package club.tilitili.mod.copy;

import club.tilitili.mod.copy.common.QuickCopyItemIdCommon;
import club.tilitili.mod.copy.mixin.AbstractContainerScreenAccessor;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.*;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

@Mod(QuickCopyItemIdCommon.MOD_ID)
public class QuickCopyItemId {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final QuickCopyItemIdCommon quickCopyItemIdCommon;
    private final GameImpl gameImpl;
    private Screen cacheScreen = null;

    public QuickCopyItemId() {
        MinecraftForge.EVENT_BUS.register(this);
        gameImpl = new GameImpl();
        quickCopyItemIdCommon = new QuickCopyItemIdCommon(gameImpl);
    }

    @SubscribeEvent
    public void onGuiMouseClickedPre(ScreenEvent.MouseButtonPressed.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) {
            LOGGER.debug("player is null!");
            return;
        }
        gameImpl.setPlayer(player);
        if (cacheScreen != event.getScreen()) {
            cacheScreen = event.getScreen();
        }
        if (!(cacheScreen instanceof AbstractContainerScreen)) {
            LOGGER.debug("Copied error");
            return;
        }
        boolean shiftIsDown = InputConstants.isKeyDown(mc.getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT)
                || InputConstants.isKeyDown(mc.getWindow().getWindow(), GLFW.GLFW_KEY_RIGHT_SHIFT);
//        if (!shiftIsDown) {
//            LOGGER.debug("no shift");
//            return;
//        }

        Slot selectedSlot = ((AbstractContainerScreenAccessor) cacheScreen).quickcopyitemid$invokeFindSlot(event.getMouseX(), event.getMouseY());
        if (selectedSlot == null) {
            LOGGER.debug("no item");
            return;
        }
        ItemStack selectedSlotStack = selectedSlot.getItem();
        if (selectedSlotStack.isEmpty()) {
            LOGGER.debug("Copied error2");
            return;
        }
        ResourceLocation resourceLocation = ForgeRegistries.ITEMS.getKey(selectedSlotStack.getItem());
        if (resourceLocation == null) {
            LOGGER.debug("no fond item");
            return;
        }
        String itemId = resourceLocation.toString();
        quickCopyItemIdCommon.onClickItem(itemId, shiftIsDown);
    }
}
