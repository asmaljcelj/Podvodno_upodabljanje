package data;

public enum VoxelType {

    FLOOR("floor"),
    OBJECT("object"),
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

