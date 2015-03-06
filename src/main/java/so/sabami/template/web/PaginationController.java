/*-
 * Copyright (c) 2004-2014 GMO-RESEARCH Inc. All rights reserved.
 * Redistribution in source and binary forms, with or without
 * modification, is limited by contract. 
 */
package so.sabami.template.web;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import so.sabami.template.domain.CookieDuplication;
import so.sabami.template.framework.pagination.PageWrapper;
import so.sabami.template.persistence.CookieDuplicationMapper;

/**
 * Cookieによる重複管理を行うコントローラ
 * @author usr160056
 * @since 2015/02/17
 */
@Controller
public class PaginationController {

    private static final Logger log = LoggerFactory.getLogger(PaginationController.class);

    @Autowired
    private CookieDuplicationMapper cookieDuplicationMapper;

    @Autowired
    private MessageSource messageSource;

    @RequestMapping(value = "/paging", method = RequestMethod.GET, produces="text/html; charset=UTF-8")
    public String paging(Model model, @PageableDefault(size=10) Pageable pageable) {
        PageWrapper<CookieDuplication> page = new PageWrapper<CookieDuplication>(cookieDuplicationMapper.findAllByPage(pageable), "/paging");
        model.addAttribute("page", page);
        List<CookieDuplication> cookies = page.getContent();
        model.addAttribute("cookies", cookies);
        return "page";
    }

}

