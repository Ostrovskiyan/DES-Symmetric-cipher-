package com.ostrovskyi;

import java.util.BitSet;

public class Main {

    public static void main(String[] args) {
        Des des = new Des();
        String key = "AABB09182736CCDD";
        String text = "Some text about something";
        String encodeText = des.encode(text, key);
        String decodeText = des.decode(encodeText, key);
        System.out.println(text);
        System.out.println(encodeText);
        System.out.println(decodeText);
    }

    private static char[] hexChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static void printBitSet(BitSet bitSet, int length){
        for(int i = 0; i < length; i += 4){
            int number = 0;
            for(int j = 3; j >= 0; j--){
                if(bitSet.get(i + j)){
                    number += (int) Math.pow(2, 3 - j);
                }
            }
            System.out.print(hexChars[number]);
            /*for(int j = 0; j < 4; j++){
                if(bitSet.get(i + j)) {
                    System.out.print(1);
                } else {
                    System.out.print(0);
                }
            }
            System.out.print(' ');
            if(i%28 == 0){
                //System.out.println();
            }*/
        }
        System.out.println('~');
    }
}
