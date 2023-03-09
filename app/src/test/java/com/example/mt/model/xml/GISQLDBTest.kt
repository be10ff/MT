package com.example.mt.model.xml

import org.junit.Test

class GISQLDBTest {

    @Test
    fun getLevel() {

        val res1 = Math.min(Integer.max(8, 4), 14)
        val res2 = Math.min(Integer.max(16, 4), 14)
        val res3 = Math.min(Integer.max(2, 4), 14)
        assert(res1 == 8)
        assert(res2 == 14)
        assert(res3 == 4)
    }
}