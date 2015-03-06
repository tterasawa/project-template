/*-
 * Copyright (c) 2004-2015 GMO-RESEARCH Inc. All rights reserved.
 * Redistribution in source and binary forms, with or without
 * modification, is limited by contract. 
 */
package so.sabami.template.framework.pagination;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.property.PropertyTokenizer;
import org.apache.ibatis.scripting.xmltags.ForEachSqlNode;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.util.StringUtils;


/**
 * ページング用インターセプター
 * org.springframework.data.domain.Pageableがパラメータ指定されている場合に order by, limit, offset を付加します
 * <ul>
 *   <li>myBatisのRowBoundsが指定されている場合Pageableよりもそちらを優先します</li>
 *   <li>sqlにorder by句が存在している場合Pageableよりもそちらを優先します</li>
 * </ul>
 * @author usr160056
 * @since 2015/02/22
 */
@Intercepts({ @Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class }) })
public class PaginationInterceptor
        implements Interceptor {

    private final static Logger log = LoggerFactory.getLogger(PaginationInterceptor.class);
    private static final ThreadLocal<Integer> PAGINATION_TOTAL = new ThreadLocal<Integer>();
    private static final ThreadLocal<Pageable> PAGE_REQUEST = new ThreadLocal<Pageable>();
    /** mapped statement parameter index. */
    private static final int MAPPED_STATEMENT_INDEX = 0;
    /** sql id , in the mapper xml file. */
    private static String _sql_regex = "[*]";

    /** clear total context. */
    public static void clean() {
        PAGE_REQUEST.remove();
        PAGINATION_TOTAL.remove();
    }

    public static int getPaginationTotal() {
        if (PAGINATION_TOTAL.get() == null) {
            return 0;
        }
        return PAGINATION_TOTAL.get();
    }

    public static Pageable getPageable() {
        return PAGE_REQUEST.get();
    }

    private Pageable getDefaultPageable() {
        return new PageRequest(0, 10);
    }

    public static final String ORDER_REGEX = "order\\s+by";

    public static boolean containOrder(String sql) {
        return containRegex(sql, ORDER_REGEX);
    }

    public static boolean containRegex(String sql, String regex) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(sql);
        return matcher.find();
    }

    private static String sortSql(String sql, Pageable page) {

        boolean order = containOrder(sql);
        // sqlにorder by が指定されている場合はsql記載のソート条件固定としPageableのソートは無視する
        if (order) {
            return sql;
        }

        Sort sort = page.getSort();
        if (sort == null) {
            return sql;
        }

        List<String> orders = new ArrayList<>();
        for (Order o : sort) {
            orders.add(o.getProperty() + " " + o.getDirection());
        }

        String orderdSql = " order by " + StringUtils.collectionToCommaDelimitedString(orders);
        return sql + orderdSql;
    }

    /** {@inheritDoc} */
    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        MappedStatement mappedStatement = (MappedStatement)invocation.getArgs()[0];
        Object parameter = invocation.getArgs()[1];
        BoundSql boundSql = mappedStatement.getBoundSql(parameter);
        String originalSql = boundSql.getSql().trim();
        RowBounds rowBounds = (RowBounds)invocation.getArgs()[2];
        Object parameterObject = boundSql.getParameterObject();

        // ページングパラメータを取得
        boolean interceptor = mappedStatement.getId().matches(_sql_regex);
        final Pageable page = interceptor
                ? PagingParametersFinder.instance.findPageable(parameter)
                : getDefaultPageable();
        PAGE_REQUEST.set(page);

        if (interceptor) {
            StringBuffer countSql = new StringBuffer();
            countSql.append("select count(1) from (").append(originalSql).append(") tmp");
            Connection connection = mappedStatement.getConfiguration().getEnvironment().getDataSource().getConnection();
            PreparedStatement countStmt = connection.prepareStatement(countSql.toString());
            BoundSql countBS = new BoundSql(mappedStatement.getConfiguration(), countSql.toString(), boundSql.getParameterMappings(), parameterObject);
            setParameters(countStmt, mappedStatement, countBS, parameterObject);
            ResultSet rs = countStmt.executeQuery();
            int totalPage = 0;
            if (rs.next()) {
                totalPage = rs.getInt(1);
            }
            PAGINATION_TOTAL.set(totalPage);
            rs.close();
            countStmt.close();
            connection.close();

            if (rowBounds == null || rowBounds == RowBounds.DEFAULT) {
                rowBounds = new RowBounds(page.getPageSize() * page.getPageNumber(), page.getPageSize());
            }

            String sortedSql = sortSql(originalSql, page);

            String pagesql = getLimitString(sortedSql, rowBounds.getOffset(), rowBounds.getLimit());
            invocation.getArgs()[2] = new RowBounds(RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);
            BoundSql newBoundSql = new BoundSql(mappedStatement.getConfiguration(), pagesql, boundSql.getParameterMappings(), boundSql.getParameterObject());
            MappedStatement newMs = copyFromMappedStatement(mappedStatement, new BoundSqlSqlSource(newBoundSql));

            invocation.getArgs()[0] = newMs;

        }

        return invocation.proceed();
    }

    /** {@inheritDoc} */
    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    /** {@inheritDoc} */
    @Override
    public void setProperties(Properties properties) {
        String sql_regex = properties.getProperty("sqlRegex");
        if (!StringUtils.isEmpty(sql_regex)) {
            _sql_regex = sql_regex;
        }
        clean();
    }

    @SuppressWarnings("unchecked")
    private void setParameters(PreparedStatement ps, MappedStatement mappedStatement, BoundSql boundSql, Object parameterObject) throws SQLException {
        ErrorContext.instance().activity("setting parameters").object(mappedStatement.getParameterMap().getId());
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        if (parameterMappings != null) {
            Configuration configuration = mappedStatement.getConfiguration();
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            MetaObject metaObject = parameterObject == null ? null : configuration.newMetaObject(parameterObject);
            for (int i = 0; i < parameterMappings.size(); i++) {
                ParameterMapping parameterMapping = parameterMappings.get(i);
                if (parameterMapping.getMode() != ParameterMode.OUT) {
                    Object value;
                    String propertyName = parameterMapping.getProperty();
                    PropertyTokenizer prop = new PropertyTokenizer(propertyName);
                    if (parameterObject == null) {
                        value = null;
                    } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                        value = parameterObject;
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        value = boundSql.getAdditionalParameter(propertyName);
                    } else if (propertyName.startsWith(ForEachSqlNode.ITEM_PREFIX) && boundSql.hasAdditionalParameter(prop.getName())) {
                        value = boundSql.getAdditionalParameter(prop.getName());
                        if (value != null) {
                            value = configuration.newMetaObject(value).getValue(propertyName.substring(prop.getName().length()));
                        }
                    } else {
                        value = metaObject == null ? null : metaObject.getValue(propertyName);
                    }
                    @SuppressWarnings("rawtypes")
                    TypeHandler typeHandler = parameterMapping.getTypeHandler();
                    if (typeHandler == null) {
                        throw new ExecutorException("There was no TypeHandler found for parameter " + propertyName + " of statement " + mappedStatement.getId());
                    }
                    typeHandler.setParameter(ps, i + 1, value, parameterMapping.getJdbcType());
                }
            }
        }
    }

    private static MappedStatement copyFromMappedStatement(MappedStatement ms, SqlSource newSqlSource) {
        MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(), ms.getId(), newSqlSource, ms.getSqlCommandType());
        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        String[] keyProperties = ms.getKeyProperties();
        builder.keyProperty(keyProperties == null ? null : StringUtils.arrayToCommaDelimitedString(keyProperties));
        builder.timeout(ms.getTimeout());
        builder.parameterMap(ms.getParameterMap());
        builder.resultMaps(ms.getResultMaps());
        builder.resultSetType(ms.getResultSetType());
        builder.cache(ms.getCache());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        builder.useCache(ms.isUseCache());
        return builder.build();
    }

    public String getLimitString(String sql, int offset, int limit) {
        StringBuilder stringBuilder = new StringBuilder(sql);
        stringBuilder.append(" limit ");
        if (offset > 0) {
            stringBuilder.append(offset).append(",").append(limit);
        } else {
            stringBuilder.append(limit);
        }
        return stringBuilder.toString();
    }

    public static class BoundSqlSqlSource implements SqlSource {
        BoundSql boundSql;

        public BoundSqlSqlSource(BoundSql boundSql) {
            this.boundSql = boundSql;
        }

        public BoundSql getBoundSql(Object parameterObject) {
            return boundSql;
        }
    }

}
