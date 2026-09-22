import java.nio.charset.StandardCharsets;

public class CustomFeistelCipherDemo {

    // ==========================================
    // CONSTANTS
    // ==========================================

    private static final int NUM_ROUNDS = 8;

    // ==========================================
    // 1. KEY SCHEDULE
    // ==========================================

    public static int[] generateRoundKeys(int masterKey) {

        int[] roundKeys = new int[NUM_ROUNDS];

        for (int i = 0; i < NUM_ROUNDS; i++) {

            roundKeys[i] = masterKey + (i + 1);
        }

        return roundKeys;
    }

    // ==========================================
    // 2. ROUND FUNCTION
    // ==========================================

    public static int roundFunction(int right, int roundKey) {

        // Step 1: Add right half and round key
        int result = right + roundKey;

        // Step 2: Rotate result left by 5 bits
        result = Integer.rotateLeft(result, 5);

        // Step 3: XOR result with round key
        result = result ^ roundKey;

        return result;
    }

    // ==========================================
    // 3. ENCRYPTION
    // ==========================================

    public static long encryptBlock(long plaintext, int masterKey) {

        // Split 64-bit plaintext into two 32-bit halves
        int left = (int) (plaintext >>> 32);
        int right = (int) plaintext;

        // Generate 8 round keys
        int[] roundKeys = generateRoundKeys(masterKey);

        // Perform 8 Feistel rounds
        // K1 -> K2 -> K3 -> K4 -> K5 -> K6 -> K7 -> K8
        for (int i = 0; i < NUM_ROUNDS; i++) {

            int temp = right;

            right = left ^ roundFunction(
                right,
                roundKeys[i]
            );

            left = temp;
        }

        // Combine left and right into 64-bit ciphertext
        return combineHalves(left, right);
    }

    // ==========================================
    // 4. DECRYPTION
    // ==========================================

    public static long decryptBlock(long ciphertext, int masterKey) {

        // Split 64-bit ciphertext into two 32-bit halves
        int left = (int) (ciphertext >>> 32);
        int right = (int) ciphertext;

        // Generate the same 8 round keys
        int[] roundKeys = generateRoundKeys(masterKey);

        // Use round keys in reverse order
        for (int i = NUM_ROUNDS - 1; i >= 0; i--) {

            int temp = left;

            left = right ^ roundFunction(left, roundKeys[i]);

            right = temp;
        }

        // Combine left and right into 64-bit plaintext
        return combineHalves(left, right);
    }

    // ==========================================
    // 5. COMBINE TWO 32-BIT HALVES
    // ==========================================

    private static long combineHalves(
    int left,
    int right) {

        return ((long) left << 32)
        | (right & 0xFFFFFFFFL);
    }

    // ==========================================
    // 6. STRING ENCRYPTION
    // ==========================================

    public static byte[] encrypt(
    String plaintext,
    int masterKey) {

        // Convert plaintext into UTF-8 bytes
        byte[] input =
            plaintext.getBytes(StandardCharsets.UTF_8);

        // Calculate number of 8-byte blocks
        int numberOfBlocks =
            (input.length + 7) / 8;

        // Create padded input
        byte[] paddedInput =
            new byte[numberOfBlocks * 8];

        // Copy plaintext into padded array
        System.arraycopy(
            input,
            0,
            paddedInput,
            0,
            input.length
        );

        // Create ciphertext array
        byte[] ciphertext =
            new byte[paddedInput.length];

        // Encrypt each 64-bit block
        for (int i = 0;
        i < paddedInput.length;
        i += 8) {

            long block =
                bytesToLong(
                    paddedInput,
                    i
                );

            long encryptedBlock =
                encryptBlock(
                    block,
                    masterKey
                );

            longToBytes(
                encryptedBlock,
                ciphertext,
                i
            );
        }

        return ciphertext;
    }

    // ==========================================
    // 7. STRING DECRYPTION
    // ==========================================

