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
package com.remember5.office.itext;

import cn.hutool.core.io.IoUtil;
import com.remember5.office.config.PDFExportConfig;
import com.remember5.office.utils.DocToPdfConverter;
import com.remember5.office.utils.PDFUtil;
import com.remember5.office.utils.ResourceFileUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wangjiahao
 * @date 2023/6/14 10:48
 */
@SpringBootTest
public class Html2PdfTest {

    @Autowired
    PDFExportConfig pdfExportConfig;

    /**
     * 数据导出(PDF 格式)
     */
    @Test
    public void testHtml2Pdf() throws Exception {
        Map<String, Object> dataMap = new HashMap<>(16);
        dataMap.put("statisticalTime", new Date().toString());
        dataMap.put("imageUrl", "http://42.193.105.146:9000/nt1/mr.jpeg");
        dataMap.put("selfSummary", "这是我的自我总结文本");

        String htmlStr = PDFUtil.freemarkerRender(dataMap, pdfExportConfig.getEmployeeKpiFtl());
        String htmlFileName = "/Users/wangjiahao/Downloads/" + System.currentTimeMillis() + (int) (Math.random() * 90000 + 10000) + ".html";
        try (PrintWriter writer = new PrintWriter(htmlFileName, "UTF-8")) {
            writer.println(htmlStr);
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }


//        byte[] pdfBytes = FreemarkerUtils.createPDF(htmlStr, ResourceFileUtil.getAbsolutePath(ttc));
        String pdfFileName = "/Users/wangjiahao/Downloads/" + System.currentTimeMillis() + (int) (Math.random() * 90000 + 10000) + ".pdf";
        Resource resource = new ClassPathResource(pdfExportConfig.getFontSimsun());
        byte[] fileBytes = IoUtil.readBytes(resource.getInputStream());
        PDFUtil.createPDF(htmlStr, fileBytes, pdfFileName);
//        PDFUtil.createPDF(htmlStr, ResourceFileUtil.getAbsolutePath(pdfExportConfig.getFontSimsun()));
//        if (pdfBytes != null && pdfBytes.length > 0) {
//            final File file = FileUtil.writeBytes(pdfBytes, );
//        }
    }

    @Test
    public void testHtml2Doc() throws Exception {
        /**
         * 数据导出(PDF 格式)
         */
        Map<String, Object> dataMap = new HashMap<>(16);
        dataMap.put("statisticalTime", new Date().toString());
        dataMap.put("imageUrl", "http://118.25.95.207:9000/ahtc/2021-08-10/4dba6307-fde7-4c62-b825-ff921c932464.png");

        String pdfpath = "/Users/wangjiahao/Downloads/" + System.currentTimeMillis() + (int) (Math.random() * 90000 + 10000) + ".pdf";
        String docpath = "/Users/wangjiahao/Downloads/" + System.currentTimeMillis() + (int) (Math.random() * 90000 + 10000) + ".docx";

        String htmlStr = PDFUtil.freemarkerRender(dataMap, ResourceFileUtil.getAbsolutePath(pdfExportConfig.getEmployeeKpiFtl()));
        Resource resource = new ClassPathResource(pdfExportConfig.getFontSimsun());
        byte[] fileBytes = IoUtil.readBytes(resource.getInputStream());
        PDFUtil.createPDF(htmlStr, fileBytes, pdfpath);
//        PDFUtil.pdf2word(pdfpath, docpath);
//        PDFUtil.createPDF(htmlStr, ResourceFileUtil.getAbsolutePath(pdfExportConfig.getFontSimsun()));
//        if (pdfBytes != null && pdfBytes.length > 0) {
//            final File file = FileUtil.writeBytes(pdfBytes, );
//        }
    }

    @Test
    public void docs2Pdf() {
        String docFilePath = "/Users/wangjiahao/Downloads/templates.docx";
        String pdfFilePath = "/Users/wangjiahao/Downloads/templates.pdf";
        DocToPdfConverter.word2pdf(docFilePath,pdfFilePath);

    }




}
