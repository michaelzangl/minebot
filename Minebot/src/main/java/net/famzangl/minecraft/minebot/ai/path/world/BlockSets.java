package net.famzangl.minecraft.minebot.ai.path.world;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.math.BlockPos;

import java.util.stream.Stream;

/**
 * This is a helper class that holds lots of block sets and helper functions to
 * check blocks for safety.
 * 
 * @author Michael Zangl
 */
public class BlockSets {

	/**
	 * 1.16.3 New Blocks were not mapped with proper names, so this allows you to convert string to Block
	 * @param blockName The string name of the block
	 * @return The Block itself.
	 */
	public static Block nameConvert(String blockName) {
		//Change to a dictionary if wanted:
		/*mappings.put("nether_gold_ore", Blocks.field_235334_I_);
		mappings.put("soul_fire", Blocks.field_235335_bO_);
		mappings.put("soul_soil", Blocks.field_235336_cN_);
		mappings.put("basalt", Blocks.field_235337_cO_);
		mappings.put("polished_basalt", Blocks.field_235338_cP_);
		mappings.put("soul_torch", Blocks.field_235339_cQ_);
		mappings.put("soul_wall_torch", Blocks.field_235340_cR_);
		mappings.put("chain", Blocks.field_235341_dI_);
		mappings.put("soul_lantern", Blocks.field_235366_md_);
		mappings.put("soul_campfire", Blocks.field_235367_mf_);
		mappings.put("warped_stem", Blocks.field_235368_mh_);
		mappings.put("stripped_warped_stem", Blocks.field_235369_mi_);
		mappings.put("warped_hyphae", Blocks.field_235370_mj_);
		mappings.put("stripped_warped_hyphae", Blocks.field_235371_mk_);
		mappings.put("warped_nylium", Blocks.field_235372_ml_);
		mappings.put("warped_fungus", Blocks.field_235373_mm_);
		mappings.put("warped_wart_block", Blocks.field_235374_mn_);
		mappings.put("warped_roots", Blocks.field_235375_mo_);
		mappings.put("nether_sprouts", Blocks.field_235376_mp_);
		mappings.put("crimson_stem", Blocks.field_235377_mq_);
		mappings.put("stripped_crimson_stem", Blocks.field_235378_mr_);
		mappings.put("crimson_hyphae", Blocks.field_235379_ms_);
		mappings.put("stripped_crimson_hyphae", Blocks.field_235380_mt_);
		mappings.put("crimson_nylium", Blocks.field_235381_mu_);
		mappings.put("crimson_fungus", Blocks.field_235382_mv_);
		mappings.put("shroomlight", Blocks.field_235383_mw_);
		mappings.put("weeping_vines", Blocks.field_235384_mx_);
		mappings.put("weeping_vines_plant", Blocks.field_235385_my_);
		mappings.put("twisting_vines", Blocks.field_235386_mz_);
		mappings.put("twisting_vines_plant", Blocks.field_235342_mA_);
		mappings.put("crimson_roots", Blocks.field_235343_mB_);
		mappings.put("crimson_planks", Blocks.field_235344_mC_);
		mappings.put("warped_planks", Blocks.field_235345_mD_);
		mappings.put("crimson_slab", Blocks.field_235346_mE_);
		mappings.put("warped_slab", Blocks.field_235347_mF_);
		mappings.put("crimson_pressure_plate", Blocks.field_235348_mG_);
		mappings.put("warped_pressure_plate", Blocks.field_235349_mH_);
		mappings.put("crimson_fence", Blocks.field_235350_mI_);
		mappings.put("warped_fence", Blocks.field_235351_mJ_);
		mappings.put("crimson_trapdoor", Blocks.field_235352_mK_);
		mappings.put("warped_trapdoor", Blocks.field_235353_mL_);
		mappings.put("crimson_fence_gate", Blocks.field_235354_mM_);
		mappings.put("warped_fence_gate", Blocks.field_235355_mN_);
		mappings.put("crimson_stairs", Blocks.field_235356_mO_);
		mappings.put("warped_stairs", Blocks.field_235357_mP_);
		mappings.put("crimson_button", Blocks.field_235358_mQ_);
		mappings.put("warped_button", Blocks.field_235359_mR_);
		mappings.put("crimson_door", Blocks.field_235360_mS_);
		mappings.put("warped_door", Blocks.field_235361_mT_);
		mappings.put("crimson_sign", Blocks.field_235362_mU_);
		mappings.put("warped_sign", Blocks.field_235363_mV_);
		mappings.put("crimson_wall_sign", Blocks.field_235364_mW_);
		mappings.put("warped_wall_sign", Blocks.field_235365_mX_);
		mappings.put("target", Blocks.field_235396_nb_);
		mappings.put("netherite_block", Blocks.field_235397_ng_);
		mappings.put("ancient_debris", Blocks.field_235398_nh_);
		mappings.put("crying_obsidian", Blocks.field_235399_ni_);
		mappings.put("respawn_anchor", Blocks.field_235400_nj_);
		mappings.put("potted_crimson_fungus", Blocks.field_235401_nk_);
		mappings.put("potted_warped_fungus", Blocks.field_235402_nl_);
		mappings.put("potted_crimson_roots", Blocks.field_235403_nm_);
		mappings.put("potted_warped_roots", Blocks.field_235404_nn_);
		mappings.put("lodestone", Blocks.field_235405_no_);
		mappings.put("blackstone", Blocks.field_235406_np_);
		mappings.put("blackstone_stairs", Blocks.field_235407_nq_);
		mappings.put("blackstone_wall", Blocks.field_235408_nr_);
		mappings.put("blackstone_slab", Blocks.field_235409_ns_);
		mappings.put("polished_blackstone", Blocks.field_235410_nt_);
		mappings.put("polished_blackstone_bricks", Blocks.field_235411_nu_);
		mappings.put("cracked_polished_blackstone_bricks", Blocks.field_235412_nv_);
		mappings.put("chiseled_polished_blackstone", Blocks.field_235413_nw_);
		mappings.put("polished_blackstone_brick_slab", Blocks.field_235414_nx_);
		mappings.put("polished_blackstone_brick_stairs", Blocks.field_235415_ny_);
		mappings.put("polished_blackstone_brick_wall", Blocks.field_235416_nz_);
		mappings.put("gilded_blackstone", Blocks.field_235387_nA_);
		mappings.put("polished_blackstone_stairs", Blocks.field_235388_nB_);
		mappings.put("polished_blackstone_slab", Blocks.field_235389_nC_);
		mappings.put("polished_blackstone_pressure_plate", Blocks.field_235390_nD_);
		mappings.put("polished_blackstone_button", Blocks.field_235391_nE_);
		mappings.put("polished_blackstone_wall", Blocks.field_235392_nF_);
		mappings.put("chiseled_nether_bricks", Blocks.field_235393_nG_);
		mappings.put("cracked_nether_bricks", Blocks.field_235394_nH_);
		*/
		Block mapped = null;
		switch (blockName) {
			case "nether_gold_ore": mapped = Blocks.field_235334_I_; break;
			case "soul_fire": mapped = Blocks.field_235335_bO_; break;
			case "soul_soil": mapped = Blocks.field_235336_cN_; break;
			case "basalt": mapped = Blocks.field_235337_cO_; break;
			case "polished_basalt": mapped = Blocks.field_235338_cP_; break;
			case "soul_torch": mapped = Blocks.field_235339_cQ_; break;
			case "soul_wall_torch": mapped = Blocks.field_235340_cR_; break;
			case "chain": mapped = Blocks.field_235341_dI_; break;
			case "soul_lantern": mapped = Blocks.field_235366_md_; break;
			case "soul_campfire": mapped = Blocks.field_235367_mf_; break;
			case "warped_stem": mapped = Blocks.field_235368_mh_; break;
			case "stripped_warped_stem": mapped = Blocks.field_235369_mi_; break;
			case "warped_hyphae": mapped = Blocks.field_235370_mj_; break;
			case "stripped_warped_hyphae": mapped = Blocks.field_235371_mk_; break;
			case "warped_nylium": mapped = Blocks.field_235372_ml_; break;
			case "warped_fungus": mapped = Blocks.field_235373_mm_; break;
			case "warped_wart_block": mapped = Blocks.field_235374_mn_; break;
			case "warped_roots": mapped = Blocks.field_235375_mo_; break;
			case "nether_sprouts": mapped = Blocks.field_235376_mp_; break;
			case "crimson_stem": mapped = Blocks.field_235377_mq_; break;
			case "stripped_crimson_stem": mapped = Blocks.field_235378_mr_; break;
			case "crimson_hyphae": mapped = Blocks.field_235379_ms_; break;
			case "stripped_crimson_hyphae": mapped = Blocks.field_235380_mt_; break;
			case "crimson_nylium": mapped = Blocks.field_235381_mu_; break;
			case "crimson_fungus": mapped = Blocks.field_235382_mv_; break;
			case "shroomlight": mapped = Blocks.field_235383_mw_; break;
			case "weeping_vines": mapped = Blocks.field_235384_mx_; break;
			case "weeping_vines_plant": mapped = Blocks.field_235385_my_; break;
			case "twisting_vines": mapped = Blocks.field_235386_mz_; break;
			case "twisting_vines_plant": mapped = Blocks.field_235342_mA_; break;
			case "crimson_roots": mapped = Blocks.field_235343_mB_; break;
			case "crimson_planks": mapped = Blocks.field_235344_mC_; break;
			case "warped_planks": mapped = Blocks.field_235345_mD_; break;
			case "crimson_slab": mapped = Blocks.field_235346_mE_; break;
			case "warped_slab": mapped = Blocks.field_235347_mF_; break;
			case "crimson_pressure_plate": mapped = Blocks.field_235348_mG_; break;
			case "warped_pressure_plate": mapped = Blocks.field_235349_mH_; break;
			case "crimson_fence": mapped = Blocks.field_235350_mI_; break;
			case "warped_fence": mapped = Blocks.field_235351_mJ_; break;
			case "crimson_trapdoor": mapped = Blocks.field_235352_mK_; break;
			case "warped_trapdoor": mapped = Blocks.field_235353_mL_; break;
			case "crimson_fence_gate": mapped = Blocks.field_235354_mM_; break;
			case "warped_fence_gate": mapped = Blocks.field_235355_mN_; break;
			case "crimson_stairs": mapped = Blocks.field_235356_mO_; break;
			case "warped_stairs": mapped = Blocks.field_235357_mP_; break;
			case "crimson_button": mapped = Blocks.field_235358_mQ_; break;
			case "warped_button": mapped = Blocks.field_235359_mR_; break;
			case "crimson_door": mapped = Blocks.field_235360_mS_; break;
			case "warped_door": mapped = Blocks.field_235361_mT_; break;
			case "crimson_sign": mapped = Blocks.field_235362_mU_; break;
			case "warped_sign": mapped = Blocks.field_235363_mV_; break;
			case "crimson_wall_sign": mapped = Blocks.field_235364_mW_; break;
			case "warped_wall_sign": mapped = Blocks.field_235365_mX_; break;
			case "target": mapped = Blocks.field_235396_nb_; break;
			case "netherite_block": mapped = Blocks.field_235397_ng_; break;
			case "ancient_debris": mapped = Blocks.field_235398_nh_; break;
			case "crying_obsidian": mapped = Blocks.field_235399_ni_; break;
			case "respawn_anchor": mapped = Blocks.field_235400_nj_; break;
			case "potted_crimson_fungus": mapped = Blocks.field_235401_nk_; break;
			case "potted_warped_fungus": mapped = Blocks.field_235402_nl_; break;
			case "potted_crimson_roots": mapped = Blocks.field_235403_nm_; break;
			case "potted_warped_roots": mapped = Blocks.field_235404_nn_; break;
			case "lodestone": mapped = Blocks.field_235405_no_; break;
			case "blackstone": mapped = Blocks.field_235406_np_; break;
			case "blackstone_stairs": mapped = Blocks.field_235407_nq_; break;
			case "blackstone_wall": mapped = Blocks.field_235408_nr_; break;
			case "blackstone_slab": mapped = Blocks.field_235409_ns_; break;
			case "polished_blackstone": mapped = Blocks.field_235410_nt_; break;
			case "polished_blackstone_bricks": mapped = Blocks.field_235411_nu_; break;
			case "cracked_polished_blackstone_bricks": mapped = Blocks.field_235412_nv_; break;
			case "chiseled_polished_blackstone": mapped = Blocks.field_235413_nw_; break;
			case "polished_blackstone_brick_slab": mapped = Blocks.field_235414_nx_; break;
			case "polished_blackstone_brick_stairs": mapped = Blocks.field_235415_ny_; break;
			case "polished_blackstone_brick_wall": mapped = Blocks.field_235416_nz_; break;
			case "gilded_blackstone": mapped = Blocks.field_235387_nA_; break;
			case "polished_blackstone_stairs": mapped = Blocks.field_235388_nB_; break;
			case "polished_blackstone_slab": mapped = Blocks.field_235389_nC_; break;
			case "polished_blackstone_pressure_plate": mapped = Blocks.field_235390_nD_; break;
			case "polished_blackstone_button": mapped = Blocks.field_235391_nE_; break;
			case "polished_blackstone_wall": mapped = Blocks.field_235392_nF_; break;
			case "chiseled_nether_bricks": mapped = Blocks.field_235393_nG_; break;
			case "cracked_nether_bricks": mapped = Blocks.field_235394_nH_; break;
		}
		if (mapped == null) {
			System.out.println("Fail: "+  blockName);
			throw new NullPointerException(blockName + " was not found in 1.16 Dictionary");
		}
		return mapped;
	}
	public static final BlockSet EMPTY = BlockSet.builder().build();

