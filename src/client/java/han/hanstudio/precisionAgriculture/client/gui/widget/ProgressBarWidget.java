package han.hanstudio.precisionAgriculture.client.gui.widget;

import han.hanstudio.precisionAgriculture.client.gui.AgriGuiTextures;
import han.hanstudio.precisionAgriculture.client.gui.AgriGuiUtils;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.util.List;
import java.util.function.Supplier;

/**
 * 可复用的进度条组件，支持工具提示
 * 用于湿度、肥力、健康度显示
 */
public class ProgressBarWidget {

    private final int x, y, width, height;
    private final Supplier<Float> valueSupplier;
    private final Supplier<Integer> colorSupplier;
    private final Text label;
    private final boolean showPercentage;

    public ProgressBarWidget(int x, int y, int width, int height, Text label,
                             Supplier<Float> valueSupplier, Supplier<Integer> colorSupplier,
                             boolean showPercentage) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.label = label;
        this.valueSupplier = valueSupplier;
        this.colorSupplier = colorSupplier;
        this.showPercentage = showPercentage;
    }

    public void render(DrawContext ctx, TextRenderer textRenderer) {
        float value = valueSupplier.get();
        int color = colorSupplier.get();

        // 绘制进度条
        AgriGuiUtils.drawGradientBar(ctx, x, y, width, height, value, color);

        // 绘制标签（如果存在）
        if (label != null) {
            ctx.drawTextWithShadow(textRenderer, label, x - 60, y, AgriGuiTextures.TEXT_COLOR);
        }

        // 绘制百分比
        if (showPercentage) {
            String percent = AgriGuiUtils.formatAgriValue(value);
            ctx.drawTextWithShadow(textRenderer, Text.literal(percent), x + width + 3, y, AgriGuiTextures.TEXT_COLOR);
        }
    }

    public boolean isMouseOver(int mouseX, int mouseY) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    public List<Text> getTooltip() {
        float value = valueSupplier.get();
        return List.of(
            label,
            Text.literal(String.format("当前值: %.1f%%", value))
        );
    }
}
