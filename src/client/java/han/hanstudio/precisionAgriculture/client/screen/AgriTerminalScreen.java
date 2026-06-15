package han.hanstudio.precisionAgriculture.client.screen;

import han.hanstudio.precisionAgriculture.network.OpenAgriTerminalPayload;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class AgriTerminalScreen extends Screen {

    private static final int W = 220, H = 190;
    private static final int TXT = 0xFF404040, WHITE = 0xFFFFFFFF;
    private OpenAgriTerminalPayload data;
    private int x, y;

    public AgriTerminalScreen(OpenAgriTerminalPayload data) {
        super(Text.translatable("screen.precision-agriculture.agri_terminal"));
        this.data = data;
    }

    public BlockPos pos() { return data.pos(); }

    public void update(int totalPlots, float avgMoisture, float avgFertility, float avgHealth, float pestRate, List<String> advice) {
        this.data = new OpenAgriTerminalPayload(data.pos(), totalPlots, avgMoisture, avgFertility, avgHealth, pestRate, advice);
    }

    @Override protected void init() { x = (width - W) / 2; y = (height - H) / 2; }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        int totalH = H + data.advice().size() * 11;
        ctx.fill(0, 0, width, height, 0xA0000000);
        han.hanstudio.precisionAgriculture.client.gui.AgriGuiUtils.drawTexturedPanel(ctx, x, y, W, totalH);
        han.hanstudio.precisionAgriculture.client.gui.AgriGuiUtils.drawTitleBar(ctx, x, y, W,
            han.hanstudio.precisionAgriculture.client.gui.AgriGuiTextures.TITLE_BAR_GREEN);

        ctx.drawCenteredTextWithShadow(textRenderer, title, x + W / 2, y + 5, WHITE);

        int lx = x + 8, ly = y + 22;
        ctx.fill(lx, ly, x + W - 8, ly + 1, 0xFF888888); ly += 6;

        ctx.drawTextWithShadow(textRenderer, Text.literal(String.format("农田总面积: %d 格", data.totalPlots())), lx, ly, TXT); ly += 12;
        drawBar(ctx, lx, ly, "平均湿度", data.avgMoisture(), 0x2288CC); ly += 14;
        drawBar(ctx, lx, ly, "平均肥力", data.avgFertility(), 0x44AA22); ly += 14;
        drawBar(ctx, lx, ly, "平均健康", data.avgHealth(), 0x22AA22); ly += 14;
        drawBar(ctx, lx, ly, "病害率  ", data.pestRate(), 0xCC2222); ly += 14;

        if (!data.advice().isEmpty()) {
            ly += 4;
            ctx.fill(lx, ly, x + W - 8, ly + 1, 0xFF888888); ly += 6;
            ctx.drawTextWithShadow(textRenderer, Text.literal("顾问建议:"), lx, ly, 0xFF336633); ly += 12;
            for (String line : data.advice()) {
                ctx.drawTextWithShadow(textRenderer, Text.literal("• " + line), lx + 4, ly, TXT); ly += 11;
            }
        }

        super.render(ctx, mouseX, mouseY, delta);
    }

    private void drawBar(DrawContext ctx, int lx, int ly, String label, float value, int color) {
        han.hanstudio.precisionAgriculture.client.gui.AgriGuiUtils.drawLabeledBar(
            ctx, lx, ly, label, value, color, textRenderer
        );
    }

    @Override public boolean keyPressed(KeyInput key) {
        if (client != null && client.options.inventoryKey.matchesKey(key)) { close(); return true; }
        return super.keyPressed(key);
    }
    @Override public void renderBackground(DrawContext ctx, int mouseX, int mouseY, float delta) {}
    @Override public boolean shouldPause() { return false; }
    @Override public boolean shouldCloseOnEsc() { return true; }
}