	public static final BlockSet AIR = BlockSet.builder().add(Blocks.AIR, Blocks.VOID_AIR, Blocks.CAVE_AIR).build();
	/**
	 * All rail blocks.
	 */
	public static final BlockSet RAILS = BlockSet.builder().add(
			Blocks.POWERED_RAIL,
			Blocks.DETECTOR_RAIL, 
			Blocks.RAIL, 
			Blocks.ACTIVATOR_RAIL).build();
    public static final BlockSet WOOL = BlockSet.builder().add(
    		Blocks.BLACK_WOOL,
			Blocks.BLUE_WOOL,
			Blocks.BROWN_WOOL,
			Blocks.CYAN_WOOL,
			Blocks.GRAY_WOOL,
			Blocks.GREEN_WOOL,
			Blocks.LIGHT_BLUE_WOOL,
			Blocks.LIGHT_GRAY_WOOL,
			Blocks.LIME_WOOL,
			Blocks.MAGENTA_WOOL,
			Blocks.ORANGE_WOOL,
			Blocks.PINK_WOOL,
			Blocks.PURPLE_WOOL,
			Blocks.RED_WOOL,
			Blocks.WHITE_WOOL,
			Blocks.YELLOW_WOOL
	).build();

	public static final BlockSet TERRACOTTA = BlockSet.builder().add(
			Blocks.BLACK_TERRACOTTA,
			Blocks.BLUE_TERRACOTTA,
			Blocks.BROWN_TERRACOTTA,
			Blocks.CYAN_TERRACOTTA,
			Blocks.GRAY_TERRACOTTA,
			Blocks.GREEN_TERRACOTTA,
			Blocks.LIGHT_BLUE_TERRACOTTA,
			Blocks.LIGHT_GRAY_TERRACOTTA,
			Blocks.LIME_TERRACOTTA,
			Blocks.MAGENTA_TERRACOTTA,
			Blocks.ORANGE_TERRACOTTA,
			Blocks.PINK_TERRACOTTA,
			Blocks.PURPLE_TERRACOTTA,
			Blocks.RED_TERRACOTTA,
			Blocks.WHITE_TERRACOTTA,
			Blocks.YELLOW_TERRACOTTA
	).build();
	public static final BlockSet STAINED_GLASS = BlockSet.builder().add(
    		Blocks.BLACK_STAINED_GLASS,
			Blocks.BLUE_STAINED_GLASS,
			Blocks.BROWN_STAINED_GLASS,
			Blocks.CYAN_STAINED_GLASS,
			Blocks.GRAY_STAINED_GLASS,
			Blocks.GREEN_STAINED_GLASS,
			Blocks.LIGHT_BLUE_STAINED_GLASS,
			Blocks.LIGHT_GRAY_STAINED_GLASS,
			Blocks.LIME_STAINED_GLASS,
			Blocks.MAGENTA_STAINED_GLASS,
			Blocks.ORANGE_STAINED_GLASS,
			Blocks.PINK_STAINED_GLASS,
			Blocks.PURPLE_STAINED_GLASS,
			Blocks.RED_STAINED_GLASS,
			Blocks.WHITE_STAINED_GLASS,
			Blocks.YELLOW_STAINED_GLASS
	).build();
    public static final BlockSet STAINED_GLASS_PANE = BlockSet.builder().add(
    		Blocks.BLACK_STAINED_GLASS_PANE,
			Blocks.BLUE_STAINED_GLASS_PANE,
			Blocks.BROWN_STAINED_GLASS_PANE,
			Blocks.CYAN_STAINED_GLASS_PANE,
			Blocks.GRAY_STAINED_GLASS_PANE,
			Blocks.GREEN_STAINED_GLASS_PANE,
			Blocks.LIGHT_BLUE_STAINED_GLASS_PANE,
			Blocks.LIGHT_GRAY_STAINED_GLASS_PANE,
			Blocks.LIME_STAINED_GLASS_PANE,
			Blocks.MAGENTA_STAINED_GLASS_PANE,
			Blocks.ORANGE_STAINED_GLASS_PANE,
			Blocks.PINK_STAINED_GLASS_PANE,
			Blocks.PURPLE_STAINED_GLASS_PANE,
			Blocks.RED_STAINED_GLASS_PANE,
			Blocks.WHITE_STAINED_GLASS_PANE,
			Blocks.YELLOW_STAINED_GLASS_PANE
	).build();
    
