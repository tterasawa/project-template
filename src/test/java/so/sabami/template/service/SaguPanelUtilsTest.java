package so.sabami.template.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import lombok.AllArgsConstructor;
import lombok.Data;

import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import so.sabami.template.service.SaguPanelUtils;

/**
 * aguPanelUtils テストケース
 * @author usr160056
 * @since 2014/12/04
 */
@RunWith(Theories.class)
public class SaguPanelUtilsTest {

    @DataPoints
    public static Fixture[] テストデータ() {
        /** "testCase", "monitor_id", "panel_type", "expected" */
        Fixture[] fixture = {
                new Fixture("monitor_idとpanel_typeからjn_monitor_idが取得できる", "1", "0999", "99900000001"),
                new Fixture("panel_typeの先頭のゼロは除去される", "99999999", "01", "199999999"),
                new Fixture("monitor_idが8桁未満の場合は8桁まで左ゼロ埋めされる", "1", "30", "3000000001"),
        };
        return fixture;
    }

    @Data
    @AllArgsConstructor
    static class Fixture {
        String testCase;
        String monitor_id;
        String panel_type;
        String expected;
    }

    @Theory
    public void convertToJnMonitorIDパターンテスト(Fixture fixture) {
        assertThat(SaguPanelUtils.convertToJnMonitorID(fixture.getMonitor_id(), fixture.getPanel_type()), is(fixture.getExpected()));
    }

}
