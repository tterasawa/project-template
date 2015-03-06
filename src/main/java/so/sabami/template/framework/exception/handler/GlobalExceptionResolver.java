/*-
 * Copyright (c) 2004-2015 GMO-RESEARCH Inc. All rights reserved.
 * Redistribution in source and binary forms, with or without
 * modification, is limited by contract. 
 */
package so.sabami.template.framework.exception.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author usr160056
 * @since 2015/02/20
 */
public class GlobalExceptionResolver
        implements HandlerExceptionResolver {

    private final static Logger log = org.slf4j.LoggerFactory.getLogger(GlobalExceptionResolver.class);

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        log.error("例外をキャッチしました。", ex);
        ModelAndView mav = new ModelAndView();
        mav.addObject("message", "予期せぬエラーが発生しました。 詳細：【" + ex + "】");
        mav.setViewName("error");
        return mav;
    }

}
