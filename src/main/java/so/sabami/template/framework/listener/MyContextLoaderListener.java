/*-
 * Copyright (c) 2004-2014 GMO-RESEARCH Inc. All rights reserved.
 * Redistribution in source and binary forms, with or without
 * modification, is limited by contract. 
 */
package so.sabami.template.framework.listener;

import javax.servlet.ServletContextEvent;

import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoaderListener;

import ch.qos.logback.classic.LoggerContext;

/**
 * logbackのAsyncAppenderの停止処理を行うようcontextDestroyedをオーバーライドします
 * @author usr160056
 * @since 2014/12/09
 */
public class MyContextLoaderListener
        extends ContextLoaderListener {

    /**
     * {@inheritDoc}
     */
    @Override
    public void contextDestroyed(ServletContextEvent event) {
        LoggerContext loggerContext = (LoggerContext)LoggerFactory.getILoggerFactory();
        loggerContext.stop();
        super.contextDestroyed(event);
    }
}
