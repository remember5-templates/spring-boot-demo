package com.remember5.elasticsearch7.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDateTime;

// @formatter:off

/*

在MappingElasticsearchConverter使用元数据驱动的对象的映射文件。元数据取自可以注释的实体属性。
提供以下注释：

@Document：在类级别应用，以指示该类是映射到数据库的候选对象。最重要的属性是：
    indexName：用于存储此实体的索引的名称。它可以包含SpEL模板表达式，例如 "log-#{T(java.time.LocalDate).now().toString()}"
    createIndex：标记是否在存储库引导中创建索引。默认值为true。请参见使用相应的映射自动创建索引
    versionType：版本管理的配置。默认值为EXTERNAL。

@Id：在字段级别应用，以标记用于标识目的的字段。

@Transient：默认情况下，存储或检索文档时，所有字段都映射到文档，此注释不包括该字段。

@PersistenceConstructor：标记从数据库实例化对象时要使用的给定构造函数，甚至是受保护的程序包。构造函数参数按名称映射到检索到的Document中的键值。

@Field：在字段级别应用并定义字段的属性，大多数属性映射到各自的Elasticsearch映射定义（以下列表不完整，请查看注释Javadoc以获得完整参考）：
    name：字段名称，它将在Elasticsearch文档中表示，如果未设置，则使用Java字段名称。
    type：字段类型，可以是Text, Keyword, Long, Integer, Short, Byte, Double, Float, Half_Float, Scaled_Float, Date, Date_Nanos, Boolean, Binary, Integer_Range, Float_Range, Long_Range, Double_Range, Date_Range, Ip_Range, Object, Nested, Ip, TokenCount, Percolator, Flattened, Search_As_You_Type。请参阅Elasticsearch映射类型
    format和日期类型的pattern定义。必须为日期类型定义format
    store：标记是否将原始字段值存储在Elasticsearch中，默认值为false。
    analyzer，searchAnalyzer，normalizer用于指定自定义分析和正规化。

@GeoPoint：将字段标记为geo_point数据类型。如果字段是GeoPoint类的实例，则可以省略。

@ValueConverter：定义用于转换给定属性的类。与注册的 Spring 不同，Converter这仅转换带注释的属性，而不是给定类型的每个属性。

@Setting：注释定义不同的索引设置。以下参数可用：
    useServerConfiguration 不发送任何设置参数，因此 Elasticsearch 服务器配置确定它们。
    settingPath 是指定义必须在类路径中解析的设置的 JSON 文件
    shards要使用的分片数，默认为1
    replicas副本数，默认为1
    refreshIntervall, 默认为“1s”
    indexStoreType, 默认为"fs"

@Field(type=FieldType.Text, analyzer=“ik_max_word”) 表示该字段是一个文本，并作最大程度拆分，默认建立索引
@Field(type=FieldType.Text,index=false) 表示该字段是一个文本，不建立索引
    1) 支持分词，全文检索,支持模糊、精确查询,不支持聚合,排序操作
    2) text类型的最大支持的字符长度无限制,适合大字段存储；
    使用场景：
        存储全文搜索数据, 例如: 邮箱内容、地址、代码块、博客文章内容等。
        默认结合standard analyzer(标准解析器)对文本进行分词、倒排索引。
        默认结合标准分析器进行词命中、词频相关度打分。


@Field(type=FieldType.Date) 表示该字段是一个文本，日期类型，默认不建立索引
@Field(type=FieldType.Long) 表示该字段是一个长整型，默认建立索引
@Field(type=FieldType.Keyword) 表示该字段内容是一个文本并作为一个整体不可分，默认建立索引
    1) 不进行分词，直接索引,支持模糊、支持精确匹配，支持聚合、排序操作。
    2) keyword类型的最大支持的长度为——32766个UTF-8类型的字符,可以通过设置ignore_above指定自持字符长度，超过给定长度后的数据将不被索引，无法通过term精确匹配检索返回结果。
    使用场景：
        存储邮箱号码、url、name、title，手机号码、主机名、状态码、邮政编码、标签、年龄、性别等数据。
        用于筛选数据(例如: select * from x where status='open')、排序、聚合(统计)。
        直接将完整的文本保存到倒排索引中。

@Field(type=FieldType.Float) 表示该字段内容是一个浮点类型并作为一个整体不可分，默认建立索引
date 、float、long都是不能够被拆分的

*/

// @formatter:on



/**
 * @author Max_Qiu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "user")
@Setting(shards = 1, replicas = 0)
public class User {
    @Id
    private Integer id;

    @Field(type = FieldType.Keyword)
    private String name;

    @Field(type = FieldType.Integer)
    private Integer age;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String address;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    private LocalDateTime createTime;

    public User(Integer id) {
        this.id = id;
    }
}
