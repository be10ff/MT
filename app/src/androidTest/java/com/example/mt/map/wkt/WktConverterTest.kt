package com.example.mt.map.wkt

import com.example.mt.model.gi.GILonLat

import org.junit.Test
import org.simpleframework.xml.Serializer
import org.simpleframework.xml.convert.Registry
import org.simpleframework.xml.convert.RegistryStrategy
import org.simpleframework.xml.core.Persister
import java.io.ByteArrayOutputStream
import java.util.regex.Pattern

class WktConverterTest {

    @Test
    fun read() {
        val registry = Registry()
        val strategy = RegistryStrategy(registry)

        val serializer: Serializer = Persister(strategy)
        registry.bind(WktPoint::class.java, WktConverter::class.java)
        val input = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" +
                "<Geometries>\n" +
                "  <POINT id=\"2\" Geometry=\"POINT(40.168931480746586 55.137109324761425)\" DateTime=\"06_02_07_20_12\" Name=\"Спас-клепики\" />\n" +
                "  <TRACK id=\"-1\" Geometry=\"/storage/sdcard0/Razan06_10_20_03.track\" Project=\"Razan\" Description=\"06_10_20_03_15\" />" +
                "</Geometries>"
        serializer.read(WktLayer::class.java, input)
            .also {
                val res = it
            }


    }

    @Test
    fun reread() {
        val registry = Registry()
        val strategy = RegistryStrategy(registry)

        val serializer: Serializer = Persister(strategy)
        registry.bind(WktLayer::class.java, WktConverter::class.java)
        val input = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" +
                "<Geometries>\n" +
                "  <POINT id=\"2\" Geometry=\"POINT(40.168931480746586 55.137109324761425)\" DateTime=\"06_02_07_20_12\" Name=\"Спас-клепики\" />\n" +
                "  <TRACK id=\"-1\" Geometry=\"/storage/sdcard0/Razan06_10_20_03.track\" Project=\"Razan\" Description=\"06_10_20_03_15\" />" +
                "</Geometries>"
        val readed = serializer.read(WktLayer::class.java, input)
            .also {
                val res = it
            }

        val stream = ByteArrayOutputStream()
        val result = serializer.write(readed, stream)
        val rrr = stream.toString()

        "<Geometries>\n" +
                "   <POINT Geometry=\"POINT(40.168931480746586 55.137109324761425)\" id=\"2\" DateTime=\"06_02_07_20_12\" Name=\"Спас-клепики\"/>\n" +
                "   <TRACK Geometry=\"/storage/sdcard0/Razan06_10_20_03.track\" id=\"-1\" Project=\"Razan\" Description=\"06_10_20_03_15\"/>\n" +
                "</Geometries>"
    }

    @Test
    fun write() {
    }

    @Test
    fun parse() {
        val input = "POINT(37.351518720345865 55.82888710585451)"

        val pattern = Pattern.compile("\\d+\\.\\d+")
        val matcher = pattern.matcher(input)
        val res = buildList<Double> {
            while (matcher.find()) {
                val result = matcher.toMatchResult().group()
                result.toDoubleOrNull()?.let { add(it) }
            }
        }.takeIf {
            it.size == 2
        }?.let {
            GILonLat(it[0], it[1])
        }



        println(res)

    }

    @Test
    fun trim() {
        val input = "POINT(37.351518720345865 55.82888710585451)"
            .apply {
                substring(indexOfFirst { it == '(' } + 1, indexOfFirst { it == ')' })
                    .split(' ')
                    .mapNotNull {
                        it.toDoubleOrNull()
                    }
                    .takeIf {
                        it.size == 2
                    }
                    ?.let {
                        GILonLat(it[0], it[1])
                    }
            }

    }
}