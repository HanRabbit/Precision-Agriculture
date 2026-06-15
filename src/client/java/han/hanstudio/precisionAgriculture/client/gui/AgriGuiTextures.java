package han.hanstudio.precisionAgriculture.client.gui;

import net.minecraft.util.Identifier;

/**
 * 集中管理 GUI 纹理坐标常量和颜色配置
 * 所有坐标引用 agriculture_gui.png (256x256)
 */
public class AgriGuiTextures {

    public static final Identifier GUI_ATLAS = Identifier.of("precision-agriculture", "textures/gui/agriculture_gui.png");

    // === 面板背景 (9-slice 区域) ===
    public static final int PANEL_BG_U = 0;
    public static final int PANEL_BG_V = 0;
    public static final int PANEL_BG_WIDTH = 176;
    public static final int PANEL_BG_HEIGHT = 166;

    // === 槽位背景 (18x18) ===
    public static final int SLOT_EMPTY_U = 176;
    public static final int SLOT_EMPTY_V = 0;
    public static final int SLOT_VALID_U = 194;
    public static final int SLOT_VALID_V = 0;
    public static final int SLOT_INVALID_U = 212;
    public static final int SLOT_INVALID_V = 0;

    // === 进度条组件 ===
    public static final int PROGRESS_BG_U = 176;
    public static final int PROGRESS_BG_V = 20;
    public static final int PROGRESS_BG_WIDTH = 100;
    public static final int PROGRESS_BG_HEIGHT = 8;

    public static final int PROGRESS_FG_U = 176;
    public static final int PROGRESS_FG_V = 30;
    public static final int PROGRESS_FG_WIDTH = 100;
    public static final int PROGRESS_FG_HEIGHT = 8;

    // === 按钮纹理 (20x20) ===
    public static final int BUTTON_MINUS_U = 230;
    public static final int BUTTON_MINUS_V = 0;
    public static final int BUTTON_PLUS_U = 230;
    public static final int BUTTON_PLUS_V = 20;
    public static final int BUTTON_HOVER_U = 230;
    public static final int BUTTON_HOVER_V = 40;

    // === 颜色调色板 (替换硬编码颜色) ===
    public static final int PANEL_BG_COLOR = 0xFFC6C6C6;
    public static final int BORDER_COLOR = 0xFF000000;

    // 标题栏颜色 - 统一农业主题
    public static final int TITLE_BAR_BROWN = 0xFF664422;    // 施肥机、农药播撒机
    public static final int TITLE_BAR_BLUE = 0xFF555599;     // 灌溉器、天气站
    public static final int TITLE_BAR_GREEN = 0xFF336633;    // 农业终端
    public static final int TITLE_BAR_PLANTER = 0xFF445588;  // 播种机
    public static final int TITLE_BAR_HARVESTER = 0xFF885544; // 收割机

    // === 基于数值的渐变颜色 ===

    // 湿度颜色 (红 -> 橙 -> 蓝)
    public static final int MOISTURE_LOW = 0xCC2222;
    public static final int MOISTURE_MED = 0xCC8800;
    public static final int MOISTURE_HIGH = 0x2288CC;

    // 肥力颜色 (红 -> 橙 -> 绿)
    public static final int FERTILITY_LOW = 0xCC2222;
    public static final int FERTILITY_MED = 0xCC8800;
    public static final int FERTILITY_HIGH = 0x44AA22;

    // 健康度颜色 (红 -> 橙 -> 绿)
    public static final int HEALTH_LOW = 0xCC2222;
    public static final int HEALTH_MED = 0xCC8800;
    public static final int HEALTH_HIGH = 0x22AA22;

    // 状态颜色
    public static final int STATUS_ACTIVE = 0x44AA22;
    public static final int STATUS_IDLE = 0x888888;
    public static final int STATUS_ERROR = 0xCC2222;

    // 文本颜色
    public static final int TEXT_COLOR = 0x404040;
    public static final int TITLE_COLOR = 0xFFFFFF;
}
