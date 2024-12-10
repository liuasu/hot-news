package cn.ls.hotnews.utils;

import java.util.regex.Pattern;

/**
 * title: CommonUtils
 * author: liaoshuo
 * date: 2024/12/8 22:33
 * description:
 */
public class CommonUtils {

    public static final Pattern[] PATTERNS = {
            // 记者信息
            Pattern.compile("（[^）]*记者[^）]*）"),
            Pattern.compile("\\([^)]*记者[^)]*\\)"),
            Pattern.compile("【[^】]*记者[^】]*】"),

            // 版权信息
            Pattern.compile("©?\\d{4}[^。]*版权所有[^。]*。"),
            Pattern.compile("版权归[^。]*所有[^。]*。"),

            // 许可声明
            Pattern.compile("未经许可[^。]*。"),
            Pattern.compile("未经授权[^。]*。"),

            // 来源信息
            Pattern.compile("来源：[^，。\\n]*"),
            Pattern.compile("责编：[^，。\\n]*"),
            Pattern.compile("编辑：[^，。\\n]*"),
            Pattern.compile("翻译：[^，。\\n]*"),
            Pattern.compile("校对：[^，。\\n]*"),
            Pattern.compile("译自：[^，。\\n]*"),
            Pattern.compile("文：[^，。\\n]*"),
            // 新增带竖线的作者/编辑/责编信息
            Pattern.compile("作者丨[^，。\\n]*"),
            Pattern.compile("图片[^，。\\n]*"),

            // 新增微信公众号来源声明
            Pattern.compile("本文来自微信公众号[^。]*。"),
            Pattern.compile("本文来自[^，。\\n]*，作者：[^，。]*，[^。]*。"),
            Pattern.compile("本文*"),
            Pattern.compile("文｜[^，。\\n]*"),
            Pattern.compile("\\|"),
            Pattern.compile("编辑*[^，。\\n]*"),
            Pattern.compile("主理[^，。\\n]*"),
            // 新增作者说的内容
            //Pattern.compile("[^，。\\n]*说：[^。]*。"),
            Pattern.compile("[^，。？\\n]*说：[^。]*。"),

            // 新增图源说明
            Pattern.compile("\\*图源[^*]*\\*"),

            // 新增带「」的内容
            Pattern.compile("「[^」]*」"),

            // 新增带中文竖线的编辑信息
    };

    public static String cleanText(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        String result = text;
        for (Pattern pattern : PATTERNS) {
            result = pattern.matcher(result).replaceAll("");
        }

        // 清理多余的空白字符
        result = result.replaceAll("\\s+", " ")
                .replaceAll("[ 　]+", " ")// 包括全角空格
                .replaceAll(" ", "")
                .replaceAll("[^\\p{L}\\p{N}\\p{P}\\p{Z}]", "")//emoji的清理
                .trim();

        return result;
    }
}