    public static final BlockSet GLAZED_TERRACOTTA = BlockSet.builder().add(
    		Blocks.BLACK_GLAZED_TERRACOTTA,
			Blocks.BLUE_GLAZED_TERRACOTTA,
			Blocks.BROWN_GLAZED_TERRACOTTA,
			Blocks.CYAN_GLAZED_TERRACOTTA,
			Blocks.GRAY_GLAZED_TERRACOTTA,
			Blocks.GREEN_GLAZED_TERRACOTTA,
			Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA,
			Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA,
			Blocks.LIME_GLAZED_TERRACOTTA,
			Blocks.MAGENTA_GLAZED_TERRACOTTA,
			Blocks.ORANGE_GLAZED_TERRACOTTA,
			Blocks.PINK_GLAZED_TERRACOTTA,
			Blocks.PURPLE_GLAZED_TERRACOTTA,
			Blocks.RED_GLAZED_TERRACOTTA,
			Blocks.WHITE_GLAZED_TERRACOTTA,
			Blocks.YELLOW_GLAZED_TERRACOTTA
	).build();
    public static final BlockSet CARPET = BlockSet.builder().add(
    		Blocks.BLACK_CARPET,
			Blocks.BLUE_CARPET,
			Blocks.BROWN_CARPET,
			Blocks.CYAN_CARPET,
			Blocks.GRAY_CARPET,
			Blocks.GREEN_CARPET,
			Blocks.LIGHT_BLUE_CARPET,
			Blocks.LIGHT_GRAY_CARPET,
			Blocks.LIME_CARPET,
			Blocks.MAGENTA_CARPET,
			Blocks.ORANGE_CARPET,
			Blocks.PINK_CARPET,
			Blocks.PURPLE_CARPET,
			Blocks.RED_CARPET,
			Blocks.WHITE_CARPET,
			Blocks.YELLOW_CARPET
	).build();

