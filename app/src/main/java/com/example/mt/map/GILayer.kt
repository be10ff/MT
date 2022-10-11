package com.example.mt.map

class GILayer(
    public val type: GILayerType,
    public val name: String,
    public val path: String
) {

    companion object {
        public fun createLayer(type: GILayerType, name: String, path: String): GILayer {
            return GILayer(type, name, path)
        }

        public val SQLTest = GILayer(GILayerType.SQL, "Worlds.sqlitedb", "Worlds.sqlitedb")
    }
}