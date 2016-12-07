package cn.clickwise.utils.other;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Key;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by lvyang on 2016/11/25.
 */
public class IDEACrypto {
    /**
     * 密钥算法
     */
    public static final String KEY_ALGORITHM = "IDEA";

    /**
     * 加密/解密算法/工作模式/填充方式
     */
    public static final String CIPHER_ALGORITHM = "IDEA/ECB/ISO10126Padding";

    /**
     * 生成密钥，只有bouncycastle支持
     *
     * @return byte[] 二进制密钥
     */
    public static byte[] initKey() throws Exception {
        //加入bouncyCastle支持
        Security.addProvider(new BouncyCastleProvider());

        //实例化密钥生成器
        KeyGenerator kg = KeyGenerator.getInstance(KEY_ALGORITHM);
        //初始化密钥生成器，IDEA要求密钥长度为128位
        kg.init(128);
        //生成密钥
        SecretKey secretKey = kg.generateKey();
        //获取二进制密钥编码形式
        return secretKey.getEncoded();
    }

    /**
     * 转换密钥
     *
     * @param key 二进制密钥
     * @return Key 密钥
     */
    private static Key toKey(byte[] key) throws Exception {
        //实例化DES密钥
        //生成密钥
        SecretKey secretKey = new SecretKeySpec(key, KEY_ALGORITHM);
        return secretKey;
    }

    /**
     * 加密数据
     *
     * @param data 待加密数据
     * @param key  密钥
     * @return byte[] 加密后的数据
     */
    private static byte[] encrypt(byte[] data, byte[] key) throws Exception {
        //加入bouncyCastle支持
        Security.addProvider(new BouncyCastleProvider());
        //还原密钥
        Key k = toKey(key);
        //实例化
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        //初始化，设置为加密模式
        cipher.init(Cipher.ENCRYPT_MODE, k);
        //执行操作
        return cipher.doFinal(data);
    }

    /**
     * 解密数据
     *
     * @param data 待解密数据
     * @param key  密钥
     * @return byte[] 解密后的数据
     */
    private static byte[] decrypt(byte[] data, byte[] key) throws Exception {
        //加入bouncyCastle支持
        Security.addProvider(new BouncyCastleProvider());
        //还原密钥
        Key k = toKey(key);
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        //初始化，设置为解密模式
        cipher.init(Cipher.DECRYPT_MODE, k);
        //执行操作
        return cipher.doFinal(data);
    }

    /**
     * 获得秘钥
     *
     * @return
     */
    public static String getKey() {
        String result = null;
        try {
            result = new String(Base64.encodeBase64(initKey()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 加密
     *
     * @param data
     * @param key
     * @return
     */
    public static String IDEAEncrypt(String data, String key) {
        String result = null;
        try {
            byte[] data_en = encrypt(data.getBytes(), Base64.decodeBase64(key.getBytes()));
            result = new String(Base64.encodeBase64(data_en));// new String(Base64.encode(data_en,Base64.NO_WRAP)); //;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String IDEADecrypts(String data, String key) {
        String result = null;
        try {
            byte[] data_de = decrypt(Base64.decodeBase64(data.getBytes()), Base64.decodeBase64(key.getBytes()));
            ;
            result = new String(data_de);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    /*public static void main(String[] args) throws Exception {
        String data = "test string";
        String key = getKey();
        System.out.println("密钥：" + key);
        String data_en = IDEAEncrypt(data, key);
        System.out.println("密文："+data_en);
        String data_de = IDEADecrypt(data_en, key);
        System.out.println("原文："+data_de);
    }*/
}
