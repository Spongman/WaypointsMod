package pw.cinque.waypoints.gui.screen;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Comparator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import pw.cinque.waypoints.Waypoint;
import pw.cinque.waypoints.WaypointsMod;
import pw.cinque.waypoints.gui.GuiSlotWaypoints;

public class GuiScreenWaypointsMenu extends GuiScreen {

	private GuiSlotWaypoints waypointsList;
	private GuiButton delete;
	private GuiButton cancel;
	private GuiButton sort;
	private static WaypointSort sortOrder = WaypointSort.File;
	
	enum WaypointSort {
		File,
		Alpha,
		Nearest,
		Farthest
	}

	public class NumberAwareComparator implements Comparator<String>
	{
	    @Override
	    public int compare(String s1, String s2)
	    {
	        int len1 = s1.length();
	        int len2 = s2.length();
	        int i1 = 0;
	        int i2 = 0;
	        while (true)
	        {
	            // handle the case when one string is longer than another
	            if (i1 == len1)
	                return i2 == len2 ? 0 : -1;
	            if (i2 == len2)
	                return 1;

	            char ch1 = s1.charAt(i1);
	            char ch2 = s2.charAt(i2);
	            if (Character.isDigit(ch1) && Character.isDigit(ch2))
	            {
	                // skip leading zeros
	                while (i1 < len1 && s1.charAt(i1) == '0')
	                    i1++;
	                while (i2 < len2 && s2.charAt(i2) == '0')
	                    i2++;

	                // find the ends of the numbers
	                int end1 = i1;
	                int end2 = i2;
	                while (end1 < len1 && Character.isDigit(s1.charAt(end1)))
	                    end1++;
	                while (end2 < len2 && Character.isDigit(s2.charAt(end2)))
	                    end2++;

	                int diglen1 = end1 - i1;
	                int diglen2 = end2 - i2;

	                // if the lengths are different, then the longer number is bigger
	                if (diglen1 != diglen2)
	                    return diglen1 - diglen2;

	                // compare numbers digit by digit
	                while (i1 < end1)
	                {
	                    if (s1.charAt(i1) != s2.charAt(i2))
	                        return s1.charAt(i1) - s2.charAt(i2);
	                    i1++;
	                    i2++;
	                }
	            }
	            else
	            {
	                // plain characters comparison
	                if (ch1 != ch2)
	                    return ch1 - ch2;
	                i1++;
	                i2++;
	            }
	        }
	    }
	}	
	
	class WaypointNameComparator implements Comparator<Waypoint> {
		NumberAwareComparator _nac = new NumberAwareComparator(); 
		public int compare(Waypoint wp1, Waypoint wp2) {
			if (wp1 == wp2)
				return 0;

			int cmp;

			cmp = _nac.compare(wp1.getServer(), wp2.getServer());
			if (cmp != 0)
				return cmp;

			cmp = _nac.compare(wp1.getWorld(), wp2.getWorld());
			if (cmp != 0)
				return cmp;

			return _nac.compare(wp1.getName(), wp2.getName());
		}
	}

	class WaypointDistanceComparator implements Comparator<Waypoint> {

		private int _scale;
		private Entity _en;

		public WaypointDistanceComparator(int scale, Entity en) {
			_scale = scale;
			_en = en;
		}

		public int compare(Waypoint wp1, Waypoint wp2) {
			double d1 = wp1.getDistance(_en);
			double d2 = wp2.getDistance(_en);
			return Double.compare(d1, d2) * _scale;
		}
	}
	

	private void sortList() {
		ArrayList<Waypoint> list = WaypointsMod.getWaypointsToRender();
		Comparator<Waypoint> comparator = null;
		switch(this.sortOrder) {
		case File:
			sort.displayString = "File";
			break;
		case Alpha:
			comparator = new WaypointNameComparator();
			sort.displayString = "Name";
			break;
		case Nearest:
			comparator = new WaypointDistanceComparator(1, Minecraft.getMinecraft().getRenderViewEntity());
			sort.displayString = "Near";
			break;
		case Farthest:
			comparator = new WaypointDistanceComparator(-1, Minecraft.getMinecraft().getRenderViewEntity());
			sort.displayString = "Far";
			break;
		}
		if (comparator != null)
			list.sort(comparator);
	}

	@Override
	public void initGui() {
		
		this.buttonList.add(delete = new GuiButton(0, this.width / 2 - 150, this.height - 24, 99, 20, "Delete"));
		this.buttonList.add(cancel = new GuiButton(1, this.width / 2 - 49, this.height - 24, 99, 20, "Cancel"));
		this.buttonList.add(sort = new GuiButton(2, this.width / 2 + 51, this.height - 24, 99, 20, ""));

		sortList();

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
			
		case 2:
			switch(this.sortOrder) {
			case File:
				this.sortOrder = WaypointSort.Alpha;
				break;
			case Alpha:
				this.sortOrder = WaypointSort.Nearest;
				break;
			case Nearest:
				this.sortOrder = WaypointSort.Farthest;
				break;
			case Farthest:
				this.sortOrder = WaypointSort.File;
				WaypointsMod.refreshWaypointsToRender();
				break;
			}
			sortList();
			return;
		}
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