	public static final BlockSet BED = BlockSet.builder().add(
			Blocks.BLACK_BED,
			Blocks.BLUE_BED,
			Blocks.BROWN_BED,
			Blocks.CYAN_BED,
			Blocks.GRAY_BED,
			Blocks.GREEN_BED,
			Blocks.LIGHT_BLUE_BED,
			Blocks.LIGHT_GRAY_BED,
			Blocks.LIME_BED,
			Blocks.MAGENTA_BED,
			Blocks.ORANGE_BED,
			Blocks.PINK_BED,
			Blocks.PURPLE_BED,
			Blocks.RED_BED,
			Blocks.WHITE_BED,
			Blocks.YELLOW_BED
	).build();

	public static final BlockSet CONCRETE = BlockSet.builder().add(
			Blocks.BLACK_CONCRETE,
			Blocks.BLUE_CONCRETE,
			Blocks.BROWN_CONCRETE,
			Blocks.CYAN_CONCRETE,
			Blocks.GRAY_CONCRETE,
			Blocks.GREEN_CONCRETE,
			Blocks.LIGHT_BLUE_CONCRETE,
			Blocks.LIGHT_GRAY_CONCRETE,
			Blocks.LIME_CONCRETE,
			Blocks.MAGENTA_CONCRETE,
			Blocks.ORANGE_CONCRETE,
			Blocks.PINK_CONCRETE,
			Blocks.PURPLE_CONCRETE,
			Blocks.RED_CONCRETE,
			Blocks.WHITE_CONCRETE,
			Blocks.YELLOW_CONCRETE
	).build();

	public static final BlockSet CONCRETE_POWDER = BlockSet.builder().add(
			Blocks.BLACK_CONCRETE_POWDER,
			Blocks.BLUE_CONCRETE_POWDER,
			Blocks.BROWN_CONCRETE_POWDER,
			Blocks.CYAN_CONCRETE_POWDER,
			Blocks.GRAY_CONCRETE_POWDER,
			Blocks.GREEN_CONCRETE_POWDER,
			Blocks.LIGHT_BLUE_CONCRETE_POWDER,
			Blocks.LIGHT_GRAY_CONCRETE_POWDER,
			Blocks.LIME_CONCRETE_POWDER,
			Blocks.MAGENTA_CONCRETE_POWDER,
			Blocks.ORANGE_CONCRETE_POWDER,
			Blocks.PINK_CONCRETE_POWDER,
			Blocks.PURPLE_CONCRETE_POWDER,
			Blocks.RED_CONCRETE_POWDER,
			Blocks.WHITE_CONCRETE_POWDER,
			Blocks.YELLOW_CONCRETE_POWDER
	).build();

	public static final BlockSet FENCE = BlockSet.builder().add(
			Blocks.OAK_FENCE,
			Blocks.SPRUCE_FENCE,
			Blocks.BIRCH_FENCE,
			Blocks.JUNGLE_FENCE,
			Blocks.DARK_OAK_FENCE,
			Blocks.ACACIA_FENCE,
			Blocks.NETHER_BRICK_FENCE,

			nameConvert("crimson_fence"),
			nameConvert("warped_fence")
			).build();

