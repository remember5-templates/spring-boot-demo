package com.remember5.office.utils;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import com.itextpdf.kernel.pdf.PdfWriter;
//import com.spire.pdf.FileFormat;
//import com.spire.pdf.PdfDocument;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author wangjiahao
 * @date 2021/12/3
 */
public class PDFUtil {

    public PDFUtil() {
    }

    private volatile static Configuration configuration;

    static {
        if (configuration == null) {
            synchronized (PDFUtil.class) {
                if (configuration == null) {
                    configuration = new Configuration(Configuration.VERSION_2_3_28);
                }
            }
        }
    }

    /**
     * freemarker 引擎渲染 html
     *
     * @param dataMap      传入 html 模板的 Map 数据
     * @param templateName html 模板文件相对路径(相对于 resources路径,路径 + 文件名)
     *                     eg: "templates/pdf_export_demo.ftl"
     * @return
     */
    public static String freemarkerRender(Map<String, Object> dataMap, String templateName) {
        configuration.setClassForTemplateLoading(PDFUtil.class, "/");
        configuration.setDefaultEncoding("UTF-8");
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        try (Writer out = new StringWriter()) {
            configuration.setLogTemplateExceptions(false);
            configuration.setWrapUncheckedExceptions(true);
            Template template = configuration.getTemplate(templateName);
            template.process(dataMap, out);
            out.flush();
            return out.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 使用 iText 生成 PDF 文档
     *
     * @param html     html 模板文件字符串
     * @param fontFile 所需字体文件(相对路径+文件名)
     */
    public static byte[] createPDF(String html, String fontFile) {
        byte[] result = null;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfWriter pdfWriter = new PdfWriter(outputStream);

            DefaultFontProvider fontProvider = new DefaultFontProvider();
            fontProvider.addSystemFonts();
            fontProvider.addFont(fontFile);
            ConverterProperties converterProperties = new ConverterProperties();
            converterProperties.setFontProvider(fontProvider);

            HtmlConverter.convertToPdf(html, pdfWriter, converterProperties);
            result = outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @param html     html 模板文件字符串
     * @param fontFile 所需字体文件(相对路径+文件名)
     * @param savePath 保存路径
     * @return
     */
    public static void createPDF(String html, byte[] fontFile, String savePath) throws IOException {
        try (PdfWriter pdfWriter = new PdfWriter(savePath)) {
            DefaultFontProvider fontProvider = new DefaultFontProvider();
            fontProvider.addFont(fontFile);
            fontProvider.addSystemFonts();

            ConverterProperties converterProperties = new ConverterProperties();
            converterProperties.setFontProvider(fontProvider);

            HtmlConverter.convertToPdf(html, pdfWriter, converterProperties);
        }
    }

    /**
     * 使用Spire.PDF 来完成pdf2word
     *
     * @param pdfPath /
     * @param docPath /
     * @throws Exception /
     */
//    public static void pdf2word(String pdfPath, String docPath) {
//        //创建一个 PdfDocument 对象
//        PdfDocument doc = new PdfDocument();
//
//        //加载 PDF 文件
//        doc.loadFromFile(pdfPath);
//
//        //将PDF转换为Doc格式文件并保存
////        doc.saveToFile(docPath, FileFormat.DOC);
//
//        //将PDF转换为Docx格式文件并保存
//        doc.saveToFile(docPath, FileFormat.DOCX);
//        doc.close();
//    }


    public static void generatePdf2Image() {
        String savePath = "/Users/wangjiahao/Downloads/";
        int dpi = 96; // 设置渲染的 DPI 值

        try {
            PDDocument document = PDDocument.load(new File(savePath + "1695117787296.pdf"));
            PDFRenderer renderer = new PDFRenderer(document);


            int totalPages = document.getNumberOfPages();
            ArrayList<BufferedImage> images = new ArrayList<>();

            for (int i = 0; i < totalPages; i++) {
                BufferedImage image = renderer.renderImageWithDPI(i, 300); // 使用300 DPI进行渲染
                images.add(image);
            }

            document.close();

            int totalWidth = 0;
            int totalHeight = 0;

            for (BufferedImage image : images) {
                totalWidth = Math.max(totalWidth, image.getWidth());
                totalHeight += image.getHeight();
            }

            BufferedImage mergedImage = new BufferedImage(totalWidth, totalHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = mergedImage.createGraphics();
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, totalWidth, totalHeight);

            int currentHeight = 0;

            for (BufferedImage image : images) {
                g2d.drawImage(image, 0, currentHeight, image.getWidth(), image.getHeight(), null);
                currentHeight += image.getHeight();
            }


            // 保存为PNG文件
            ImageIO.write(mergedImage, "png", new File(savePath + "test.png"));
            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static int getPageHeightWithoutMargins(PDPage page) {
        // 计算页面高度（去掉页边距）
        return (int) (page.getCropBox().getHeight() - page.getCropBox().getLowerLeftY());
    }

    public static void main(String[] args) {
        generatePdf2Image();
    }

}
