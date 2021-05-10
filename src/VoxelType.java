public enum VoxelType {

    FLOOR("floor"),
    CUBE("cube"),
    FLUID("fluid"),
    AIR("air");

    private String type;

    VoxelType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}

