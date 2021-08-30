import java.util.Arrays;

public class MixColumns {

    private static int[][] matrix = { { 0x03, 0x0b }, { 0x01, 0x0d },
            { 0x01, 0x09 }, { 0x02, 0x0e } };

    private final int a, b, c, d;

    public MixColumns(boolean decrypt) {
        int arrayIndex = decrypt ? 1 : 0;
        a = matrix[0][arrayIndex];
        b = matrix[1][arrayIndex];
        c = matrix[2][arrayIndex];
        d = matrix[3][arrayIndex];
    }

    // Multiplies two bytes in garlois field 2^8
    private int multiply(int a, int b) {
        int returnValue = 0;
        int temp = 0;
        while (a != 0) {
            if ((a & 1) != 0)
                returnValue = (returnValue ^ b);
            temp =  (b & 0x80);
            b =  (b << 1);
            if (temp != 0)
                b =  (b ^ 0x1b);
            a =  ((a & 0xff) >> 1);
        }
        return returnValue;
    }
    public String[][]mixCols(String[][]a){
        int[][]t=new int[4][4];
        for(int i=0; i<4;i++)
            for(int j=0; j<4; j++)
                t[i][j]=(int)Long.parseUnsignedLong(a[i][j],16);
        t=mixColumns(t);
        for(int i=0; i<4; i++)
            for(int j=0; j<4; j++){
                if(256<=t[i][j])t[i][j]-=256;
                a[i][j]=Long.toHexString(t[i][j]);
                while(a[i][j].length()<2)
                    a[i][j]='0'+a[i][j];
            }
        return a;
    }
    public int[][] mixColumns(int[][] input) {
        int[] temp = new int[4];
        for (int i = 0; i < 4; i++) {
            temp[0] = multiply(d, input[0][i]) ^ multiply(a, input[1][i])
                    ^ multiply(b, input[2][i]) ^ multiply(c, input[3][i]);
            temp[1] = multiply(c, input[0][i]) ^ multiply(d, input[1][i])
                    ^ multiply(a, input[2][i]) ^ multiply(b, input[3][i]);
            temp[2] = multiply(b, input[0][i]) ^ multiply(c, input[1][i])
                    ^ multiply(d, input[2][i]) ^ multiply(a, input[3][i]);
            temp[3] = multiply(a, input[0][i]) ^ multiply(b, input[1][i])
                    ^ multiply(c, input[2][i]) ^ multiply(d, input[3][i]);
            for (int j = 0; j < 4; j++)
                input[j][i] =  (temp[j]);
        }
        return input;
    }
}