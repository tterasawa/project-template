/*-
 * Copyright (c) 2004-2015 GMO-RESEARCH Inc. All rights reserved.
 * Redistribution in source and binary forms, with or without
 * modification, is limited by contract. 
 */
package so.sabami.template.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.dbunit.dataset.ITable;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import so.sabami.template.framework.test.AbstractDatabaseTest;
import so.sabami.template.service.CookieDuplicationService;

import com.github.springtestdbunit.annotation.DatabaseSetup;

/**
 * @author usr160056
 * @since 2015/02/13
 */
@RunWith(Enclosed.class)
public class CookieDuplicationServiceTest {

    @Transactional
    @DatabaseSetup("/fixture/CookieDuplicationServiceTest/empty.xml")
    public static class DBにパラメータのjn_monitor_idが存在しない
            extends AbstractDatabaseTest {

        @Autowired
        private CookieDuplicationService target;

        @Test
        public void gridが空の場合新規にgridが新規に発行される() throws Exception {
            String grid = target.manageCookie("", 100000001L);
            assertThat(grid, is(notNullValue()));

            ITable actual = getIDatabaseConnection().createDataSet().getTable("cookie_duplication");
            assertThat(actual.getRowCount(), is(1));
            assertThat(String.valueOf(actual.getValue(0, "jn_monitor_id")), is("100000001"));
            assertThat(String.valueOf(actual.getValue(0, "grid")), is(grid));
        }

        @Test
        public void gridが空でない場合_引数のgridがDBに登録されて返却される() throws Exception {
            String grid = target.manageCookie("XXX", 100000001L);
            assertThat(grid, is("XXX"));

            ITable actual = getIDatabaseConnection().createDataSet().getTable("cookie_duplication");
            assertThat(actual.getRowCount(), is(1));
            assertThat(String.valueOf(actual.getValue(0, "jn_monitor_id")), is("100000001"));
            assertThat(String.valueOf(actual.getValue(0, "grid")), is("XXX"));
        }
    }

    @Transactional
    @DatabaseSetup("/fixture/CookieDuplicationServiceTest/base.xml")
    public static class DBにパラメータのjn_monitor_idが存在する
            extends AbstractDatabaseTest {

        @Autowired
        private CookieDuplicationService target;

        @Test
        public void gridが空の場合はDBから取得したgridが返却される() throws Exception {
            String grid = target.manageCookie("", 100000001L);
            assertThat(grid, is("AAA"));
        }

        @Test
        public void gridがDBから取得した値と同じ場合はDBから取得したgridが返却される() throws Exception {
            String grid = target.manageCookie("AAA", 100000001L);
            assertThat(grid, is("AAA"));
        }

        @Test
        public void gridがDBから取得した値と異なる場合はDBの値がDBから取得したgridが返却され_DBのgridの値が返却されたgridに更新される() throws Exception {
            String grid = target.manageCookie("BBB", 100000001L);
            assertThat(grid, is("AAA"));

            ITable actual = getIDatabaseConnection().createDataSet().getTable("cookie_duplication");
            assertThat(actual.getRowCount(), is(2));
            assertThat(String.valueOf(actual.getValue(0, "jn_monitor_id")), is("100000001"));
            assertThat(String.valueOf(actual.getValue(0, "grid")), is("AAA"));
            assertThat(String.valueOf(actual.getValue(1, "jn_monitor_id")), is("100000002"));
            assertThat(String.valueOf(actual.getValue(1, "grid")), is("AAA")); // BBBだったがAAAに更新されている
        }

    }

}
