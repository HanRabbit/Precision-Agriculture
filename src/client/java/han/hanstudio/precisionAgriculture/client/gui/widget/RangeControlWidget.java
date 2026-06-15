package han.hanstudio.precisionAgriculture.client.gui.widget;

import han.hanstudio.precisionAgriculture.client.gui.AgriGuiTextures;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.function.Consumer;

/**
 * 可复用的范围控制组件，带 +/- 按钮和范围显示
 * 用于所有机器界面（5+ 个机器）
 */
public class RangeControlWidget {

    private final int x, y;
    private int range;
    private final int minRange, maxRange;
    private final Consumer<Integer> onRangeChanged;
    private ButtonWidget minusButton, plusButton;

    public RangeControlWidget(int x, int y, int initialRange, int minRange, int maxRange, Consumer<Integer> onRangeChanged) {
        this.x = x;
        this.y = y;
        this.range = initialRange;
        this.minRange = minRange;
        this.maxRange = maxRange;
        this.onRangeChanged = onRangeChanged;
    }

    public void createButtons(Consumer<ButtonWidget> addButton) {
        minusButton = ButtonWidget.builder(Text.literal("-"), b -> changeRange(-1))
                .dimensions(x - 60, y, 20, 20)
                .build();
        plusButton = ButtonWidget.builder(Text.literal("+"), b -> changeRange(1))
                .dimensions(x + 40, y, 20, 20)
                .build();

        addButton.accept(minusButton);
        addButton.accept(plusButton);
    }

    private void changeRange(int delta) {
        range = Math.max(minRange, Math.min(maxRange, range + delta));
        onRangeChanged.accept(range);
    }

    public void render(DrawContext ctx, TextRenderer textRenderer) {
        String rangeText = String.format("工作范围: %d 格 (%dx%d)", range, range * 2 + 1, range * 2 + 1);
        ctx.drawCenteredTextWithShadow(textRenderer, Text.literal(rangeText), x, y + 6, AgriGuiTextures.TEXT_COLOR);
    }

    public void setRange(int range) {
        this.range = Math.max(minRange, Math.min(maxRange, range));
    }

    public int getRange() {
        return range;
    }
}
