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
package com.remember5.springboot3.es8.util;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;

/**
 * @author wangjiahao
 * @date 2025/7/12 23:42
 */
public class EnhancedCsvLoader {

    // 注册自定义转换器
    private static final ConversionService conversionService = createConversionService();

    // 字段映射缓存
    private static final Map<Class<?>, Map<String, Function<String, Object>>> customMappers = new HashMap<>();

    // 字段拼接规则缓存
    private static final Map<Class<?>, Map<String, List<String>>> fieldConcatenationRules = new HashMap<>();

    private static ConversionService createConversionService() {
        DefaultConversionService service = new DefaultConversionService();
        // 添加常用类型转换
        service.addConverter(String.class, Date.class, value -> new Date(Long.parseLong(value)));
        service.addConverter(String.class, LocalDateTime.class, value -> LocalDateTime.parse(value));
        service.addConverter(String.class, Boolean.class, value -> "1".equals(value) || "true".equalsIgnoreCase(value));
        return service;
    }

    /**
     * 注册自定义字段映射器
     * @param type 实体类型
     * @param fieldName 字段名
     * @param mapper 转换函数
     */
    public static <T> void registerCustomMapper(Class<T> type, String fieldName, Function<String, Object> mapper) {
        customMappers.computeIfAbsent(type, k -> new HashMap<>()).put(fieldName, mapper);
    }

    /**
     * 注册字段拼接规则
     * @param type 实体类型
     * @param targetField 目标字段
     * @param sourceFields 源字段列表
     */
    public static <T> void registerConcatenationRule(Class<T> type, String targetField, List<String> sourceFields) {
        fieldConcatenationRules.computeIfAbsent(type, k -> new HashMap<>()).put(targetField, sourceFields);
    }

    /**
     * 加载CSV数据并进行增强处理
     */
    public static <T> List<T> loadCsvData(String filePath, Class<T> type) {
        try (Reader reader = new FileReader(filePath)) {
            // 1. 基础CSV解析
            HeaderColumnNameMappingStrategy<T> strategy = new HeaderColumnNameMappingStrategy<>();
            strategy.setType(type);

            CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(reader)
                    .withMappingStrategy(strategy)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            List<T> result = csvToBean.parse();

            // 2. 应用自定义转换和拼接
            return processEntities(result, type);
        } catch (Exception e) {
            throw new RuntimeException("CSV加载失败: " + filePath, e);
        }
    }

    private static <T> List<T> processEntities(List<T> entities, Class<T> type) {
        List<T> processed = new ArrayList<>();

        for (T entity : entities) {
            try {
                // 应用自定义字段映射
                applyCustomMappings(entity, type);

                // 应用字段拼接
                applyFieldConcatenation(entity, type);

                processed.add(entity);
            } catch (Exception e) {
                throw new RuntimeException("处理实体时出错", e);
            }
        }

        return processed;
    }

    private static <T> void applyCustomMappings(T entity, Class<T> type)
            throws IllegalAccessException, NoSuchFieldException {

        Map<String, Function<String, Object>> mappers = customMappers.get(type);
        if (mappers == null) return;

        for (Map.Entry<String, Function<String, Object>> entry : mappers.entrySet()) {
            String fieldName = entry.getKey();
            Function<String, Object> mapper = entry.getValue();

            Field field = getField(type, fieldName);
            field.setAccessible(true);

            // 获取原始值
            Object rawValue = field.get(entity);
            if (rawValue == null) continue;

            // 应用转换
            Object convertedValue = mapper.apply(rawValue.toString());
            field.set(entity, convertedValue);
        }
    }

    private static <T> void applyFieldConcatenation(T entity, Class<T> type)
            throws IllegalAccessException, NoSuchFieldException {

        Map<String, List<String>> rules = fieldConcatenationRules.get(type);
        if (rules == null) return;

        for (Map.Entry<String, List<String>> entry : rules.entrySet()) {
            String targetField = entry.getKey();
            List<String> sourceFields = entry.getValue();

            // 构建拼接值
            StringBuilder concatenated = new StringBuilder();
            for (String sourceField : sourceFields) {
                Field source = getField(type, sourceField);
                source.setAccessible(true);
                Object value = source.get(entity);
                if (value != null) {
                    concatenated.append(value).append(" ");
                }
            }

            // 设置目标字段
            Field target = getField(type, targetField);
            target.setAccessible(true);
            target.set(entity, concatenated.toString().trim());
        }
    }

    private static Field getField(Class<?> type, String fieldName) throws NoSuchFieldException {
        try {
            return type.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            // 尝试从父类查找
            Class<?> superClass = type.getSuperclass();
            if (superClass != null) {
                return superClass.getDeclaredField(fieldName);
            }
            throw e;
        }
    }

    /**
     * 自动类型转换（处理基本类型）
     */
    private static Object convertValue(Class<?> targetType, String value) {
        if (targetType == String.class) return value;
        if (conversionService.canConvert(String.class, targetType)) {
            return conversionService.convert(value, targetType);
        }
        throw new IllegalArgumentException("无法转换类型: " + targetType);
    }
}