    public static String decrypt(
    byte[] ciphertext,
    int masterKey) {

        byte[] plaintext =
            new byte[ciphertext.length];

        // Decrypt each 64-bit block
        for (int i = 0;
        i < ciphertext.length;
        i += 8) {

            long block =
                bytesToLong(
                    ciphertext,
                    i
                );

            long decryptedBlock =
                decryptBlock(
                    block,
                    masterKey
                );

            longToBytes(
                decryptedBlock,
                plaintext,
                i
            );
        }

        // Remove zero padding
        int length = plaintext.length;

        while (length > 0 &&
        plaintext[length - 1] == 0) {

            length--;
        }

        // Convert bytes back to UTF-8 text
        return new String(
            plaintext,
            0,
            length,
            StandardCharsets.UTF_8
        );
    }

    // ==========================================
    // 8. CONVERT BYTES TO LONG
    // ==========================================

    private static long bytesToLong(
    byte[] data,
    int offset) {

        long value = 0;

        for (int i = 0; i < 8; i++) {

            value <<= 8;

            value |=
            (data[offset + i] & 0xFFL);
        }

        return value;
    }

    // ==========================================
    // 9. CONVERT LONG TO BYTES
    // ==========================================

    private static void longToBytes(
    long value,
    byte[] data,
    int offset) {

        for (int i = 7; i >= 0; i--) {

            data[offset + i] =
            (byte) (value & 0xFF);

            value >>>= 8;
        }
    }

    // ==========================================
    // 10. CONVERT CIPHERTEXT TO HEX
    // ==========================================

    public static String bytesToHex(
    byte[] data) {

        StringBuilder result =
            new StringBuilder();

        for (byte b : data) {

            result.append(
                String.format(
                    "%02X",
                    b
                )
            );
        }

        return result.toString();
    }

    // ==========================================
    // 11. MAIN METHOD / DEMONSTRATION
    // ==========================================

    public static void main(String[] args) {

        // Test plaintext
        String plaintext = "HELLO WORLD";

        // Example master key
        int masterKey = 123456789;

        // ==========================================
        // PROGRAM HEADER
        // ==========================================

        System.out.println(
            "======================================"
        );

        System.out.println(
            "   CUSTOM FEISTEL BLOCK CIPHER"
        );

        System.out.println(
            "======================================"
        );

        System.out.println();

        // ==========================================
        // ORIGINAL PLAINTEXT
        // ==========================================

        System.out.println(
            "Original Plaintext:"
        );

        System.out.println(
            plaintext
        );

        System.out.println();

        // ==========================================
        // MASTER KEY
        // ==========================================

        System.out.println(
            "Master Key:"
        );

        System.out.println(
            masterKey
        );

        System.out.println();

        // ==========================================
        // ROUND KEYS
        // ==========================================

        int[] roundKeys =
            generateRoundKeys(
                masterKey
            );

        System.out.println(
            "Round Keys:"
        );

        for (int i = 0;
        i < roundKeys.length;
        i++) {

            System.out.println(
                "K" + (i + 1)
                + " = "
                + roundKeys[i]
            );
        }

        System.out.println();

        // ==========================================
        // ENCRYPTION
        // ==========================================

        byte[] ciphertext =
            encrypt(
                plaintext,
                masterKey
            );

        System.out.println(
            "Ciphertext:"
        );

        System.out.println(
            bytesToHex(
                ciphertext
            )
        );

        System.out.println();

        // ==========================================
        // DECRYPTION
        // ==========================================

        String decrypted =
            decrypt(
                ciphertext,
                masterKey
            );

        System.out.println(
            "Decrypted Plaintext:"
        );

        System.out.println(
            decrypted
        );

        System.out.println();

        // ==========================================
        // CORRECTNESS TEST
        // ==========================================

        if (plaintext.equals(decrypted)) {

            System.out.println(
                "Result: SUCCESS"
            );

            System.out.println(
                "The decrypted plaintext matches "
                + "the original plaintext."
            );

        } else {

            System.out.println(
                "Result: FAILED"
            );

            System.out.println(
                "The decrypted plaintext does not "
                + "match the original plaintext."
            );
        }

        System.out.println();

        // ==========================================
        // PROGRAM FOOTER
        // ==========================================

        System.out.println(
            "======================================"
        );
    }
}