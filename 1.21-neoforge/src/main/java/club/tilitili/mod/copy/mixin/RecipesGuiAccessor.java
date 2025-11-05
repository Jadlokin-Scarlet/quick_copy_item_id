package club.tilitili.mod.copy.mixin;

import mezz.jei.gui.input.IClickableIngredientInternal;
import mezz.jei.gui.recipes.RecipesGui;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.stream.Stream;

@Mixin(RecipesGui.class)
public interface RecipesGuiAccessor {
    @Invoker("getIngredientUnderMouse")
    Stream<IClickableIngredientInternal<?>> quickcopyitemid$invokeFindSlot(double mouseX, double mouseY);

//    @Invoker("slotClicked")
//    void quickcopyitemid$invokeSlotClicked(Slot slot, int index, int button, ClickType clickType);
//
//    @Accessor("isQuickCrafting")
//    boolean quickcopyitemid$getIsQuickCrafting();
//
//    @Accessor("isQuickCrafting")
//    void quickcopyitemid$setIsQuickCrafting(boolean value);
//
//    @Accessor("quickCraftingButton")
//    int quickcopyitemid$getQuickCraftingButton();
//
//    @Accessor("skipNextRelease")
//    void quickcopyitemid$setSkipNextRelease(boolean value);
}