	public static final BlockSet WALL = BlockSet.builder().add(
			Blocks.ANDESITE_WALL,
			Blocks.BRICK_WALL,
			Blocks.COBBLESTONE_WALL,
			Blocks.DIORITE_WALL,
			Blocks.END_STONE_BRICK_WALL,
			Blocks.GRANITE_WALL,
			Blocks.MOSSY_COBBLESTONE_WALL,
			Blocks.MOSSY_STONE_BRICK_WALL,
			Blocks.NETHER_BRICK_WALL,
			Blocks.PRISMARINE_WALL,
			Blocks.RED_NETHER_BRICK_WALL,
			Blocks.RED_SANDSTONE_WALL,
			Blocks.SANDSTONE_WALL,
			Blocks.STONE_BRICK_WALL,

			nameConvert("polished_blackstone_wall"),
			nameConvert("polished_blackstone_brick_wall"),
			nameConvert("blackstone_wall")
	).build();

	public static final BlockSet WOODEN_DOR = BlockSet.builder().add(
			Blocks.OAK_DOOR,
			Blocks.SPRUCE_DOOR,
			Blocks.BIRCH_DOOR,
			Blocks.JUNGLE_DOOR,
			Blocks.DARK_OAK_DOOR,
			Blocks.ACACIA_DOOR,

			nameConvert("crimson_door"),
			nameConvert("warped_door")
			).build();

	public static final BlockSet WOODEN_SLAB = BlockSet.builder().add(
			Blocks.OAK_SLAB,
			Blocks.SPRUCE_SLAB,
			Blocks.BIRCH_SLAB,
			Blocks.JUNGLE_SLAB,
			Blocks.DARK_OAK_SLAB,
			Blocks.ACACIA_SLAB,

			nameConvert("crimson_slab"),
			nameConvert("warped_slab")
			).build();

	public static final BlockSet WOODEN_PRESSURE_PLATE = BlockSet.builder().add(
			Blocks.OAK_PRESSURE_PLATE,
			Blocks.SPRUCE_PRESSURE_PLATE,
			Blocks.BIRCH_PRESSURE_PLATE,
			Blocks.JUNGLE_PRESSURE_PLATE,
			Blocks.DARK_OAK_PRESSURE_PLATE,
			Blocks.ACACIA_PRESSURE_PLATE,

			nameConvert("crimson_pressure_plate"),
			nameConvert("warped_pressure_plate")
			).build();

	public static final BlockSet WOODEN_BUTTON = BlockSet.builder().add(
			Blocks.OAK_BUTTON,
			Blocks.SPRUCE_BUTTON,
			Blocks.BIRCH_BUTTON,
			Blocks.JUNGLE_BUTTON,
			Blocks.DARK_OAK_BUTTON,
			Blocks.ACACIA_BUTTON,

			nameConvert("crimson_button"),
			nameConvert("warped_button")
			).build();

	public static final BlockSet SAPLING = BlockSet.builder().add(
			Blocks.OAK_SAPLING,
			Blocks.SPRUCE_SAPLING,
			Blocks.BIRCH_SAPLING,
			Blocks.JUNGLE_SAPLING,
			Blocks.DARK_OAK_SAPLING,
			Blocks.ACACIA_SAPLING).build();

	public static final BlockSet SIGN = BlockSet.builder().add(
			Blocks.OAK_SIGN,
			Blocks.SPRUCE_SIGN,
			Blocks.BIRCH_SIGN,
			Blocks.JUNGLE_SIGN,
			Blocks.DARK_OAK_SIGN,
			Blocks.ACACIA_SIGN,

			nameConvert("crimson_sign"),
			nameConvert("warped_sign")

			).build();

	public static final BlockSet FENCE_GATE = BlockSet.builder().add(
			Blocks.OAK_FENCE_GATE,
			Blocks.SPRUCE_FENCE_GATE,
			Blocks.BIRCH_FENCE_GATE,
			Blocks.JUNGLE_FENCE_GATE,
			Blocks.DARK_OAK_FENCE_GATE,
			Blocks.ACACIA_FENCE_GATE,

			nameConvert("crimson_fence_gate"),
			nameConvert("warped_fence_gate")
			).build();

	/**
	 * All leaves.
	 */
	public static final BlockSet LEAVES = BlockSet.builder().add(
			Blocks.OAK_LEAVES,
			Blocks.SPRUCE_LEAVES,
			Blocks.BIRCH_LEAVES,
			Blocks.JUNGLE_LEAVES,
			Blocks.DARK_OAK_LEAVES,
			Blocks.ACACIA_LEAVES).build();

	/**
	 * Only consider leaves that do not decay as safe ground.
	 */
	public static final BlockSet PERSISTENT_LEAVES = BlockSet.builder().add(
			LEAVES,
			state -> state.get(LeavesBlock.PERSISTENT)
	).build();

	public static final BlockSet CORALBLOCKS = BlockSet.builder().add(
			Blocks.DRIED_KELP_BLOCK,
			Blocks.DEAD_TUBE_CORAL_BLOCK,
			Blocks.DEAD_BRAIN_CORAL_BLOCK,
			Blocks.DEAD_BUBBLE_CORAL_BLOCK,
			Blocks.DEAD_FIRE_CORAL_BLOCK,
			Blocks.DEAD_HORN_CORAL_BLOCK,
			Blocks.TUBE_CORAL_BLOCK,
			Blocks.BRAIN_CORAL_BLOCK,
			Blocks.BUBBLE_CORAL_BLOCK,
			Blocks.FIRE_CORAL_BLOCK,
			Blocks.HORN_CORAL_BLOCK).build();


	public static final BlockSet PLANKS = BlockSet.builder().add(
			Blocks.OAK_PLANKS,
			Blocks.SPRUCE_PLANKS,
			Blocks.BIRCH_PLANKS,
			Blocks.JUNGLE_PLANKS,
			Blocks.DARK_OAK_PLANKS,
			Blocks.ACACIA_PLANKS,

			nameConvert("crimson_planks"),
			nameConvert("warped_planks")
			).build();

