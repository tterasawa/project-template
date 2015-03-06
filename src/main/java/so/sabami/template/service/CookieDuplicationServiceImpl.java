/*-
 * Copyright (c) 2004-2015 GMO-RESEARCH Inc. All rights reserved.
 * Redistribution in source and binary forms, with or without
 * modification, is limited by contract. 
 */
package so.sabami.template.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import so.sabami.template.domain.CookieDuplication;
import so.sabami.template.persistence.CookieDuplicationMapper;

/**
 * @author usr160056
 * @since 2015/02/13
 */
@Service
public class CookieDuplicationServiceImpl
        implements CookieDuplicationService {

    @Autowired
    private CookieDuplicationMapper cookieDuplicationMapper;

    @Value("${remove.url}") 
    private String removeUrl;

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly=false, rollbackFor=Exception.class)
    public String manageCookie(String cookieGrid, Long jnMonitorId) {

        CookieDuplication cookieDuplication = cookieDuplicationMapper.getCookieDuplicationByJnMonitorId(jnMonitorId);

        if (cookieDuplication == null) {
            CookieDuplication data = new CookieDuplication();
            data.setJn_monitor_id(jnMonitorId);
            if (StringUtils.isEmpty(cookieGrid)) {
                String randomString = RandomStringUtils.randomNumeric(5);
                String newGrid = Blowfish.encrypt(Blowfish.KEY, String.valueOf(System.nanoTime()) + randomString);
                data.setGrid(newGrid);
            } else {
                data.setGrid(cookieGrid);
            }
            cookieDuplicationMapper.insert(data);
            return data.getGrid();
        } else {
            if (StringUtils.isNotEmpty(cookieGrid) && !cookieDuplication.getGrid().equals(cookieGrid)) {
                cookieDuplicationMapper.update(cookieDuplication.getGrid(), cookieGrid);
            }
        }
        return cookieDuplication.getGrid();
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly=false, rollbackFor=Exception.class)
    public void remove(String cookieGrid, Long jnMonitorId) {
        cookieDuplicationMapper.delete(jnMonitorId, cookieGrid);
    }

    /** {@inheritDoc} */
    @Override
    public String createCookieRemoveUrl(Long jnMonitorId) {
        CookieDuplication cookieDuplication = cookieDuplicationMapper.getCookieDuplicationByJnMonitorId(jnMonitorId);
        if (cookieDuplication == null) {
            throw new IllegalArgumentException("unauthorized access.");
        }
        String value = cookieDuplication.getJn_monitor_id() + ":" + cookieDuplication.getGrid() + ":" + cookieDuplication.getId();
        String url = removeUrl + "/" + jnMonitorId + "/" + Blowfish.encrypt(Blowfish.KEY, value);
        return url;
    }
}
