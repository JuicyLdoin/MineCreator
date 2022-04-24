package ua.ldoin.minecreator.utils.block;

import org.bukkit.Material;

public class SerializableBlock {

    private final Material block;
    private final byte data;

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

            Material block;

            try {

                block = Material.getMaterial(Integer.parseInt(bits[0]));

            } catch (Exception ignored) {

                block = Material.getMaterial(bits[0]);

            }

            this.block = block;

            if (bits.length == 2)
                data = Byte.parseByte(bits[1]);
            else
                data = 0;

        } catch (NumberFormatException nfe) {

            throw new IllegalArgumentException("Unable to convert id to integer and data to byte");

        }
    }

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