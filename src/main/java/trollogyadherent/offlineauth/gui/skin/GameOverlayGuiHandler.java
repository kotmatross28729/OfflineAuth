package trollogyadherent.offlineauth.gui.skin;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.lwjgl.opengl.GL11;
import trollogyadherent.offlineauth.Config;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.packet.PacketHandler;
import trollogyadherent.offlineauth.packet.packets.QuerySkinNameFromServerPacket;
import trollogyadherent.offlineauth.skin.client.ClientSkinUtil;
import trollogyadherent.offlineauth.util.Util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

/* Class responsible for drawing player faces in the tab menu */
public class GameOverlayGuiHandler extends GuiIngame {

    FontRenderer fontRenderer;

    public GameOverlayGuiHandler(Minecraft mc) {
        super(mc);
        fontRenderer = mc.fontRenderer;
    }

    @SubscribeEvent
    public void attach(DrawScreenEvent.Pre e) {
        //if (e.gui instanceof GuiOptions) {}
    }

    @SubscribeEvent
    public void open(RenderGameOverlayEvent.Pre e) {
        if (!Config.facesInTabMenu) {
            return;
        }
        if(e.type == RenderGameOverlayEvent.ElementType.PLAYER_LIST) {
            e.setCanceled(true);
        }

        if (e.type != RenderGameOverlayEvent.ElementType.PLAYER_LIST) {
            return;
        }

        if (fontRenderer == null) {
            fontRenderer = Minecraft.getMinecraft().fontRenderer;
        }

        ScoreObjective scoreobjective = this.mc.theWorld.getScoreboard().func_96539_a(0);
        NetHandlerPlayClient handler = mc.thePlayer.sendQueue;

        if (mc.gameSettings.keyBindPlayerList.getIsKeyPressed() && (!mc.isIntegratedServerRunning() || handler.playerInfoList.size() > 1 || scoreobjective != null))
        {
            this.mc.mcProfiler.startSection("playerList");
            List<GuiPlayerInfo> players = (List<GuiPlayerInfo>)handler.playerInfoList;

            ScaledResolution res = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
            int width = res.getScaledWidth();

            int maxPlayers = handler.currentServerMaxPlayers;
            int rows = maxPlayers;
            int columns = 1;

            for (columns = 1; rows > 20; rows = (maxPlayers + columns - 1) / columns)
            {
                columns++;
            }

            int columnWidth = 300 / columns;

            if (columnWidth > 150)
            {
                columnWidth = 150;
            }

            int left = (width - columns * columnWidth) / 2;
            byte border = 10;
            drawRect(left - 1, border - 1, left + columnWidth * columns, border + 9 * rows, Integer.MIN_VALUE);

            for (int i = 0; i < maxPlayers; i++)
            {
                int xPos = left + i % columns * columnWidth;
                int yPos = border + i / columns * 9;
                drawRect(xPos, yPos, xPos + columnWidth - 1, yPos + 8, 553648127);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glEnable(GL11.GL_ALPHA_TEST);

                if (i < players.size())
                {
                    GuiPlayerInfo player = (GuiPlayerInfo)players.get(i);
                    ScorePlayerTeam team = mc.theWorld.getScoreboard().getPlayersTeam(player.name);
                    String displayName = ScorePlayerTeam.formatPlayerName(team, player.name);
                    fontRenderer.drawStringWithShadow(displayName, xPos + 10, yPos, 16777215);

                    if (OfflineAuth.varInstanceClient.clientRegistry.getSkinNameByDisplayName(displayName) == null && !OfflineAuth.varInstanceClient.clientRegistry.skinNameIsBeingQueried(displayName)) {
                        IMessage msg = new QuerySkinNameFromServerPacket.SimpleMessage(displayName);
                        PacketHandler.net.sendToServer(msg);
                        OfflineAuth.varInstanceClient.clientRegistry.setSkinNameIsBeingQueried(displayName, true);
                        OfflineAuth.varInstanceClient.clientRegistry.insert(null, null, mc.theWorld.getPlayerEntityByName(displayName), null, displayName);
                    } else {
                        ResourceLocation rl;
                        File imageFile = ClientSkinUtil.getSkinFile(OfflineAuth.varInstanceClient.clientRegistry.getSkinNameByDisplayName(displayName));
                        if (imageFile == null || !imageFile.exists()) {
                            //OfflineAuth.error("Error skin image does not exist: " + displayName);
                            continue;
                        } else {
                            if (OfflineAuth.varInstanceClient.clientRegistry.getTabMenuResourceLocation(displayName) == null) {
                                BufferedImage bufferedImage;
                                try {
                                    if (!Util.pngIsSane(imageFile)) {
                                        OfflineAuth.error("Sussy error loading skin image, not sane: " + displayName);
                                        return;
                                    }
                                    bufferedImage = ImageIO.read(imageFile);
                                    if (bufferedImage.getHeight() != bufferedImage.getWidth()) {
                                        BufferedImage bufferedImageNew = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight() * 2, bufferedImage.getType());
                                        Graphics g = bufferedImageNew.getGraphics();
                                        g.drawImage(bufferedImage, 0, 0, null);
                                        g.dispose();
                                        bufferedImage = bufferedImageNew;
                                    }
                                    rl = new ResourceLocation("offlineauth", "tabmenuskins/" + displayName);
                                    ClientSkinUtil.loadTexture(bufferedImage, rl);
                                    OfflineAuth.varInstanceClient.clientRegistry.setTabMenuResourceLocation(displayName, rl);
                                } catch (IOException e_) {
                                    OfflineAuth.error("Error loading skin image " + displayName);
                                    return;
                                }
                            }
                            mc.getTextureManager().bindTexture(OfflineAuth.varInstanceClient.clientRegistry.getTabMenuResourceLocation(displayName));
                            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                            Gui.func_152125_a(xPos, yPos, 8.0F, (float) 8, 8, 8, 8, 8, 64.0F, 64.0F);
                        }
                    }


                    if (scoreobjective != null)
                    {
                        int endX = xPos + fontRenderer.getStringWidth(displayName) + 5;
                        int maxX = xPos + columnWidth - 12 - 5;

                        if (maxX - endX > 5)
                        {
                            Score score = scoreobjective.getScoreboard().func_96529_a(player.name, scoreobjective);
                            String scoreDisplay = EnumChatFormatting.YELLOW + "" + score.getScorePoints();
                            fontRenderer.drawStringWithShadow(scoreDisplay, maxX - fontRenderer.getStringWidth(scoreDisplay), yPos, 16777215);
                        }
                    }

                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

                    mc.getTextureManager().bindTexture(Gui.icons);
                    int pingIndex = 4;
                    int ping = player.responseTime;
                    if (ping < 0) pingIndex = 5;
                    else if (ping < 150) pingIndex = 0;
                    else if (ping < 300) pingIndex = 1;
                    else if (ping < 600) pingIndex = 2;
                    else if (ping < 1000) pingIndex = 3;

                    zLevel += 100.0F;
                    drawTexturedModalRect(xPos + columnWidth - 12, yPos, 0, 176 + pingIndex * 8, 10, 8);
                    zLevel -= 100.0F;
                }
            }
        }
    }

    /*@SubscribeEvent
    public void draw(DrawScreenEvent.Post e) {
        if (e.gui instanceof GuiOptions) {

            if (reflectedBtnLst != null) {
                for (Object gb : ((List) reflectedBtnLst)) {
                    if (((GuiButton) gb).id == 69) {
                        return;
                    }
                }
                if (Minecraft.getMinecraft().getNetHandler() != null) {
                    if (ClientUtil.isSinglePlayer()) {
                        ((List) reflectedBtnLst).add(new GuiButton(69, e.gui.width - 85, 5, 80, 20, "Set Skin"));
                    } else {
                        ((List) reflectedBtnLst).add(new GuiButton(69, e.gui.width - 85, 5, 80, 20, "Upload Skin"));
                    }
                }
            }
        }
    }*/

    // making a skin stealer?? or maybe teleport requests?
    @SubscribeEvent
    public void action(ActionPerformedEvent.Post e) {
        /*
        if (e.gui instanceof GuiOptions && e.button.id == 69) {
            Minecraft.getMinecraft().displayGuiScreen(new SkinManagmentGUI(Minecraft.getMinecraft().currentScreen));
        }
        */
    }

    public void drawTexturedModalRect(int p_73729_1_, int p_73729_2_, int p_73729_3_, int p_73729_4_, int p_73729_5_, int p_73729_6_)
    {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double)(p_73729_1_ + 0), (double)(p_73729_2_ + p_73729_6_), (double)this.zLevel, (double)((float)(p_73729_3_ + 0) * f), (double)((float)(p_73729_4_ + p_73729_6_) * f1));
        tessellator.addVertexWithUV((double)(p_73729_1_ + p_73729_5_), (double)(p_73729_2_ + p_73729_6_), (double)this.zLevel, (double)((float)(p_73729_3_ + p_73729_5_) * f), (double)((float)(p_73729_4_ + p_73729_6_) * f1));
        tessellator.addVertexWithUV((double)(p_73729_1_ + p_73729_5_), (double)(p_73729_2_ + 0), (double)this.zLevel, (double)((float)(p_73729_3_ + p_73729_5_) * f), (double)((float)(p_73729_4_ + 0) * f1));
        tessellator.addVertexWithUV((double)(p_73729_1_ + 0), (double)(p_73729_2_ + 0), (double)this.zLevel, (double)((float)(p_73729_3_ + 0) * f), (double)((float)(p_73729_4_ + 0) * f1));
        tessellator.draw();
    }
}
