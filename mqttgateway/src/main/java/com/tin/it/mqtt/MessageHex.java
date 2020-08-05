package com.tin.it.mqtt;

public class MessageHex {

    private static final char HexCharArr[] = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};

    private static final String HexStr = "0123456789ABCDEF";

    public static String byteArrToHex(byte[] btArr) {
        char strArr[] = new char[btArr.length * 2];
        int i = 0;
        for (byte bt : btArr) {
            strArr[i++] = HexCharArr[bt>>>4 & 0xf];
            strArr[i++] = HexCharArr[bt & 0xf];
        }
        return new String(strArr);
    }

    public static byte[] hexToByteArr(String hexStr) {
        char[] charArr = hexStr.toCharArray();
        byte btArr[] = new byte[charArr.length / 2];
        int index = 0;
        for (int i = 0; i < charArr.length; i++) {
            int highBit = HexStr.indexOf(charArr[i]);
            int lowBit = HexStr.indexOf(charArr[++i]);
            btArr[index] = (byte) (highBit << 4 | lowBit);
            index++;
        }
        return btArr;
    }

    public static void main(String[] args) {
        String srcStr = "04000417";
        String hex = byteArrToHex(srcStr.getBytes());
        System.out.println(hex);
    }
}
