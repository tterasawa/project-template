/*-
 * Copyright (c) 2004-2015 GMO-RESEARCH Inc. All rights reserved.
 * Redistribution in source and binary forms, with or without
 * modification, is limited by contract. 
 */
package so.sabami.template.persistence;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.session.RowBounds;
import org.springframework.data.domain.Pageable;

import so.sabami.template.domain.CookieDuplication;

/**
 * @author usr160056
 * @since 2015/02/13
 */
public interface CookieDuplicationMapper {

    @Select("select * from cookie_duplication")
    List<CookieDuplication> findAllByPage(@Param("page") Pageable page);

    @Select("SELECT * FROM cookie_duplication WHERE jn_monitor_id = #{jnMonitorId}")
    CookieDuplication getCookieDuplicationByJnMonitorId(long jnMonitorId);

    @Insert(
            "insert into cookie_duplication (jn_monitor_id, grid, created, modified) "
            + "values (#{jn_monitor_id}, #{grid}, now(), now())"
    )
    @SelectKey(before = false, keyProperty = "id", resultType = int.class, statement = { "select last_insert_id()" })
    int insert(CookieDuplication cookieDuplication);

    @Update("update cookie_duplication set grid = #{updateGrid}, modified = now() where grid = #{whereGrid}")
    int update(@Param("updateGrid") String updateGrid, @Param("whereGrid") String whereGrid);

    @Delete("delete from cookie_duplication where jn_monitor_id = #{jnMonitorId} and grid = #{grid}")
    int delete(@Param("jnMonitorId") Long jnMonitorId, @Param("grid") String grid);

    List<CookieDuplication> getCookieDuplicationByGrid(@Param("grid") String grid, RowBounds rb);

}