	public static final BlockSet LOGS = BlockSet.builder().add(
			Blocks.OAK_LOG,
			Blocks.SPRUCE_LOG,
			Blocks.BIRCH_LOG,
			Blocks.JUNGLE_LOG,
			Blocks.DARK_OAK_LOG,
			Blocks.ACACIA_LOG,

			nameConvert("warped_stem"),
			nameConvert("crimson_stem"),
			nameConvert("warped_hyphae"),
			nameConvert("crimson_hyphae")
			).build();

	public static final BlockSet STRIPPED_WOOD = BlockSet.builder().add(
			Blocks.STRIPPED_OAK_WOOD,
			Blocks.STRIPPED_SPRUCE_WOOD,
			Blocks.STRIPPED_BIRCH_WOOD,
			Blocks.STRIPPED_JUNGLE_WOOD,
			Blocks.STRIPPED_DARK_OAK_WOOD,
			Blocks.STRIPPED_ACACIA_WOOD,

			nameConvert("stripped_crimson_stem"), //With this
			nameConvert("stripped_warped_stem")

			).build();

	public static final BlockSet STRIPPED_LOGS = BlockSet.builder().add(
			Blocks.STRIPPED_OAK_LOG,
			Blocks.STRIPPED_SPRUCE_LOG,
			Blocks.STRIPPED_BIRCH_LOG,
			Blocks.STRIPPED_JUNGLE_LOG,
			Blocks.STRIPPED_DARK_OAK_LOG,
			Blocks.STRIPPED_ACACIA_LOG,

			nameConvert("stripped_warped_hyphae"), //Might have this wrong way around
			nameConvert("stripped_crimson_hyphae")

			).build();


	public static final BlockSet LOWER_SLABS;
	public static final BlockSet UPPER_SLABS;
	public static final BlockSet DOUBLE_SLABS;
	static {
		BlockSet.Builder lower = BlockSet.builder();
		BlockSet.Builder upper = BlockSet.builder();
		BlockSet.Builder both = BlockSet.builder();
		Stream.of(
				Blocks.OAK_SLAB,
				Blocks.SPRUCE_SLAB,
				Blocks.BIRCH_SLAB,
				Blocks.JUNGLE_SLAB,
				Blocks.ACACIA_SLAB,
				Blocks.DARK_OAK_SLAB,
				Blocks.STONE_SLAB,
				Blocks.SMOOTH_STONE_SLAB,
				Blocks.SANDSTONE_SLAB,
				Blocks.CUT_SANDSTONE_SLAB,
				Blocks.PETRIFIED_OAK_SLAB,
				Blocks.COBBLESTONE_SLAB,
				Blocks.BRICK_SLAB,
				Blocks.STONE_BRICK_SLAB,
				Blocks.NETHER_BRICK_SLAB,
				Blocks.QUARTZ_SLAB,
				Blocks.RED_SANDSTONE_SLAB,
				Blocks.CUT_RED_SANDSTONE_SLAB,
				Blocks.PURPUR_SLAB,
				Blocks.POLISHED_GRANITE_SLAB,
				Blocks.SMOOTH_RED_SANDSTONE_SLAB,
				Blocks.MOSSY_STONE_BRICK_SLAB,
				Blocks.POLISHED_DIORITE_SLAB,
				Blocks.MOSSY_COBBLESTONE_SLAB,
				Blocks.END_STONE_BRICK_SLAB,
				Blocks.SMOOTH_SANDSTONE_SLAB,
				Blocks.SMOOTH_QUARTZ_SLAB,
				Blocks.GRANITE_SLAB,
				Blocks.ANDESITE_SLAB,
				Blocks.RED_NETHER_BRICK_SLAB,
				Blocks.POLISHED_ANDESITE_SLAB,
				Blocks.DIORITE_SLAB,
				Blocks.PRISMARINE_SLAB,
				Blocks.PRISMARINE_BRICK_SLAB,
				Blocks.DARK_PRISMARINE_SLAB,

				nameConvert("crimson_slab"),
				nameConvert("warped_slab"),
				nameConvert("blackstone_slab"),
				nameConvert("polished_blackstone_brick_slab"),
				nameConvert("polished_blackstone_slab")

				).map(Block::getDefaultState)
				.flatMap(state -> Stream.of(state, state.with(SlabBlock.WATERLOGGED, true)))
				.forEach(state -> {
			lower.add(state.with(SlabBlock.TYPE, SlabType.BOTTOM));
			upper.add(state.with(SlabBlock.TYPE, SlabType.TOP));
			both.add(state.with(SlabBlock.TYPE, SlabType.DOUBLE));
		});
		LOWER_SLABS = lower.build();
		UPPER_SLABS = upper.build();
		DOUBLE_SLABS = both.build();
	}
	public static final BlockSet HALF_SLABS = BlockSet.builder().add(LOWER_SLABS).add(UPPER_SLABS).build();


	public static final BlockSet WALL_SIGN = BlockSet.builder().add(Blocks.ACACIA_WALL_SIGN, Blocks.BIRCH_WALL_SIGN, Blocks.DARK_OAK_WALL_SIGN, Blocks.JUNGLE_WALL_SIGN, Blocks.OAK_WALL_SIGN, Blocks.SPRUCE_WALL_SIGN,
			nameConvert("crimson_wall_sign"),
			nameConvert("warped_wall_sign")

			).build();

