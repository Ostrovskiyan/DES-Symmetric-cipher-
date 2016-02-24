package com.ostrovskyi;

import java.util.BitSet;

public class Main {

    public static void main(String[] args) {
        /*String input = "SomeText";
        char[] inputChars = input.toCharArray();
        System.out.println(inputChars.length);
        BitSet bitSet = new BitSet(64);
        for(int i = 0; i < input.length(); i += 4){
            for(int j = 0; j < 4; j++){
                int shift = j*16;
                int temp = (int) inputChars[i + j];
                for(int k = 15; k >= 0; k--){
                    bitSet.set(shift + k, temp % 2 == 1);
                    temp /= 2;
                }
            }
            for(int j = 0; j < 64; j++){
                if(j%8 == 0){
                    System.out.print('_');
                }
                if(bitSet.get(j)){
                    System.out.print(1);
                } else {
                    System.out.print(0);
                }
            }
            System.out.println();
        }
        System.out.println((char)0b0000_0000_0110_1111);*/
        System.out.println(DesTables.substitutionBoxes.length);
    }
}
