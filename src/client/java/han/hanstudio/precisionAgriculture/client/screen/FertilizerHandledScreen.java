package han.hanstudio.precisionAgriculture.client.screen;

import han.hanstudio.precisionAgriculture.client.gui.AgriGuiTextures;
import han.hanstudio.precisionAgriculture.client.gui.widget.RangeControlWidget;
import han.hanstudio.precisionAgriculture.network.SetFertilizerRangePayload;
import han.hanstudio.precisionAgriculture.screen.FertilizerScreenHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class FertilizerHandledScreen extends BaseAgriHandledScreen<FertilizerScreenHandler> {

    private final BlockPos pos;
    private int range;
    private int fertilizedLast;
    private RangeControlWidget rangeControl;

    public FertilizerHandledScreen(FertilizerScreenHandler handler, PlayerInventory inv, Text title) {
        super(handler, inv, title);
        // 从原 FertilizerScreen 迁移：需要通过网络包传递 pos, range, fertilizedLast
        // 临时默认值，后续通过 updateStatus 更新
        this.pos = BlockPos.ORIGIN;
        this.range = 3;
        this.fertilizedLast = 0;
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
        ClientPlayNetworking.send(new SetFertilizerRangePayload(pos, range));
    }

    @Override
    protected void drawMachineContent(DrawContext ctx, float delta, int mouseX, int mouseY) {
        // 范围控制显示
        if (rangeControl != null) {
            rangeControl.render(ctx, textRenderer);
        }

        // 状态文本
        Text status = fertilizedLast > 0
            ? Text.literal("状态: 施肥中 (" + fertilizedLast + " 处)")
            : Text.literal("状态: 待命中");
        drawStatusText(ctx, status, x + 8, y + 60, fertilizedLast > 0);

        // 槽位提示文本
        ctx.drawCenteredTextWithShadow(textRenderer,
            Text.literal("左键放入肥料 / 右键取出"),
            x + backgroundWidth / 2, y + backgroundHeight - 94 - 6,
            0xFF666666);
    }

    @Override
    protected int getTitleBarColor() {
        return AgriGuiTextures.TITLE_BAR_BROWN;
    }

    /**
     * 从服务器同步状态更新（通过网络包调用）
     */
    public void updateStatus(BlockPos pos, int range, int fertilizedLast) {
        this.range = range;
        this.fertilizedLast = fertilizedLast;
        if (rangeControl != null) {
            rangeControl.setRange(range);
        }
    }

    public BlockPos getPos() {
        return pos;
    }
}