	/**
	 * Flowers and stuff like that
	 */
	private static final BlockSet explicitFootWalkableBlocks = BlockSet.builder().add(
			Blocks.GRASS,
			Blocks.CORNFLOWER,
			Blocks.CHORUS_FLOWER,
			Blocks.SUNFLOWER,
			Blocks.POPPY,
			Blocks.WHEAT,
			Blocks.CARROTS,
			Blocks.POTATOES,
			Blocks.PUMPKIN_STEM,
			Blocks.MELON_STEM,
			Blocks.CHORUS_PLANT,
			Blocks.KELP_PLANT,
			Blocks.RED_MUSHROOM,
			Blocks.BROWN_MUSHROOM,
			Blocks.REDSTONE_WIRE,
			Blocks.SNOW,
			Blocks.NETHER_WART,
            Blocks.CHORUS_FLOWER,
            Blocks.CHORUS_PLANT,
            Blocks.END_ROD,
            Blocks.BEETROOTS,
			Blocks.DEAD_BUSH,
			Blocks.ROSE_BUSH,

			nameConvert("crimson_fungus"),
			nameConvert("nether_sprouts"),
			nameConvert("warped_fungus")
			).add(RAILS).add(CARPET).add(SAPLING).add(SIGN).add(WALL_SIGN).build();

	/**
	 * Blocks that fall down.
	 */
	public static final BlockSet FALLING = BlockSet.builder().add(Blocks.GRAVEL,
			Blocks.SAND).add(CONCRETE_POWDER).build();

	public static final BlockSet WALL_TORCHES = BlockSet.builder()
			.add(Blocks.WALL_TORCH, Blocks.REDSTONE_WALL_TORCH,
					nameConvert("soul_wall_torch")
					).build();

	/**
	 * Torches.
	 */
	public static final BlockSet FLOOR_TORCHES = BlockSet.builder().add(
			Blocks.TORCH,
			Blocks.REDSTONE_TORCH,
			nameConvert("soul_torch")
			).build();

	public static final BlockSet TORCH = BlockSet.builder().add(FLOOR_TORCHES).add(WALL_TORCHES).build();


	/**
	 * Blocks our head can walk though. Signs could be added here, but we stay
	 * away from them for now.
	 */
	public static final BlockSet HEAD_CAN_WALK_THROUGH = BlockSet.builder().add(
			Blocks.KELP_PLANT,
			Blocks.CHORUS_PLANT,
			Blocks.TALL_GRASS,
			Blocks.SUGAR_CANE).add(AIR).add(TORCH).build();

	public static final BlockSet FEET_CAN_WALK_THROUGH = BlockSet.builder().add(explicitFootWalkableBlocks).add(HEAD_CAN_WALK_THROUGH).build();

	private static final BlockSet explicitSafeSideBlocks = BlockSet.builder().add(
			Blocks.ANVIL,
			Blocks.CACTUS,
			Blocks.COBWEB,
			Blocks.GLASS_PANE,
			Blocks.ENCHANTING_TABLE,
			Blocks.LILY_PAD,
			Blocks.BREWING_STAND, 
			Blocks.VINE,
			Blocks.CHEST,
			Blocks.TRAPPED_CHEST, 
			Blocks.TRIPWIRE, 
			Blocks.TRIPWIRE_HOOK,
			Blocks.STONE_PRESSURE_PLATE,
			Blocks.STONE_BUTTON,
			Blocks.DRAGON_EGG,
			Blocks.TURTLE_EGG,
			Blocks.FLOWER_POT).add(FEET_CAN_WALK_THROUGH).add(WALL).add(FENCE).add(FENCE_GATE).add(BED).add(WOODEN_PRESSURE_PLATE).add(WOODEN_BUTTON).build();

	/**
	 * Blocks we can just walk over/next to without problems.
	 */
	public static final BlockSet SIMPLE_CUBE = BlockSet.builder().add(
			Blocks.ANDESITE,
			Blocks.GRANITE,
			Blocks.BEDROCK,
			Blocks.END_STONE,
			Blocks.END_STONE_BRICKS,
			Blocks.BOOKSHELF,
			Blocks.BRICKS,
			Blocks.BROWN_MUSHROOM_BLOCK,
			Blocks.CAKE,
			Blocks.CLAY,
			Blocks.COAL_BLOCK,
			Blocks.COAL_ORE,
			Blocks.COBBLESTONE,
			Blocks.CRAFTING_TABLE,
			Blocks.DIAMOND_BLOCK,
			Blocks.DIAMOND_ORE,
			Blocks.DIRT,
			Blocks.PODZOL,
			Blocks.EMERALD_BLOCK,
			Blocks.PURPUR_BLOCK,
			Blocks.PURPUR_PILLAR,
			Blocks.PURPUR_SLAB,
			Blocks.NETHER_BRICKS,
			Blocks.RED_NETHER_BRICKS,
			Blocks.BONE_BLOCK,
			Blocks.EMERALD_ORE,
			Blocks.OBSERVER,
			// FIXME: Not a cube.
			Blocks.FARMLAND,

			Blocks.FURNACE, Blocks.GLASS, Blocks.GLOWSTONE,
			Blocks.GRASS_BLOCK, Blocks.GOLD_BLOCK, Blocks.GOLD_ORE,
			Blocks.CLAY, Blocks.IRON_BLOCK, Blocks.IRON_ORE,
			Blocks.LAPIS_BLOCK, Blocks.LAPIS_ORE,
			Blocks.MELON,
			Blocks.MOSSY_COBBLESTONE,
			Blocks.MYCELIUM,
			Blocks.NETHER_BRICKS,
			Blocks.RED_NETHER_BRICKS,
			Blocks.END_STONE_BRICKS,
			Blocks.NETHERRACK,
			// Watch out, this cannot be broken easily !
			Blocks.OBSIDIAN, Blocks.PACKED_ICE, Blocks.PUMPKIN,
			Blocks.QUARTZ_BLOCK, Blocks.NETHER_QUARTZ_ORE, Blocks.RED_MUSHROOM_BLOCK,
			Blocks.REDSTONE_BLOCK, Blocks.REDSTONE_LAMP, Blocks.REDSTONE_ORE,
			Blocks.SANDSTONE, Blocks.FROSTED_ICE,
			Blocks.SNOW,
			// FIXME: Both are not cubes
			Blocks.SOUL_SAND, Blocks.GRASS_PATH, nameConvert("soul_soil"),
			Blocks.STONE, Blocks.BRICKS,
			Blocks.SNOW_BLOCK,
			Blocks.DIORITE,
			Blocks.POLISHED_ANDESITE,
			Blocks.POLISHED_DIORITE,
			Blocks.POLISHED_GRANITE,
			Blocks.HAY_BLOCK,
			nameConvert("nether_gold_ore"),
			nameConvert("basalt"),
			nameConvert("polished_basalt"),
			nameConvert("shroomlight"),
			nameConvert("warped_wart_block"),
			nameConvert("target"),
			nameConvert("netherite_block"),
			nameConvert("ancient_debris"),
			nameConvert("crying_obsidian"),
			nameConvert("respawn_anchor"),
			nameConvert("lodestone"),
			nameConvert("blackstone"),
			nameConvert("polished_blackstone"),
			nameConvert("chiseled_polished_blackstone"),
			nameConvert("gilded_blackstone"),
			nameConvert("chiseled_nether_bricks"),
			nameConvert("cracked_nether_bricks"),
			nameConvert("crimson_nylium"),
			nameConvert("warped_nylium"),
			nameConvert("crying_obsidian")



			).add(WOOL).add(PERSISTENT_LEAVES).add(CORALBLOCKS)
			.add(LOGS).add(STRIPPED_LOGS).add(STRIPPED_WOOD)
			.add(CONCRETE).add(STAINED_GLASS).add(TERRACOTTA).add(GLAZED_TERRACOTTA).add(PLANKS).add(DOUBLE_SLABS).build();

