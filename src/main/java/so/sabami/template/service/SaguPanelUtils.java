/*-
 * Copyright (c) 2004-2014 GMO-RESEARCH Inc. All rights reserved.
 * Redistribution in source and binary forms, with or without
 * modification, is limited by contract. 
 */
package so.sabami.template.service;

import org.apache.commons.lang3.StringUtils;

/**
 * 検証用アプリなのでsynergyに依存したくないため必要なメソッドだけコピーしています
 * @author usr160056
 * @since 2014/12/04
 */
public class SaguPanelUtils {

    /**
     * モニターIDとパネル種別からjn_monitor_idに変換します.<br>
     *
     * @param monitor_id - モニターID
     * @param panel_type - パネルタイプ
     * @return jn_monitor_id
     */
    public static String convertToJnMonitorID(String monitor_id, String panel_type) {
        // パネルタイプの先頭の 0 は除きます.
        String pre_string = StringUtils.removeStart(panel_type, "0");
        String mon_string = StringUtils.leftPad(monitor_id, 8, "0");
        return pre_string + mon_string;
    }

}
