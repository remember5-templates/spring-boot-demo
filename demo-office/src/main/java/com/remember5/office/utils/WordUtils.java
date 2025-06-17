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
package com.remember5.office.utils;


import com.deepoove.poi.XWPFTemplate;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.BaseFont;
import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.ooxml.POIXMLDocument;
import org.apache.poi.ooxml.extractor.POIXMLTextExtractor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

/**
 * word常用工具类
 *
 * @author wangjiahao
 * @date 2023/6/2 16:50
 */
public class WordUtils {


    /**
     * 写入word 填充内容，使用{{key}} 就可以填充
     * 在使用生产环境的时候，如果获取resources下的文件请使用以下代码
     * 需要注入 Autowired private ResourceLoader resourceLoader
     * Resource resource = resourceLoader.getResource("classpath:template/template.docx")
     * resource.getInputStream()
     * </p>
     *
     * @param inputStream /
     * @param data        拼写的数据 Map<String,String> 必须是这种格式
     * @param outFileUrl  /
     * @throws IOException /
     */
    public static void doWriter(InputStream inputStream, Map<String, String> data, String outFileUrl) throws IOException {
        // 写入
        XWPFTemplate template = XWPFTemplate.compile(inputStream).render(data);
        // 输出流
        template.writeToFile(outFileUrl);
        template.close();
    }

    /**
     * word转pdf
     *
     * @param docFile 文档路径
     * @param pdfFile pdf结果路径
     * @throws Exception /
     */
    public static void word2pdf(String docFile, String pdfFile) {
        PdfOptions options = PdfOptions.create();
        //在Linux docker服务器中支持中文
        options.fontProvider((familyName, encoding, size, style, color) -> {
            try {
                // 使用iTextAsian.jar中的字体 需要引入itext-asian
//                BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
                // 使用资源字体(ClassPath)
                // BaseFont bfChinese = BaseFont.createFont("/SIMYOU.TTF", BaseFont.IDENTITY_H,BaseFont.NOT_EMBEDDED);
                // 使用其他的字体
                 BaseFont bfChinese = BaseFont.createFont("/Users/wangjiahao/Downloads/SimSun.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
                Font fontChinese = new Font(bfChinese, size, style, color);
                if (familyName != null) {
                    fontChinese.setFamily(familyName);
                }
                return fontChinese;
            } catch (Exception e) {
                return null;
            }
        });

        try (XWPFDocument document = new XWPFDocument(Files.newInputStream(Paths.get(docFile)));
             OutputStream outputStream = Files.newOutputStream(Paths.get(pdfFile))) {
            PdfConverter.getInstance().convert(document, outputStream, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String readDocsContent(String filePath) {
        String buffer = "";
        try {
            if (filePath.endsWith(".doc")) {
                InputStream is = new FileInputStream(filePath);
                WordExtractor ex = new WordExtractor(is);
                buffer = ex.getText();
                ex.close();
            } else if (filePath.endsWith("docx")) {
                OPCPackage opcPackage = POIXMLDocument.openPackage(filePath);
                POIXMLTextExtractor extractor = new XWPFWordExtractor(opcPackage);
                buffer = extractor.getText();
                extractor.close();
            } else {
                System.out.println("此文件不是word文件！");
            }
            System.err.println(buffer);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer;
    }

    /**
     * 读取doc的文件内容
     *
     * @param file doc文件
     * @return content
     */
    public static String readDocContent(File file) {
        String buffer = "";
        try {
            InputStream is = Files.newInputStream(file.toPath());
            WordExtractor ex = new WordExtractor(is);
            buffer = ex.getText();
            ex.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer;
    }

    /**
     * 读取docx的内容
     *
     * @param file docx文件
     * @return content
     */
    public static String readDocxContent(File file) {
        String buffer = "";
        try {
            OPCPackage opcPackage = OPCPackage.open(file);
            POIXMLTextExtractor extractor = new XWPFWordExtractor(opcPackage);
            buffer = extractor.getText();
            extractor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer;
    }


}
