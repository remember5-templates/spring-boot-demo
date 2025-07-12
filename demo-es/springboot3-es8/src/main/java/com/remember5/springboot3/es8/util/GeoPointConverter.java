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
import com.remember5.springboot3.es8.entity.BurgerKingMerchant;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

/**
 * @author wangjiahao
 * @date 2025/7/13 00:03
 */
public class GeoPointConverter extends AbstractBeanField<BurgerKingMerchant, GeoPoint> {
    @Override
    protected Object convert(String value) throws CsvDataTypeMismatchException {
        if (value == null || value.isEmpty()) return null;

        try {
            String[] parts = value.split(",");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid GeoPoint format: " + value);
            }
            return new GeoPoint(
                    Double.parseDouble(parts[0]),
                    Double.parseDouble(parts[1])
            );
        } catch (Exception e) {
            throw new CsvDataTypeMismatchException(value, GeoPoint.class, e.getMessage());
        }
    }
}
