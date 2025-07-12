package com.remember5.elasticsearch7.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.sql.Timestamp;

/**
 * @author wangjiahao
 * @date 2022/5/27 00:06
 */
@Data
@Builder
@Document(indexName = "log_access")
@NoArgsConstructor
@AllArgsConstructor
public class LogAccessEntity {

    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private String vMethod;

    @Field(type = FieldType.Keyword)
    private String vUri;

    @Field(type = FieldType.Keyword)
    private String vIp;

    @Field(type = FieldType.Integer)
    private Integer iStatus;

    @Field(type = FieldType.Keyword)
    private String vType;

    @Field(type = FieldType.Keyword)
    private String vBrowser;

    @Field(type = FieldType.Boolean)
    private Boolean bSuccess;

    @Field(type = FieldType.Keyword)
    private String vApplication;

    @Field(type = FieldType.Keyword)
    private String vDataId;

    @Field(type = FieldType.Keyword)
    private String vAliasAtAppModule;

    @Field(type = FieldType.Keyword)
    private String vAliasAtAppModuleFunction;

    @Field(type = FieldType.Boolean)
    private Boolean bSkip;

    @Field(type = FieldType.Keyword)
    private String idAtAuthUser;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    private Timestamp tCreate;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String vBody;

    @Field(type = FieldType.Keyword)
    private String idAtAppModule;

    @Field(type = FieldType.Keyword)
    private String vDevice;


}
