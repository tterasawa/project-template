/*-
 * Copyright (c) 2004-2015 GMO-RESEARCH Inc. All rights reserved.
 * Redistribution in source and binary forms, with or without
 * modification, is limited by contract. 
 */
package so.sabami.template.framework.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * リクエストパラメータをログ出力します
 * @author usr160056
 * @since 2015/02/20
 */
public class RequestLoggingInterceptor
        extends HandlerInterceptorAdapter {

    private final static Logger log = LoggerFactory.getLogger(RequestLoggingInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        StringBuilder sb = new StringBuilder("# BeforeRequest # ");
        sb.append(request.getRequestURI()+ " ");
        request.getParameterMap().entrySet().forEach(entry -> sb.append("[" + entry.getKey() + "=" + StringUtils.join(entry.getValue(), ",") + "]"));
        log.info(sb.toString());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        StringBuilder sb = new StringBuilder("# AfterRequest # ");
        sb.append(request.getRequestURI()+ " ");
        request.getParameterMap().entrySet()
        .forEach(entry -> sb.append("[" + entry.getKey() + "=" + StringUtils.join(entry.getValue(), ",") + "]"));
        log.info(sb.toString());
    }

}
