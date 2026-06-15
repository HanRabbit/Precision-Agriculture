package han.hanstudio.precisionAgriculture.client.screen;

import han.hanstudio.precisionAgriculture.network.OpenWeatherStationPayload;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;

public class WeatherStationScreen extends Screen {

    private static final int W = 200, H = 120;
    private static final int TXT = 0xFF404040, WHITE = 0xFFFFFFFF;
    private final OpenWeatherStationPayload data;
    private int x, y;

    public WeatherStationScreen(OpenWeatherStationPayload data) {
        super(Text.translatable("screen.precision-agriculture.weather_station"));
        this.data = data;
    }

    @Override protected void init() { x = (width - W) / 2; y = (height - H) / 2; }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(0, 0, width, height, 0xA0000000);
        han.hanstudio.precisionAgriculture.client.gui.AgriGuiUtils.drawTexturedPanel(ctx, x, y, W, H);
        han.hanstudio.precisionAgriculture.client.gui.AgriGuiUtils.drawTitleBar(ctx, x, y, W,
            han.hanstudio.precisionAgriculture.client.gui.AgriGuiTextures.TITLE_BAR_BLUE);

        ctx.drawCenteredTextWithShadow(textRenderer, title, x + W / 2, y + 5, WHITE);

        int lx = x + 8, ly = y + 22;
        int tempColor = han.hanstudio.precisionAgriculture.client.gui.AgriGuiUtils.temperatureColor(data.temperature());
        ctx.drawTextWithShadow(textRenderer, Text.literal(String.format("环境温度: %.1f °C", data.temperature())), lx, ly, tempColor); ly += 14;

        int filled = data.lightLevel() * 10 / 15;
        ctx.drawTextWithShadow(textRenderer,
                Text.literal("光照强度: [" + "█".repeat(filled) + "░".repeat(10 - filled) + "] " + data.lightLevel() + "/15"),
                lx, ly, TXT); ly += 14;

        ctx.drawTextWithShadow(textRenderer,
                Text.literal("降雨状态: " + (data.raining() ? "是" : "否")),
                lx, ly, data.raining() ? 0xFF2255AA : 0xFF228822);

        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override public boolean keyPressed(KeyInput key) {
        if (client != null && client.options.inventoryKey.matchesKey(key)) { close(); return true; }
        return super.keyPressed(key);
    }
    @Override public void renderBackground(DrawContext ctx, int mouseX, int mouseY, float delta) {}
    @Override public boolean shouldPause() { return false; }
    @Override public boolean shouldCloseOnEsc() { return true; }
}
