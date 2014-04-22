package net.famzangl.minecraft.minebot;

/**
 * Mob farming script
 * 
 * @author michael
 * 
 */
public class MobFarmHandler /* extends Helper */{
	// private static final int E_SLOT = 2;
	//
	// private static final int DIST = 3;
	//
	// static enum State {
	// NONE, ETABLE_OPENING, ETABLE_ITEM_PUT, ETABLE_ENCHANTED,
	// ETABLE_ITEM_TAKE, HIT_MOBS
	// }
	//
	// private KeyBinding farm = new KeyBinding("Farm mobs",
	// Keyboard.getKeyIndex("M"), "Command Mod");
	//
	// State state = State.NONE;
	//
	// private int waitTimer = 0;
	//
	// private int clickTimer;
	//
	// @SubscribeEvent
	// public void onPlayerTick(PlayerTickEvent evt) {
	// if (farm.getIsKeyPressed()) {
	// state = State.HIT_MOBS;
	// } else if (stop.getIsKeyPressed()) {
	// if (state != State.NONE) {
	// System.out.println("Abort.");
	// }
	// state = State.NONE;
	// }
	//
	// switch (state) {
	// case NONE:
	// break;
	// case HIT_MOBS:
	// if (hasEnoughLevelsForEnchanting()) {
	// if (faceEnchantingTable() && openEnchantingTable()) {
	// state = State.ETABLE_OPENING;
	// }
	// } else {
	// hitMoreMobs();
	// state = State.HIT_MOBS;
	// }
	// break;
	// case ETABLE_OPENING:
	// if (placeEnchantmentItem()) {
	// state = State.ETABLE_ITEM_PUT;
	// }
	// break;
	// case ETABLE_ITEM_PUT:
	// if (pickEnchantment()) {
	// state = State.ETABLE_ENCHANTED;
	// }
	// break;
	// case ETABLE_ENCHANTED:
	// if (takeEnchantmentItem()) {
	// state = State.ETABLE_ITEM_TAKE;
	// }
	// break;
	// case ETABLE_ITEM_TAKE:
	// if (closeEtable()) {
	// state = State.HIT_MOBS;
	// }
	// break;
	// default:
	// System.out.println("Cannot handle state  " + state);
	// }
	// }
	//
	// private boolean hasEnoughLevelsForEnchanting() {
	// return this.mc.thePlayer.experienceLevel >= 30;
	// }
	//
	// private void hitMoreMobs() {
	// MovingObjectPosition objectMouseOver = mc.objectMouseOver;
	// if (objectMouseOver == null
	// || objectMouseOver.typeOfHit !=
	// MovingObjectPosition.MovingObjectType.ENTITY) {
	// if (waitTimer < 100) {
	// waitTimer++;
	// return;
	// }
	// waitTimer = 0;
	// List<Entity> entsInBBList = mc.theWorld
	// .getEntitiesWithinAABBExcludingEntity(
	// mc.renderViewEntity,
	// mc.renderViewEntity.boundingBox
	// .addCoord(-DIST, -DIST, -DIST)
	// .addCoord(DIST, DIST, DIST)
	// .expand((double) 1, (double) 1, (double) 1),
	// new IEntitySelector() {
	// @Override
	// public boolean isEntityApplicable(Entity var1) {
	// return var1 instanceof EntityLiving;
	// }
	// });
	// if (entsInBBList.isEmpty()) {
	// System.out.println("No entity in range");
	// return;
	// }
	// System.out.println("Face next entity in range");
	// int n = new Random().nextInt(entsInBBList.size());
	// Entity e = entsInBBList.get(n);
	// AxisAlignedBB ebb = e.boundingBox;
	// face((ebb.maxX + ebb.minX) / 2, ebb.minY + 0.2,
	// (ebb.maxZ + ebb.minZ) / 2);
	// } else {
	// if (clickTimer < 10) {
	// clickTimer++;
	// }
	// clickTimer = 0;
	// Entity facedEntity = objectMouseOver.entityHit;
	// if (facedEntity instanceof EntityLiving) {
	// mc.thePlayer.swingItem();
	// mc.playerController.attackEntity(mc.thePlayer, facedEntity);
	// }
	// }
	// }
	//
	// private boolean faceEnchantingTable() {
	// Pos pos = findBlock(Blocks.enchanting_table);
	// if (pos == null) {
	// System.out.println("Could not find table around player.");
	// return false;
	// }
	//
	// face(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5);
	// return true;
	// }
	//
	// private boolean openEnchantingTable() {
	// // mc.playerController.attackEntity(par1EntityPlayer, par2Entity);
	// ItemStack itemstack = mc.thePlayer.inventory.getCurrentItem();
	// MovingObjectPosition objectMouseOver = mc.objectMouseOver;
	// if (objectMouseOver == null
	// || objectMouseOver.typeOfHit !=
	// MovingObjectPosition.MovingObjectType.BLOCK) {
	// System.out.println("Not facing any block.");
	// return false;
	// }
	// Block block = mc.theWorld.getBlock(objectMouseOver.blockX,
	// objectMouseOver.blockY, objectMouseOver.blockZ);
	// if (!Block.isEqualTo(block, Blocks.enchanting_table)) {
	// System.out.println("Not facing enchantment table.");
	// return false;
	// }
	//
	// mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld,
	// itemstack, objectMouseOver.blockX, objectMouseOver.blockY,
	// objectMouseOver.blockZ, objectMouseOver.sideHit,
	// objectMouseOver.hitVec);
	// return true;
	// }
	//
	// private boolean placeEnchantmentItem() {
	// if (!(mc.currentScreen instanceof GuiEnchantment)) {
	// System.out.println("Screen not opened.");
	// return false;
	// }
	// GuiEnchantment screen = (GuiEnchantment) mc.currentScreen;
	// if (!screen.inventorySlots.getSlot(0).getHasStack()) {
	// for (int i = 1; i < 9 * 4 + 1; i++) {
	// Slot slot = screen.inventorySlots.getSlot(i);
	// if (slot == null || !slot.canTakeStack(mc.thePlayer)) {
	// continue;
	// }
	// ItemStack stack = slot.getStack();
	// if (stack != null && stack.isItemEnchantable()) {
	// mc.playerController.windowClick(
	// screen.inventorySlots.windowId, i, 0, 1,
	// mc.thePlayer);
	// // ItemStack itemstack3 =
	// // screen.inventorySlots.transferStackInSlot(mc.thePlayer,
	// // i);
	// // System.out.println("Selected slot " + i + ". Got stack "
	// // + itemstack3 + " (should be null)");
	// System.out.println("Selected slot " + i);
	// return true;
	// }
	// }
	// System.out.println("No item to put.");
	// return false;
	// } else {
	// return true;
	// }
	// }
	//
	// private boolean pickEnchantment() {
	// if (!(mc.currentScreen instanceof GuiEnchantment)) {
	// System.out.println("Screen not opened.");
	// return false;
	// }
	// GuiEnchantment screen = (GuiEnchantment) mc.currentScreen;
	// if (!screen.inventorySlots.getSlot(0).getHasStack()) {
	// System.out.println("No stack in slot.");
	// return false;
	// }
	// if (screen.inventorySlots.getSlot(0).getStack().isItemEnchanted()) {
	// System.out.println("Already enchanted.");
	// return true;
	// }
	//
	// try {
	// Field field = GuiEnchantment.class
	// .getDeclaredField("field_147075_G");
	// field.setAccessible(true);
	// ContainerEnchantment c = (ContainerEnchantment) field.get(screen);
	//
	// if (c.enchantLevels[E_SLOT] == 0) {
	// System.out.println("No enchantment levels computed.");
	// return false;
	// }
	// if (this.mc.thePlayer.experienceLevel < c.enchantLevels[E_SLOT]) {
	// System.out.println("Abort enchantment, not enough levels.");
	// return true;
	// }
	// if (c.enchantItem(this.mc.thePlayer, E_SLOT)) {
	// this.mc.playerController.sendEnchantPacket(c.windowId, E_SLOT);
	// }
	// System.out.println("Sent enchant request package.");
	// return true;
	// } catch (Throwable e) {
	// e.printStackTrace();
	// return false;
	// }
	// }
	//
	// private boolean takeEnchantmentItem() {
	// if (!(mc.currentScreen instanceof GuiEnchantment)) {
	// System.out.println("Screen not opened.");
	// return false;
	// }
	// GuiEnchantment screen = (GuiEnchantment) mc.currentScreen;
	// if (screen.inventorySlots.getSlot(0).getHasStack()
	// && screen.inventorySlots.getSlot(0).getStack()
	// .isItemEnchanted()) {
	// mc.playerController.windowClick(screen.inventorySlots.windowId, 0,
	// 0, 1, mc.thePlayer);
	// System.out.println("Taking item");
	// return true;
	// } else {
	// System.out.println("No good stack in slot0.");
	// return false;
	// }
	// }
	//
	// private boolean closeEtable() {
	// this.mc.displayGuiScreen((GuiScreen) null);
	// this.mc.setIngameFocus();
	// return true;
	// }
}
