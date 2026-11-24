package com.saiful.dialme.core.common.logging

import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class RotatingLogFileTest {

    @get:Rule
    val tmp = TemporaryFolder()

    @Test
    fun append_thenRead_returnsText() {
        val log = RotatingLogFile(File(tmp.root, "logs/a.log"))
        log.append("hello")
        log.append("world")
        assertEquals("hello\nworld\n", log.read(1000))
    }

    @Test
    fun read_returnsOnlyTheEndWhenTooLong() {
        val log = RotatingLogFile(File(tmp.root, "a.log"))
        log.append("0123456789")
        assertEquals("789\n", log.read(4))
    }

    @Test
    fun read_missingFileGivesEmptyString() {
        assertEquals("", RotatingLogFile(File(tmp.root, "none.log")).read(100))
    }

    @Test
    fun append_rotatesWhenFileIsTooBig() {
        val file = File(tmp.root, "a.log")
        val log = RotatingLogFile(file, maxBytes = 20)
        log.append("x".repeat(30))
        log.append("fresh")
        assertEquals("fresh\n", log.read(1000))
        assertTrue(File(file.path + ".old").exists())
    }

    @Test
    fun clear_removesFiles() {
        val file = File(tmp.root, "a.log")
        val log = RotatingLogFile(file)
        log.append("data")
        log.clear()
        assertFalse(file.exists())
        assertEquals("", log.read(100))
    }
}
