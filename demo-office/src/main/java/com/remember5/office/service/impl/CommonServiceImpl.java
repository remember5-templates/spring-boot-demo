package com.remember5.office.service.impl;

import com.remember5.office.config.PDFExportConfig;
import com.remember5.office.service.CommonService;
import com.remember5.office.utils.PDFUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wangjiahao
 * @date 2021/12/3
 */
@Service("commonService")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CommonServiceImpl implements CommonService {

    private final PDFExportConfig pdfExportConfig;

    /**
     * PDF 文件导出
     *
     * @return
     */
    @Override
    public ResponseEntity<?> export() {
        HttpHeaders headers = new HttpHeaders();

        // 数据导出(PDF 格式)
        Map<String, Object> dataMap = new HashMap<>(16);
        dataMap.put("statisticalTime",new Date().toString());
        dataMap.put("imageUrl","http://118.25.95.207:9000/ahtc/2021-08-10/4dba6307-fde7-4c62-b825-ff921c932464.png");

        String htmlStr = PDFUtil.freemarkerRender(dataMap, pdfExportConfig.getEmployeeKpiFtl());
        byte[] pdfBytes = PDFUtil.createPDF(htmlStr, pdfExportConfig.getFontSimsun());
        if (pdfBytes != null && pdfBytes.length > 0) {
            String fileName = System.currentTimeMillis() + (int) (Math.random() * 90000 + 10000) + ".pdf";
            headers.setContentDispositionFormData("attachment", fileName);
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<byte[]>(pdfBytes, headers, HttpStatus.OK);
        }

        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        return new ResponseEntity<String>("{ \"code\" : \"404\", \"message\" : \"not found\" }",
                headers, HttpStatus.NOT_FOUND);
    }

}
