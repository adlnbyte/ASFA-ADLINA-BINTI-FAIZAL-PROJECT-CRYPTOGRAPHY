import java.security.SecureRandom;

public class StreamCipherDemo {

    // ==========================================
    // 1. KEY GENERATION
    // ==========================================

    public static byte[] generateKey(int length) {

        byte[] key = new byte[length];

        SecureRandom random = new SecureRandom();
        random.nextBytes(key);

        return key;
    }


    // ==========================================
    // 2. KEY INITIALIZATION
    // ==========================================

    public static int[] initializeState(byte[] key) {

        int[] S = new int[256];

        // Initialize state array
        for (int i = 0; i < 256; i++) {
            S[i] = i;
        }

        int j = 0;

        // Key Scheduling Algorithm
        for (int i = 0; i < 256; i++) {

            j = (j + S[i] +
                 (key[i % key.length] & 0xFF)) % 256;

            // Swap S[i] and S[j]
            int temp = S[i];
            S[i] = S[j];
            S[j] = temp;
        }

        return S;
    }


    // ==========================================
    // 3. KEYSTREAM GENERATION
    // ==========================================

    public static byte[] generateKeystream(byte[] key, int length) {

        int[] S = initializeState(key);

        int i = 0;
        int j = 0;

        byte[] keystream = new byte[length];

        for (int n = 0; n < length; n++) {

            i = (i + 1) % 256;

            j = (j + S[i]) % 256;

            // Swap S[i] and S[j]
            int temp = S[i];
            S[i] = S[j];
            S[j] = temp;

            // Generate keystream byte
            int k = S[(S[i] + S[j]) % 256];

            keystream[n] = (byte) k;
        }

        return keystream;
    }


    // ==========================================
    // 4. XOR ENCRYPTION
    // ==========================================

    public static byte[] encrypt(String plaintext, byte[] key) {

        byte[] plaintextBytes = plaintext.getBytes();

        // Generate keystream
        byte[] keystream =
                generateKeystream(key, plaintextBytes.length);

        byte[] ciphertext =
                new byte[plaintextBytes.length];

        // XOR plaintext with keystream
        for (int i = 0; i < plaintextBytes.length; i++) {

            ciphertext[i] =
                    (byte) (plaintextBytes[i] ^ keystream[i]);
        }

        return ciphertext;
    }


    // ==========================================
    // 5. XOR DECRYPTION
    // ==========================================

    public static String decrypt(byte[] ciphertext, byte[] key) {

        // Generate the same keystream
        byte[] keystream =
                generateKeystream(key, ciphertext.length);

        byte[] plaintext =
                new byte[ciphertext.length];

        // XOR ciphertext with keystream
        for (int i = 0; i < ciphertext.length; i++) {

            plaintext[i] =
                    (byte) (ciphertext[i] ^ keystream[i]);
        }

        return new String(plaintext);
    }


    // ==========================================
    // 6. CONVERT BYTES TO HEX
    // ==========================================

    public static String toHex(byte[] data) {

        StringBuilder hex = new StringBuilder();

        for (byte b : data) {

            hex.append(
                String.format("%02X", b)
            );
        }

        return hex.toString();
    }


    // ==========================================
    // 7. MAIN PROGRAM / TESTING
    // ==========================================

    public static void main(String[] args) {

        // Plaintext
        String plaintext = "HELLO WORLD";


        // Generate 16-byte secret key
        byte[] secretKey = generateKey(16);


        // Encrypt
        byte[] ciphertext =
                encrypt(plaintext, secretKey);


        // Decrypt
        String decryptedText =
                decrypt(ciphertext, secretKey);


        // Display results
        System.out.println(
            "===== SIMPLIFIED RC4-LIKE STREAM CIPHER ====="
        );

        System.out.println();

        System.out.println(
            "Original Plaintext  : " + plaintext
        );

        System.out.println(
            "Generated Key       : " + toHex(secretKey)
        );

        System.out.println(
            "Ciphertext          : " + toHex(ciphertext)
        );

        System.out.println(
            "Decrypted Plaintext : " + decryptedText
        );

        System.out.println();


        // Correctness Test
        if (plaintext.equals(decryptedText)) {

            System.out.println(
                "Status              : SUCCESS"
            );

            System.out.println(
                "Encryption and decryption are correct."
            );

        } else {

            System.out.println(
                "Status              : FAILED"
            );
        }
    }
}