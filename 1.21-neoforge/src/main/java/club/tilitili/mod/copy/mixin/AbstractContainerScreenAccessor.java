package club.tilitili.mod.copy.mixin;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractContainerScreen.class)
public interface AbstractContainerScreenAccessor {
    @Invoker("findSlot")
    Slot quickcopyitemid$invokeFindSlot(double x, double y);

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
