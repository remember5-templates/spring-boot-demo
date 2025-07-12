package com.remember5.elasticsearch7.mapper;

import com.remember5.elasticsearch7.entity.LogAccessEntity;
import org.mapstruct.Mapper;

/**
 * @author wangjiahao
 * @date 2022/5/27 00:24
 */
@Mapper(componentModel = "spring")
public interface LogAccessMapper {
    LogAccessEntity toEsEntity(LogAccessEntity logAccessEntity);
}
