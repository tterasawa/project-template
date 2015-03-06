package so.sabami.template.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;

import mockit.Mocked;
import mockit.NonStrictExpectations;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import so.sabami.template.exception.DecryptException;
import so.sabami.template.exception.EncryptException;
import so.sabami.template.service.Blowfish;

/**
 * Blowfish テストケース
 * @author usr160056
 * @since 2015/02/16
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-test-config.xml" })
public class BlowfishTest {

    /**
     * 暗号化テスト
     * @throws Exception 例外
     */
    @Test
    public void 暗号化出来る() throws Exception {
        assertThat(Blowfish.encrypt(Blowfish.KEY, "terasawa"), is("45c77f42931d0d1e863779c2ccdbc9ef"));
    }

    /**
     * 複合テスト
     * @throws Exception 例外
     */
    @Test
    public void 複合出来る() throws Exception {
        assertThat(Blowfish.decrypt(Blowfish.KEY, "45c77f42931d0d1e863779c2ccdbc9ef"), is("terasawa"));
    }

    /**
     * 暗号化テスト
     * @throws Exception 例外
     */
    @SuppressWarnings("static-access")
    @Test(expected = EncryptException.class)
    public void 暗号化に失敗した場合はEncryptExceptionがthrowされる(final @Mocked Cipher cipher) throws Exception {
        new NonStrictExpectations() {{
            cipher.getInstance("Blowfish"); result = new NoSuchAlgorithmException();
        }};
        Blowfish.encrypt(Blowfish.KEY, "terasawa");
    }

    /**
     * 暗号化テスト
     * @throws Exception 例外
     */
    @SuppressWarnings("static-access")
    @Test(expected = DecryptException.class)
    public void 複合に失敗した場合はDecryptExceptionがthrowされる(final @Mocked Cipher cipher) throws Exception {
        new NonStrictExpectations() {{
            cipher.getInstance("Blowfish"); result = new NoSuchAlgorithmException();
        }};
        Blowfish.decrypt(Blowfish.KEY, "dummy");
    }

}
