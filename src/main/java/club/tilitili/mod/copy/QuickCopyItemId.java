package club.tilitili.mod.copy;

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

// The value here should match an entry in the META-INF/mods.toml file
@Mod(QuickCopyItemId.MODID)
public class QuickCopyItemId
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "quick_copy_item_id";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    private Screen cacheScreen = null;

    public QuickCopyItemId() {
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

    }

    @SubscribeEvent
    public void onGuiMouseClickedPre(ScreenEvent.MouseButtonPressed.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) {
            LOGGER.warn("player is null!");
            return;
        }
        if (cacheScreen != event.getScreen()) {
            cacheScreen = event.getScreen();
        }
        if (!(cacheScreen instanceof AbstractContainerScreen)) {
            LOGGER.warn("Copied error");
            return;
        }
        boolean shiftIsDown = InputConstants.isKeyDown(mc.getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT)
                || InputConstants.isKeyDown(mc.getWindow().getWindow(), GLFW.GLFW_KEY_RIGHT_SHIFT);
        if (!shiftIsDown) {
            LOGGER.warn("no shift");
            return;
        }

        Slot selectedSlot = ((AbstractContainerScreenAccessor) cacheScreen).quickcopyitemid$invokeFindSlot(event.getMouseX(), event.getMouseY());
        if (selectedSlot == null) {
            LOGGER.warn("no item");
            return;
        }
        ItemStack selectedSlotStack = selectedSlot.getItem();
        if (selectedSlotStack.isEmpty()) {
            LOGGER.warn("Copied error2");
            return;
        }
        ResourceLocation resourceLocation = ForgeRegistries.ITEMS.getKey(selectedSlotStack.getItem());
        if (resourceLocation == null) {
            LOGGER.warn("no fond item");
            return;
        }
        String itemId = resourceLocation.toString();
        mc.keyboardHandler.setClipboard(itemId);
        player.displayClientMessage(Component.literal("Copied Item ID: " + itemId), true);
//        Main.onMouseScrolled(event.getScreen(), event.getMouseX(), event.getMouseY(), event.getDeltaY());
    }
}
