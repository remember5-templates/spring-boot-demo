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

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author wangjiahao
 * @date 2025/7/13 00:11
 */
public class DateConverter extends AbstractBeanField<Object, Date> {

    // 支持多种日期格式
    private static final String[] DATE_FORMATS = {
            "yyyy/MM/dd HH:mm",    // 2017/12/20 16:21
            "yyyy-MM-dd HH:mm:ss",  // 2017-12-20 16:21:00
            "MM/dd/yyyy HH:mm",     // 12/20/2017 16:21
            "dd/MM/yyyy HH:mm"      // 20/12/2017 16:21
    };

    @Override
    protected Object convert(String value) throws CsvDataTypeMismatchException {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        // 尝试所有支持的格式
        for (String format : DATE_FORMATS) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                sdf.setLenient(false); // 严格模式
                return sdf.parse(value.trim());
            } catch (ParseException e) {
                // 尝试下一个格式
            }
        }

        // 所有格式都失败时抛出异常
        throw new CsvDataTypeMismatchException(value, Date.class,
                "无法解析日期: " + value + "。支持的格式: " + String.join(", ", DATE_FORMATS));
    }
}
