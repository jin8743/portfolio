package com.portfolio.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

public class UsernamePasswordRegexTest {


    /** 숫자, 알파벳으로만 이루어 졌으며
     * 길이가 3 ~ 20 사이여야함
     */
    public static boolean isValidUsername(String str) {
        String regex = "[a-zA-Z0-9]{3,20}";
        return Pattern.matches(regex, str);
    }


    /** 숫자, 알파벳,특수문자 가 전부 한번씩은 있어야 되고
     * 길이가 8 ~ 20 사이여야함
     * */
    public static boolean isValidPassword(String str) {
        String regex = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()])[A-Za-z\\d!@#$%^&*()]{8,20}$";

        return Pattern.matches(regex, str);
    }


    @Test
    @DisplayName("아이디 검증")
    void isValidUsername() {
        String str1 = "aBc123"; // true
        String str2 = "aBc";    // true
        String str3 = "aBc123456789012345678"; // false (길이가 길음)
        String str4 = "aBc@123"; // false (특수문자 포함)
        String str5 = "aB c123"; // false (공백 포함)
        String str6 = "1aBc23";  // true
        String str7 = "1111111"; //true
        String str8 = "aaacvfe"; //true

        Assertions.assertTrue(isValidUsername(str1));
        Assertions.assertTrue(isValidUsername(str2));
        Assertions.assertFalse(isValidUsername(str3));
        Assertions.assertFalse(isValidUsername(str4));
        Assertions.assertFalse(isValidUsername(str5));
        Assertions.assertTrue(isValidUsername(str6));
        Assertions.assertTrue(isValidUsername(str7));
        Assertions.assertTrue(isValidUsername(str8));
    }

    @Test
    @DisplayName("비밀번호 검증")
    void isValidPassword() {
        String str1 = "aBc123!@#"; // true
        String str2 = "aBc123";    // false (특수문자 미포함)
        String str3 = "aBc123456789012345678"; // false (길이가 길음)
        String str4 = "aBc@123"; // false (길이가 짧음)
        String str5 = "aBc 123"; // false (공백 포함)
        String str6 = "1aBc23!@#";  // true
        String str7 = "aaaaaa111!"; // true

        Assertions.assertTrue(isValidPassword(str1));
        Assertions.assertFalse(isValidPassword(str2));
        Assertions.assertFalse(isValidPassword(str3));
        Assertions.assertFalse(isValidPassword(str4));
        Assertions.assertFalse(isValidPassword(str5));
        Assertions.assertTrue(isValidPassword(str6));
        Assertions.assertTrue(isValidPassword(str7));
    }
}
