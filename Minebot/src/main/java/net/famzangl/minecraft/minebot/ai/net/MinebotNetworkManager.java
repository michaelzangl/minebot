package net.famzangl.minecraft.minebot.ai.net;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.INetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketDirection;
import net.minecraft.network.ProtocolType;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import javax.crypto.SecretKey;
import java.net.InetAddress;
import java.net.SocketAddress;

public class MinebotNetworkManager extends NetworkManager {

    private NetworkManager parentManager;
    private Intercepts<IPacket<IServerPlayNetHandler>> intercept;

    public MinebotNetworkManager(NetworkManager parentManager, Intercepts<IPacket<IServerPlayNetHandler>> intercept) {
        super(parentManager.getDirection());
        this.parentManager = parentManager;
        this.intercept = intercept;
    }

    @Override
    public void channelActive(ChannelHandlerContext p_channelActive_1_) throws Exception {
        parentManager.channelActive(p_channelActive_1_);
    }

    @Override
    public void setConnectionState(ProtocolType newState) {
        parentManager.setConnectionState(newState);
    }

    @Override
    public void channelInactive(ChannelHandlerContext p_channelInactive_1_) throws Exception {
        parentManager.channelInactive(p_channelInactive_1_);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext p_exceptionCaught_1_, Throwable p_exceptionCaught_2_) {
        parentManager.exceptionCaught(p_exceptionCaught_1_, p_exceptionCaught_2_);
    }

    @Override
    public void setNetHandler(INetHandler handler) {
        parentManager.setNetHandler(handler);
    }

    @Override
    public void sendPacket(IPacket<?> packetIn) {
        intercept.withInterceptors((IPacket<IServerPlayNetHandler>) packetIn, parentManager::sendPacket);
    }

    @Override
    public void sendPacket(IPacket<?> packetIn, @Nullable GenericFutureListener<? extends Future<? super Void>> p_201058_2_) {
        intercept.withInterceptors((IPacket<IServerPlayNetHandler>) packetIn, p -> parentManager.sendPacket(p, p_201058_2_));
    }

    @Override
    public void tick() {
        parentManager.tick();
    }

    @Override
    public SocketAddress getRemoteAddress() {
        return parentManager.getRemoteAddress();
    }

    @Override
    public void closeChannel(ITextComponent message) {
        parentManager.closeChannel(message);
    }

    @Override
    public boolean isLocalChannel() {
        return parentManager.isLocalChannel();
    }

    @OnlyIn(Dist.CLIENT)
    public static NetworkManager createNetworkManagerAndConnect(InetAddress address, int serverPort, boolean useNativeTransport) {
        return NetworkManager.createNetworkManagerAndConnect(address, serverPort, useNativeTransport);
    }

    @OnlyIn(Dist.CLIENT)
    public static NetworkManager provideLocalClient(SocketAddress address) {
        return NetworkManager.provideLocalClient(address);
    }

    @Override
    public void enableEncryption(SecretKey key) {
        parentManager.enableEncryption(key);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean isEncrypted() {
        return parentManager.isEncrypted();
    }

    @Override
    public boolean isChannelOpen() {
        return parentManager.isChannelOpen();
    }

    @Override
    public boolean hasNoChannel() {
        return parentManager.hasNoChannel();
    }

    @Override
    public INetHandler getNetHandler() {
        return parentManager.getNetHandler();
    }

    @Override
    @Nullable
    public ITextComponent getExitMessage() {
        return parentManager.getExitMessage();
    }

    @Override
    public void disableAutoRead() {
        parentManager.disableAutoRead();
    }

    @Override
    public void setCompressionThreshold(int threshold) {
        parentManager.setCompressionThreshold(threshold);
    }

    @Override
    public void handleDisconnection() {
        parentManager.handleDisconnection();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public float getPacketsReceived() {
        return parentManager.getPacketsReceived();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public float getPacketsSent() {
        return parentManager.getPacketsSent();
    }

    @Override
    public Channel channel() {
        return parentManager.channel();
    }

    @Override
    public PacketDirection getDirection() {
        return parentManager.getDirection();
    }

    @Override
    public boolean acceptInboundMessage(Object msg) throws Exception {
        return parentManager.acceptInboundMessage(msg);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        parentManager.channelRead(ctx, msg);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        parentManager.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        parentManager.channelUnregistered(ctx);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        parentManager.channelReadComplete(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        parentManager.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        parentManager.channelWritabilityChanged(ctx);
    }

    @Override
    public boolean isSharable() {
        return parentManager.isSharable();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        parentManager.handlerAdded(ctx);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        parentManager.handlerRemoved(ctx);
    }


}
