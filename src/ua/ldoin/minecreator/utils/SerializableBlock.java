package ua.ldoin.minecreator.utils;

public class SerializableBlock {

    public SerializableBlock(int block) {

        this.block = block;
        this.data = 0;

    }

    public SerializableBlock(int block, byte data) {

        this.block = block;
        this.data = data;

    }

    public SerializableBlock(String self) {

        String[] bits = self.split(":");

        if (bits.length != 2)
            throw new IllegalArgumentException("String form of SerializableBlock didn't have exactly 2 numbers");

        try {

            block = Integer.parseInt(bits[0]);
            data = Byte.parseByte(bits[1]);

        } catch (NumberFormatException nfe) {

            throw new IllegalArgumentException("Unable to convert id to integer and data to byte");

        }
    }

    private final int block;
    private final byte data;

    public int getBlock() {

        return block;

    }

    public byte getData() {

        return data;

    }

    public String toString() {

        return block + ":" + data;

    }

    public boolean equals(Object o) {

        return (o instanceof SerializableBlock && block == ((SerializableBlock)o).block && data == ((SerializableBlock)o).data);

    }
}