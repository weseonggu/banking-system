package com.msa.banking.account.infrastructure.encryption;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class EncryptionUtil {

    private static final String ALGORITHM = "AES";

    // AES 암호화
    public static String encrypt(String data, String secretKey) throws Exception {
        SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encrypted = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    // AES 복호화
    public static String decrypt(String encryptedData, String secretKey) throws Exception {
        SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decoded = Base64.getDecoder().decode(encryptedData);
        byte[] decrypted = cipher.doFinal(decoded);
        return new String(decrypted);
    }

    public static void main(String[] args) throws Exception {
        String secretKey = "1234567890123456";  // 16바이트 키
        String accountNumber = "123-4567-8901234";  // 평문 계좌번호

        // 암호화
        String encryptedAccountNumber = encrypt(accountNumber, secretKey);
        System.out.println("Encrypted Account Number: " + encryptedAccountNumber);

        // 복호화
        String decryptedAccountNumber = decrypt(encryptedAccountNumber, secretKey);
        System.out.println("Decrypted Account Number: " + decryptedAccountNumber);
    }
}
