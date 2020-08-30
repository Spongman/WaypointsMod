package pw.cinque.waypoints.gui.screen;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.lang3.math.NumberUtils;
import org.lwjgl.input.Keyboard;

import pw.cinque.waypoints.Waypoint;
import pw.cinque.waypoints.WaypointsMod;
import pw.cinque.waypoints.gui.GuiColorPicker;

public class GuiScreenCreateWaypoint extends GuiScreen {

	private GuiTextField name;
	private GuiTextField coordsX;
	private GuiTextField coordsY;
	private GuiTextField coordsZ;
	private GuiColorPicker colorPicker;
	private GuiButton create;
	private GuiButton cancel;
	private GuiScreen parentScreen;
	private Waypoint waypoint;
	private ArrayList<GuiTextField> textFields = new ArrayList<GuiTextField>();
	
	public GuiScreenCreateWaypoint(GuiScreen parentScreen, Waypoint waypoint) {
		this.parentScreen = parentScreen;
		this.waypoint = waypoint;
	}

	@Override
	public void initGui() {
		this.name = addTextField(this.width / 2 - 100, this.height / 2 - 48, 200, 20);
		this.name.setFocused(true);

		this.coordsX = addTextField(this.width / 2 - 100, this.height / 2 - 10, 64, 20);
		this.coordsY = addTextField(this.width / 2 - 32, this.height / 2 - 10, 63, 20);
		this.coordsZ = addTextField(this.width / 2 + 35, this.height / 2 - 10, 64, 20);

		this.buttonList.add(colorPicker = new GuiColorPicker(0, this.width / 2 - 101, this.height / 2 + 25, 202, 20));
		this.buttonList.add(create = new GuiButton(1, this.width / 2 - 101, this.height / 2 + 50, 100, 20, waypoint == null ? "Create" : "Save"));
		this.buttonList.add(cancel = new GuiButton(2, this.width / 2 + 1, this.height / 2 + 50, 100, 20, "Cancel"));

		if (this.waypoint != null) {
			this.name.setText(waypoint.getName());
			this.coordsX.setText(String.valueOf((int) waypoint.getX()));
			this.coordsY.setText(String.valueOf((int) waypoint.getY()));
			this.coordsZ.setText(String.valueOf((int) waypoint.getZ()));
			colorPicker.setColor(waypoint.getColor());
		} else {
			this.coordsX.setText(String.valueOf((int) mc.thePlayer.posX));
			this.coordsY.setText(String.valueOf((int) mc.thePlayer.posY));
			this.coordsZ.setText(String.valueOf((int) mc.thePlayer.posZ));
		}
		updateCommitButton();
	}
	
	private GuiTextField addTextField(int x, int y, int width, int height) {
		GuiTextField field = new GuiTextField(textFields.size(), this.fontRendererObj, x, y, width, height);
		textFields.add(field);
		return field;
	}

	@Override
	public void drawScreen(int x, int y, float partialTicks) {
		this.drawDefaultBackground();

		this.drawCenteredString(this.fontRendererObj, waypoint == null ? "Create Waypoint" : "Edit Waypoint", this.width / 2, 10, 0xFFFFFF);
		this.drawCenteredString(this.fontRendererObj, "Name:", this.width / 2, this.height / 2 - 60, 0xFFFFFF);
		this.drawCenteredString(this.fontRendererObj, "Coordinates (X/Y/Z):", this.width / 2, this.height / 2 - 22, 0xFFFFFF);
		this.drawCenteredString(this.fontRendererObj, "Color:", this.width / 2, this.height / 2 + 14, 0xFFFFFF);

		for (GuiTextField field : textFields)
			field.drawTextBox();

		super.drawScreen(x, y, partialTicks);
	}

	@Override
	protected void keyTyped(char character, int index) {
		if (index == Keyboard.KEY_NUMPADENTER || index == Keyboard.KEY_RETURN) {
			if (this.create.enabled)
				createWaypoint();
		} else if (index == Keyboard.KEY_ESCAPE) {
			mc.displayGuiScreen(null);
		} else if (index == Keyboard.KEY_TAB) {
			for (GuiTextField field : textFields) {
				if (field.isFocused()) {
					GuiTextField other = textFields.get((field.getId() + 1) % textFields.size());
					other.setFocused(true);
					//other.setCursorPositionEnd();
					//other.setSelectionPos(0);
					field.setFocused(false);
					break;
				}
			}
		} else {
			for (GuiTextField field : textFields) {
				if (field.isFocused()) {
					field.textboxKeyTyped(character, index);
					break;
				}
			}
		}
		
		updateCommitButton();
	}
	
	private void updateCommitButton() {
		for (Waypoint waypoint : WaypointsMod.getWaypointsToRender()) {
			if (waypoint != this.waypoint && waypoint.getName().equalsIgnoreCase(name.getText())) {
				this.create.enabled = false;
				return;
			}
		}

		this.create.enabled = name.getText().length() > 0 && NumberUtils.isDigits(coordsX.getText().replace("-", "")) && NumberUtils.isDigits(coordsY.getText().replace("-", ""))
				&& NumberUtils.isDigits(coordsZ.getText().replace("-", ""));
	}

	@Override
	public void updateScreen() {
		for (GuiTextField field : textFields)
			field.updateCursorCounter();
	}

	protected void createWaypoint() {
		String name = this.name.getText();
		int x = Integer.valueOf(coordsX.getText());
		int y = Integer.valueOf(coordsY.getText());
		int z = Integer.valueOf(coordsZ.getText());
		int color = colorPicker.getSelectedColor();

		if (this.waypoint == null) {
			String world = mc.theWorld.provider.getDimensionName();
			String server = WaypointsMod.getWorldName();
			WaypointsMod.addWaypoint(new Waypoint(name, world, server, x, y, z, color));
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
		for (GuiTextField field : textFields)
			field.mouseClicked(x, y, key);
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

}
