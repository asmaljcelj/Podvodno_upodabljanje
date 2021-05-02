package fluid_generation;

public enum VoxelType {

    FLOOR("floor"),
    CUBE("cube"),
    OTHER("other");

    private String type;

    VoxelType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}

