package han.hanstudio.precisionAgriculture.client.screen;

import han.hanstudio.precisionAgriculture.client.gui.AgriGuiTextures;
import han.hanstudio.precisionAgriculture.client.gui.AgriGuiUtils;
import han.hanstudio.precisionAgriculture.screen.PlanterScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class PlanterHandledScreen extends HandledScreen<PlanterScreenHandler> {

    public PlanterHandledScreen(PlanterScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundHeight = 166;
        this.playerInventoryTitleY = this.backgroundHeight - 94;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        // 绘制主背景面板
        AgriGuiUtils.drawTexturedPanel(context, x, y, backgroundWidth, backgroundHeight);

        // 绘制标题栏
        AgriGuiUtils.drawTitleBar(context, x, y, backgroundWidth, AgriGuiTextures.TITLE_BAR_PLANTER);

        // 绘制槽位背景（3x3）
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                int slotX = x + 62 + col * 18;
                int slotY = y + 17 + row * 18;
                context.fill(slotX - 1, slotY - 1, slotX + 17, slotY + 17, 0xFF8B8B8B);
            }
        }
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        // 绘制标题（相对于GUI左上角的坐标）
        context.drawText(textRenderer, title, this.titleX, this.titleY, 0xFF404040, false);
        // 绘制玩家物品栏标题
        context.drawText(textRenderer, playerInventoryTitle, this.playerInventoryTitleX, this.playerInventoryTitleY, 0xFF404040, false);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // 先渲染背景（暗化效果）
        renderBackground(context, mouseX, mouseY, delta);
        // 再调用父类的render来绘制GUI
        super.render(context, mouseX, mouseY, delta);
        // 最后绘制工具提示
        drawMouseoverTooltip(context, mouseX, mouseY);
    }
}
