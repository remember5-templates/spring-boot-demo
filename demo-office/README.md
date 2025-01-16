1. FLTL模板引擎生成PDF
2. PDF拼接签名图片
3. WORD内容填充
4. WORD转换PDF
5. PDF转换WORD



# 使用freemarker生成html

在pom文件中添加以下依赖

```xml

<dependencies>
    <!-- freemarker生成 html -->
    <dependency>
        <groupId>org.freemarker</groupId>
        <artifactId>freemarker</artifactId>
        <version>${freemarker.version}</version>
    </dependency>
    <!--    iText, html2pdf    -->
    <!-- https://mvnrepository.com/artifact/com.itextpdf/html2pdf -->
    <dependency>
        <groupId>com.itextpdf</groupId>
        <artifactId>html2pdf</artifactId>
        <version>${html2pdf.version}</version>
    </dependency>
    <!--    iText, font    -->
    <!-- https://mvnrepository.com/artifact/com.itextpdf/font-asian -->
    <dependency>
        <groupId>com.itextpdf</groupId>
        <artifactId>font-asian</artifactId>
        <version>${font-asian.version}</version>
    </dependency>
</dependencies>
```

`application.yml`添加配置

```yaml
spring:
  freemarker:
    # 禁用模板缓存
    cache: false
    # 编码格式
    charset: UTF-8
    # freemarker模板后缀 默认是 .ftl
    suffix: .ftl
    # 是否为此技术启用MVC视图分辨率。
    enabled: true
    # Content-Type值
    content-type: text/html
    # #模板加载路径 按需配置 ,默认路径是 classpath:/templates/
    template-loader-path: classpath:/templates/

## pdf export config 这个可以写成配置，也可以写成static的属性
pdfExport:
  # 注意字体
  fontSimsun: static/fonts/simsun.ttc
  # 模版位置
  employeeKpiFtl: templates/pdf_export_employee_kpi.ftl
```

查看工具类 `PDFUtil#freemarkerRender`


# iText html2PDF

查看工具类 `PDFUtil#createPDF`

# poi 读取excel、word
读取word和填充查看`com/remember5/office/poi/PoiTest.java:52`
读取excel 有很多种方式，poi暂时有冲突未解决



# 使用Spire.PDF 来完成pdf2word
查看`com/remember5/office/utils/PDFUtil.java:117`
