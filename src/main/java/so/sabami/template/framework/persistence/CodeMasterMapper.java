/*-
 * Copyright (c) 2004-2015 GMO-RESEARCH Inc. All rights reserved.
 * Redistribution in source and binary forms, with or without
 * modification, is limited by contract. 
 */
package so.sabami.template.framework.persistence;

import java.util.List;

import so.sabami.template.framework.domain.CodeMasterParam;
import so.sabami.template.framework.domain.LabelValue;

/**
 * 汎用的に利用するコードマスター検索用マッパー
 * @author usr160056
 * @since 2015/02/13
 */
public interface CodeMasterMapper {

    List<LabelValue> selectMaster(CodeMasterParam param);

}
