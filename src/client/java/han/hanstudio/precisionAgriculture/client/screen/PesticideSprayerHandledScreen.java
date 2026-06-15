package han.hanstudio.precisionAgriculture.client.screen;

import han.hanstudio.precisionAgriculture.client.gui.AgriGuiTextures;
import han.hanstudio.precisionAgriculture.client.gui.widget.RangeControlWidget;
import han.hanstudio.precisionAgriculture.network.SetPesticideSprayerRangePayload;
import han.hanstudio.precisionAgriculture.screen.PesticideSprayerScreenHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class PesticideSprayerHandledScreen extends BaseAgriHandledScreen<PesticideSprayerScreenHandler> {

    private final BlockPos pos;
    private int range;
    private int sprayedLast;
    private RangeControlWidget rangeControl;

    public PesticideSprayerHandledScreen(PesticideSprayerScreenHandler handler, PlayerInventory inv, Text title) {
        super(handler, inv, title);
        // 临时默认值，后续通过 updateStatus 更新
        this.pos = BlockPos.ORIGIN;
        this.range = 3;
        this.sprayedLast = 0;
        backgroundHeight = 166;
        playerInventoryTitleY = backgroundHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        rangeControl = new RangeControlWidget(
            x + backgroundWidth / 2,
            y + 28,
            range,
            1,
            5,
            this::onRangeChanged
        );
        rangeControl.createButtons(this::addDrawableChild);
    }

    private void onRangeChanged(int newRange) {
        this.range = newRange;
        ClientPlayNetworking.send(new SetPesticideSprayerRangePayload(pos, range));
    }

    @Override
    protected void drawMachineContent(DrawContext ctx, float delta, int mouseX, int mouseY) {
        // 范围控制显示
        if (rangeControl != null) {
            rangeControl.render(ctx, textRenderer);
        }

        // 槽位标签
        int slotY = y + 55;
        ctx.drawTextWithShadow(textRenderer, Text.literal("杀菌剂槽:"), x + 16, slotY, AgriGuiTextures.TEXT_COLOR);
        ctx.drawTextWithShadow(textRenderer, Text.literal("杀虫剂槽:"), x + 88, slotY, AgriGuiTextures.TEXT_COLOR);

        // 状态文本
        Text status = sprayedLast > 0
            ? Text.literal("状态: 正在播撒 (" + sprayedLast + " 处)")
            : Text.literal("状态: 待命中");
        drawStatusText(ctx, status, x + 8, y + backgroundHeight - 94 - 20, sprayedLast > 0);

        // 提示文本
        ctx.drawCenteredTextWithShadow(textRenderer,
            Text.literal("左键放入手中农药 / 右键取出"),
            x + backgroundWidth / 2, y + backgroundHeight - 94 - 6,
            0xFF666666);
    }

    @Override
    protected int getTitleBarColor() {
        return AgriGuiTextures.TITLE_BAR_BROWN;
    }

    /**
     * 从服务器同步状态更新
     */
    public void updateStatus(BlockPos pos, int range, int sprayedLast) {
        this.range = range;
        this.sprayedLast = sprayedLast;
        if (rangeControl != null) {
            rangeControl.setRange(range);
        }
    }

    public BlockPos getPos() {
        return pos;
    }
}
