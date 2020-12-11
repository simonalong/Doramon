package com.simon.ocean;


/**
 * @author shizi
 * @since 2020-11-30 18:37:54
 */
public class JsonStrUtils {

    /**
     * 专门用于解析json对应的string到界面回车的显示
     * <p>
     * 比如：[{"age":12,"name":{"women":"test","age":12,"array":[{"name":"test","age":12}],"tem":[1,2,3,4,5],"haode":"women"}},{"age":22,"name":"haode2"}]
     * 结果为：
     * [
     *     {
     *         "age":12,
     *         "name":
     *         {
     *             "women":"test",
     *             "age":12,
     *             "array":
     *             [
     *                 {
     *                     "name":"test",
     *                     "age":12
     *                 }
     *             ],
     *             "tem":
     *             [
     *                 1,2,3,4,5
     *             ],
     *             "haode":"women"
     *         }
     *     },
     *     {
     *         "age":22,
     *         "name":"haode2"
     *     }
     * ]
     */
    public static String prettyJson(String jsonContent) {
        if (null == jsonContent || "".equals(jsonContent)) {
            return null;
        }
        String jsonContentTem = jsonContent.replace("\n", "").replace(" ", "");
        StringBuilder stringBuilder = new StringBuilder();
        // 是否位于数组
        boolean inArray = false;
        // 是否有回车
        boolean haveEnter = true;
        // 是否有空格
        boolean haveSpace = false;
        // 是否已经添加空格
        boolean haveAppendSpace = false;
        // 上一个处理者是否是左大括号
        boolean isRightBracket = false;
        char[] charList = jsonContentTem.toCharArray();
        int spaceCount = 0;
        for (char c : charList) {
            if (c == ',') {
                if (inArray) {
                    stringBuilder.append(c);
                    continue;
                }
                if (isRightBracket) {
                    stringBuilder.append(",\n");
                } else {
                    stringBuilder.append(",\n");
                    if (haveSpace) {
                        stringBuilder.append(addSpace(spaceCount));
                    }

                    haveEnter = true;
                    haveAppendSpace = true;
                }
            } else if (c == '{') {
                if (!haveEnter) {
                    stringBuilder.append("\n");
                }
                if (haveSpace) {
                    stringBuilder.append(addSpace(spaceCount));
                }
                haveSpace = true;
                haveEnter = true;
                isRightBracket = false;
                spaceCount++;

                stringBuilder.append("{\n");
                stringBuilder.append(addSpace(spaceCount));
                haveAppendSpace = true;
            } else if (c == '}') {
                spaceCount--;

                stringBuilder.append("\n");
                if (haveSpace) {
                    stringBuilder.append(addSpace(spaceCount));
                }
                stringBuilder.append("}");

                haveEnter = true;
                isRightBracket = true;
            } else if (c == '[') {
                if (!haveEnter) {
                    stringBuilder.append("\n");
                }
                if (haveSpace) {
                    stringBuilder.append(addSpace(spaceCount));
                }

                inArray = true;
                haveSpace = true;
                isRightBracket = false;
                stringBuilder.append("[\n");
                haveEnter = true;
                spaceCount++;
                haveAppendSpace = false;
            } else if (c == ']') {
                spaceCount--;
                stringBuilder.append("\n");
                if (haveSpace) {
                    stringBuilder.append(addSpace(spaceCount));
                }
                stringBuilder.append("]");

                inArray = false;
                isRightBracket = false;
                haveAppendSpace = false;
            } else {
                if (c == ':') {
                    inArray = false;
                }
                if (!haveAppendSpace) {
                    if (haveSpace) {
                        stringBuilder.append(addSpace(spaceCount));
                    }
                    haveAppendSpace = true;
                }
                stringBuilder.append(c);

                isRightBracket = false;
                haveEnter = false;
            }
        }
        return stringBuilder.toString();
    }

    public static String addSpace(int count) {
        if (count <= 0) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < count; i++) {
            stringBuilder.append("    ");
        }
        return stringBuilder.toString();
    }
}
