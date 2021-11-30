package util;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Строковые утилиты
 */
public class StringUtils {

    /**
     * Преобразует строку в целое число. Если пусто, возвращает заданное
     * значение по умолчанию.
     *
     * @param str строка для преобразования
     * @param defVal default
     * @return int value
     */
    public static int parseInt(String str, int defVal) {
        try {
            if (isBlank(str)) {
                return defVal;
            }
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return defVal;
        }
    }

    /**
     * Определяет, является ли строка пустой или имеет длину 0.
     *
     * @param str
     */
    public static boolean isBlank(String str) {
        return str == null || str.trim().length() == 0;
    }

    /**
     * Определяет, является ли строка пустой.
     */
    public static boolean isEmpty(String str) {
        return str == null || "".equals(str.trim());
    }

    /**
     * Используйте указанный разделитель для разделения значений в данном
     * массиве, чтобы сформировать строку.
     *
     * @param array string array
     * @param seperator
     */
    public static String conc(String[] array, String seperator) {
        String rst = "";
        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                rst += seperator;
            }
            rst += array[i];
        }
        return rst;
    }

    /**
     * Если значение пустое, возвращается значение по умолчанию
     */
    public static String getValue(String value, String defValue) {
        if (value == null || "".equals(value)) {
            return defValue;
        }
        return value;
    }

    /**
     * Операция заполнения строки.
     *
     * @param rs исходная строка
     * @param ch символы, используемые для заполнения, символы могут быть только
     * видимыми символами
     * @param num длина строки байтового массива
     * @param left padding left или right
     * @ return строка завершения
     */
    public static String fillChar(String rs, char ch, int num, boolean left) {
        int rsLen = rs.getBytes().length;
        StringBuilder sb = new StringBuilder();
        //Оставленное завершение
        if (left) {
            if (num >= rsLen) {
                for (int i = 0; i < num - rsLen; i++) {
                    sb.append(ch);
                }
                sb.append(rs);
            } else {
                sb.append(rs.substring(0, num));
            }
        } else {
            if (num >= rsLen) {
                sb.append(rs);
                for (int i = 0; i < num - rsLen; i++) {
                    sb.append(ch);
                }
            } else {
                sb.append(rs.substring(0, num));
            }
        }
        return sb.toString();
    }

    /**
     * Удалить отступы из строк
     *
     * @param rs исходная строка
     * @param ch символы, используемые для заполнения, символы могут быть только
     * видимыми символами
     * @param left true для левого отступа, false для правого отступа
     * @return удаленная строка
     */
    public static String removeFillChar(String rs, char ch, boolean left) {
        if (left) {
            if (rs.charAt(0) != ch) {
                return rs;
            }
            int idx = rs.length();
            for (int i = 0; i < rs.length(); i++) {
                if (rs.charAt(i) != ch) {
                    idx = i;
                    break;
                }
            }
            return rs.substring(idx);
        } else {
            if (rs.charAt(rs.length() - 1) != ch) {
                return rs;
            }
            int idx = -1;
            for (int i = rs.length() - 1; i >= 0; i--) {
                if (rs.charAt(i) != ch) {
                    idx = i;
                    break;
                }
            }
            return rs.substring(0, idx + 1);
        }
    }

    /**
     * Убрать левые пробелы из строки
     */
    public static String ltrim(String s) {
        int len = s.length();
        int st = 0;
        char[] val = s.toCharArray();

        while ((st < len) && (val[st] <= ' ')) {
            st++;
        }
        return (st > 0) ? s.substring(st, len) : s;
    }

    /**
     * Убрать правые пробелы из строки
     */
    public static String rtrim(String s) {
        int len = s.length();
        int st = 0;
        char[] val = s.toCharArray();

        while ((st < len) && (val[len - 1] <= ' ')) {
            len--;
        }
        return (len < s.length()) ? s.substring(st, len) : s;
    }

    /**
     * Определить, соответствует ли строка заданному регулярному выражению.
     *
     * @param регулярное выражение строка регулярного выражения
     */
    public static boolean patternMatches(String regex, String str) {
        if (str == null) {
            return false;
        }
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    /**
     * Определяет, является ли строка строкой, начинающейся с буквы, цифры или
     * символа подчеркивания.
     */
    public static boolean isValidVariableName(String name) {
        return StringUtils.isEmpty(name) ? false : patternMatches("^[a-zA-Z_]+\\w*$", name);
    }

    /**
     * Определяет, начинается ли строка с буквы или подчеркивания и может ли она
     * содержать . и []
     */
    public static boolean isValidLeftOperator(String name) {
        return StringUtils.isEmpty(name) ? false : patternMatches("^[a-zA-Z_]+(\\.?\\w+(\\[\\d+\\])*)*$", name);
    }

    /**
     * Определить, содержит ли строка заданное строковое регулярное выражение
     */
    public static boolean startsWith(String str, String regexp) {
        return Pattern.compile(regexp).matcher(str).find();
    }

    public static String repeat(String str, int times) {
        String r = "";
        for (int i = 0; i < times; i++) {
            r += str;
        }
        return r;
    }

    /**
     * Заполненная справа строка ASCii, один китайский символ считается за два
     * символа
     *
     * @param str
     * @param length
     * @param padding
     * @return
     */
    public static String asciiPaddingR(String str, int length, String padding) {
        String rst = asciiTrimR(str, length);
        int alen = asciiLength(rst);
        if (alen == length) {
            return rst;
        }
        rst += repeat(padding, (length - alen) / asciiLength(padding));
        return rst;
    }

    /**
     * Обрезанная справа строка ASCii, один китайский символ считается за два
     * символа
     *
     * @param str
     * @param length
     * @return
     */
    public static String asciiTrimR(String str, int length) {
        int alen = asciiLength(str);
        if (alen <= length) {
            return str;
        }
        String result = "";
        alen = 0;
        for (int i = 0; i < length; i++) {
            char c = str.charAt(i);
            alen += c > 127 ? 2 : 1;
            if (alen <= length) {
                result += c;
            } else {
                break;
            }
        }
        return result;
    }

    /**
     * Длина строки ASCii, один китайский символ считается за два символа
     *
     * @param str
     * @return
     */
    public static int asciiLength(String str) {
        int length = 0;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            length += c > 127 ? 2 : 1;
        }

        return length;
    }

    /**
     * Положение ascii в положение строки
     *
     * @param str
     * @param asciiIdx
     * @return
     */
    public static int asciiIdx2StrIdx(String str, int asciiIdx) {
        int length = 0;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            length += c > 127 ? 2 : 1;
            if (length >= asciiIdx) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Заполните строку АSCii, китайский символ считается как два символа
     *
     * @param str
     * @param startIdx, index from 0
     * @param fillStr
     * @return
     */
    public static String asciiFill(String str, int startIdx, String fillStr) {
        String charset = Charset.defaultCharset().name();
        try {
            byte[] bs = str.getBytes(charset);
            byte[] fb = fillStr.getBytes(charset);
            int capacity = bs.length;
            if (startIdx + fb.length > bs.length) {
                capacity = startIdx + fb.length;
            }
            ByteBuffer bb = ByteBuffer.allocate(capacity);
            bb.put(bs);
            if (startIdx > bs.length) {
                for (int i = startIdx - bs.length; i > 0; i--) {
                    bb.put((byte) 0x20);
                }
            }
            //int size = 0;
            boolean isChiChar = false;
            boolean isChiCharBegin = false;
            int fillLastIdx = startIdx + fb.length;
            for (int i = 0; i < bs.length && fillLastIdx > 0; i++) {
                if (bb.get(i) >= 0) {
                    isChiChar = false;
                    isChiCharBegin = false;
                } else {
                    isChiChar = true;
                    isChiCharBegin = i == 0 ? true : !isChiCharBegin;
                }
                if (i == startIdx) {
                    if (isChiChar && !isChiCharBegin) {
                        bb.put(i - 1, (byte) 0x20);
                        bb.put(i, (byte) 0x20);
                    }
                } else if (i == fillLastIdx) {
                    if (isChiChar && !isChiCharBegin) {
                        bb.put(i - 1, (byte) 0x20);
                        bb.put(i, (byte) 0x20);
                    }
                    break;
                }

            }
            bb.position(startIdx);
            bb.put(fb);
            return new String(bb.array(), charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Заполните строку АSCii, китайский символ считается как два символа
     *
     * @param str
     * @param startIdx, index from 0
     * @param fillStr
     * @return
     */
    public static String asciiFill2(String str, int startIdx, String fillStr) {
        int idx = asciiIdx2StrIdx(str, startIdx);
        String ret = "";
        if (idx == -1) {
            ret = asciiPaddingR(str, startIdx, " ");
            ret += fillStr;
        } else {
            ret = str.substring(0, idx) + fillStr;
            if (ret.length() < str.length()) {
                ret += str.substring(ret.length());
            }
        }
        return ret;
    }

    public static String hexChars2Str(String delimChar) {
        if (delimChar.matches("(0[x,X](-){0,1}[0-9,a-f,A-F]+)+")) {
            String[] ss = delimChar.split("0[x,X]");
            byte[] bs = new byte[ss.length - 1];
            for (int i = 1; i < ss.length; i++) {

                byte b = (byte) Integer.parseInt(ss[i], 16);
                bs[i - 1] = b;

            }
            return new String(bs);
        }
        return delimChar;

    }
}
