import cn.hutool.core.io.unit.DataUnit;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import org.junit.Test;

public class DemoTest {

    @Test
    public void test01() {
        long start = System.currentTimeMillis();
        digest();
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }

    private void digest() {
        Digester md5 = new Digester(DigestAlgorithm.MD5);
        for (int i = 0; i < 1000000; i++) {


            String testStr = "ahhihiiiiiiiiiiiiiiiiiiiiiiiiiiibc";
            String digestHex = md5.digestHex(testStr);
        }
    }

    @Test
    public void test02() {
        Digester md5 = new Digester(DigestAlgorithm.SHA256);

        String testStr = "ahhihiiiiiiiiiiiiiiiiiiiiiiiiiiibc";
        String digestHex = md5.digestHex(testStr);
        System.out.println(digestHex);
    }
}