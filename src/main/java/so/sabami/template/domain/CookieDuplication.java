/*-
 * Copyright (c) 2004-2015 GMO-RESEARCH Inc. All rights reserved.
 * Redistribution in source and binary forms, with or without
 * modification, is limited by contract. 
 */
package so.sabami.template.domain;

import java.util.Date;

import lombok.Data;

/**
 * @author usr160056
 * @since 2015/02/13
 */
@Data
public class CookieDuplication {
    private long id;
    private String grid;
    private Long jn_monitor_id;
    private Date created;
    private Date modified;
}
