import javax.sound.midi.Soundbank;
import java.util.*;
import java.io.*;


public class SimplifiedAES_1710110364_Utkarsh_Sharma {

    static HashMap<String, String> sbox_map = new HashMap<>();
    static String[] W = new String[6];

    //setting the the sbox for nibble substitution
    static String[] sbox = {"1001", "0100", "1010", "1011", "1101", "0001"
            , "1000", "0101", "0110", "0010", "0000", "0011", "1100", "1110",
            "1111", "0111"
    };

    // setting up inverse sbox for inverse nibble substitution
    static String[] sbox_inverse = {"1010", "0101", "1001", "1011", "0001", "0111", "1000", "1111"
            , "0110", "0000", "0010", "0011", "1100", "0100", "1101", "1110"
    };


    public static void main(String[] args) throws IOException {

        Scanner s = new Scanner(System.in);

        BufferedReader ob = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Press 1 for encryption");
        System.out.println("Press 2 for decryption");
        int ch = s.nextInt();
        if (ch == 1) {
            System.out.println("Enter 16 bit plaintext");
            String plaintext = ob.readLine();
            System.out.println("Enter 16 bit key");


            String key = ob.readLine();
            keyGeneration(key);
            System.out.println("The corresponding ciphertext:");

            encrypt(plaintext);


        } else if (ch == 2) {
            System.out.println("Enter 16 bit Ciphertext");
            String ciphertext = ob.readLine();
            System.out.println("Enter 16 bit key");


            String key = ob.readLine();
            keyGeneration(key);
            System.out.println("The corresponding plaintext:");

            decrypt(ciphertext);

        } else {
            System.out.println("Invalid Choice!! Run again");
        }


    }


    public static void encrypt(String plaintext) {

        // Adding Round 0 key
        String temp_value = xor(plaintext, W[0] + W[1]);

        //nibble substitution using s boxes
        String s1 = nibbleSubstitution(temp_value.substring(0, 8));
        String s2 = nibbleSubstitution(temp_value.substring(8, 16));
        temp_value = s1 + s2;

        // shift row function, swapping 2nd and 4th row
        temp_value = shiftRow(temp_value);

        //calling the mix column function
        temp_value = mixColumn(temp_value);

        //Adding round 1 key
        temp_value = xor(temp_value, W[2] + W[3]);
        //Final round
        //nibble substitution
        temp_value = nibbleSubstitution(temp_value.substring(0, 8)) +
                nibbleSubstitution(temp_value.substring(8, 16));


        //performing shift row again
        temp_value = shiftRow(temp_value);

        //ciphertext obtained after adding round 2 key
        String cipher_text = xor(temp_value, W[4] + W[5]);
        System.out.println(cipher_text);


    }

    public static void decrypt(String ciphertext) {
        //Adding round 2 key

        String temp_value = xor(ciphertext, W[4] + W[5]);

        //performing inverse shift row
        //same as normal
        temp_value = shiftRow(temp_value);

        //inverse nibble substitution
        temp_value = inverseNibbleSubsitution(temp_value.substring(0, 8)) +
                inverseNibbleSubsitution(temp_value.substring(8, 16));

        //adding round 1 key
        temp_value = xor(temp_value, W[2] + W[3]);

        //making the nibbles in matrix form
        String rot = rotateNibble(temp_value.substring(4, 12));
        temp_value = temp_value.substring(0, 4) + rot + temp_value.substring(12, 16);


        //performing inverse mix column
        temp_value = inverseMixColumn(temp_value);

        //inverse shift row again
        temp_value = shiftRow(temp_value);

        //inverse nibble substitution
        temp_value = inverseNibbleSubsitution(temp_value.substring(0, 8)) +
                inverseNibbleSubsitution(temp_value.substring(8, 16));


        //Adding Round 0 key to get the plaintext
        String plaintext = xor(temp_value, W[0] + W[1]);
        System.out.println(plaintext);
//        System.out.println(temp_value);


    }


