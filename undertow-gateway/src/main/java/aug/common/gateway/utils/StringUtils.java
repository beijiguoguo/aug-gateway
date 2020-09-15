package aug.common.gateway.utils;

import java.util.UUID;

/**
 * 字符串工具
 *
 * @author guoxiaoyong
 * @date 2020/9/6
 */
public class StringUtils {
    public static String uuid() {
        return UUID.randomUUID().toString();
    }

    public static boolean hasText(CharSequence c){
        return org.springframework.util.StringUtils.hasText(c);
    }
}
