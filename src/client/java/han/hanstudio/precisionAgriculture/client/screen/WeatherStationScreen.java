package han.hanstudio.precisionAgriculture.client.screen;

import han.hanstudio.precisionAgriculture.network.OpenWeatherStationPayload;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class WeatherStationScreen extends Screen {

    private final OpenWeatherStationPayload data;

    public WeatherStationScreen(OpenWeatherStationPayload data) {
        super(Text.translatable("screen.precision-agriculture.weather_station"));
        this.data = data;
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);
        int cx = width / 2;
        int y = height / 2 - 40;

        ctx.drawCenteredTextWithShadow(textRenderer, title, cx, y, 0xAADDFF);
        y += 24;

        ctx.drawCenteredTextWithShadow(textRenderer,
                Text.literal(String.format("环境温度: %.1f°C", data.temperature())), cx, y,
                data.temperature() > 35 ? 0xFF6644 : data.temperature() < 5 ? 0x88CCFF : 0xFFDD88);
        y += 14;

        ctx.drawCenteredTextWithShadow(textRenderer,
                Text.literal("光照强度: " + lightBar(data.lightLevel())), cx, y, 0xFFFF88);
        y += 14;

        ctx.drawCenteredTextWithShadow(textRenderer,
                Text.literal("降雨: " + (data.raining() ? "§b是" : "§a否")), cx, y, 0xCCCCFF);

        ctx.drawCenteredTextWithShadow(textRenderer,
                Text.literal("§7[ESC 关闭]"), cx, height / 2 + 50, 0x888888);
    }

    private String lightBar(int level) {
        int filled = level * 10 / 15;
        return "[" + "█".repeat(filled) + "░".repeat(10 - filled) + "] " + level + "/15";
    }

    @Override public boolean shouldPause() { return false; }
}
