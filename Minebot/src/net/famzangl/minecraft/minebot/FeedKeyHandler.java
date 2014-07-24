package net.famzangl.minecraft.minebot;

import java.util.List;

import net.minecraft.block.BlockColored;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.init.Items;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;

public class FeedKeyHandler {
	private static Minecraft mc = Minecraft.getMinecraft();

	private static final int MAX_INTERACTIONS_PER_HIT = 10;

	public static FeedKeyHandler instance = new FeedKeyHandler();

	public FeedKeyHandler() {
	}

	public static void Pressed(KeyInputEvent event, boolean breedOrSit,
			boolean onlyCurrent) {
		if (mc.currentScreen != null) {
			return;
		}
		instance.interact(breedOrSit, onlyCurrent);
	}

	/**
	 * Keys:
	 * 
	 * F: Feed (=> heal) or unsit (depends on item) G: Breed or sit V: As F, but
	 * only type that is pointed to B: As G, but only tpye that is pointed to
	 */
	private void interact(boolean breedOrSit, boolean onlyCurrent) {
		// r.mousePress(InputEvent.BUTTON3_MASK);
		// r.mouseRelease(InputEvent.BUTTON3_MASK);

		int color = -1;
		String filterClass = null;
		if (onlyCurrent) {
			final Entity hit = mc.objectMouseOver.entityHit;
			if (hit == null) {
				return;
			}
			filterClass = hit.getClass().getCanonicalName();

			if (hit instanceof EntityWolf) {
				final EntityWolf wolf = (EntityWolf) hit;
				color = wolf.getCollarColor();
			}
		}
		int interactedWith = 0;
		final List<Entity> entsInBBList = null;
		for (final Entity e : entsInBBList) {
			if (!(e instanceof EntityAnimal)) {
				continue;
			}
			final EntityAnimal animal = (EntityAnimal) e;

			System.out.print(e.getClass().getName() + "," + e.posX + ", "
					+ e.posY + ", " + e.posZ + ": ");

			if (interactedWith > MAX_INTERACTIONS_PER_HIT) {
				System.out.println("skipped (maximum)");
				break;
			}

			// filter
			if (filterClass != null
					&& !e.getClass().getCanonicalName().equals(filterClass)
					&& (color == -1 || e instanceof EntityWolf
							&& ((EntityWolf) e).getCollarColor() == color)) {
				System.out.println("skipped (filter)");
				continue;
			}

			// do action
			final ItemStack currentItem = mc.thePlayer.inventory
					.getCurrentItem();
			if (currentItem != null
					&& (currentItem.getItem() instanceof ItemFood
							|| currentItem.getItem() == Items.wheat || currentItem
								.getItem() instanceof ItemSeeds)) {
				// feed
				// if (breedOrSit) {
				if (!animal.isBreedingItem(currentItem)) {
					System.out.println("Cannot breed with this item.");
					continue;
				}
				if (animal.isInLove()) {
					System.out.println("Already in love.");
					continue;
				}

				// }
			} else if (currentItem != null
					&& currentItem.getItem() == Items.dye) {
				if (!(e instanceof EntityWolf)) {
					System.out.println("Can only color a wolf.");
					continue;
				}
				final int newColor = BlockColored.func_150032_b(currentItem
						.getItemDamage());
				if (((EntityWolf) e).getCollarColor() == newColor) {
					System.out.println("Already has color.");
					continue;
				}

			} else if (currentItem != null
					&& currentItem.getItem() instanceof ItemSword) {
				if (e instanceof EntityTameable
						&& mc.thePlayer.equals(((EntityTameable) e).getOwner())) {
					System.out.println("Owned, so not attacked.");
					continue;
				}
				if (animal.getGrowingAge() != 0) {
					System.out.println("Not adult");
					continue;
				}
				mc.playerController.attackEntity(mc.thePlayer, e);
				System.out.println("hit!");
				interactedWith++;
				continue;
			} else if (currentItem != null
					&& currentItem.getItem() instanceof ItemShears) {
				if (!(e instanceof EntitySheep)) {
					System.out.println("no sheep.");
					continue;
				}
				if (!((EntitySheep) e).isShearable(currentItem, e.worldObj,
						(int) e.posX, (int) e.posY, (int) e.posZ)) {
					System.out.println("not shearable.");
					continue;
				}
			} else {
				// sit/unsit
				if (!(e instanceof EntityTameable)) {
					System.out.println("Not a wolf/cat");
					continue;
				}

				final EntityTameable tameable = (EntityTameable) e;
				if (!tameable.isTamed()) {
					System.out.println("Not tamed.");
					continue;
				}
				if (!mc.thePlayer.equals(tameable.getOwner())) {
					System.out.println("Not owned.");
					continue;
				}

				if (breedOrSit == tameable.isSitting()) {
					System.out.println("Already in right sitting state.");
					continue;
				}
			}

			mc.playerController.interactWithEntitySendPacket(mc.thePlayer, e);
			System.out.println("interact");
			interactedWith++;
		}
		// mc.playerController.getBlockReachDistance()
	}
}