	/**
	 * All stairs. It is no problem to walk on them.
	 */
	public static final BlockSet STAIRS = BlockSet.builder().add(Blocks.ACACIA_STAIRS,
			Blocks.BIRCH_STAIRS, Blocks.BRICK_STAIRS, Blocks.DARK_OAK_STAIRS,
			Blocks.JUNGLE_STAIRS, Blocks.NETHER_BRICK_STAIRS, Blocks.PURPUR_STAIRS,
			Blocks.OAK_STAIRS, Blocks.SANDSTONE_STAIRS, Blocks.SPRUCE_STAIRS,
			Blocks.STONE_BRICK_STAIRS, Blocks.STONE_STAIRS, Blocks.STONE_SLAB,
			Blocks.QUARTZ_STAIRS,

			nameConvert("crimson_stairs"),
			nameConvert("warped_stairs"),
			nameConvert("blackstone_stairs"),
			nameConvert("polished_blackstone_brick_stairs"),
			nameConvert("polished_blackstone_stairs")

			).build();

	/**
	 * Blocks that form a solid ground.
	 */
	public static final BlockSet SAFE_GROUND = BlockSet.builder().add(SIMPLE_CUBE).add(FALLING).add(HALF_SLABS).build();

	public static final BlockSet SAFE_SIDE = 
			BlockSet.builder().add(explicitSafeSideBlocks).add(SAFE_GROUND).add(LEAVES).add(FEET_CAN_WALK_THROUGH).add(AIR).build();

	public static final BlockSet SAFE_CEILING = BlockSet.builder()
			.add(STAIRS)
			.add(HALF_SLABS)
			.add(FEET_CAN_WALK_THROUGH)
			.add(SIMPLE_CUBE)
			// Decaying leaves are usually not considered safe
			.add(LEAVES)
			.add(AIR).add(Blocks.VINE, Blocks.CACTUS).build();

	/**
	 * Blocks you need to destroy but that are then safe.
	 */
	public static final BlockSet SAFE_AFTER_DESTRUCTION = BlockSet.builder().add(
			Blocks.VINE, Blocks.CACTUS).build();


	/**
	 * Blocks that are considered indestructable and should be avoided.
	 */
	public static final BlockSet INDESTRUCTABLE = BlockSet.builder().add(
			Blocks.BEDROCK,
			Blocks.BARRIER,
            //Fix SHULKER_BOX !!!!! Deals Damage to walking Player but found no matching category.
            // Blocks.SHULKER_BOX,
            //Fix NETHER_WART_BLOCK !!!!! Deals Damage to walking Player but found no matching category.
			//Do you mean Magma block?
            Blocks.NETHER_WART_BLOCK,
			Blocks.OBSIDIAN).build();

	public static final BlockSet WATER = BlockSet.builder().add(Blocks.WATER).build();

	public static final BlockSet TREE_BLOCKS = BlockSet.builder().add(LOGS).add(LEAVES).build();
	public static final BlockSet FURNACE = BlockSet.builder().add(Blocks.FURNACE).build();

	public static boolean safeSideAround(WorldData world, int x, int y, int z) {
		return SAFE_SIDE.isAt(world, x + 1, y, z)
				&& SAFE_SIDE.isAt(world, x - 1, y, z)
				&& SAFE_SIDE.isAt(world, x, y, z + 1)
				&& SAFE_SIDE.isAt(world, x, y, z - 1);
	}

	public static boolean safeSideAround(WorldData world, BlockPos pos) {
		return safeSideAround(world, pos.getX(), pos.getY(), pos.getZ());
	}

	public static boolean safeSideAndCeilingAround(WorldData world, int x,
			int y, int z) {
		return safeSideAround(world, x, y, z)
				&& SAFE_CEILING.isAt(world, x, y + 1, z);
	}

	public static boolean safeSideAndCeilingAround(WorldData world, BlockPos pos) {
		return safeSideAndCeilingAround(world, pos.getX(), pos.getY(),
				pos.getZ());
	}

	private BlockSets() {
	}
}
