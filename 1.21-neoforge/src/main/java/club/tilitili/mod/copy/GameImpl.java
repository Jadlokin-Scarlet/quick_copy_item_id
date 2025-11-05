package club.tilitili.mod.copy;

import club.tilitili.mod.copy.common.GameImp;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class GameImpl implements GameImp {
	private Player player;
	@Override
	public void setClipboard(String itemId) {
		Minecraft mc = Minecraft.getInstance();
		mc.keyboardHandler.setClipboard(itemId);
	}

	@Override
	public void displayClientMessage(String message) {
		if (player != null) {
			Component messageComponent = Component.literal(message);
			player.displayClientMessage(messageComponent, false);
		}
	}


	public GameImpl setPlayer(Player player) {
		this.player = player;
		return this;
	}
}
