package ua.ldoin.minecreator.utils.block;

import org.bukkit.Material;

public class SerializableBlock {

    public SerializableBlock(Material block) {

        this.block = block;
        this.data = 0;

    }

    public SerializableBlock(Material block, byte data) {

        this.block = block;
        this.data = data;

    }

    public SerializableBlock(String self) {

        String[] bits = self.split(":");

        try {

            block = Material.getMaterial(bits[0]);

            if (bits.length == 2)
                data = Byte.parseByte(bits[1]);
            else
                data = 0;

        } catch (NumberFormatException nfe) {

            throw new IllegalArgumentException("Unable to convert id to integer and data to byte");

        }
    }

    private final Material block;
    private final byte data;

    public Material getBlock() {

        return block;

    }

    public byte getData() {

        return data;

    }

    public String toString() {

        return block + ":" + data;

    }

    public boolean equals(Object o) {

        return (o instanceof SerializableBlock && block == ((SerializableBlock) o).block && data == ((SerializableBlock) o).data);

    }
}