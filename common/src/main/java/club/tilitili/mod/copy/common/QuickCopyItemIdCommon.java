package club.tilitili.mod.copy.common;

public class QuickCopyItemIdCommon {
    public static final String MOD_ID = "quick_copy_item_id";

    private GameImp gameImp;

    public QuickCopyItemIdCommon(GameImp gameImp) {
        this.gameImp = gameImp;
    }

    public void onClickItem(String itemId, boolean shiftIsDown) {
//        if (!shiftIsDown) {
//            LOGGER.warn("no shift");
//            return;
//        }
        gameImp.setClipboard(itemId);
        gameImp.displayClientMessage("Copied Item ID: " + itemId);
    }
}
