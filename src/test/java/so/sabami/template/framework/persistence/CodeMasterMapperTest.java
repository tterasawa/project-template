package so.sabami.template.framework.persistence;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.BadSqlGrammarException;

import so.sabami.template.framework.domain.CodeMasterParam;
import so.sabami.template.framework.domain.LabelValue;
import so.sabami.template.framework.test.AbstractDatabaseTest;

import com.github.springtestdbunit.annotation.DatabaseSetup;

/**
 * CodeMasterMapper テストケース
 * @author usr160056
 * @since 2015/03/06
 */
@RunWith(Enclosed.class)
public class CodeMasterMapperTest {

    @DatabaseSetup("/fixture/CodeMasterMapperTest/base.xml")
    public static class マスター検索のテスト
            extends AbstractDatabaseTest {

        @Autowired
        private CodeMasterMapper target;

        @Test(expected = BadSqlGrammarException.class)
        public void テーブル名を指定しないと例外が発生する() throws Exception {
            CodeMasterParam param = new CodeMasterParam();
            target.selectMaster(param);
        }

        @Test
        public void テーブル名だけを指定すると全件検索が出来る() throws Exception {
            CodeMasterParam param = new CodeMasterParam();
            param.setTableName("code_master");
            List<LabelValue> data = target.selectMaster(param);
            assertThat(data.size(), is(3));
            assertThat(data.get(0).getValue(), is("01"));
            assertThat(data.get(0).getLabel(), is("ラベル01"));
            assertThat(data.get(1).getValue(), is("02"));
            assertThat(data.get(1).getLabel(), is("ラベル02"));
            assertThat(data.get(2).getValue(), is("03"));
            assertThat(data.get(2).getLabel(), is("ラベル03"));
        }

        @Test
        public void where指定して条件検索が出来る() throws Exception {
            CodeMasterParam param = new CodeMasterParam();
            param.setTableName("code_master");
            param.setWhere("item_cd = '02' AND item_nm like '%ル02%'");
            List<LabelValue> data = target.selectMaster(param);
            assertThat(data.size(), is(1));
            assertThat(data.get(0).getValue(), is("02"));
            assertThat(data.get(0).getLabel(), is("ラベル02"));
        }

        @Test
        public void orderBy指定してソートが出来る() throws Exception {
            CodeMasterParam param = new CodeMasterParam();
            param.setTableName("code_master");
            param.setOrderBy("id desc");
            List<LabelValue> data = target.selectMaster(param);
            assertThat(data.size(), is(3));
            assertThat(data.get(0).getValue(), is("03"));
            assertThat(data.get(0).getLabel(), is("ラベル03"));
            assertThat(data.get(1).getValue(), is("02"));
            assertThat(data.get(1).getLabel(), is("ラベル02"));
            assertThat(data.get(2).getValue(), is("01"));
            assertThat(data.get(2).getLabel(), is("ラベル01"));
        }

        @Test
        public void languageを指定して多言語ラベルが取得できる() throws Exception {
            CodeMasterParam param = new CodeMasterParam();
            param.setTableName("code_master");
            param.setWhere("item_cd = '01'");
            param.setLanguage("en");
            List<LabelValue> data = target.selectMaster(param);
            assertThat(data.size(), is(1));
            assertThat(data.get(0).getValue(), is("01"));
            assertThat(data.get(0).getLabel(), is("label01"));
        }

        @Test
        public void 標準カラム名からカラム名を変更して検索出来る() throws Exception {
            CodeMasterParam param = new CodeMasterParam();
            param.setTableName("code_master");
            param.setWhere("item_cd = '01'");
            param.setValueColumn("item_code");
            param.setLabelColumn("item_name");
            List<LabelValue> data = target.selectMaster(param);
            assertThat(data.size(), is(1));
            assertThat(data.get(0).getValue(), is("001"));
            assertThat(data.get(0).getLabel(), is("ラベル変更01"));
        }

        @Test
        public void カラム名と言語を両方していして検索出来る() throws Exception {
            CodeMasterParam param = new CodeMasterParam();
            param.setTableName("code_master");
            param.setWhere("item_cd = '01'");
            param.setValueColumn("item_code");
            param.setLabelColumn("item_name");
            param.setLanguage("en");
            List<LabelValue> data = target.selectMaster(param);
            assertThat(data.size(), is(1));
            assertThat(data.get(0).getValue(), is("001"));
            assertThat(data.get(0).getLabel(), is("labelChange01"));
        }

    }

}
