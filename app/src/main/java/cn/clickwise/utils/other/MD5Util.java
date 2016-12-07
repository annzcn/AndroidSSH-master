package cn.clickwise.utils.other;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Sylthas
 */
public class MD5Util {
    private static final char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F'};
    /**
     * @param inStr
     * @return 32byte MD5 Value
     */
    public static String getMD5(String inStr) {
        byte[] inStrBytes = inStr.getBytes();
        try {
            MessageDigest MD = MessageDigest.getInstance("MD5");
            MD.update(inStrBytes);
            byte[] mdByte = MD.digest();
            char[] str = new char[mdByte.length * 2];
            int k = 0;
            for (int i = 0; i < mdByte.length; i++) {
                byte temp = mdByte[i];
                str[k++] = hexDigits[temp >>> 4 & 0xf];
                str[k++] = hexDigits[temp & 0xf];
            }
            return new String(str);
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static String encryption(String plainText) {
        String re_md5 = new String();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }

            re_md5 = buf.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return re_md5;
    }
}
