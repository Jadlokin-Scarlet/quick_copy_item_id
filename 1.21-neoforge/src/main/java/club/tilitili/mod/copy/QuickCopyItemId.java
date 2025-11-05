package club.tilitili.mod.copy;

import club.tilitili.mod.copy.common.QuickCopyItemIdCommon;
import club.tilitili.mod.copy.mixin.AbstractContainerScreenAccessor;
import club.tilitili.mod.copy.mixin.RecipesGuiAccessor;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
import mezz.jei.gui.input.IClickableIngredientInternal;
import mezz.jei.gui.recipes.RecipesGui;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.stream.Stream;

@Mod(QuickCopyItemIdCommon.MOD_ID)
public class QuickCopyItemId {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final QuickCopyItemIdCommon quickCopyItemIdCommon;
	private final GameImpl gameImpl;
	private Screen cacheScreen = null;

	public static final Lazy<KeyMapping> EXAMPLE_MAPPING = Lazy.of(() -> new KeyMapping(
			"key.quickcopyitemid.copyid", // Will be localized using this translation key
			KeyConflictContext.UNIVERSAL, // Mapping can only be used when a screen is open
			InputConstants.Type.MOUSE, // Default mapping is on the keyboard
			GLFW.GLFW_MOUSE_BUTTON_LEFT, // Default key is P
			"key.categories.misc" // Mapping will be in the misc category
	));

	public QuickCopyItemId(IEventBus modEventBus) {
		NeoForge.EVENT_BUS.register(this);
		modEventBus.addListener(this::registerBindings);
		gameImpl = new GameImpl();
		quickCopyItemIdCommon = new QuickCopyItemIdCommon(gameImpl);
	}

	public void registerBindings(RegisterKeyMappingsEvent event) {
		event.register(EXAMPLE_MAPPING.get());
	}

	@SubscribeEvent
	public void onClientTick(ClientTickEvent.Post event) {
		while (EXAMPLE_MAPPING.get().consumeClick()) {
			LOGGER.debug("on ClientTickEvent!");
			Minecraft mc = Minecraft.getInstance();
			LocalPlayer player = mc.player;
			if (player == null) {
				LOGGER.debug("player is null!");
				return;
			}
			gameImpl.setPlayer(player);
			ItemStack itemStack = player.getMainHandItem();
			this.copyItemByStack(itemStack, false);
		}
	}

	@SubscribeEvent
	public void onScreenMouseButtonPressed(ScreenEvent.MouseButtonPressed.Pre event) {
		if (EXAMPLE_MAPPING.get().isActiveAndMatches(InputConstants.Type.MOUSE.getOrCreate(event.getButton()))) {
			this.copyInScreen(event.getScreen(), event.getMouseX(), event.getMouseY());
		}
	}

	@SubscribeEvent
	public void onScreenKeyPressed(ScreenEvent.KeyPressed.Pre event) {
		if (EXAMPLE_MAPPING.get().isActiveAndMatches(InputConstants.getKey(event.getKeyCode(), event.getScanCode()))) {
//			this.copyInScreen(event.getScreen(), event.getMouseX(), event.getMouseY());
		}
	}

