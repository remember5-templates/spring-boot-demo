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

import com.lowagie.text.Font;
import com.lowagie.text.pdf.BaseFont;
import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wangjiahao
 * @date 2025/6/17 18:15
 */
public class DocToPdfConverter {


    // Wingdings 特殊字符映射表 (¨ → 未选中框, ü → 选中框)
    private static final Map<Character, Character> WINGDINGS_MAP = new HashMap<>();
    static {
        WINGDINGS_MAP.put('¨', '\u2610'); // ☐
        WINGDINGS_MAP.put('ü', '\u2611'); // ☑
    }
    public static void word2pdf(String docFile, String pdfFile) {
        // 创建字体集（优先处理Wingdings）
        FontSet fontSet = new FontSet();

        // 1. 注册Wingdings字体（符号字体优先）
        fontSet.addFont("Wingdings", loadFont("/wingding.ttf"));

        // 2. 注册中文字体（SimSun）
        fontSet.addFont("SimSun", loadFont("/SimSun.ttf"));

        // 3. 设置默认字体（用于未匹配字体）
        BaseFont defaultFont = loadFont("/SimSun.ttf");
        PdfOptions options = PdfOptions.create();
        options.fontProvider((familyName, encoding, size, style, color) -> {
            try {
                // 优先匹配Wingdings
                if ("Wingdings".equalsIgnoreCase(familyName)) {
                    return new Font(fontSet.getFont("Wingdings"), size, style, color);
                }

                // 其次匹配中文字体
                BaseFont font = fontSet.getFont(familyName);
                if (font == null) {
                    font = defaultFont; // 回退到默认字体
                }
                return new Font(font, size, style, color);
            } catch (Exception e) {
                return new Font(defaultFont, size, style, color);
            }
        });
        try (XWPFDocument document = new XWPFDocument(Files.newInputStream(Paths.get(docFile)));
             OutputStream outputStream = Files.newOutputStream(Paths.get(pdfFile))) {

            // 关键：预处理Wingdings字符
            preprocessWingdingsChars(document);

            PdfConverter.getInstance().convert(document, outputStream, options);
        } catch (Exception e) {
            handleException(e);
        }
    }
    // 加载字体（兼容JAR内资源）
    private static BaseFont loadFont(String resourcePath) {
        try {
            // 优先从类路径加载（生产环境）
            if (DocToPdfConverter.class.getResource(resourcePath) != null) {
                return BaseFont.createFont(
                        resourcePath,
                        BaseFont.IDENTITY_H,
                        BaseFont.EMBEDDED
                );
            }
            // 开发环境备用路径
            else {
                return BaseFont.createFont(
                        "/Users/wangjiahao/Downloads" + resourcePath, // 根据实际调整
                        BaseFont.IDENTITY_H,
                        BaseFont.EMBEDDED
                );
            }
        } catch (Exception e) {
            throw new RuntimeException("字体加载失败: " + resourcePath, e);
        }
    }
    // 预处理Wingdings字符（关键步骤）
    private static void preprocessWingdingsChars(XWPFDocument document) {
        document.getParagraphs().forEach(para -> {
            para.getRuns().forEach(run -> {
                String text = run.getText(0);
                if (text != null && containsWingdingsChar(text)) {
                    // 替换特殊字符
                    StringBuilder sb = new StringBuilder();
                    for (char c : text.toCharArray()) {
                        sb.append(WINGDINGS_MAP.getOrDefault(c, c));
                    }
                    run.setText(sb.toString(), 0);
                }
            });
        });
    }
    // 检测Wingdings特殊字符
    private static boolean containsWingdingsChar(String text) {
        for (char c : text.toCharArray()) {
            if (WINGDINGS_MAP.containsKey(c)) return true;
        }
        return false;
    }
    // 字体集管理
    private static class FontSet {
        private final Map<String, BaseFont> fonts = new HashMap<>();
        public void addFont(String name, BaseFont font) {
            fonts.put(name.toLowerCase(), font);
        }
        public BaseFont getFont(String name) {
            return (name != null) ? fonts.get(name.toLowerCase()) : null;
        }
    }
    // 异常处理（见章节3）
    private static void handleException(Exception e) {
        // 实现见异常处理章节
    }
}
