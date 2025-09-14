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
package com.remember5.junit.card;

/**
 * @author wangjiahao
 * @date 2025/9/14 11:09
 */
public class TableFormatter {
    /**
     * 计算字符串的实际显示宽度（中文字符算2个宽度）
     */
    public static int getDisplayWidth(String str) {
        if (str == null) return 0;
        int width = 0;
        for (char c : str.toCharArray()) {
            // 中文字符范围判断
            if (c >= '\u4e00' && c <= '\u9fff') {
                width += 2;
            } else {
                width += 1;
            }
        }
        return width;
    }

    /**
     * 左对齐字符串到指定宽度
     */
    public static String padLeft(String str, int width) {
        int displayWidth = getDisplayWidth(str);
        if (displayWidth >= width) return str;

        StringBuilder sb = new StringBuilder(str);
        for (int i = displayWidth; i < width; i++) {
            sb.append(' ');
        }
        return sb.toString();
    }

    /**
     * 右对齐字符串到指定宽度
     */
    public static String padRight(String str, int width) {
        int displayWidth = getDisplayWidth(str);
        if (displayWidth >= width) return str;

        StringBuilder sb = new StringBuilder();
        for (int i = displayWidth; i < width; i++) {
            sb.append(' ');
        }
        sb.append(str);
        return sb.toString();
    }

    /**
     * 格式化表格行
     */
    public static String formatTableRow(Object[] columns, int[] columnWidths) {
        StringBuilder sb = new StringBuilder("│");
        for (int i = 0; i < columns.length; i++) {
            sb.append(padLeft(columns[i].toString(), columnWidths[i]))
                    .append("│");
        }
        return sb.toString();
    }

    /**
     * 生成分隔线
     */
    public static String generateSeparator(int[] columnWidths) {
        StringBuilder sb = new StringBuilder("├");
        for (int width : columnWidths) {
            for (int i = 0; i < width; i++) {
                sb.append("─");
            }
            sb.append("┼");
        }
        sb.replace(sb.length() - 1, sb.length(), "┤");
        return sb.toString();
    }
}
