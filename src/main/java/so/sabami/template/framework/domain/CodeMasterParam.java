/*-
 * Copyright (c) 2004-2015 GMO-RESEARCH Inc. All rights reserved.
 * Redistribution in source and binary forms, with or without
 * modification, is limited by contract. 
 */
package so.sabami.template.framework.domain;

import lombok.Data;

/**
 * @author usr160056
 * @since 2015/03/06
 */
@Data
public class CodeMasterParam {
    /** tableName is required */
    private String tableName;
    private String valueColumn;
    private String labelColumn;
    private String language;
    private String where;
    private String orderBy;
}
