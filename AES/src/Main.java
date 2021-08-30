import java.math.BigInteger;
import java.util.Scanner;

public class Main {

    private static String[][] input;
    private static String[][] key;
    public static String[][] sBox = {{"63", "7c", "77", "7b", "f2", "6b", "6f", "c5", "30", "01", "67", "2b", "fe", "d7", "ab", "76"},
            {"ca", "82", "c9", "7d", "fa", "59", "47", "f0", "ad", "d4", "a2", "af", "9c", "a4", "72", "c0"},
            {"b7", "fd", "93", "26", "36", "3f", "f7", "cc", "34", "a5", "e5", "f1", "71", "d8", "31", "15"},
            {"04", "c7", "23", "c3", "18", "96", "05", "9a", "07", "12", "80", "e2", "eb", "27", "b2", "75"},
            {"09", "83", "2c", "1a", "1b", "6e", "5a", "a0", "52", "3b", "d6", "b3", "29", "e3", "2f", "84"},
            {"53", "d1", "00", "ed", "20", "fc", "b1", "5b", "6a", "cb", "be", "39", "4a", "4c", "58", "cf"},
            {"d0", "ef", "aa", "fb", "43", "4d", "33", "85", "45", "f9", "02", "7f", "50", "3c", "9f", "a8"},
            {"51", "a3", "40", "8f", "92", "9d", "38", "f5", "bc", "b6", "da", "21", "10", "ff", "f3", "d2"},
            {"cd", "0c", "13", "ec", "5f", "97", "44", "17", "c4", "a7", "7e", "3d", "64", "5d", "19", "73"},
            {"60", "81", "4f", "dc", "22", "2a", "90", "88", "46", "ee", "b8", "14", "de", "5e", "0b", "db"},
            {"e0", "32", "3a", "0a", "49", "06", "24", "5c", "c2", "d3", "ac", "62", "91", "95", "e4", "79"},
            {"e7", "c8", "37", "6d", "8d", "d5", "4e", "a9", "6c", "56", "f4", "ea", "65", "7a", "ae", "08"},
            {"ba", "78", "25", "2e", "1c", "a6", "b4", "c6", "e8", "dd", "74", "1f", "4b", "bd", "8b", "8a"},
            {"70", "3e", "b5", "66", "48", "03", "f6", "0e", "61", "35", "57", "b9", "86", "c1", "1d", "9e"},
            {"e1", "f8", "98", "11", "69", "d9", "8e", "94", "9b", "1e", "87", "e9", "ce", "55", "28", "df"},
            {"8c", "a1", "89", "0d", "bf", "e6", "42", "68", "41", "99", "2d", "0f", "b0", "54", "bb", "16"}};


    public static String[][] Rcon = {{"01", "02", "04", "08", "10", "20", "40", "80", "1b", "36"},
            {"00", "00", "00", "00", "00", "00", "00", "00", "00", "00"},
            {"00", "00", "00", "00", "00", "00", "00", "00", "00", "00"},
            {"00", "00", "00", "00", "00", "00", "00", "00", "00", "00"}};


    private static String[][] matrix = {{"02", "03","01","01"}, {"01", "02","03","01"},
            {"01", "01","02","03"}, {"03", "01","01","02"}};

    private static MixColumns  mxCol = new MixColumns(false);

    private static String[][] keySchedule(int round, String[][] key) {
        for (int i = 0; i < 4; i++) {
            if (i == 0) {
                String[] col3 = {key[0][3], key[1][3], key[2][3], key[3][3]};
                for (int j = 0; j < 4; j++) {
                    key[i][j] = xor(xor(key[j][i], generalSubBytes(colRotation(col3))[j]), Rcon[j][round]); //XOR[first in w0, first in col(wi-1 = 3), Rcon first col]
                }
            } else {
                for (int j = 0; j < 4; j++) {
                    key[i][j] = xor(key[j][i], key[j][i - 1]); //XOR[curr col, one before col]
                }
            }
        }
        return key;
    }

    private static String[] colRotation(String[] col) {
        String tmp = col[0];
        col[0] = col[1];
        col[1] = col[2];
        col[2] = col[3];
        col[3] = tmp;
        return col;
    }

