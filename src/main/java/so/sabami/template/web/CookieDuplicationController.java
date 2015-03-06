/*-
 * Copyright (c) 2004-2015 GMO-RESEARCH Inc. All rights reserved.
 * Redistribution in source and binary forms, with or without
 * modification, is limited by contract. 
 */
package so.sabami.template.web;

import java.util.Locale;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import so.sabami.template.service.Blowfish;
import so.sabami.template.service.CookieDuplicationService;

/**
 * Cookieによる重複管理を行うコントローラ
 * @author usr160056
 * @since 2015/02/17
 */
@Controller
@Slf4j
public class CookieDuplicationController {

//    private static final Logger log = LoggerFactory.getLogger(CookieDuplicationController.class);

    @Autowired
    private CookieDuplicationService cookieDuplicationManageService;

    @Autowired
    private MessageSource messageSource;

    @ResponseBody
    @RequestMapping(value = "/grid", method = RequestMethod.GET, produces="text/plain;charset=UTF-8")
    public String cookieDuplicationManage(@Valid @ModelAttribute ModelRequest modelRequest, BindingResult result, @CookieValue(value="grid", defaultValue="") String cookieGrid, Model model, HttpServletResponse response,Pageable pageable) {

        log.info("cookie:grid [{}], jn_monitor_id [{}]", cookieGrid, modelRequest.getJn_monitor_id());
        if (result.hasErrors()) {
            return messageSource.getMessage(result.getFieldError("jn_monitor_id"), Locale.JAPAN);
        }
        System.out.println(pageable);
        String grid = cookieDuplicationManageService.manageCookie(cookieGrid, modelRequest.getJn_monitor_id());
        Cookie cookie = new Cookie("grid", grid);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 365); //有効期限1年
        response.addCookie(cookie);
        return "成功";
    }

    @ResponseBody
    @RequestMapping(value = "/clear/{jn_monitor_id}/{p}", method = RequestMethod.GET, produces="text/plain;charset=UTF-8")
    public String clearCookieDuplication(@PathVariable Long jn_monitor_id,@PathVariable String p, HttpServletResponse response) {
        String decriptParam = Blowfish.decrypt(Blowfish.KEY, p);
        /** params[0] = jn_monitor_id, params[1] = grid, params[2] = id */
        String[] params = decriptParam.split(":");
        if (jn_monitor_id.toString().equals(params[0])) {
            cookieDuplicationManageService.remove(params[1], jn_monitor_id);
            Cookie cookie = new Cookie("grid", params[1]);
            cookie.setPath("/");
            cookie.setMaxAge(0); //Cookie削除
            response.addCookie(cookie);
            return "Cookie削除しました";
        }
        return "Cookie削除しました(嘘)";
    }

    @ResponseBody
    @RequestMapping(value = "/generate/{jn_monitor_id}", method = RequestMethod.GET, produces="text/plain;charset=UTF-8")
    public String getCookieRemoveURL(@PathVariable Long jn_monitor_id) {
        String url = cookieDuplicationManageService.createCookieRemoveUrl(jn_monitor_id);
        return url;
    }

    @Data
    private class ModelRequest {
        @NotNull
        private Long jn_monitor_id;
    }

}
