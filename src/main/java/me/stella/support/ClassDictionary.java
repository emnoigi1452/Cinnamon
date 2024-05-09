package me.stella.support;

public enum ClassDictionary {

    v1_12_R1(
            "ItemStack",
            "NBTTagCompound",
            "NBTBase",
            "NBTCompressedStreamTools",
            "NNBTTagCompound",
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
