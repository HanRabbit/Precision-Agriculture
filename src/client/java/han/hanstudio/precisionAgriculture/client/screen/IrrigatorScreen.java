package han.hanstudio.precisionAgriculture.client.screen;

import han.hanstudio.precisionAgriculture.network.OpenIrrigatorPayload;
import han.hanstudio.precisionAgriculture.network.SetIrrigatorRangePayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class IrrigatorScreen extends Screen {

    private static final int W = 180, H = 100;
    private final BlockPos pos;
    private int range;
    private int x, y;

    public IrrigatorScreen(OpenIrrigatorPayload data) {
        super(Text.literal("自动灌溉器"));
        this.pos = data.pos();
        this.range = data.range();
    }

    @Override
    protected void init() {
        x = (width - W) / 2;
        y = (height - H) / 2;
        addDrawableChild(ButtonWidget.builder(Text.literal("-"), b -> setRange(range - 1))
                .dimensions(x + 50, y + 52, 20, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("+"), b -> setRange(range + 1))
                .dimensions(x + 110, y + 52, 20, 20).build());
    }

    private void setRange(int r) {
        range = Math.max(1, Math.min(5, r));
        ClientPlayNetworking.send(new SetIrrigatorRangePayload(pos, range));
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(0, 0, width, height, 0xA0000000);
        han.hanstudio.precisionAgriculture.client.gui.AgriGuiUtils.drawTexturedPanel(ctx, x, y, W, H);
        han.hanstudio.precisionAgriculture.client.gui.AgriGuiUtils.drawTitleBar(ctx, x, y, W,
            han.hanstudio.precisionAgriculture.client.gui.AgriGuiTextures.TITLE_BAR_BLUE);
        ctx.drawCenteredTextWithShadow(textRenderer, title, x + W / 2, y + 5, 0xFFFFFFFF);
        ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("工作范围: " + range + " 格"), x + W / 2, y + 32, 0xFF404040);
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
