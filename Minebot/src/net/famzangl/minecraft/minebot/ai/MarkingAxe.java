package net.famzangl.minecraft.minebot.ai;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class MarkingAxe extends ItemAxe {

	private AIController sendEvents;
	private boolean itemJustUsed;

	protected MarkingAxe(ToolMaterial p_i45327_1_, AIController sendEvents) {
		super(p_i45327_1_);
		this.sendEvents = sendEvents;
	}

	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player,
			World world, int x, int y, int z, int side, float hitX, float hitY,
			float hitZ) {
		// System.out.println("Item used first.");
		return super.onItemUseFirst(stack, player, world, x, y, z, side, hitX,
				hitY, hitZ);
	}

	@Override
	public boolean onItemUse(ItemStack par1ItemStack,
			EntityPlayer par2EntityPlayer, World par3World, int x, int y,
			int z, int side, float par8, float par9, float par10) {
		// System.out.println("Item used.");
		if (itemJustUsed && par3World.isRemote) {
			sendEvents.positionMarkEvent(x, y, z, side);
			itemJustUsed = false;
		}
		return super.onItemUse(par1ItemStack, par2EntityPlayer, par3World, x,
				y, z, side, par8, par9, par10);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World,
			EntityPlayer par3EntityPlayer) {
		if (par2World.isRemote) {
			itemJustUsed = true;
		}
//		System.out.println("Rightclick: " + par2World.isRemote);
		return super.onItemRightClick(par1ItemStack, par2World,
				par3EntityPlayer);
	}

	public void setIcon(IIcon icon) {
		itemIcon = icon;
	}
}
