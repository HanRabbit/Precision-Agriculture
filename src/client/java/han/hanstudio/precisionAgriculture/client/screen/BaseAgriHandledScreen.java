package han.hanstudio.precisionAgriculture.client.screen;

import han.hanstudio.precisionAgriculture.client.gui.AgriGuiTextures;
import han.hanstudio.precisionAgriculture.client.gui.AgriGuiUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;

/**
 * 所有农业机器 HandledScreen 的基类
 * 提供通用的渲染工具、纹理加载和样式
 */
public abstract class BaseAgriHandledScreen<T extends ScreenHandler> extends HandledScreen<T> {

    public BaseAgriHandledScreen(T handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void drawBackground(DrawContext ctx, float delta, int mouseX, int mouseY) {
        // 绘制主面板背景（使用纹理图集）
        AgriGuiUtils.drawTexturedPanel(ctx, x, y, backgroundWidth, backgroundHeight);

        // 绘制标题栏
        AgriGuiUtils.drawTitleBar(ctx, x, y, backgroundWidth, getTitleBarColor());

        // 绘制自定义机器特定内容
        drawMachineContent(ctx, delta, mouseX, mouseY);
    }

    /**
     * 重写此方法以绘制机器特定的 UI 元素（状态栏、指示器等）
     */
    protected abstract void drawMachineContent(DrawContext ctx, float delta, int mouseX, int mouseY);

    /**
     * 重写此方法以返回机器特定的标题栏颜色
     * 默认为棕色（施肥机主题）
     */
    protected int getTitleBarColor() {
        return AgriGuiTextures.TITLE_BAR_BROWN;
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);

        // 绘制标题
        ctx.drawCenteredTextWithShadow(textRenderer, title, x + backgroundWidth / 2, y + 5, AgriGuiTextures.TITLE_COLOR);

        // 绘制槽位工具提示
        drawMouseoverTooltip(ctx, mouseX, mouseY);

        // 渲染自定义元素的工具提示
        renderCustomTooltips(ctx, mouseX, mouseY);
    }

    /**
     * 重写以添加自定义 UI 元素的工具提示
     */
    protected void renderCustomTooltips(DrawContext ctx, int mouseX, int mouseY) {
        // 默认：无自定义工具提示
    }

    /**
     * 辅助方法：绘制带颜色编码的状态文本
     */
    protected void drawStatusText(DrawContext ctx, Text text, int x, int y, boolean isActive) {
        ctx.drawTextWithShadow(textRenderer, text, x, y,
            isActive ? AgriGuiTextures.STATUS_ACTIVE : AgriGuiTextures.STATUS_IDLE);
    }

    /**
     * 辅助方法：绘制居中的机器信息文本
     */
    protected void drawMachineInfo(DrawContext ctx, Text text, int y) {
        ctx.drawCenteredTextWithShadow(textRenderer, text, x + backgroundWidth / 2, y, AgriGuiTextures.TEXT_COLOR);
    }
}
