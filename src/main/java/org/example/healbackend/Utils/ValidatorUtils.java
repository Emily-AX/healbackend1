package org.example.healbackend.Utils;

public class ValidatorUtils {

    // 邮箱正则（支持大多数常见格式）
    private static final String EMAIL_REGEX =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                    "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    // 中国手机号正则（最新号段）
    private static final String CHINA_PHONE_REGEX =
            "^1(3[0-9]|4[5-9]|5[0-35-9]|6[2567]|7[0-8]|8[0-9]|9[0-35-9])\\d{8}$";

    /**
     * 验证邮箱格式
     * @param email 邮箱地址
     * @return true=格式正确，false=格式错误
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) return false;
        return email.matches(EMAIL_REGEX);
    }

    /**
     * 验证中国手机号格式
     * @param phone 手机号码
     * @return true=格式正确，false=格式错误
     */
    public static boolean isValidChinaPhone(String phone) {
        if (phone == null || phone.isEmpty()) return false;
        return phone.matches(CHINA_PHONE_REGEX);
    }

}