	private void copyInScreen(Screen screen, double mouseX, double mouseY) {
		Minecraft mc = Minecraft.getInstance();
		LocalPlayer player = mc.player;
		if (player == null) {
			LOGGER.debug("player is null!");
			return;
		}
		gameImpl.setPlayer(player);
		boolean shiftIsDown = InputConstants.isKeyDown(mc.getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT)
				|| InputConstants.isKeyDown(mc.getWindow().getWindow(), GLFW.GLFW_KEY_RIGHT_SHIFT);
		if (cacheScreen != screen) {
			cacheScreen = screen;
		}
		if (cacheScreen instanceof AbstractContainerScreen) {
			Slot selectedSlot = ((AbstractContainerScreenAccessor) cacheScreen).quickcopyitemid$invokeFindSlot(mouseX, mouseY);
			if (selectedSlot == null) {
				LOGGER.debug("selectedSlot is null!");
				return;
			}
			ItemStack itemStack = selectedSlot.getItem();
			this.copyItemByStack(itemStack, shiftIsDown);
		} else if (cacheScreen instanceof RecipesGui) {
			Stream<IClickableIngredientInternal<?>> internalStream = ((RecipesGuiAccessor) cacheScreen).quickcopyitemid$invokeFindSlot(mouseX, mouseY);
			IClickableIngredientInternal<?> ingredientInternal = internalStream.findFirst().orElse(null);
			if (ingredientInternal == null) {
				LOGGER.debug("ingredientInternal is null!");
				return;
			}
			ItemStack itemStack = ingredientInternal.getElement().getTypedIngredient().getItemStack().orElse(null);
			this.copyItemByStack(itemStack, shiftIsDown);
		} else {
			LOGGER.debug("Copied error"+cacheScreen.getClass());
		}
	}

	private void copyItemByStack(ItemStack itemStack, boolean shiftIsDown) {
		if (itemStack == null || itemStack.isEmpty()) {
			LOGGER.debug("ItemStack is null");
			return;
		}
		Item item = itemStack.getItem();
		if (item == null) {
			LOGGER.debug("Item is null");
			return;
		}
		ResourceLocation resourceLocation = BuiltInRegistries.ITEM.getKey(item);
		if (resourceLocation == null) {
			LOGGER.debug("ResourceLocation is null");
			return;
		}
		String itemId = resourceLocation.toString();
		quickCopyItemIdCommon.onClickItem(itemId, shiftIsDown);
	}

//	@SubscribeEvent
//	public void onItemStackedOnOtherEvent(PlayerInteractEvent.RightClickItem event) {
//		LOGGER.debug("on PlayerInteractEvent.RightClickItem!");
//	}
//
//	@SubscribeEvent
//	public void onItemStackedOnOtherEvent(ItemStackedOnOtherEvent event) {
//		LOGGER.debug("on ItemStackedOnOtherEvent!");
//		Player player = event.getPlayer();
//		if (player == null) {
//			LOGGER.debug("player is null!");
//			return;
//		}
//		gameImpl.setPlayer(player);
//		ItemStack itemStack = event.getCarriedItem();
//		if (itemStack.isEmpty()) {
//			LOGGER.debug("ItemStack is null");
//			return;
//		}
//		Item item = itemStack.getItem();
//		if (item == null) {
//			LOGGER.debug("Item is null");
//			return;
//		}
//		ResourceLocation resourceLocation = BuiltInRegistries.ITEM.getKey(item);
//		if (resourceLocation == null) {
//			LOGGER.debug("ResourceLocation is null");
//			return;
//		}
//		String itemId = resourceLocation.toString();
//		quickCopyItemIdCommon.onClickItem(itemId, false);
//	}
//	@SubscribeEvent
//	public void onAttackEntityEvent(AttackEntityEvent event) {
//		LOGGER.debug("on AttackEntityEvent!");
//		Player player = event.getEntity();
//		if (player == null) {
//			LOGGER.debug("player is null!");
//			return;
//		}
//		gameImpl.setPlayer(player);
//
//		Entity entity = event.getTarget();
//		if (entity == null) {
//			LOGGER.debug("entity is null!");
//			return;
//		}
//		ItemStack itemStack = entity.getWeaponItem();
//		if (itemStack.isEmpty()) {
//			LOGGER.debug("ItemStack is null");
//			return;
//		}
//		Item item = itemStack.getItem();
//		if (item == null) {
//			LOGGER.debug("Item is null");
//			return;
//		}
//		ResourceLocation resourceLocation = BuiltInRegistries.ITEM.getKey(item);
//		if (resourceLocation == null) {
//			LOGGER.debug("ResourceLocation is null");
//			return;
//		}
//		String itemId = resourceLocation.toString();
//		quickCopyItemIdCommon.onClickItem(itemId, false);
//	}
}
