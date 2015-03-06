/*-
 * Copyright (c) 2004-2014 GMO-RESEARCH Inc. All rights reserved.
 * Redistribution in source and binary forms, with or without
 * modification, is limited by contract. 
 */
package so.sabami.template.listener;

import javax.servlet.ServletContextEvent;

import mockit.Expectations;
import mockit.Mocked;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import so.sabami.template.framework.listener.MyContextLoaderListener;
import ch.qos.logback.classic.LoggerContext;

/**
 * @author usr160056
 * @since 2014/12/09
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-test-config.xml" })
public class MyContextLoaderListenerTest {

    @Mocked
    private LoggerContext loggerContext;

    @Mocked
    private LoggerFactory loggerFactory;

    private MyContextLoaderListener target;

    /**
     * 初期処理
     */
    @Before
    public void setup() {
        target = new MyContextLoaderListener();
    }

    /**
     * AsyncAppenderの停止処理確認
     * @throws Exception 例外
     */
    @Test
    public void contextDestroyedで非同期ログの停止処理が呼び出されること() throws Exception {
        new Expectations() {
            {
                loggerContext.stop(); times = 1;
            }
        };
        target.contextDestroyed(new ServletContextEvent(new MockServletContext()));
    }

}
