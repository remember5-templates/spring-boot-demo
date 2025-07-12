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
package com.remember5.demo.springboot3.es7.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.io.Serializable;

/**
 * 预付卡信息
 *
 * @author wangjiahao
 * @date 2025/7/11 15:50
 */
@Data
@Document(indexName = "goods_info")
@Setting(shards = 1, replicas = 0)
@Accessors(chain = true)
@NoArgsConstructor
public class ESGoodsInfo implements Serializable {

    private static final long serialVersionUID=1L;


    @Id
    private Integer id;

    public ESGoodsInfo(Integer id) {
        this.id = id;
    }

}
