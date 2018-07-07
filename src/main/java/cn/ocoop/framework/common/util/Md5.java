package cn.ocoop.framework.common.util;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

import static com.google.common.base.Charsets.UTF_8;

public class Md5 {

    public static HashCode encrypt(String value,Object salt) {
        return Hashing.hmacMd5(String.valueOf(salt).getBytes()).hashString(value, UTF_8);
    }

    public static String encryptToString(String value, String salt) {
        return encrypt(value,salt).toString();
    }
}
