package co.cablebox.tv.utils;

import android.util.Log;

import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class MCrypt {

    static char[] HEX_CHARS = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};

    private static int iv = 1310122399;
    private IvParameterSpec ivspec;
    private SecretKeySpec keyspec;
    private Cipher cipher;
    private static int SecretKey = 1152351091;

    public MCrypt()
    {
        Log.d("looooo: ",f3240g);
        Log.d("looooo: ",f3240p);
        ivspec = new IvParameterSpec(f3240g.getBytes());
        keyspec = new SecretKeySpec(f3240p.getBytes(), "AES");
        try {
            cipher = Cipher.getInstance("AES/CBC/NoPadding");
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public byte[] encrypt(String text) throws Exception
    {
        if(text == null || text.length() == 0)
            throw new Exception("Empty string");

        byte[] encrypted = null;

        try {
            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);

            encrypted = cipher.doFinal(padString(text).getBytes());
        } catch (Exception e)
        {
            throw new Exception("[encrypt] " + e.getMessage());
        }

        return encrypted;
    }

    public byte[] decrypt(String code) throws Exception
    {
        if(code == null || code.length() == 0)
            throw new Exception("Empty string");

        byte[] decrypted = null;

        try {
            cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);

            decrypted = cipher.doFinal(hexToBytes(code));
            //Remove trailing zeroes
            if( decrypted.length > 0)
            {
                int trim = 0;
                for( int i = decrypted.length - 1; i >= 0; i-- ) if( decrypted[i] == 0 ) trim++;

                if( trim > 0 )
                {
                    byte[] newArray = new byte[decrypted.length - trim];
                    System.arraycopy(decrypted, 0, newArray, 0, decrypted.length - trim);
                    decrypted = newArray;
                }
            }
        } catch (Exception e)
        {
            throw new Exception("[decrypt] " + e.getMessage());
        }
        return decrypted;
    }


    public static String bytesToHex(byte[] buf)
    {
        char[] chars = new char[2 * buf.length];
        for (int i = 0; i < buf.length; ++i)
        {
            chars[2 * i] = HEX_CHARS[(buf[i] & 0xF0) >>> 4];
            chars[2 * i + 1] = HEX_CHARS[buf[i] & 0x0F];
        }
        return new String(chars);
    }


    public static byte[] hexToBytes(String str) {
        if (str==null) {
            return null;
        } else if (str.length() < 2) {
            return null;
        } else {
            int len = str.length() / 2;
            byte[] buffer = new byte[len];
            for (int i=0; i<len; i++) {
                buffer[i] = (byte) Integer.parseInt(str.substring(i*2,i*2+2),16);
            }
            return buffer;
        }
    }



    private static String padString(String source)
    {
        char paddingChar = 0;
        int size = 16;
        int x = source.length() % size;
        int padLength = size - x;

        for (int i = 0; i < padLength; i++)
        {
            source += paddingChar;
        }

        return source;
    }
    public static final String f3240g = new Object() {

        /* renamed from: a */
        int f3252a;
        public String toString() {
            this.f3252a = 1131263053;
            this.f3252a = -922422543;
            this.f3252a = -1631007395;
            this.f3252a = iv;
            this.f3252a = -797115685;
            this.f3252a = -206234162;
            this.f3252a = -1569622157;
            this.f3252a = -703419504;
            this.f3252a = -1467434979;
            this.f3252a = -1470088198;
            this.f3252a = 1532560322;
            this.f3252a = 712015029;
            this.f3252a = 975517844;
            this.f3252a = -1313019075;
            this.f3252a = iv;
            this.f3252a = -1162234098;
            return new String(new byte[]{(byte) (this.f3252a >>> 19), (byte) (this.f3252a >>> 9), (byte) (this.f3252a >>> 23), (byte) (this.f3252a >>> 10), (byte) (this.f3252a >>> 15), (byte) (this.f3252a >>> 16), (byte) (this.f3252a >>> 21), (byte) (this.f3252a >>> 21), (byte) (this.f3252a >>> 12), (byte) (this.f3252a >>> 7)});
        }
    }.toString();
    public static final String f3240p = new Object() {

        /* renamed from: a */
        int f3252a;
        public String toString() {
            this.f3252a = 1131263053;
            this.f3252a = -922422543;
            this.f3252a = -1631007395;
            this.f3252a = SecretKey;
            this.f3252a = -797115685;
            this.f3252a = -206234162;
            this.f3252a = -1569622157;
            this.f3252a = -703419504;
            this.f3252a = -1467434979;
            this.f3252a = -1470088198;
            this.f3252a = 1532560322;
            this.f3252a = 712015029;
            this.f3252a = 975517844;
            this.f3252a = -1313019075;
            this.f3252a = SecretKey;
            this.f3252a = -1162234098;
            return new String(new byte[]{(byte) (this.f3252a >>> 19), (byte) (this.f3252a >>> 9), (byte) (this.f3252a >>> 23), (byte) (this.f3252a >>> 10), (byte) (this.f3252a >>> 15), (byte) (this.f3252a >>> 16), (byte) (this.f3252a >>> 21), (byte) (this.f3252a >>> 21), (byte) (this.f3252a >>> 18), (byte) (this.f3252a >>> 7)});
        }
    }.toString();
}