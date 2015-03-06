package so.sabami.template.persistence;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import so.sabami.template.domain.CookieDuplication;
import so.sabami.template.framework.test.AbstractDatabaseTest;
import so.sabami.template.persistence.CookieDuplicationMapper;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;

/**
 * CookieDuplicationMapper テストケース
 * @author usr160056
 * @since 2015/02/13
 */
@RunWith(Enclosed.class)
public class CookieDuplicationMapperTest {

    @DatabaseSetup("/fixture/CookieDuplicationMapperTest/empty.xml")
    public static class データが空のテスト
            extends AbstractDatabaseTest {

        @Autowired
        private CookieDuplicationMapper target;

        @Test
        @ExpectedDatabase(
                value = "/fixture/CookieDuplicationMapperTest/expected.xml",
                table = "cookie_duplication",
                query = "select jn_monitor_id, grid from cookie_duplication")
        public void 登録が出来る() throws Exception {
            CookieDuplication data = new CookieDuplication();
            data.setJn_monitor_id(999999999L);
            data.setGrid("CCC");
            target.insert(data);
        }

    }

    @DatabaseSetup("/fixture/CookieDuplicationMapperTest/base.xml")
    public static class データが空じゃないテスト
            extends AbstractDatabaseTest {

        @Autowired
        private CookieDuplicationMapper target;

        @Test
        public void gridを指定してcookie_duplicationが取得できる() {
            RowBounds bounds = new RowBounds(0, 2);
            List<CookieDuplication> list = target.getCookieDuplicationByGrid("AAA", bounds);
            assertThat(list.size(), is(2));
            assertThat(list.get(0).getId(), is(1L));
            assertThat(list.get(0).getJn_monitor_id(), is(100000001L));
            assertThat(list.get(0).getGrid(), is("AAA"));
            assertThat(list.get(1).getId(), is(3L));
            assertThat(list.get(1).getJn_monitor_id(), is(200000001L));
            assertThat(list.get(1).getGrid(), is("AAA"));
        }

        @Test
        public void jn_monitor_idを指定してcookie_duplicationが取得できる() {
            CookieDuplication data = target.getCookieDuplicationByJnMonitorId(100000002);
            assertThat(data.getId(), is(2L));
            assertThat(data.getJn_monitor_id(), is(100000002L));
            assertThat(data.getGrid(), is("BBB"));
        }

        @Test
        @DatabaseSetup("/fixture/CookieDuplicationMapperTest/base.xml")
        public void 更新ができる() {
            // @TODO 実装する
        }

    }

    @DatabaseSetup("/fixture/CookieDuplicationMapperTest/page.xml")
    public static class ページングのテスト
            extends AbstractDatabaseTest {

        @Autowired
        private CookieDuplicationMapper target;

        @Test
        public void ページング() {
            Pageable page = new PageRequest(4, 5); // ページは0からなので4=5ページ目
            List<CookieDuplication> list = target.findAllByPage(page);
            assertThat(list.size(), is(5));
            // 全行やるのはしんどいので最初の行と最後の行をアサート
            assertThat(list.get(0).getId(), is(21L));
            assertThat(list.get(0).getJn_monitor_id(), is(300000018L));
            assertThat(list.get(0).getGrid(), is("CCC"));
            assertThat(list.get(4).getId(), is(25L));
            assertThat(list.get(4).getJn_monitor_id(), is(300000022L));
            assertThat(list.get(4).getGrid(), is("CCC"));
        }
    }

}
