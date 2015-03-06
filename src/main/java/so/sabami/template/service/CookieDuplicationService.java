/*-
 * Copyright (c) 2004-2015 GMO-RESEARCH Inc. All rights reserved.
 * Redistribution in source and binary forms, with or without
 * modification, is limited by contract. 
 */
package so.sabami.template.service;

/**
 * cookie重複管理サービス
 * @author usr160056
 * @since 2015/02/13
 */
public interface CookieDuplicationService {

    /**
     * cookie重複管理
     * @param cookieGrid cookieに保持されていたgrid
     * @param jnMonitorId 統一ID
     * @return 管理後のgrid
     */
    String manageCookie(String cookieGrid, Long jnMonitorId);

    /**
     * cookie管理データ削除
     * @param cookieGrid cookieに保持されていたgrid
     * @param jnMonitorId 統一ID
     */
    void remove(String cookieGrid, Long jnMonitorId);

    /**
     * cookie管理データ削除用URL生成
     * @param jnMonitorId 統一ID
     */
    String createCookieRemoveUrl(Long jnMonitorId);

}
