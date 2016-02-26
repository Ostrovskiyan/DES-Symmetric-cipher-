package com.ostrovskyi;

import java.util.BitSet;

/**
 * Created by Альберт on 20.02.2016.
 */
public class DesConverter {

    private static final int BITS_IN_UNICODE_CHAR = 16;

    public static BitSet stringHexKeyToBitSet(String key){
        char[] keyChars = key.toCharArray();
        BitSet bitSet = new BitSet(64);
        for(int i = 0; i < keyChars.length; i++){
            switch (keyChars[i]){
                case '0':
                    setBitsToBitSet(bitSet, i*4, 0, 0, 0, 0);
                    break;
                case '1':
                    setBitsToBitSet(bitSet, i*4, 0, 0, 0, 1);
                    break;
                case '2':
                    setBitsToBitSet(bitSet, i*4, 0, 0, 1, 0);
                    break;
                case '3':
                    setBitsToBitSet(bitSet, i*4, 0, 0, 1, 1);
                    break;
                case '4':
                    setBitsToBitSet(bitSet, i*4, 0, 1, 0, 0);
                    break;
                case '5':
                    setBitsToBitSet(bitSet, i*4, 0, 1, 0, 1);
                    break;
                case '6':
                    setBitsToBitSet(bitSet, i*4, 0, 1, 1, 0);
                    break;
                case '7':
                    setBitsToBitSet(bitSet, i*4, 0, 1, 1, 1);
                    break;
                case '8':
                    setBitsToBitSet(bitSet, i*4, 1, 0, 0, 0);
                    break;
                case '9':
                    setBitsToBitSet(bitSet, i*4, 1, 0, 0, 1);
                    break;
                case 'A':
                    setBitsToBitSet(bitSet, i*4, 1, 0, 1, 0);
                    break;
                case 'B':
                    setBitsToBitSet(bitSet, i*4, 1, 0, 1, 1);
                    break;
                case 'C':
                    setBitsToBitSet(bitSet, i*4, 1, 1, 0, 0);
                    break;
                case 'D':
                    setBitsToBitSet(bitSet, i*4, 1, 1, 0, 1);
                    break;
                case 'E':
                    setBitsToBitSet(bitSet, i*4, 1, 1, 1, 0);
                    break;
                case 'F':
                    setBitsToBitSet(bitSet, i*4, 1, 1, 1, 1);
                    break;
            }
        }
        return bitSet;
    }

    private static void setBitsToBitSet(BitSet bitSet, int startIndex, int firstBit, int secondBit, int thirdBit, int fourthBit){
        bitSet.set(startIndex, firstBit == 1);
        bitSet.set(startIndex + 1, secondBit == 1);
        bitSet.set(startIndex + 2, thirdBit == 1);
        bitSet.set(startIndex + 3, fourthBit == 1);
    }

    public static BitSet convertFourCharactersLineToBitSet(char[] input, int startIndex, int blockLength){
        BitSet result = new BitSet(blockLength);
        for(int i = 0; i < blockLength / BITS_IN_UNICODE_CHAR; i++){
            int shift = i*BITS_IN_UNICODE_CHAR;
            int localChar = (int) input[startIndex + i];
            for(int j = BITS_IN_UNICODE_CHAR - 1; j >= 0; j--){
                result.set(shift + j, localChar % 2 == 1);
                localChar /= 2;
            }
        }
        return result;
    }

    public static String BitSetToString(BitSet input){
        StringBuilder result = new StringBuilder();
        for(int i = 0; i < input.length(); i += 16){
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
