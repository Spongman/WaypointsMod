package pw.cinque.waypoints.gui.screen;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import pw.cinque.waypoints.IWaypointRepository;
import pw.cinque.waypoints.Waypoint;

public class GuiScreenDeleteConfirm extends GuiScreen {

	private final GuiScreenWaypointsMenu parent;
	private final Waypoint waypoint;
	private final IWaypointRepository waypoints;

	public GuiScreenDeleteConfirm(IWaypointRepository waypoints, GuiScreenWaypointsMenu parent, Waypoint waypoint) {
		this.waypoints = waypoints;
		this.parent = parent;
		this.waypoint = waypoint;
	}

	@Override
	public void initGui() {
		buttonList.add(new GuiButton(0, width / 2 - 101, height / 2 + 12, 100, 20, "Confirm"));
		buttonList.add(new GuiButton(1, width / 2 + 1, height / 2 + 12, 100, 20, "Cancel"));
	}

	@Override
	public void drawScreen(int x, int y, float partialTicks) {
		drawDefaultBackground();
		drawCenteredString(mc.fontRendererObj, "Are you sure you want to delete waypoint '" + waypoint.getName() + "'?", width / 2, height / 2 - 12, 0xFFFFFF);
		super.drawScreen(x, y, partialTicks);
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		switch (button.id) {
		case 0:
			waypoints.removeWaypoint(waypoint);
			mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Waypoint '" + waypoint.getName() + "' deleted!"));
			mc.displayGuiScreen(parent);
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
