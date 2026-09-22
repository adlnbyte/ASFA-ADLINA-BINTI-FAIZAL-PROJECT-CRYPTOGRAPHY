public class PerformanceTestingEvaluation {

    // ==========================================
    // CREATE TEST DATA
    // ==========================================

    public static String createTestData(int size) {

        StringBuilder data = new StringBuilder();

        for (int i = 0; i < size; i++) {
            data.append("A");
        }

        return data.toString();
    }


    // ==========================================
    // PERFORMANCE TEST
    // ==========================================

    public static void test(int size, String sizeName) {

        System.out.println();
        System.out.println("======================================");
        System.out.println("FILE SIZE: " + sizeName);
        System.out.println("======================================");


        // Create test data
        String plaintext = createTestData(size);


        // ==========================================
        // STREAM CIPHER
        // ==========================================

        byte[] streamKey =
            StreamCipherDemo.generateKey(16);


        // Stream Encryption
        long startStreamEncryption =
            System.nanoTime();

        byte[] streamCiphertext =
            StreamCipherDemo.encrypt(
                plaintext,
                streamKey
            );

        long endStreamEncryption =
            System.nanoTime();


        // Stream Decryption
        long startStreamDecryption =
            System.nanoTime();

        String streamDecrypted =
            StreamCipherDemo.decrypt(
                streamCiphertext,
                streamKey
            );

        long endStreamDecryption =
            System.nanoTime();


        double streamEncryptionTime =
            (endStreamEncryption -
             startStreamEncryption)
            / 1_000_000.0;

        double streamDecryptionTime =
            (endStreamDecryption -
             startStreamDecryption)
            / 1_000_000.0;


        // ==========================================
        // FEISTEL CIPHER
        // ==========================================

        int masterKey = 123456789;


        // Feistel Encryption
        long startFeistelEncryption =
            System.nanoTime();

        byte[] feistelCiphertext =
            CustomFeistelCipherDemo.encrypt(
                plaintext,
                masterKey
            );

        long endFeistelEncryption =
            System.nanoTime();


        // Feistel Decryption
        long startFeistelDecryption =
            System.nanoTime();

        String feistelDecrypted =
            CustomFeistelCipherDemo.decrypt(
                feistelCiphertext,
                masterKey
            );

        long endFeistelDecryption =
            System.nanoTime();


        double feistelEncryptionTime =
            (endFeistelEncryption -
             startFeistelEncryption)
            / 1_000_000.0;

        double feistelDecryptionTime =
            (endFeistelDecryption -
             startFeistelDecryption)
            / 1_000_000.0;


        // ==========================================
        // DISPLAY RESULTS
        // ==========================================

        System.out.println();

        System.out.println(
            "Stream Encryption : "
            + streamEncryptionTime
            + " ms"
        );

        System.out.println(
            "Stream Decryption : "
            + streamDecryptionTime
            + " ms"
        );

        System.out.println(
            "Feistel Encryption: "
            + feistelEncryptionTime
            + " ms"
        );

        System.out.println(
            "Feistel Decryption: "
            + feistelDecryptionTime
            + " ms"
        );


        // ==========================================
        // CORRECTNESS CHECK
        // ==========================================

        if (plaintext.equals(streamDecrypted)
            && plaintext.equals(feistelDecrypted)) {

            System.out.println();
            System.out.println(
                "Correctness: SUCCESS"
            );

        } else {

            System.out.println();
            System.out.println(
                "Correctness: FAILED"
            );
        }
    }


    // ==========================================
    // MAIN METHOD
    // ==========================================

    public static void main(String[] args) {

        System.out.println(
            "======================================"
        );

        System.out.println(
            "   PERFORMANCE TESTING EVALUATION"
        );

        System.out.println(
            "======================================");


        // 1 KB
        test(
            1024,
            "1 KB"
        );


        // 100 KB
        test(
            102400,
            "100 KB"
        );


        // 1 MB
        test(
            1048576,
            "1 MB"
        );


        System.out.println();
        System.out.println(
            "======================================"
        );

        System.out.println(
            "       TESTING COMPLETED"
        );

        System.out.println(
            "======================================"
        );
    }
}
