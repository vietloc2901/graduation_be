package locnv.haui.commons;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class EncryptUtils {
    private static final SecretKeySpec UPLOAD_ENCRYPT_KEY = new SecretKeySpec(new byte[16], "AES");
    private static final String TRANSFORMATION_METHOD = "AES/GCM/NoPadding";
    private final static int GCM_IV_LENGTH = 12;
    private final static int GCM_TAG_LENGTH = 16;

    private EncryptUtils() {}

    public static String decryptFileUploadPath(String path) throws GeneralSecurityException {
        return decrypt(path, UPLOAD_ENCRYPT_KEY);
    }

    public static String encryptFileUploadPath(String encryptedPath) throws GeneralSecurityException {
        return encrypt(encryptedPath, UPLOAD_ENCRYPT_KEY);
    }

    private static String encrypt(String privateString, SecretKey skey) throws GeneralSecurityException {
        try {
            byte[] iv = new byte[GCM_IV_LENGTH];
            (new SecureRandom()).nextBytes(iv);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION_METHOD);
            GCMParameterSpec ivSpec = new GCMParameterSpec(GCM_TAG_LENGTH * Byte.SIZE, iv);
            cipher.init(Cipher.ENCRYPT_MODE, skey, ivSpec);

            byte[] ciphertext = cipher.doFinal(privateString.getBytes("UTF8"));
            byte[] encrypted = new byte[iv.length + ciphertext.length];
            System.arraycopy(iv, 0, encrypted, 0, iv.length);
            System.arraycopy(ciphertext, 0, encrypted, iv.length, ciphertext.length);
            String encoded = Base64.getEncoder().encodeToString(encrypted);
            return encoded;
        } catch (NoSuchPaddingException |
            NoSuchAlgorithmException |
            InvalidAlgorithmParameterException |
            UnsupportedEncodingException |
            IllegalBlockSizeException |
            BadPaddingException exception) {
            throw new GeneralSecurityException(exception);
        }

    }

    private static String decrypt(String encrypted, SecretKey skey) throws GeneralSecurityException {
        try {
            byte[] decoded = Base64.getDecoder().decode(encrypted);

            byte[] iv = Arrays.copyOfRange(decoded, 0, GCM_IV_LENGTH);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION_METHOD);
            GCMParameterSpec ivSpec = new GCMParameterSpec(GCM_TAG_LENGTH * Byte.SIZE, iv);
            cipher.init(Cipher.DECRYPT_MODE, skey, ivSpec);

            byte[] ciphertext = cipher.doFinal(decoded, GCM_IV_LENGTH, decoded.length - GCM_IV_LENGTH);

            String result = new String(ciphertext, "UTF8");

            return result;
        } catch (NoSuchPaddingException |
            NoSuchAlgorithmException |
            InvalidAlgorithmParameterException |
            UnsupportedEncodingException |
            IllegalBlockSizeException |
            BadPaddingException exception) {
            throw new GeneralSecurityException(exception);
        }
    }
}
