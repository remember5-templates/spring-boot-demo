/**
 * Copyright [2022] [remember5]
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.remember5.es.es7.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Elasticsearch工具类
 * 主要用于处理数组字段的null值问题
 *
 * @author wangjiahao
 * @date 2025/7/12 14:40
 */
public class ElasticsearchUtils {

    /**
     * 确保List不为null，如果为null则返回空ArrayList
     *
     * @param list 原始List
     * @param <T>  泛型类型
     * @return 非null的List
     */
    public static <T> List<T> ensureNotNull(List<T> list) {
        return list != null ? list : new ArrayList<>();
    }

    /**
     * 安全地添加元素到List，确保List不为null
     *
     * @param list  原始List
     * @param item  要添加的元素
     * @param <T>   泛型类型
     * @return 添加元素后的List
     */
    public static <T> List<T> safeAdd(List<T> list, T item) {
        List<T> result = ensureNotNull(list);
        if (item != null) {
            result.add(item);
        }
        return result;
    }

    /**
     * 安全地添加多个元素到List，确保List不为null
     *
     * @param list  原始List
     * @param items 要添加的元素数组
     * @param <T>   泛型类型
     * @return 添加元素后的List
     */
    @SafeVarargs
    public static <T> List<T> safeAddAll(List<T> list, T... items) {
        List<T> result = ensureNotNull(list);
        if (items != null) {
            for (T item : items) {
                if (item != null) {
                    result.add(item);
                }
            }
        }
        return result;
    }

    /**
     * 安全地添加另一个List的元素，确保List不为null
     *
     * @param list      原始List
     * @param otherList 要添加的另一个List
     * @param <T>       泛型类型
     * @return 添加元素后的List
     */
    public static <T> List<T> safeAddAll(List<T> list, List<T> otherList) {
        List<T> result = ensureNotNull(list);
        if (otherList != null) {
            result.addAll(otherList);
        }
        return result;
    }

    /**
     * 检查List是否为空（null或size为0）
     *
     * @param list 要检查的List
     * @param <T>  泛型类型
     * @return 如果为空返回true，否则返回false
     */
    public static <T> boolean isEmpty(List<T> list) {
        return list == null || list.isEmpty();
    }

    /**
     * 检查List是否不为空（不为null且size大于0）
     *
     * @param list 要检查的List
     * @param <T>  泛型类型
     * @return 如果不为空返回true，否则返回false
     */
    public static <T> boolean isNotEmpty(List<T> list) {
        return !isEmpty(list);
    }
}
