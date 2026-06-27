package han.hanstudio.precisionAgriculture.client.screen;

import han.hanstudio.precisionAgriculture.network.OpenHarvesterPayload;
import han.hanstudio.precisionAgriculture.network.SetMachineRangePayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class HarvesterScreen extends Screen {

    private static final int W = 200, H = 130;
    private final BlockPos pos;
    private int range;
    private int harvestedLast;
    private int x, y;

    public HarvesterScreen(OpenHarvesterPayload data) {
        super(Text.translatable("screen.precision-agriculture.harvester"));
        this.pos = data.pos();
        this.range = data.range();
        this.harvestedLast = data.harvestedLast();
    }

    public void updateStatus(int range, int harvestedLast) {
        this.range = range;
        this.harvestedLast = harvestedLast;
    }

    public BlockPos pos() { return pos; }

    @Override
    protected void init() {
        x = (width - W) / 2;
        y = (height - H) / 2;
        addDrawableChild(ButtonWidget.builder(Text.literal("-"), b -> setRange(range - 1))
                .dimensions(x + 60, y + 52, 20, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("+"), b -> setRange(range + 1))
                .dimensions(x + 120, y + 52, 20, 20).build());
    }

    private void setRange(int r) {
        range = Math.max(1, Math.min(5, r));
        ClientPlayNetworking.send(new SetMachineRangePayload(pos, range, true));
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(0, 0, width, height, 0xA0000000);
        han.hanstudio.precisionAgriculture.client.gui.AgriGuiUtils.drawTexturedPanel(ctx, x, y, W, H);
        han.hanstudio.precisionAgriculture.client.gui.AgriGuiUtils.drawTitleBar(ctx, x, y, W,
            han.hanstudio.precisionAgriculture.client.gui.AgriGuiTextures.TITLE_BAR_HARVESTER);
        ctx.drawCenteredTextWithShadow(textRenderer, title, x + W / 2, y + 5, 0xFFFFFFFF);
        ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("工作范围: " + range + " 格 (" + (range * 2 + 1) + "x" + (range * 2 + 1) + ")"), x + W / 2, y + 28, 0xFF404040);
        ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("最近收割: " + harvestedLast + " 株"), x + W / 2, y + 88, harvestedLast > 0 ? 0xFFAA6600 : 0xFF666666);
        ctx.drawCenteredTextWithShadow(textRenderer, Text.literal(harvestedLast > 0 ? "状态: 工作中" : "状态: 等待成熟"), x + W / 2, y + 102, harvestedLast > 0 ? 0xFFAA6600 : 0xFF888888);
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
