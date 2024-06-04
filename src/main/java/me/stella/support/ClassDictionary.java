package me.stella.support;

public enum ClassDictionary {

    v1_12_R1(
            "ItemStack",
            "MojangsonParser",
            "NBTTagCompound",
            "NBTBase",
            "NBTNumber",
            "NBTCompressedStreamTools",
            "NBTTagList",
            "RegionFile",
            "RegionFileCache"
    ),
    v1_13_R1(
            "ItemStack",
                    "MojangsonParser",
                    "NBTTagCompound",
                    "NBTBase",
                    "NBTNumber",
                    "NBTCompressedStreamTools",
                    "NBTTagList",
                    "RegionFile",
                    "RegionFileCache"
    ),
    v1_14_R1(
            "ItemStack",
            "MojangsonParser",
            "NBTTagCompound",
            "NBTBase",
            "NBTNumber",
            "NBTCompressedStreamTools",
            "NBTTagList",
            "RegionFile",
            "RegionFileCache"
    ),
    v1_15_R1(
            "ItemStack",
            "MojangsonParser",
            "NBTTagCompound",
            "NBTBase",
            "NBTNumber",
            "NBTCompressedStreamTools",
            "NBTTagList",
            "RegionFile",
            "RegionFileCache"
    ),
    v1_16_R3(
            "ItemStack",
            "MojangsonParser",
            "NBTTagCompound",
            "NBTBase",
            "NBTNumber",
            "NBTCompressedStreamTools",
            "NBTTagList",
            "RegionFile",
            "RegionFileCache"
    ),
    v1_17_R1(
            "ItemStack",
            "MojangsonParser",
            "NBTTagCompound",
            "NBTBase",
            "NBTNumber",
            "NBTCompressedStreamTools",
            "NBTTagList",
            "RegionFile",
            "RegionFileCache"
    ),
    v1_18_R2(
            "ItemStack",
            "MojangsonParser",
            "NBTTagCompound",
            "NBTBase",
            "NBTNumber",
            "NBTCompressedStreamTools",
            "NBTTagList",
            "RegionFile",
            "RegionFileCache"
    ),
    v1_19_R1(
            "ItemStack",
            "MojangsonParser",
            "NBTTagCompound",
            "NBTBase",
            "NBTNumber",
            "NBTCompressedStreamTools",
            "NBTTagList",
            "RegionFile",
            "RegionFileCache"
    ),
    v1_20_R1(
            "ItemStack",
            "MojangsonParser",
            "NBTTagCompound",
            "NBTBase",
            "NBTNumber",
            "NBTCompressedStreamTools",
            "NBTTagList",
            "RegionFile",
            "RegionFileCache"
    );



    private final String[] classes;
    ClassDictionary(String... className) {
        this.classes = className;
    }

    public String[] getClasses() {
        return this.classes;
    }

}
