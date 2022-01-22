package ua.ldoin.minecreator.utils.block;

public class OverlayBlock {

    public OverlayBlock(String string) {

        ground = new SerializableBlock(string.split(";")[0]);
        block = new SerializableBlock(string.split(";")[1]);

    }

    public OverlayBlock(SerializableBlock ground, SerializableBlock block) {

        this.ground = ground;
        this.block = block;

    }

    private final SerializableBlock ground;
    private final SerializableBlock block;

    public SerializableBlock getGround() {

        return ground;

    }

    public SerializableBlock getBlock() {

        return block;

    }

    public String toString() {

        return ground.toString() + ";" + block.toString();

    }
}