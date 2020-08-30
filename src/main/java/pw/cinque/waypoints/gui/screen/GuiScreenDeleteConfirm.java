package pw.cinque.waypoints.gui.screen;

import pw.cinque.waypoints.Waypoint;
import pw.cinque.waypoints.WaypointsMod;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class GuiScreenDeleteConfirm extends GuiScreen {

	private GuiScreenWaypointsMenu parent;
	private Waypoint waypoint;
	
	public GuiScreenDeleteConfirm(GuiScreenWaypointsMenu parent, Waypoint waypoint) {
		this.parent = parent;
		this.waypoint = waypoint;
	}
	
	@Override
	public void initGui() {
		this.buttonList.add(new GuiButton(0, this.width / 2 - 101, this.height / 2 + 12, 100, 20, "Confirm"));
		this.buttonList.add(new GuiButton(1, this.width / 2 + 1, this.height / 2 + 12, 100, 20, "Cancel"));
	}

	@Override
	public void drawScreen(int x, int y, float partialTicks) {
		this.drawDefaultBackground();
		this.drawCenteredString(mc.fontRendererObj, "Are you sure you want to delete waypoint '" + waypoint.getName() + "'?", this.width / 2, this.height / 2 - 12, 0xFFFFFF);
		super.drawScreen(x, y, partialTicks);
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		switch (button.id) {
		case 0:
			WaypointsMod.removeWaypoint(waypoint);
			mc.displayGuiScreen(null);
			mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Waypoint '" + waypoint.getName() + "' removed!"));
			return;
			
		case 1:
			mc.displayGuiScreen(parent);
			return;
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

}
