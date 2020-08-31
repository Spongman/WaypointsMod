package pw.cinque.waypoints.gui.screen;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.lang3.math.NumberUtils;
import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import pw.cinque.waypoints.IWaypointRepository;
import pw.cinque.waypoints.Waypoint;
import pw.cinque.waypoints.gui.GuiColorPicker;
import pw.cinque.waypoints.listener.WorldListener;

public class GuiScreenCreateWaypoint extends GuiScreen {

	private GuiTextField name;
	private GuiTextField coordsX;
	private GuiTextField coordsY;
	private GuiTextField coordsZ;
	private GuiColorPicker colorPicker;
	private GuiButton create;
	private GuiButton cancel;
	private final GuiScreen parentScreen;
	private final Waypoint waypoint;
	private final ArrayList<GuiTextField> textFields = new ArrayList<GuiTextField>();
	private final IWaypointRepository waypoints;

	public GuiScreenCreateWaypoint(IWaypointRepository waypoints, GuiScreen parentScreen, Waypoint waypoint) {
		this.waypoints = waypoints;
		this.parentScreen = parentScreen;
		this.waypoint = waypoint;
	}

	@Override
	public void initGui() {
		name = addTextField(width / 2 - 100, height / 2 - 48, 200, 20);
		name.setFocused(true);

		coordsX = addTextField(width / 2 - 100, height / 2 - 10, 64, 20);
		coordsY = addTextField(width / 2 - 32, height / 2 - 10, 63, 20);
		coordsZ = addTextField(width / 2 + 35, height / 2 - 10, 64, 20);

		buttonList.add(colorPicker = new GuiColorPicker(0, width / 2 - 101, height / 2 + 25, 202, 20));
		buttonList.add(create = new GuiButton(1, width / 2 - 101, height / 2 + 50, 100, 20, waypoint == null ? "Create" : "Save"));
		buttonList.add(cancel = new GuiButton(2, width / 2 + 1, height / 2 + 50, 100, 20, "Cancel"));

		if (waypoint != null) {
			name.setText(waypoint.getName());
			coordsX.setText(String.valueOf(waypoint.getX()));
			coordsY.setText(String.valueOf(waypoint.getY()));
			coordsZ.setText(String.valueOf(waypoint.getZ()));
			colorPicker.setColor(waypoint.getColor());
		} else {
			coordsX.setText(String.valueOf((int) mc.thePlayer.posX));
			coordsY.setText(String.valueOf((int) mc.thePlayer.posY));
			coordsZ.setText(String.valueOf((int) mc.thePlayer.posZ));
		}
		updateCommitButton();
	}

	private GuiTextField addTextField(int x, int y, int width, int height) {
		final GuiTextField field = new GuiTextField(textFields.size(), fontRendererObj, x, y, width, height);
		textFields.add(field);
		return field;
	}

	@Override
	public void drawScreen(int x, int y, float partialTicks) {
		drawDefaultBackground();

		drawCenteredString(fontRendererObj, waypoint == null ? "Create Waypoint" : "Edit Waypoint", width / 2, 10, 0xFFFFFF);
		drawCenteredString(fontRendererObj, "Name:", width / 2, height / 2 - 60, 0xFFFFFF);
		drawCenteredString(fontRendererObj, "Coordinates (X/Y/Z):", width / 2, height / 2 - 22, 0xFFFFFF);
		drawCenteredString(fontRendererObj, "Color:", width / 2, height / 2 + 14, 0xFFFFFF);

		for (final GuiTextField field : textFields) {
			field.drawTextBox();
		}

		super.drawScreen(x, y, partialTicks);
	}

	@Override
	protected void keyTyped(char character, int index) {
		if (index == Keyboard.KEY_NUMPADENTER || index == Keyboard.KEY_RETURN) {
			if (create.enabled) {
				createWaypoint();
			}
		} else if (index == Keyboard.KEY_ESCAPE) {
			mc.displayGuiScreen(null);
		} else if (index == Keyboard.KEY_TAB) {
			for (final GuiTextField field : textFields)
				if (field.isFocused()) {
					final GuiTextField other = textFields.get((field.getId() + 1) % textFields.size());
					other.setFocused(true);
					// other.setCursorPositionEnd();
					// other.setSelectionPos(0);
					field.setFocused(false);
					break;
				}
		} else {
			for (final GuiTextField field : textFields)
				if (field.isFocused()) {
					field.textboxKeyTyped(character, index);
					break;
				}
		}

		updateCommitButton();
	}

	private void updateCommitButton() {
		for (final Waypoint waypoint : waypoints.getWaypointsToRender())
			if (waypoint != this.waypoint && waypoint.getName().equalsIgnoreCase(name.getText())) {
				create.enabled = false;
				return;
			}

		create.enabled = name.getText().length() > 0 && NumberUtils.isDigits(coordsX.getText().replace("-", "")) && NumberUtils.isDigits(coordsY.getText().replace("-", ""))
				&& NumberUtils.isDigits(coordsZ.getText().replace("-", ""));
	}

	@Override
	public void updateScreen() {
		for (final GuiTextField field : textFields) {
			field.updateCursorCounter();
		}
	}

	protected void createWaypoint() {
		final String name = this.name.getText();
		final int x = Integer.valueOf(coordsX.getText());
		final int y = Integer.valueOf(coordsY.getText());
		final int z = Integer.valueOf(coordsZ.getText());
		final int color = colorPicker.getSelectedColor();

		if (waypoint == null) {
			final String world = mc.theWorld.provider.getDimensionName();
			final String server = WorldListener.getWorldName();
			waypoints.addWaypoint(new Waypoint(name, world, server, x, y, z, color));
			mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Waypoint '" + name + "' created!"));
		} else {
			waypoint.setName(name);
			waypoint.setX(x);
			waypoint.setY(y);
			waypoint.setZ(z);
			waypoint.setColor(color);
			mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Waypoint '" + name + "' updated!"));
		}
		mc.displayGuiScreen(parentScreen);
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		switch (button.id) {
		case 0:
			colorPicker.nextColor();
			return;

		case 1:
			createWaypoint();
			return;

		case 2:
			mc.displayGuiScreen(parentScreen);
			return;
		}
	}

	@Override
	protected void mouseClicked(int x, int y, int key) throws IOException {
		super.mouseClicked(x, y, key);
		for (final GuiTextField field : textFields) {
			field.mouseClicked(x, y, key);
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

}
