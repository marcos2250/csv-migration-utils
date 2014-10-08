package m3;

import marcos2250.csvmigrationutils.misc.CSVUtils;

import org.junit.Assert;
import org.junit.Test;

public class CSVUtilsTest {

    @Test
    public void testSplitModelo() {

        String testStr = //
        "\"355553,\"1\",\"ABABABABA\",211,0,0.00,135000.00,\"2003-10-31\",\"2007-03-29\",,21,355872,,,1,,,,,,,,,,,,";

        String[] strings = CSVUtils.split(testStr, ",");

        Assert.assertEquals(27, strings.length);

    }

    @Test
    public void testSplitTerminandoComVirgula() {

        String testStr = "a,,,b,,,";

        String[] strings = CSVUtils.split(testStr, ",");

        Assert.assertEquals(7, strings.length);

    }

    @Test
    public void testVirgula() {

        String testStr = "aa aa aa,\"bb, bb bb\",ccc,ddd,, ,  ,\"eee\",, \"fff\"";

        String[] strings = CSVUtils.split(testStr, ",");

        Assert.assertEquals(10, strings.length);

        Assert.assertEquals("aa aa aa", strings[0]);
        Assert.assertEquals("\"bb, bb bb\"", strings[1]);
        Assert.assertEquals("ccc", strings[2]);
        Assert.assertEquals("ddd", strings[3]);
        Assert.assertEquals("", strings[4]);
        Assert.assertEquals(" ", strings[5]);
        Assert.assertEquals("  ", strings[6]);
        Assert.assertEquals("\"eee\"", strings[7]);
        Assert.assertEquals("", strings[8]);
        Assert.assertEquals(" \"fff\"", strings[9]);

    }

    @Test
    public void splitComPontoEVirgula() {

        String testStr = "aa aa aa;\"bb, bb ; bb\";ccc;ddd;; ;  ;\"eeee\";;;";

        String[] strings = CSVUtils.split(testStr, ";");

        Assert.assertEquals(11, strings.length);

        Assert.assertEquals("aa aa aa", strings[0]);
        Assert.assertEquals("\"bb, bb ; bb\"", strings[1]);
        Assert.assertEquals("ccc", strings[2]);
        Assert.assertEquals("ddd", strings[3]);
        Assert.assertEquals("", strings[4]);
        Assert.assertEquals(" ", strings[5]);
        Assert.assertEquals("  ", strings[6]);
        Assert.assertEquals("\"eeee\"", strings[7]);
        Assert.assertEquals("", strings[8]);
        Assert.assertEquals("", strings[9]);
        Assert.assertEquals(" ", strings[10]);

    }
}
