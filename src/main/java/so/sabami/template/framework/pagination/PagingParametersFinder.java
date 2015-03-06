/*
 * Copyright © 2012-2013 mumu@yfyang. All Rights Reserved.
 */

package so.sabami.template.framework.pagination;


import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;

/**
 * <p>
 * Paging <code>PaginationCriteria</code> finds.
 * </p>
 *
 * @author poplar.yfyang
 * @version 1.0 2012-12-09 7:22 PM
 * @since JDK 1.5
 */
public enum  PagingParametersFinder {

    instance;

    /**
     * The search parameters by use of interim storage of results.
     */
    private final Map<Object, String> search_map = new HashMap<>();

    /**
     * private constructor
     */
    private PagingParametersFinder() {
    }


    /**
     * from the formulation of the objects found in the paging parameters object.
     *
     * @param object object.
     * @return paging parameters.
     */
    public Pageable findPageable(Object object) {
        if (object == null) {
            return null;
        }
        try {
            return findPageableFromObject(object);
        } finally {
            //cleanup query the value of the temporary Map.
            search_map.clear();
        }
    }

    /**
     * In the object to find whether contains <code>Pageable</code> objects.
     *
     * @param object parameter object.
     * @return PaginationCriteria
     */
    private Pageable findPageableFromObject(Object object) {

        if (search_map.containsKey(object)) {
            return null;
        }
        //object class
        Class<?> obj_class = object.getClass();
        Pageable pc;
        //primitive
        if (isPrimitiveType(obj_class)) {
            pc = null;
        } else if (object instanceof Pageable) {
            pc = (Pageable) object;
        } else if (object instanceof Map) {
            pc = findPageableFromMap((Map) object);
        } else if (object instanceof Collection) {
            pc = findPageableFromCollection((Collection) object);
        } else if (obj_class.isArray()) {
            pc = findPageableFromArray(object);
        } else {
            pc = null;
//            BeanMap map = new BeanMap(object);
//            return findPageableFromMap(map);
        }


        search_map.put(object, StringUtils.EMPTY);
        return pc;
    }

    /**
     * In the array to find whether it contains the <code>Pageable</code> object.
     *
     * @param array the array.
     * @return PageQuery
     */
    private Pageable findPageableFromArray(Object array) {
        if (search_map.containsKey(array)) {
            return null;
        }

        Object object;
        Pageable pc;
        for (int i = 0; i < Array.getLength(array); i++) {
            object = Array.get(array, i);
            pc = findPageableFromObject(object);
            if (pc != null) {
                search_map.put(array, StringUtils.EMPTY);
                return pc;
            }
        }
        search_map.put(array, StringUtils.EMPTY);
        return null;
    }

    /**
     * In the Collection to find whether contains <code>Pageable</code> objects.
     *
     * @param collection parameter collection.
     * @return PageQuery
     */
    private Pageable findPageableFromCollection(Collection collection) {
        if (search_map.containsKey(collection)) {
            return null;
        }
        Pageable pc;

        for (Object e : collection) {
            pc = findPageableFromObject(e);
            if (pc != null) {
                search_map.put(collection, StringUtils.EMPTY);
                return pc;
            }
        }

        search_map.put(collection, StringUtils.EMPTY);
        return null;
    }

    /**
     * In the Map to find whether contains <code>Pageable</code> objects.
     *
     * @param map parameter map.
     * @return Pageable
     */
    private Pageable findPageableFromMap(Map map) {
        if (search_map.containsKey(map)) {
            return null;
        }

        Pageable pc;
        for (Object value : map.values()) {
            pc = findPageableFromObject(value);
            if (pc != null) {
                search_map.put(map, StringUtils.EMPTY);
                return pc;
            }
        }

        search_map.put(map, StringUtils.EMPTY);
        return null;
    }

    /**
     * プリミティブ型の判定をします。Stringクラスもプリミティブ型に含まれています
     * @param clazz 判定したいタイプ
     * @return clazzにプリミティブ型もしくはそのラッパークラスを指定した場合は<code>true</code>を返します。</br>
     * nullを含むその他のクラスを指定した場合は<code>false</code>を返します。
     */
    public static boolean isPrimitiveType(Class clazz) {
        return clazz != null && (clazz.isPrimitive() || clazz.equals(Long.class) || clazz.equals(Integer.class)
                || clazz.equals(Short.class) || clazz.equals(Byte.class) || clazz.equals(Double.class)
                || clazz.equals(Float.class) || clazz.equals(Boolean.class) || clazz.equals(Character.class) || clazz.equals(String.class));

    }
}
