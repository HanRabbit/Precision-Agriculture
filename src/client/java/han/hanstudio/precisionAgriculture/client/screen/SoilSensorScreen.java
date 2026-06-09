package han.hanstudio.precisionAgriculture.client.screen;

import han.hanstudio.precisionAgriculture.network.OpenSoilSensorPayload;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class SoilSensorScreen extends Screen {

    private static final int W = 200, H = 190;
    private static final int TXT = 0xFF404040, WHITE = 0xFFFFFFFF;
    private final OpenSoilSensorPayload data;
    private int x, y;

    public SoilSensorScreen(OpenSoilSensorPayload data) {
        super(Text.translatable("screen.precision-agriculture.soil_sensor"));
        this.data = data;
    }

    @Override protected void init() { x = (width - W) / 2; y = (height - H) / 2; }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(0, 0, width, height, 0xA0000000);
        drawPanel(ctx, x, y, W, H, 0xFF555599);
        ctx.drawCenteredTextWithShadow(textRenderer, title, x + W / 2, y + 5, WHITE);

        BlockPos p = data.pos();
        int lx = x + 8, ly = y + 22;
        ctx.drawTextWithShadow(textRenderer, Text.literal(String.format("位置: %d, %d, %d", p.getX(), p.getY(), p.getZ())), lx, ly, TXT); ly += 14;
        drawBar(ctx, lx, ly, "湿度", data.moisture(), moistureColor(data.moisture())); ly += 14;
        drawBar(ctx, lx, ly, "肥力", data.fertility(), fertilityColor(data.fertility())); ly += 14;
        ctx.drawTextWithShadow(textRenderer, Text.literal(String.format("温度: %.1f °C", data.temperature())), lx, ly, TXT); ly += 14;
        drawBar(ctx, lx, ly, "健康度", data.health(), healthColor(data.health())); ly += 14;
        boolean hasPest = data.pestType() != null && !data.pestType().isEmpty();
        ctx.drawTextWithShadow(textRenderer, Text.literal("病虫害: " + (hasPest ? data.pestType() : "无")), lx, ly, hasPest ? 0xFFCC0000 : 0xFF008000);
        super.render(ctx, mouseX, mouseY, delta);
    }

    private void drawPanel(DrawContext ctx, int px, int py, int pw, int ph, int titleColor) {
        ctx.fill(px, py, px + pw, py + ph, 0xFFC6C6C6);
        ctx.fill(px + 1, py + 1, px + pw - 1, py + 17, titleColor);
        ctx.fill(px, py, px + pw, py + 1, 0xFF000000);
        ctx.fill(px, py + ph - 1, px + pw, py + ph, 0xFF000000);
        ctx.fill(px, py, px + 1, py + ph, 0xFF000000);
        ctx.fill(px + pw - 1, py, px + pw, py + ph, 0xFF000000);
    }

    private void drawBar(DrawContext ctx, int lx, int ly, String label, float value, int color) {
        int barX = lx + 50, barW = 100, barH = 8;
        ctx.fill(barX, ly, barX + barW, ly + barH, 0xFF333333);
        ctx.fill(barX, ly, barX + (int)(value / 100f * barW), ly + barH, 0xFF000000 | color);
        ctx.drawTextWithShadow(textRenderer, Text.literal(label), lx, ly, TXT);
        ctx.drawTextWithShadow(textRenderer, Text.literal(String.format("%.0f%%", value)), barX + barW + 3, ly, TXT);
    }

    private int moistureColor(float v) { return v < 30 ? 0xCC2222 : v < 60 ? 0xCC8800 : 0x2288CC; }
    private int fertilityColor(float v) { return v < 30 ? 0xCC2222 : v < 50 ? 0xCC8800 : 0x44AA22; }
    private int healthColor(float v)    { return v < 30 ? 0xCC2222 : v < 60 ? 0xCC8800 : 0x22AA22; }

    @Override public boolean keyPressed(KeyInput key) {
        if (client != null && client.options.inventoryKey.matchesKey(key)) { close(); return true; }
        return super.keyPressed(key);
    }
    @Override public void renderBackground(DrawContext ctx, int mouseX, int mouseY, float delta) {}
    @Override public boolean shouldPause() { return false; }
    @Override public boolean shouldCloseOnEsc() { return true; }
}
