package com.example.mt.model.gi

import org.junit.Test

class LonLatFormatTest {
    var deg : Number = 0
    var min : Number = 0
    var sec : Number = 0
    @Test
    fun getDegrees() {
        var res = LonLatFormat.DD_dddd(55.99999999)

        deg = res._degrees
        min = res._minutes
        sec = res._seconds

        deg = res.getDegrees()
        min = res.getMinutes()
        sec = res.getSeconds()

        res.setDegrees(34.50000000f)
        res.setMinutes(6)
        res.setSeconds(60)

        deg = res._degrees
        min = res._minutes
        sec = res._seconds

        deg = res.getDegrees()
        min = res.getMinutes()
        sec = res.getSeconds()

        val r= res


    }
    @Test
    fun getMin() {
        var res = LonLatFormat.DD_MMmm(55.99999999)

        deg = res._degrees
        min = res._minutes
        sec = res._seconds

        deg = res.getDegrees()
        min = res.getMinutes()
        sec = res.getSeconds()

        res.setDegrees(34)
        res.setMinutes(6.666666f)
        res.setSeconds(60)

        deg = res._degrees
        min = res._minutes
        sec = res._seconds

        deg = res.getDegrees()
        min = res.getMinutes()
        sec = res.getSeconds()

        val r= res


    }

    @Test
    fun getSec() {
        var res = LonLatFormat.DD_MM_SSss(55.99999999)

        deg = res._degrees
        min = res._minutes
        sec = res._seconds

        deg = res.getDegrees()
        min = res.getMinutes()
        sec = res.getSeconds()

        res.setDegrees(34)
        res.setMinutes(16)
//        res.setSeconds(30)

        deg = res._degrees
        min = res._minutes
        sec = res._seconds

        deg = res.getDegrees()
        min = res.getMinutes()
        sec = res.getSeconds()

        res.setDegrees(18)
        res.setMinutes(43)
        res.setSeconds(30)

        deg = res._degrees
        min = res._minutes
        sec = res._seconds

        deg = res.getDegrees()
        min = res.getMinutes()
        sec = res.getSeconds()
        val r= res


    }

    @Test
    fun getdms() {
        var res = LonLatFormat.DD_dddd(55.5050)

        deg = res.getDegrees()
        min = res.getMinutes()
        sec = res.getSeconds()
        val sr = res.toString()

        val res1 = LonLatFormat.DD_MMmm(res.internal)


        deg = res1.getDegrees()
        min = res1.getMinutes()
        sec = res1.getSeconds()
        val sr1 = res1.toString()

        val res2 = LonLatFormat.DD_MM_SSss(res.internal)


        deg = res2.getDegrees()
        min = res2.getMinutes()
        sec = res2.getSeconds()
        val sr2 = res2.toString()

        var res3 = LonLatFormat.DD_dddd(res1.internal)
        val sr3 = res3.toString()
        var res4 = LonLatFormat.DD_dddd(res2.internal)
        val sr4 = res4.toString()



        val res10 = LonLatFormat.DD_MM_SSss(0.0)
        res10.setDegrees(10)
        res10.setMinutes(20)
        res10.setSeconds(30)
        val sr10 = res10.toString()

        var res11 = LonLatFormat.DD_dddd(res10.internal)
        val sr11 = res11.toString()

        var res12 = LonLatFormat.DD_MM_SSss(res11.internal)
        val sr12 = res12.toString()
        val tttt = sr4
    }
}