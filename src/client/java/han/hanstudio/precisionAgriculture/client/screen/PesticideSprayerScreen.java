package han.hanstudio.precisionAgriculture.client.screen;

import han.hanstudio.precisionAgriculture.network.OpenPesticideSprayerPayload;
import han.hanstudio.precisionAgriculture.network.PesticideSprayerSlotClickPayload;
import han.hanstudio.precisionAgriculture.network.SetPesticideSprayerRangePayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.Click;
import net.minecraft.client.input.KeyInput;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class PesticideSprayerScreen extends Screen {

    private static final int W = 220, H = 180;
    private final BlockPos pos;
    private int range;
    private int sprayedLast;
    private ItemStack slot0 = ItemStack.EMPTY;
    private ItemStack slot1 = ItemStack.EMPTY;
    private int x, y;
    private int slot0X, slot1X, slotsY;

    public PesticideSprayerScreen(OpenPesticideSprayerPayload data) {
        super(Text.literal("自动农药播撒机"));
        this.pos = data.pos();
        this.range = data.range();
        this.sprayedLast = data.sprayedLast();
    }

    public BlockPos pos() { return pos; }

    public void updateStatus(int range, int sprayedLast, ItemStack s0, ItemStack s1) {
        this.range = range; this.sprayedLast = sprayedLast;
        this.slot0 = s0; this.slot1 = s1;
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
        range = Math.max(1, Math.min(5, r));
        ClientPlayNetworking.send(new SetPesticideSprayerRangePayload(pos, range));
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

        int slotY = y + 80;
        ctx.drawTextWithShadow(textRenderer, Text.literal("杀菌剂槽:"), x + 16, slotY, TXT);
        ctx.drawTextWithShadow(textRenderer, Text.literal("杀虫剂槽:"), x + W / 2 + 4, slotY, TXT);

        slot0X = x + 16;
        slot1X = x + W / 2 + 4;
        slotsY = slotY + 12;
        ctx.fill(slot0X, slotsY, slot0X + 18, slotsY + 18, 0xFF555555);
        ctx.fill(slot0X + 1, slotsY + 1, slot0X + 17, slotsY + 17, 0xFF8B8B8B);
        ctx.fill(slot1X, slotsY, slot1X + 18, slotsY + 18, 0xFF555555);
        ctx.fill(slot1X + 1, slotsY + 1, slot1X + 17, slotsY + 17, 0xFF8B8B8B);

        if (!slot0.isEmpty()) {
            ctx.drawItem(slot0, slot0X + 1, slotsY + 1);
            ctx.drawStackOverlay(textRenderer, slot0, slot0X + 1, slotsY + 1);
        }
        if (!slot1.isEmpty()) {
            ctx.drawItem(slot1, slot1X + 1, slotsY + 1);
            ctx.drawStackOverlay(textRenderer, slot1, slot1X + 1, slotsY + 1);
        }

        ctx.drawCenteredTextWithShadow(textRenderer,
                Text.literal("左键放入手中农药 / 右键取出"),
                x + W / 2, y + H - 30, 0xFF666666);
        ctx.drawCenteredTextWithShadow(textRenderer,
                Text.literal(sprayedLast > 0 ? "状态: 正在播撒 (" + sprayedLast + " 处)" : "状态: 待命中"),
                x + W / 2, y + H - 16, sprayedLast > 0 ? 0xFF44AA22 : 0xFF888888);

        super.render(ctx, mouseX, mouseY, delta);
    }

    private static boolean isOver(int mx, int my, int sx, int sy) {
        return mx >= sx + 1 && mx < sx + 17 && my >= sy + 1 && my < sy + 17;
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        int button = click.button();
        if (button == 0 || button == 1) {
            int mx = (int) click.x(), my = (int) click.y();
            if (isOver(mx, my, slot0X, slotsY)) {
                ClientPlayNetworking.send(new PesticideSprayerSlotClickPayload(pos, 0, button));
                return true;
            }
            if (isOver(mx, my, slot1X, slotsY)) {
                ClientPlayNetworking.send(new PesticideSprayerSlotClickPayload(pos, 1, button));
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
