package net.famzangl.minecraft.minebot.ai.net;

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
public class MinebotClientPlayNetHandler implements IClientPlayNetHandler {

    private IClientPlayNetHandler parentHandler;
    private Intercepts<IPacket<IClientPlayNetHandler>> handlers;

    public MinebotClientPlayNetHandler(IClientPlayNetHandler parentHandler, Intercepts<IPacket<IClientPlayNetHandler>> handlers) {
        this.parentHandler = parentHandler;
        this.handlers = handlers;
    }

    @Override
    public void handleSpawnObject(SSpawnObjectPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleSpawnObject);
    }

    @Override
    public void handleSpawnExperienceOrb(SSpawnExperienceOrbPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleSpawnExperienceOrb);
    }

    @Override
    public void handleSpawnGlobalEntity(SSpawnGlobalEntityPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleSpawnGlobalEntity);
    }

    @Override
    public void handleSpawnMob(SSpawnMobPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleSpawnMob);
    }

    @Override
    public void handleScoreboardObjective(SScoreboardObjectivePacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleScoreboardObjective);
    }

    @Override
    public void handleSpawnPainting(SSpawnPaintingPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleSpawnPainting);
    }

    @Override
    public void handleSpawnPlayer(SSpawnPlayerPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleSpawnPlayer);
    }

    @Override
    public void handleAnimation(SAnimateHandPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleAnimation);
    }

    @Override
    public void handleStatistics(SStatisticsPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleStatistics);
    }

    @Override
    public void handleRecipeBook(SRecipeBookPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleRecipeBook);
    }

    @Override
    public void handleBlockBreakAnim(SAnimateBlockBreakPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleBlockBreakAnim);
    }

    @Override
    public void handleSignEditorOpen(SOpenSignMenuPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleSignEditorOpen);
    }

    @Override
    public void handleUpdateTileEntity(SUpdateTileEntityPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleUpdateTileEntity);
    }

    @Override
    public void handleBlockAction(SBlockActionPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleBlockAction);
    }

    @Override
    public void handleBlockChange(SChangeBlockPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleBlockChange);
    }

    @Override
    public void handleChat(SChatPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleChat);
    }

    @Override
    public void handleMultiBlockChange(SMultiBlockChangePacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleMultiBlockChange);
    }

    @Override
    public void handleMaps(SMapDataPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleMaps);
    }

    @Override
    public void handleConfirmTransaction(SConfirmTransactionPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleConfirmTransaction);
    }

    @Override
    public void handleCloseWindow(SCloseWindowPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleCloseWindow);
    }

    @Override
    public void handleWindowItems(SWindowItemsPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleWindowItems);
    }

    @Override
    public void handleOpenHorseWindow(SOpenHorseWindowPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleOpenHorseWindow);
    }

    @Override
    public void handleWindowProperty(SWindowPropertyPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleWindowProperty);
    }

    @Override
    public void handleSetSlot(SSetSlotPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleSetSlot);
    }

    @Override
    public void handleCustomPayload(SCustomPayloadPlayPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleCustomPayload);
    }

    @Override
    public void handleDisconnect(SDisconnectPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleDisconnect);
    }

    @Override
    public void handleEntityStatus(SEntityStatusPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleEntityStatus);
    }

    @Override
    public void handleEntityAttach(SMountEntityPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleEntityAttach);
    }

    @Override
    public void handleSetPassengers(SSetPassengersPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleSetPassengers);
    }

    @Override
    public void handleExplosion(SExplosionPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleExplosion);
    }

    @Override
    public void handleChangeGameState(SChangeGameStatePacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleChangeGameState);
    }

    @Override
    public void handleKeepAlive(SKeepAlivePacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleKeepAlive);
    }

    @Override
    public void handleChunkData(SChunkDataPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleChunkData);
    }

    @Override
    public void processChunkUnload(SUnloadChunkPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::processChunkUnload);
    }

    @Override
    public void handleEffect(SPlaySoundEventPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleEffect);
    }

    @Override
    public void handleJoinGame(SJoinGamePacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleJoinGame);
    }

    @Override
    public void handleEntityMovement(SEntityPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleEntityMovement);
    }

    @Override
    public void handlePlayerPosLook(SPlayerPositionLookPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handlePlayerPosLook);
    }

    @Override
    public void handleParticles(SSpawnParticlePacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleParticles);
    }

    @Override
    public void handlePlayerAbilities(SPlayerAbilitiesPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handlePlayerAbilities);
    }

    @Override
    public void handlePlayerListItem(SPlayerListItemPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handlePlayerListItem);
    }

    @Override
    public void handleDestroyEntities(SDestroyEntitiesPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleDestroyEntities);
    }

    @Override
    public void handleRemoveEntityEffect(SRemoveEntityEffectPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleRemoveEntityEffect);
    }

    @Override
    public void handleRespawn(SRespawnPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleRespawn);
    }

    @Override
    public void handleEntityHeadLook(SEntityHeadLookPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleEntityHeadLook);
    }

    @Override
    public void handleHeldItemChange(SHeldItemChangePacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleHeldItemChange);
    }

    @Override
    public void handleDisplayObjective(SDisplayObjectivePacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleDisplayObjective);
    }

    @Override
    public void handleEntityMetadata(SEntityMetadataPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleEntityMetadata);
    }

    @Override
    public void handleEntityVelocity(SEntityVelocityPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleEntityVelocity);
    }

    @Override
    public void handleEntityEquipment(SEntityEquipmentPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleEntityEquipment);
    }

    @Override
    public void handleSetExperience(SSetExperiencePacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleSetExperience);
    }

    @Override
    public void handleUpdateHealth(SUpdateHealthPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleUpdateHealth);
    }

    @Override
    public void handleTeams(STeamsPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleTeams);
    }

    @Override
    public void handleUpdateScore(SUpdateScorePacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleUpdateScore);
    }

    @Override
    public void handleSpawnPosition(SSpawnPositionPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleSpawnPosition);
    }

    @Override
    public void handleTimeUpdate(SUpdateTimePacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleTimeUpdate);
    }

    @Override
    public void handleSoundEffect(SPlaySoundEffectPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleSoundEffect);
    }

    @Override
    public void func_217266_a(SSpawnMovingSoundEffectPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::func_217266_a);
    }

    @Override
    public void handleCustomSound(SPlaySoundPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleCustomSound);
    }

    @Override
    public void handleCollectItem(SCollectItemPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleCollectItem);
    }

    @Override
    public void handleEntityTeleport(SEntityTeleportPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleEntityTeleport);
    }

    @Override
    public void handleEntityProperties(SEntityPropertiesPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleEntityProperties);
    }

    @Override
    public void handleEntityEffect(SPlayEntityEffectPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleEntityEffect);
    }

    @Override
    public void handleTags(STagsListPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleTags);
    }

    @Override
    public void handleCombatEvent(SCombatPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleCombatEvent);
    }

    @Override
    public void handleServerDifficulty(SServerDifficultyPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleServerDifficulty);
    }

    @Override
    public void handleCamera(SCameraPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleCamera);
    }

    @Override
    public void handleWorldBorder(SWorldBorderPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleWorldBorder);
    }

    @Override
    public void handleTitle(STitlePacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleTitle);
    }

    @Override
    public void handlePlayerListHeaderFooter(SPlayerListHeaderFooterPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handlePlayerListHeaderFooter);
    }

    @Override
    public void handleResourcePack(SSendResourcePackPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleResourcePack);
    }

    @Override
    public void handleUpdateBossInfo(SUpdateBossInfoPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleUpdateBossInfo);
    }

    @Override
    public void handleCooldown(SCooldownPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleCooldown);
    }

    @Override
    public void handleMoveVehicle(SMoveVehiclePacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleMoveVehicle);
    }

    @Override
    public void handleAdvancementInfo(SAdvancementInfoPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleAdvancementInfo);
    }

    @Override
    public void handleSelectAdvancementsTab(SSelectAdvancementsTabPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleSelectAdvancementsTab);
    }

    @Override
    public void handlePlaceGhostRecipe(SPlaceGhostRecipePacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handlePlaceGhostRecipe);
    }

    @Override
    public void handleCommandList(SCommandListPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleCommandList);
    }

    @Override
    public void handleStopSound(SStopSoundPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleStopSound);
    }

    @Override
    public void handleTabComplete(STabCompletePacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleTabComplete);
    }

    @Override
    public void handleUpdateRecipes(SUpdateRecipesPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleUpdateRecipes);
    }

    @Override
    public void handlePlayerLook(SPlayerLookPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handlePlayerLook);
    }

    @Override
    public void handleNBTQueryResponse(SQueryNBTResponsePacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleNBTQueryResponse);
    }

    @Override
    public void handleUpdateLight(SUpdateLightPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleUpdateLight);
    }

    @Override
    public void handleOpenBookPacket(SOpenBookWindowPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleOpenBookPacket);
    }

    @Override
    public void handleOpenWindowPacket(SOpenWindowPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleOpenWindowPacket);
    }

    @Override
    public void handleMerchantOffers(SMerchantOffersPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleMerchantOffers);
    }

    @Override
    public void handleUpdateViewDistancePacket(SUpdateViewDistancePacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleUpdateViewDistancePacket);
    }

    @Override
    public void handleChunkPositionPacket(SUpdateChunkPositionPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::handleChunkPositionPacket);
    }

    @Override
    public void func_225312_a(SPlayerDiggingPacket packetIn) {
        handlers.withInterceptors(packetIn, parentHandler::func_225312_a);
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
