/*-
 * Copyright (c) 2004-2015 GMO-RESEARCH Inc. All rights reserved.
 * Redistribution in source and binary forms, with or without
 * modification, is limited by contract. 
 */
package so.sabami.template.exception;

import org.springframework.core.NestedRuntimeException;

/**
 * @author usr160056
 * @since 2015/02/16
 */
public class DecryptException
        extends NestedRuntimeException {

    /** serialVersionUID */
    private static final long serialVersionUID = 8222197735836475821L;

    /**
     * コンストラクタ
     * @param cause 例外
     */
    public DecryptException(Throwable cause) {
        super(cause.getMessage(), cause);
    }

}
