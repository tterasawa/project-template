package so.sabami.template.web;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.servlet.http.Cookie;

import mockit.Mocked;
import mockit.StrictExpectations;

import org.junit.Before;
import org.junit.Test;

import so.sabami.template.framework.test.AbstractControllerTest;
import so.sabami.template.service.CookieDuplicationServiceImpl;

/**
 * CookieDuplicationController テストケース
 * @author usr160056
 * @since 2015/02/19
 */
public class CookieDuplicationControllerTest
        extends AbstractControllerTest {

    /**
     * 初期処理
     */
    @Before
    public void setup() {
        super.setup();
    }

    @Test
    public void cookie無しでアクセスした場合にserviceの戻り値がcookieのgridにセットされる(final @Mocked CookieDuplicationServiceImpl service) throws Exception {
        new StrictExpectations() {{
            service.manageCookie("", 100000001L); result = "AAA";
        }};
        mockMvc.perform(get("/grid")
            .param("jn_monitor_id", "100000001"))
            .andExpect(status().isOk())
            .andExpect(cookie().path("grid", is("/")))
            .andExpect(cookie().value("grid", is("AAA")))
            .andExpect(cookie().maxAge("grid", 60 * 60 * 24 * 365))
            .andExpect(content().string(containsString("成功")));
    }

    @Test
    public void cookieのgridがserviceの戻り値と異なる場合にserviceの戻り値がcookieのgridにセットされる(final @Mocked CookieDuplicationServiceImpl service) throws Exception {
        new StrictExpectations() {{
            service.manageCookie("異なる値", 100000001L); result = "AAA";
        }};
        mockMvc.perform(get("/grid")
            .cookie(new Cookie("grid","異なる値"))
            .param("jn_monitor_id", "100000001"))
            .andExpect(status().isOk())
            .andExpect(cookie().path("grid", is("/")))
            .andExpect(cookie().value("grid", is("AAA")))
            .andExpect(cookie().maxAge("grid", 60 * 60 * 24 * 365))
            .andExpect(content().string(containsString("成功")));
    }

    @Test
    public void jn_monitor_idがnullの場合はエラーメッセージが返却される(final @Mocked CookieDuplicationServiceImpl service) throws Exception {
        mockMvc.perform(get("/grid"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("値が未入力です。")));
    }

    @Test
    public void jn_monitor_idが空の場合はエラーメッセージが返却される(final @Mocked CookieDuplicationServiceImpl service) throws Exception {
        mockMvc.perform(get("/grid")
            .param("jn_monitor_id", ""))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("値が未入力です。")));
    }

    @Test
    public void jn_monitor_idが数値でない場合はエラーメッセージが返却される() throws Exception {
        mockMvc.perform(get("/grid")
            .param("jn_monitor_id", "あ"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("jn_monitor_idは整数で入力してください。")));
    }

}
