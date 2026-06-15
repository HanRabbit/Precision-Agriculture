package han.hanstudio.precisionAgriculture.client.gui;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

/**
 * GUI 渲染的静态工具方法
 */
public class AgriGuiUtils {

    /**
     * 绘制 9-slice 纹理面板背景
     */
    public static void drawTexturedPanel(DrawContext ctx, int x, int y, int width, int height) {
        // 第一阶段使用改进的填充方法，保持一致的颜色
        ctx.fill(x, y, x + width, y + height, AgriGuiTextures.PANEL_BG_COLOR);
        ctx.fill(x, y, x + width, y + 1, AgriGuiTextures.BORDER_COLOR);
        ctx.fill(x, y + height - 1, x + width, y + height, AgriGuiTextures.BORDER_COLOR);
        ctx.fill(x, y, x + 1, y + height, AgriGuiTextures.BORDER_COLOR);
        ctx.fill(x + width - 1, y, x + width, y + height, AgriGuiTextures.BORDER_COLOR);
    }

    /**
     * 绘制带农业主题颜色的标题栏
     */
    public static void drawTitleBar(DrawContext ctx, int x, int y, int width, int color) {
        ctx.fill(x + 1, y + 1, x + width - 1, y + 17, color);
    }

    /**
     * 绘制带渐变颜色的水平进度条
     */
    public static void drawGradientBar(DrawContext ctx, int x, int y, int width, int height, float value, int color) {
        // 背景
        ctx.fill(x, y, x + width, y + height, 0xFF333333);
        // 前景填充部分
        int filledWidth = (int)(value / 100f * width);
        ctx.fill(x, y, x + filledWidth, y + height, 0xFF000000 | color);
    }

    /**
     * 绘制带标签和百分比显示的进度条
     */
    public static void drawLabeledBar(DrawContext ctx, int x, int y, String label, float value, int color, TextRenderer textRenderer) {
        int barX = x + 64;
        int barW = 100;
        int barH = 8;

        drawGradientBar(ctx, barX, y, barW, barH, value, color);
        ctx.drawTextWithShadow(textRenderer, Text.literal(label), x, y, AgriGuiTextures.TEXT_COLOR);
        ctx.drawTextWithShadow(textRenderer, Text.literal(String.format("%.0f%%", value)), barX + barW + 3, y, AgriGuiTextures.TEXT_COLOR);
    }

    /**
     * 返回湿度值对应的颜色 (红 -> 橙 -> 蓝)
     */
    public static int moistureColor(float value) {
        if (value < 30) return AgriGuiTextures.MOISTURE_LOW;
        if (value < 60) return AgriGuiTextures.MOISTURE_MED;
        return AgriGuiTextures.MOISTURE_HIGH;
    }

    /**
     * 返回肥力值对应的颜色 (红 -> 橙 -> 绿)
     */
    public static int fertilityColor(float value) {
        if (value < 30) return AgriGuiTextures.FERTILITY_LOW;
        if (value < 50) return AgriGuiTextures.FERTILITY_MED;
        return AgriGuiTextures.FERTILITY_HIGH;
    }

    /**
     * 返回健康度值对应的颜色 (红 -> 橙 -> 绿)
     */
    public static int healthColor(float value) {
        if (value < 30) return AgriGuiTextures.HEALTH_LOW;
        if (value < 60) return AgriGuiTextures.HEALTH_MED;
        return AgriGuiTextures.HEALTH_HIGH;
    }

    /**
     * 一致地格式化农业数值
     */
    public static String formatAgriValue(float value) {
        return String.format("%.0f%%", value);
    }

    /**
     * 绘制温度值并返回对应颜色
     */
    public static int temperatureColor(float temperature) {
        if (temperature > 35) return 0xCC2200;  // 热 - 红色
        if (temperature < 5) return 0x2266AA;   // 冷 - 蓝色
        return 0x884400;                         // 适宜 - 橙色
    }
}
