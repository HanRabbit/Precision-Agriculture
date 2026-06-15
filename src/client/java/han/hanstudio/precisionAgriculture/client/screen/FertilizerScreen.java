package han.hanstudio.precisionAgriculture.client.screen;

import han.hanstudio.precisionAgriculture.network.FertilizerSlotClickPayload;
import han.hanstudio.precisionAgriculture.network.OpenFertilizerPayload;
import han.hanstudio.precisionAgriculture.network.SetFertilizerRangePayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.Click;
import net.minecraft.client.input.KeyInput;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class FertilizerScreen extends Screen {

    private static final int W = 220, H = 160;
    private final BlockPos pos;
    private int range;
    private int fertilizedLast;
    private ItemStack slot0 = ItemStack.EMPTY;
    private int x, y;
    private int slotX, slotY;

    public FertilizerScreen(OpenFertilizerPayload data) {
        super(Text.literal("自动施肥机"));
        this.pos = data.pos();
        this.range = data.range();
        this.fertilizedLast = data.fertilizedLast();
    }

    public BlockPos pos() { return pos; }

    public void updateStatus(int range, int fertilizedLast, ItemStack s0) {
        this.range = range;
        this.fertilizedLast = fertilizedLast;
        this.slot0 = s0;
    }

    @Override
    protected void init() {
        x = (width - W) / 2;
        y = (height - H) / 2;
        addDrawableChild(ButtonWidget.builder(Text.literal("-"), b -> setRange(range - 1))
                .dimensions(x + 70, y + 52, 20, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("+"), b -> setRange(range + 1))
                .dimensions(x + 130, y + 52, 20, 20).build());
    }

    private void setRange(int r) {
        range = Math.clamp(r, 1, 5);
        ClientPlayNetworking.send(new SetFertilizerRangePayload(pos, range));
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        int TXT = 0xFF404040, WHITE = 0xFFFFFFFF;
        ctx.fill(0, 0, width, height, 0xA0000000);
        ctx.fill(x, y, x + W, y + H, 0xFFC6C6C6);
        ctx.fill(x, y, x + W, y + 17, 0xFF664422);
        ctx.fill(x, y, x + W, y + 1, 0xFF000000);
        ctx.fill(x, y + H - 1, x + W, y + H, 0xFF000000);
        ctx.fill(x, y, x + 1, y + H, 0xFF000000);
        ctx.fill(x + W - 1, y, x + W, y + H, 0xFF000000);

        ctx.drawCenteredTextWithShadow(textRenderer, title, x + W / 2, y + 5, WHITE);
        ctx.drawCenteredTextWithShadow(textRenderer,
                Text.literal("工作范围: " + range + " 格 (" + (range * 2 + 1) + "x" + (range * 2 + 1) + ")"),
                x + W / 2, y + 28, TXT);

        slotY = y + 80;
        ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("肥料槽:"), x + W / 2, slotY, TXT);

        slotX = x + W / 2 - 9;
        slotY = slotY + 12;
        ctx.fill(slotX, slotY, slotX + 18, slotY + 18, 0xFF555555);
        ctx.fill(slotX + 1, slotY + 1, slotX + 17, slotY + 17, 0xFF8B8B8B);

        if (!slot0.isEmpty()) {
            ctx.drawItem(slot0, slotX + 1, slotY + 1);
            ctx.drawStackOverlay(textRenderer, slot0, slotX + 1, slotY + 1);
        }

        ctx.drawCenteredTextWithShadow(textRenderer,
                Text.literal("左键放入肥料 / 右键取出"),
                x + W / 2, y + H - 30, 0xFF666666);
        ctx.drawCenteredTextWithShadow(textRenderer,
                Text.literal(fertilizedLast > 0 ? "状态: 施肥中 (" + fertilizedLast + " 处)" : "状态: 待命中"),
                x + W / 2, y + H - 16, fertilizedLast > 0 ? 0xFF44AA22 : 0xFF888888);

        super.render(ctx, mouseX, mouseY, delta);
    }

    private boolean isOver(int mx, int my) {
        return mx >= slotX + 1 && mx < slotX + 17 && my >= slotY + 1 && my < slotY + 17;
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        int button = click.button();
        if (button == 0 || button == 1) {
            int mx = (int) click.x(), my = (int) click.y();
            if (isOver(mx, my)) {
                ClientPlayNetworking.send(new FertilizerSlotClickPayload(pos, button));
                return true;
            }
        }
        return super.mouseClicked(click, doubled);
    }

    @Override public boolean keyPressed(KeyInput key) {
        if (client != null && client.options.inventoryKey.matchesKey(key)) { close(); return true; }
        return super.keyPressed(key);
    }
    @Override public void renderBackground(DrawContext ctx, int mouseX, int mouseY, float delta) {}
    @Override public boolean shouldPause() { return false; }
    @Override public boolean shouldCloseOnEsc() { return true; }
}
