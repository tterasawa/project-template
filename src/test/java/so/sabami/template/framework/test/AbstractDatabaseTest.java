package so.sabami.template.framework.test;

import org.apache.ibatis.session.SqlSessionFactory;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-test-config.xml" })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionDbUnitTestExecutionListener.class })
public abstract class AbstractDatabaseTest {

    @Autowired
    protected SqlSessionFactory sqlSessionFactory;

    /**
     * springでトランザクション管理されたIDatabaseConnectionを返却します
     * @return IDatabaseConnection テストケース内のトランザクション管理された接続
     * @throws DatabaseUnitException
     */
    protected IDatabaseConnection getIDatabaseConnection() throws DatabaseUnitException {
        return new DatabaseConnection(DataSourceUtils.getConnection(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource()));
    }

}
