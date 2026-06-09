package han.hanstudio.precisionAgriculture.client.screen;

import han.hanstudio.precisionAgriculture.network.OpenSoilSensorPayload;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class SoilSensorScreen extends Screen {

    private final OpenSoilSensorPayload data;

    public SoilSensorScreen(OpenSoilSensorPayload data) {
        super(Text.translatable("screen.precision-agriculture.soil_sensor"));
        this.data = data;
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);
        int cx = width / 2;
        int y = height / 2 - 60;

        ctx.drawCenteredTextWithShadow(textRenderer, title, cx, y, 0xFFFFAA);
        y += 20;

        BlockPos p = data.pos();
        ctx.drawCenteredTextWithShadow(textRenderer,
                Text.literal(String.format("位置: (%d, %d, %d)", p.getX(), p.getY(), p.getZ())), cx, y, 0xCCCCCC);
        y += 14;

        ctx.drawCenteredTextWithShadow(textRenderer,
                bar("湿度", data.moisture()), cx, y, moistureColor(data.moisture())); y += 14;
        ctx.drawCenteredTextWithShadow(textRenderer,
                bar("肥力", data.fertility()), cx, y, fertilityColor(data.fertility())); y += 14;
        ctx.drawCenteredTextWithShadow(textRenderer,
                Text.literal(String.format("温度: %.1f°C", data.temperature())), cx, y, 0xFFDD88); y += 14;
        ctx.drawCenteredTextWithShadow(textRenderer,
                bar("健康度", data.health()), cx, y, healthColor(data.health())); y += 14;

        String pest = data.pestType().isEmpty() ? "无" : data.pestType();
        ctx.drawCenteredTextWithShadow(textRenderer,
                Text.literal("病虫害: " + pest), cx, y, data.pestType().isEmpty() ? 0x55FF55 : 0xFF5555);

        ctx.drawCenteredTextWithShadow(textRenderer,
                Text.literal("§7[ESC 关闭]"), cx, height / 2 + 70, 0x888888);

    }

    private Text bar(String label, float value) {
        int filled = (int) (value / 10);
        String bar = "█".repeat(filled) + "░".repeat(10 - filled);
        return Text.literal(String.format("%s: [%s] %.1f%%", label, bar, value));
    }

    private int moistureColor(float v) { return v < 30 ? 0xFF4444 : v < 60 ? 0xFFAA00 : 0x44AAFF; }
    private int fertilityColor(float v) { return v < 30 ? 0xFF4444 : v < 50 ? 0xFFAA00 : 0x88FF44; }
    private int healthColor(float v) { return v < 30 ? 0xFF4444 : v < 60 ? 0xFFAA00 : 0x44FF44; }

    @Override public boolean shouldPause() { return false; }
}