    private static void subBytes() {
        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input.length; j++) {
                input[i][j] = sBox[Character.digit(input[i][j].charAt(0), 16)][Character.digit(input[i][j].charAt(1), 16)];
            }
        }
    } //WORK GOOD!

    private static String[] generalSubBytes(String[] input) {
        for (int i = 0; i < input.length; i++) {
            input[i] = sBox[Character.digit(input[i].charAt(0), 16)][Character.digit(input[i].charAt(1), 16)];
        }
        return input;
    }

    private static void addRoundKey() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                input[j][i] = xor(input[j][i], key[j][i]);
            }
        }
    } //WORK GOOD!

    private static void shiftRows() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < i; j++) {
                String tmp = input[i][0];
                input[i][0] = input[i][1];
                input[i][1] = input[i][2];
                input[i][2] = input[i][3];
                input[i][3] = tmp;
            }
        }
    }

    /*private static void mixColumns() { //Shift is maybe requierd.
        String[] column = new String[4];

        for (int i = 0; i < 4; i++) { //Going through each columns.
            for (int j = 0; j < 4; j++) {
                column[j] = input[j][i];
            }
            for (int j = 0; j < 4; j++) {//Going through each element in the column.
                input[j][i] = xor(xor(xor(column[0], matrix[j][0]),xor(column[1], matrix[j][1])),xor(xor(column[2], matrix[j][2]),xor(column[3], matrix[j][3])));
            }
        }
    }*/



    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        //System.out.println("Enter text to encrypt:");
        input = asciiToHexArray("qwertyasdfghzxcv");
        //System.out.println("Enter key:");
        key = asciiToHexArray("nonsensenonsense");
        String encrypted = encrypt();
        System.out.println(encrypted);
        //Decrypt decrypt = new Decrypt(encrypted, key); //TODO: the program think the key is null or something
    } //WORK GOOD!

    private static String encrypt() {
        addRoundKey(); //Initial round
        for (int i = 0; i < 10; i++) { //9 main rounds + final round
            subBytes();
            shiftRows();
            if (i != 9)
                mxCol.mixCols(input);
            key = keySchedule(i, key);
            addRoundKey();
        }
        return hexArrayToAscii(input);
    } //WORK GOOD!

    private static String[][] asciiToHexArray(String str) {
        String[][] ans = new String[4][4];
        for (int i = 0; i < str.length(); i++) {
            ans[i / 4][i % 4] = asciiToHex(str.charAt(i));
        }
        return ans;
    } //WORK GOOD!

    private static String asciiToHex(char arg) {
        String binaryStr = Integer.toBinaryString(arg);
        int decimal = Integer.parseInt(binaryStr, 2);
        String hexStr = Integer.toString(decimal, 16);
        return hexStr;
    } //WORK GOOD!

    private static String hexArrayToAscii(String[][] input) {
        String ans = "";
        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input[0].length; j++) {
                ans += (char) Integer.valueOf(input[i][j], 16).intValue(); //Going through each box and changing the hex to dec to ascii.
            }
        }
        return ans;
    } //WORK GOOD!

    private static String xor(String first, String sec) {
        String bin1 = new BigInteger(first, 16).toString(2);
        String bin2 = new BigInteger(sec, 16).toString(2);
        while (bin1.length() < bin2.length())
            bin1 = "0" + bin1;
        while (bin1.length() > bin2.length())
            bin2 = "0" + bin2;
        String ans = "";
        for (int i = 0; i < bin1.length(); i++) {
            ans += boolToBin(binToBool(bin1.charAt(i)) ^ binToBool(bin2.charAt(i)));
        }
        ans = Integer.toString(Integer.parseInt(ans, 2), 16);
        if (ans.length() < 2)
            ans = "0" + ans;
        return ans;
    }//WORK GOOD!

    private static boolean binToBool(char c) {
        if (c == '0')
            return false;
        return true;
    }//WORK GOOD!

    private static char boolToBin(boolean b) {
        if (b)
            return '1';
        return '0';
    }//WORK GOOD!

}

