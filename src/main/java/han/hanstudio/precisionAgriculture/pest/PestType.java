package han.hanstudio.precisionAgriculture.pest;

public enum PestType {
    FUNGAL_DISEASE("真菌病", true),
    LEAF_BLIGHT("叶枯病", true),
    APHIDS("蚜虫", false),
    PEST_INVASION("害虫侵袭", false);

    public final String displayName;
    public final boolean isDisease;

    PestType(String displayName, boolean isDisease) {
        this.displayName = displayName;
        this.isDisease = isDisease;
    }
}
