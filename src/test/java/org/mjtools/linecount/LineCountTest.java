package org.mjtools.linecount;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mjtools.linecount.LineCount.DEF_EXT;
import static org.mjtools.linecount.LineCount.MISSING_PATH;
import static org.mjtools.linecount.LineCount.VERBOSE;
import static org.mjtools.linecount.LineCount.main;

class LineCountTest {
    private static final String PATH = "src/main/java/";

    private ByteArrayOutputStream outputStream;
    private LineCount lineCount;
    private File file;

    @BeforeEach
    void setup() {
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        lineCount = new LineCount(new String[]{PATH});
    }

    @Test
    void test_run() {
        lineCount.run();
        assertEquals(1, lineCount.counts().size());
    }

    @Test
    void test_checkArgs() {
        var args = new String[]{PATH, "-abc=ABC"};

        assertEquals(DEF_EXT, lineCount.fileExt());

        assertEquals("ABC", lineCount.checkArgs(args, "-abc", "XYZ"));

        args = new String[]{PATH};
        assertEquals("XYZ", lineCount.checkArgs(args, "-abc", "XYZ"));
    }

    @Test
    void test_verbose() {
        assertFalse(lineCount.verbose());

        lineCount = new LineCount(new String[]{PATH, VERBOSE});
        assertTrue(lineCount.verbose());
    }

    @Test
    void test_main() {
        main(new String[]{});
        assertEquals(MISSING_PATH, outputStream.toString().trim());
    }

}