package pw.cinque.waypoints.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiSlot;
import pw.cinque.waypoints.IWaypointRepository;
import pw.cinque.waypoints.Waypoint;
import pw.cinque.waypoints.gui.screen.GuiScreenCreateWaypoint;
import pw.cinque.waypoints.gui.screen.GuiScreenWaypointsMenu;

public class GuiSlotWaypoints extends GuiSlot {

	private final Minecraft mc = Minecraft.getMinecraft();
	private final GuiScreenWaypointsMenu parent;
	private int selectedIndex = -1;
	private final IWaypointRepository waypoints;

	public GuiSlotWaypoints(IWaypointRepository waypoints, GuiScreenWaypointsMenu parent) {
		super(Minecraft.getMinecraft(), parent.width, parent.height, 48, parent.height - 48, 24);
		this.waypoints = waypoints;
		this.parent = parent;
	}

	@Override
	protected int getSize() {
		return waypoints.getWaypointsToRender().size();
	}

	@Override
	protected boolean isSelected(int index) {
		return selectedIndex == index;
	}

	public int getSelectedIndex() {
		return selectedIndex;
	}

	@Override
	protected int getContentHeight() {
		return getSize() * 24;
	}

	@Override
	protected void drawBackground() {
	}

	@Override
	protected void elementClicked(int index, boolean doubleClicked, int p_148144_3_, int p_148144_4_) {
		selectedIndex = index;

		if (doubleClicked) {
			final Waypoint waypoint = waypoints.getWaypointsToRender().get(selectedIndex);
			mc.displayGuiScreen(new GuiScreenCreateWaypoint(waypoints, parent, waypoint));
		}
	}

	@Override
	protected void drawSlot(int index, int x, int y, int p_148126_4_, int p_148126_6_, int p_148126_7_) {
		final Waypoint waypoint = waypoints.getWaypointsToRender().get(index);
		parent.drawString(parent.getFontRenderer(), waypoint.getName(), x + 2, y, 0xFFFFFF);
		parent.drawString(parent.getFontRenderer(), waypoint.getWorld() + " - " + waypoint.getX() + " / " + waypoint.getY() + " / " + waypoint.getZ(), x + 2, y + 12, 0x777777);
	}

}
