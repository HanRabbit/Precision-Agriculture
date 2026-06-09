package han.hanstudio.precisionAgriculture.client.screen;

import han.hanstudio.precisionAgriculture.network.OpenAgriTerminalPayload;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.List;

public class AgriTerminalScreen extends Screen {

    private final OpenAgriTerminalPayload data;

    public AgriTerminalScreen(OpenAgriTerminalPayload data) {
        super(Text.translatable("screen.precision-agriculture.agri_terminal"));
        this.data = data;
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);
        int cx = width / 2;
        int y = 20;

        ctx.drawCenteredTextWithShadow(textRenderer, title, cx, y, 0x88FF88); y += 20;

        // Stats panel
        ctx.fill(cx - 140, y - 4, cx + 140, y + 70, 0x88000000);
        ctx.drawCenteredTextWithShadow(textRenderer,
                Text.literal("§e农场统计"), cx, y, 0xFFFF55); y += 14;
        ctx.drawTextWithShadow(textRenderer,
                String.format("农田总面积: §f%d 格", data.totalPlots()), cx - 130, y, 0xCCCCCC); y += 11;
        ctx.drawTextWithShadow(textRenderer,
                String.format("平均湿度:  §b%.1f%%", data.avgMoisture()), cx - 130, y, 0xCCCCCC); y += 11;
        ctx.drawTextWithShadow(textRenderer,
                String.format("平均肥力:  §a%.1f%%", data.avgFertility()), cx - 130, y, 0xCCCCCC); y += 11;
        ctx.drawTextWithShadow(textRenderer,
                String.format("平均健康度: §6%.1f%%", data.avgHealth()), cx - 130, y, 0xCCCCCC); y += 11;
        ctx.drawTextWithShadow(textRenderer,
                String.format("病害率:   §c%.1f%%", data.pestRate()), cx - 130, y, 0xCCCCCC); y += 18;

        // Progress bars
        drawBar(ctx, cx - 130, y, data.avgMoisture(), 0xFF4488FF, "湿"); y += 12;
        drawBar(ctx, cx - 130, y, data.avgFertility(), 0xFF44FF44, "肥"); y += 12;
        drawBar(ctx, cx - 130, y, data.avgHealth(), 0xFFFFAA00, "健"); y += 18;

        // Advisor section
        ctx.fill(cx - 140, y - 4, cx + 140, y + data.advice().size() * 12 + 8, 0x88000000);
        ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("§e智能顾问建议"), cx, y, 0xFFFF55); y += 14;
        for (String line : data.advice()) {
            ctx.drawTextWithShadow(textRenderer, "§7> §f" + line, cx - 130, y, 0xEEEEEE);
            y += 12;
        }

        ctx.drawCenteredTextWithShadow(textRenderer,
                Text.literal("§7[ESC 关闭]"), cx, height - 20, 0x888888);

    }

    private void drawBar(DrawContext ctx, int x, int y, float value, int color, String label) {
        int barWidth = 260;
        int filled = (int) (value / 100f * barWidth);
        ctx.fill(x, y, x + barWidth, y + 8, 0xFF333333);
        ctx.fill(x, y, x + filled, y + 8, color);
        ctx.drawTextWithShadow(textRenderer, label + " " + String.format("%.0f%%", value),
                x + barWidth + 4, y, 0xFFFFFF);
    }

    @Override public boolean shouldPause() { return false; }
}
