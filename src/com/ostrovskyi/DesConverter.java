package com.ostrovskyi;

import java.util.BitSet;

/**
 * Created by Альберт on 20.02.2016.
 */
public class DesConverter {

    private static final int COUNT_OF_UNICODE_CHARACTERS_IN_BLOCK = 4;

    public static BitSet convertFourCharactersLineToBitSet(String input, int blockLength){
        BitSet result = new BitSet(blockLength);
        char[] inputChars = input.toCharArray();
        for(int i = 0; i < inputChars.length; i++){
            int shift = i*16;
            int localChar = (int) inputChars[i];
            for(int j = 15; j >= 0; j--){
                result.set(shift + j, localChar % 2 == 1);
                localChar /= 2;
            }
        }
        return result;
    }

    public static String BitSetToString(BitSet input){
        StringBuilder result = new StringBuilder();
        for(int i = 0; i < input.size(); i += 16){
            char character = 0;
            for(int j = 15; j >= 0; j--){
                if(input.get(i + j)){
                    character += (int) Math.pow(2, 15 - j);
                }
            }
            result.append(character);
        }
        return result.toString();
    }
}
