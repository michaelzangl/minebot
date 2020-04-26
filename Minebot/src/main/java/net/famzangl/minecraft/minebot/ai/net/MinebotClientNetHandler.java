package net.famzangl.minecraft.minebot.ai.net;

import net.famzangl.minecraft.minebot.MinebotMod;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SAdvancementInfoPacket;
import net.minecraft.network.play.server.SAnimateBlockBreakPacket;
import net.minecraft.network.play.server.SAnimateHandPacket;
import net.minecraft.network.play.server.SBlockActionPacket;
import net.minecraft.network.play.server.SCameraPacket;
import net.minecraft.network.play.server.SChangeBlockPacket;
import net.minecraft.network.play.server.SChangeGameStatePacket;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.network.play.server.SChunkDataPacket;
import net.minecraft.network.play.server.SCloseWindowPacket;
import net.minecraft.network.play.server.SCollectItemPacket;
import net.minecraft.network.play.server.SCombatPacket;
import net.minecraft.network.play.server.SCommandListPacket;
import net.minecraft.network.play.server.SConfirmTransactionPacket;
import net.minecraft.network.play.server.SCooldownPacket;
import net.minecraft.network.play.server.SCustomPayloadPlayPacket;
import net.minecraft.network.play.server.SDestroyEntitiesPacket;
import net.minecraft.network.play.server.SDisconnectPacket;
import net.minecraft.network.play.server.SDisplayObjectivePacket;
import net.minecraft.network.play.server.SEntityEquipmentPacket;
import net.minecraft.network.play.server.SEntityHeadLookPacket;
import net.minecraft.network.play.server.SEntityMetadataPacket;
import net.minecraft.network.play.server.SEntityPacket;
import net.minecraft.network.play.server.SEntityPropertiesPacket;
import net.minecraft.network.play.server.SEntityStatusPacket;
import net.minecraft.network.play.server.SEntityTeleportPacket;
import net.minecraft.network.play.server.SEntityVelocityPacket;
import net.minecraft.network.play.server.SExplosionPacket;
import net.minecraft.network.play.server.SHeldItemChangePacket;
import net.minecraft.network.play.server.SJoinGamePacket;
import net.minecraft.network.play.server.SKeepAlivePacket;
import net.minecraft.network.play.server.SMapDataPacket;
import net.minecraft.network.play.server.SMerchantOffersPacket;
import net.minecraft.network.play.server.SMountEntityPacket;
import net.minecraft.network.play.server.SMoveVehiclePacket;
import net.minecraft.network.play.server.SMultiBlockChangePacket;
import net.minecraft.network.play.server.SOpenBookWindowPacket;
import net.minecraft.network.play.server.SOpenHorseWindowPacket;
import net.minecraft.network.play.server.SOpenSignMenuPacket;
import net.minecraft.network.play.server.SOpenWindowPacket;
import net.minecraft.network.play.server.SPlaceGhostRecipePacket;
import net.minecraft.network.play.server.SPlayEntityEffectPacket;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import net.minecraft.network.play.server.SPlaySoundEventPacket;
import net.minecraft.network.play.server.SPlaySoundPacket;
import net.minecraft.network.play.server.SPlayerAbilitiesPacket;
import net.minecraft.network.play.server.SPlayerDiggingPacket;
import net.minecraft.network.play.server.SPlayerListHeaderFooterPacket;
import net.minecraft.network.play.server.SPlayerListItemPacket;
import net.minecraft.network.play.server.SPlayerLookPacket;
import net.minecraft.network.play.server.SPlayerPositionLookPacket;
import net.minecraft.network.play.server.SQueryNBTResponsePacket;
import net.minecraft.network.play.server.SRecipeBookPacket;
import net.minecraft.network.play.server.SRemoveEntityEffectPacket;
import net.minecraft.network.play.server.SRespawnPacket;
import net.minecraft.network.play.server.SScoreboardObjectivePacket;
import net.minecraft.network.play.server.SSelectAdvancementsTabPacket;
import net.minecraft.network.play.server.SSendResourcePackPacket;
import net.minecraft.network.play.server.SServerDifficultyPacket;
import net.minecraft.network.play.server.SSetExperiencePacket;
import net.minecraft.network.play.server.SSetPassengersPacket;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.network.play.server.SSpawnExperienceOrbPacket;
import net.minecraft.network.play.server.SSpawnGlobalEntityPacket;
import net.minecraft.network.play.server.SSpawnMobPacket;
import net.minecraft.network.play.server.SSpawnMovingSoundEffectPacket;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.network.play.server.SSpawnPaintingPacket;
import net.minecraft.network.play.server.SSpawnParticlePacket;
import net.minecraft.network.play.server.SSpawnPlayerPacket;
import net.minecraft.network.play.server.SSpawnPositionPacket;
import net.minecraft.network.play.server.SStatisticsPacket;
import net.minecraft.network.play.server.SStopSoundPacket;
import net.minecraft.network.play.server.STabCompletePacket;
import net.minecraft.network.play.server.STagsListPacket;
import net.minecraft.network.play.server.STeamsPacket;
import net.minecraft.network.play.server.STitlePacket;
import net.minecraft.network.play.server.SUnloadChunkPacket;
import net.minecraft.network.play.server.SUpdateBossInfoPacket;
import net.minecraft.network.play.server.SUpdateChunkPositionPacket;
import net.minecraft.network.play.server.SUpdateHealthPacket;
import net.minecraft.network.play.server.SUpdateLightPacket;
import net.minecraft.network.play.server.SUpdateRecipesPacket;
import net.minecraft.network.play.server.SUpdateScorePacket;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.network.play.server.SUpdateTimePacket;
import net.minecraft.network.play.server.SUpdateViewDistancePacket;
import net.minecraft.network.play.server.SWindowItemsPacket;
import net.minecraft.network.play.server.SWindowPropertyPacket;
import net.minecraft.network.play.server.SWorldBorderPacket;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class MinebotClientNetHandler implements IClientPlayNetHandler {

    private IClientPlayNetHandler parentHandler;
    private Intercepts<IPacket<IClientPlayNetHandler>> handlers;

    public MinebotClientNetHandler(IClientPlayNetHandler parentHandler, Intercepts<IPacket<IClientPlayNetHandler>> handlers) {
        this.parentHandler = parentHandler;
        this.handlers = handlers;
    }

    @Override
    public void handleSpawnObject(SSpawnObjectPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP) parentHandler.handleSpawnObject(packetIn);
    }

    @Override
    public void handleSpawnExperienceOrb(SSpawnExperienceOrbPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP)
            parentHandler.handleSpawnExperienceOrb(packetIn);
    }

    @Override
    public void handleSpawnGlobalEntity(SSpawnGlobalEntityPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP)
            parentHandler.handleSpawnGlobalEntity(packetIn);
    }

    @Override
    public void handleSpawnMob(SSpawnMobPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP) parentHandler.handleSpawnMob(packetIn);
    }

    @Override
    public void handleScoreboardObjective(SScoreboardObjectivePacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP)
            parentHandler.handleScoreboardObjective(packetIn);
    }

    @Override
    public void handleSpawnPainting(SSpawnPaintingPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP)
            parentHandler.handleSpawnPainting(packetIn);
    }

    @Override
    public void handleSpawnPlayer(SSpawnPlayerPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP) parentHandler.handleSpawnPlayer(packetIn);
    }

    @Override
    public void handleAnimation(SAnimateHandPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP) parentHandler.handleAnimation(packetIn);
    }

    @Override
    public void handleStatistics(SStatisticsPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP) parentHandler.handleStatistics(packetIn);
    }

    @Override
    public void handleRecipeBook(SRecipeBookPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP) parentHandler.handleRecipeBook(packetIn);
    }

    @Override
    public void handleBlockBreakAnim(SAnimateBlockBreakPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP)
            parentHandler.handleBlockBreakAnim(packetIn);
    }

    @Override
    public void handleSignEditorOpen(SOpenSignMenuPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP)
            parentHandler.handleSignEditorOpen(packetIn);
    }

    @Override
    public void handleUpdateTileEntity(SUpdateTileEntityPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP)
            parentHandler.handleUpdateTileEntity(packetIn);
    }

    @Override
    public void handleBlockAction(SBlockActionPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP) parentHandler.handleBlockAction(packetIn);
    }

    @Override
    public void handleBlockChange(SChangeBlockPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP) parentHandler.handleBlockChange(packetIn);
    }

    @Override
    public void handleChat(SChatPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP) parentHandler.handleChat(packetIn);
    }

    @Override
    public void handleMultiBlockChange(SMultiBlockChangePacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP)
            parentHandler.handleMultiBlockChange(packetIn);
    }

    @Override
    public void handleMaps(SMapDataPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP) parentHandler.handleMaps(packetIn);
    }

    @Override
    public void handleConfirmTransaction(SConfirmTransactionPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP)
            parentHandler.handleConfirmTransaction(packetIn);
    }

    @Override
    public void handleCloseWindow(SCloseWindowPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP) parentHandler.handleCloseWindow(packetIn);
    }

    @Override
    public void handleWindowItems(SWindowItemsPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP) parentHandler.handleWindowItems(packetIn);
    }

    @Override
    public void handleOpenHorseWindow(SOpenHorseWindowPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP)
            parentHandler.handleOpenHorseWindow(packetIn);
    }

    @Override
    public void handleWindowProperty(SWindowPropertyPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP)
            parentHandler.handleWindowProperty(packetIn);
    }

    @Override
    public void handleSetSlot(SSetSlotPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP) parentHandler.handleSetSlot(packetIn);
    }

    @Override
    public void handleCustomPayload(SCustomPayloadPlayPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP)
            parentHandler.handleCustomPayload(packetIn);
    }

    @Override
    public void handleDisconnect(SDisconnectPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP) parentHandler.handleDisconnect(packetIn);
    }

    @Override
    public void handleEntityStatus(SEntityStatusPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP)
            parentHandler.handleEntityStatus(packetIn);
    }

    @Override
    public void handleEntityAttach(SMountEntityPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP)
            parentHandler.handleEntityAttach(packetIn);
    }

    @Override
    public void handleSetPassengers(SSetPassengersPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP)
            parentHandler.handleSetPassengers(packetIn);
    }

    @Override
    public void handleExplosion(SExplosionPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP) parentHandler.handleExplosion(packetIn);
    }

    @Override
    public void handleChangeGameState(SChangeGameStatePacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP)
            parentHandler.handleChangeGameState(packetIn);
    }

    @Override
    public void handleKeepAlive(SKeepAlivePacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP) parentHandler.handleKeepAlive(packetIn);
    }

    @Override
    public void handleChunkData(SChunkDataPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP) parentHandler.handleChunkData(packetIn);
    }

    @Override
    public void processChunkUnload(SUnloadChunkPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP)
            parentHandler.processChunkUnload(packetIn);
    }

    @Override
    public void handleEffect(SPlaySoundEventPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP) parentHandler.handleEffect(packetIn);
    }

    @Override
    public void handleJoinGame(SJoinGamePacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP) parentHandler.handleJoinGame(packetIn);
    }

    @Override
    public void handleEntityMovement(SEntityPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP)
            parentHandler.handleEntityMovement(packetIn);
    }

    @Override
    public void handlePlayerPosLook(SPlayerPositionLookPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP)
            parentHandler.handlePlayerPosLook(packetIn);
    }

    @Override
    public void handleParticles(SSpawnParticlePacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP) parentHandler.handleParticles(packetIn);
    }

    @Override
    public void handlePlayerAbilities(SPlayerAbilitiesPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP)
            parentHandler.handlePlayerAbilities(packetIn);
    }

    @Override
    public void handlePlayerListItem(SPlayerListItemPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP)
            parentHandler.handlePlayerListItem(packetIn);
    }

    @Override
    public void handleDestroyEntities(SDestroyEntitiesPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP)
            parentHandler.handleDestroyEntities(packetIn);
    }

    @Override
    public void handleRemoveEntityEffect(SRemoveEntityEffectPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP)
            parentHandler.handleRemoveEntityEffect(packetIn);
    }

    @Override
    public void handleRespawn(SRespawnPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP) parentHandler.handleRespawn(packetIn);
    }

    @Override
    public void handleEntityHeadLook(SEntityHeadLookPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP)
            parentHandler.handleEntityHeadLook(packetIn);
    }

    @Override
    public void handleHeldItemChange(SHeldItemChangePacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP)
            parentHandler.handleHeldItemChange(packetIn);
    }

    @Override
    public void handleDisplayObjective(SDisplayObjectivePacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP)
            parentHandler.handleDisplayObjective(packetIn);
    }

    @Override
    public void handleEntityMetadata(SEntityMetadataPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP)
            parentHandler.handleEntityMetadata(packetIn);
    }

    @Override
    public void handleEntityVelocity(SEntityVelocityPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP)
            parentHandler.handleEntityVelocity(packetIn);
    }

    @Override
    public void handleEntityEquipment(SEntityEquipmentPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP)
            parentHandler.handleEntityEquipment(packetIn);
    }

    @Override
    public void handleSetExperience(SSetExperiencePacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP)
            parentHandler.handleSetExperience(packetIn);
    }

    @Override
    public void handleUpdateHealth(SUpdateHealthPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP)
            parentHandler.handleUpdateHealth(packetIn);
    }

    @Override
    public void handleTeams(STeamsPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP) parentHandler.handleTeams(packetIn);
    }

    @Override
    public void handleUpdateScore(SUpdateScorePacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP) parentHandler.handleUpdateScore(packetIn);
    }

    @Override
    public void handleSpawnPosition(SSpawnPositionPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP)
            parentHandler.handleSpawnPosition(packetIn);
    }

    @Override
    public void handleTimeUpdate(SUpdateTimePacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP) parentHandler.handleTimeUpdate(packetIn);
    }

    @Override
    public void handleSoundEffect(SPlaySoundEffectPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP) parentHandler.handleSoundEffect(packetIn);
    }

    @Override
    public void func_217266_a(SSpawnMovingSoundEffectPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP) parentHandler.func_217266_a(packetIn);
    }

    @Override
    public void handleCustomSound(SPlaySoundPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP) parentHandler.handleCustomSound(packetIn);
    }

    @Override
    public void handleCollectItem(SCollectItemPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP) parentHandler.handleCollectItem(packetIn);
    }

    @Override
    public void handleEntityTeleport(SEntityTeleportPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP)
            parentHandler.handleEntityTeleport(packetIn);
    }

    @Override
    public void handleEntityProperties(SEntityPropertiesPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP)
            parentHandler.handleEntityProperties(packetIn);
    }

    @Override
    public void handleEntityEffect(SPlayEntityEffectPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP)
            parentHandler.handleEntityEffect(packetIn);
    }

    @Override
    public void handleTags(STagsListPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP) parentHandler.handleTags(packetIn);
    }

    @Override
    public void handleCombatEvent(SCombatPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP) parentHandler.handleCombatEvent(packetIn);
    }

    @Override
    public void handleServerDifficulty(SServerDifficultyPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP)
            parentHandler.handleServerDifficulty(packetIn);
    }

    @Override
    public void handleCamera(SCameraPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP) parentHandler.handleCamera(packetIn);
    }

    @Override
    public void handleWorldBorder(SWorldBorderPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP) parentHandler.handleWorldBorder(packetIn);
    }

    @Override
    public void handleTitle(STitlePacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP) parentHandler.handleTitle(packetIn);
    }

    @Override
    public void handlePlayerListHeaderFooter(SPlayerListHeaderFooterPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP)
            parentHandler.handlePlayerListHeaderFooter(packetIn);
    }

    @Override
    public void handleResourcePack(SSendResourcePackPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP)
            parentHandler.handleResourcePack(packetIn);
    }

    @Override
    public void handleUpdateBossInfo(SUpdateBossInfoPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP)
            parentHandler.handleUpdateBossInfo(packetIn);
    }

    @Override
    public void handleCooldown(SCooldownPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP) parentHandler.handleCooldown(packetIn);
    }

    @Override
    public void handleMoveVehicle(SMoveVehiclePacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP) parentHandler.handleMoveVehicle(packetIn);
    }

    @Override
    public void handleAdvancementInfo(SAdvancementInfoPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP)
            parentHandler.handleAdvancementInfo(packetIn);
    }

    @Override
    public void handleSelectAdvancementsTab(SSelectAdvancementsTabPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP)
            parentHandler.handleSelectAdvancementsTab(packetIn);
    }

    @Override
    public void handlePlaceGhostRecipe(SPlaceGhostRecipePacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP)
            parentHandler.handlePlaceGhostRecipe(packetIn);
    }

    @Override
    public void handleCommandList(SCommandListPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP) parentHandler.handleCommandList(packetIn);
    }

    @Override
    public void handleStopSound(SStopSoundPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP) parentHandler.handleStopSound(packetIn);
    }

    @Override
    public void handleTabComplete(STabCompletePacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP) parentHandler.handleTabComplete(packetIn);
    }

    @Override
    public void handleUpdateRecipes(SUpdateRecipesPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP)
            parentHandler.handleUpdateRecipes(packetIn);
    }

    @Override
    public void handlePlayerLook(SPlayerLookPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP) parentHandler.handlePlayerLook(packetIn);
    }

    @Override
    public void handleNBTQueryResponse(SQueryNBTResponsePacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP)
            parentHandler.handleNBTQueryResponse(packetIn);
    }

    @Override
    public void handleUpdateLight(SUpdateLightPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP) parentHandler.handleUpdateLight(packetIn);
    }

    @Override
    public void handleOpenBookPacket(SOpenBookWindowPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP)
            parentHandler.handleOpenBookPacket(packetIn);
    }

    @Override
    public void handleOpenWindowPacket(SOpenWindowPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP)
            parentHandler.handleOpenWindowPacket(packetIn);
    }

    @Override
    public void handleMerchantOffers(SMerchantOffersPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP)
            parentHandler.handleMerchantOffers(packetIn);
    }

    @Override
    public void handleUpdateViewDistancePacket(SUpdateViewDistancePacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP)
            parentHandler.handleUpdateViewDistancePacket(packetIn);
    }

    @Override
    public void handleChunkPositionPacket(SUpdateChunkPositionPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP)
            parentHandler.handleChunkPositionPacket(packetIn);
    }

    @Override
    public void func_225312_a(SPlayerDiggingPacket packetIn) {
        if (handlers.intercept(packetIn) != Intercepts.EInterceptResult.DROP) parentHandler.func_225312_a(packetIn);
    }

    @Override
    public void onDisconnect(ITextComponent reason) {
        parentHandler.onDisconnect(reason);
    }

    @Override
    public NetworkManager getNetworkManager() {
        return parentHandler.getNetworkManager();
    }
}