    public static String shiftRow(String str) {

        String s1 = str.substring(0, 4);
        String s2 = str.substring(4, 8);
        String s3 = str.substring(8, 12);
        String s4 = str.substring(12, 16);
        return s1 + s4 + s3 + s2;


    }


    public static void keyGeneration(String key) {
        // Key 0 is W[0]+W[1]
        // Key 1 is W[2]+W[3]
        // Key 02 is W[4]+W[5]

        W[0] = key.substring(0, 8);
        W[1] = key.substring(8, 16);
        W[2] = xor(xor(W[0], "10000000"), nibbleSubstitution(rotateNibble(W[1])));
        W[3] = xor(W[2], W[1]);
        W[4] = xor(xor(W[2], "00110000"), nibbleSubstitution(rotateNibble(W[3])));
        W[5] = xor(W[4], W[3]);


    }

    public static String rotateNibble(String key) {

        //swaps the nibbles

        String s1 = key.substring(0, 4);
        String s2 = key.substring(4, 8);
        return s2 + s1;


    }

    public static String nibbleSubstitution(String key) {
        // using s box to substitute nibbles
        String s1 = sbox[Integer.parseInt(key.substring(0, 4), 2)];
        String s2 = sbox[Integer.parseInt(key.substring(4, 8), 2)];
        return s1 + s2;
    }

    public static String inverseNibbleSubsitution(String key) {
        // using inverse s box to substitute nibbles
        String s1 = sbox_inverse[Integer.parseInt(key.substring(0, 4), 2)];
        String s2 = sbox_inverse[Integer.parseInt(key.substring(4, 8), 2)];
        return s1 + s2;


    }


    public static String mixColumn(String str) {

        String s00 = xor(str.substring(0, 4), multiplication("0100", str.substring(8, 12)));
        String s10 = xor(str.substring(8, 12), multiplication("0100", str.substring(0, 4)));
        String s01 = xor(str.substring(4, 8), multiplication("0100", str.substring(12, 16)));
        String s11 = xor(str.substring(12, 16), multiplication("0100", str.substring(4, 8)));
        return s00 + s10 + s01 + s11;
    }

    public static String inverseMixColumn(String str) {
        String s00 = xor(multiplication("1001", str.substring(0, 4)),
                multiplication("0010", str.substring(8, 12)));


        String s10 = xor(multiplication("1001", str.substring(8, 12)),
                multiplication("0010", str.substring(0, 4)));

        String s01 = xor(multiplication("1001", str.substring(4, 8)),
                multiplication("0010", str.substring(12, 16)));

        String s11 = xor(multiplication("1001", str.substring(12, 16)),
                multiplication("0010", str.substring(4, 8)));

        return s00 + s10 + s01 + s11;


    }


    public static String multiplication(String s1, String s2) {
        // multiplication of two polynomials provided in the mix column cols
        int t1 = Integer.parseInt(s1, 2);
        int t2 = Integer.parseInt(s2, 2);
        int p = 0;
        while (t2 > 0) {
            if ((t2 & 0b1) != 0) {
                p ^= t1;


            }

            t1 <<= 1;
            if (((t1 & 0b10000) != 0))
                t1 ^= 0b11;
            t2 >>= 1;
        }
        int val = p & 0b1111;

        // adding zeroes to make strings even
        String ans = "";
        if (Integer.toBinaryString(val).length() < 4) {
            int temp = 4 - Integer.toBinaryString(val).length();
            for (int i = 0; i < temp; i++) {
                ans = ans + "0";

            }
            ans = ans + Integer.toBinaryString(val);
            return ans;

        } else
            return Integer.toBinaryString(val);


    }


    public static String xor(String a, String b) {
        // performing xor on 2 input values
        String xor = "";
        for (int i = 0; i < a.length(); i++) {
            if (a.charAt(i) == b.charAt(i))
                xor = xor + "0";
            else
                xor = xor + "1";
        }
        return xor;
    }

}
