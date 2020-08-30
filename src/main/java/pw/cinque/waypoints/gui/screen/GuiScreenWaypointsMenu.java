package pw.cinque.waypoints.gui.screen;

import java.io.IOException;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import pw.cinque.waypoints.Waypoint;
import pw.cinque.waypoints.WaypointsMod;
import pw.cinque.waypoints.gui.GuiSlotWaypoints;

public class GuiScreenWaypointsMenu extends GuiScreen {

	private GuiSlotWaypoints waypointsList;
	private GuiButton delete;
	private GuiButton cancel;

	@Override
	public void initGui() {
		this.buttonList.add(delete = new GuiButton(0, this.width / 2 - 101, this.height - 24, 100, 20, "Delete"));
		this.buttonList.add(cancel = new GuiButton(1, this.width / 2 + 1, this.height - 24, 100, 20, "Cancel"));
		
		this.delete.enabled = false;
		this.waypointsList = new GuiSlotWaypoints(this);
	}

	@Override
	public void drawScreen(int x, int y, float partialTicks) {
		this.drawDefaultBackground();

		this.waypointsList.drawScreen(x, y, partialTicks);
		this.drawCenteredString(this.fontRendererObj, "Waypoints Menu", this.width / 2, 18, 0xFFFFFF);

		super.drawScreen(x, y, partialTicks);
	}

	@Override
	public void updateScreen() {
		delete.enabled = waypointsList.getSelectedIndex() != -1;
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		switch (button.id) {
		case 0:
			Waypoint waypoint = WaypointsMod.getWaypointsToRender().get(waypointsList.getSelectedIndex());
			mc.displayGuiScreen(new GuiScreenDeleteConfirm(this, waypoint));
			return;
			
		case 1:
			mc.displayGuiScreen(null);
			return;
		}
	
	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		this.waypointsList.handleMouseInput();
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
	
	public FontRenderer getFontRenderer() {
		return this.fontRendererObj;
	}

}
