package com.ostrovskyi;

import java.util.BitSet;

/**
 * Created by Альберт on 20.02.2016.
 */
public class Des {

    private static final int BLOCK_LENGTH = 64;
    private static final int SUBSTITUTION_BLOCK_LENGTH = 6;
    private static final int COUNT_OF_BIT_IN_SUBSTITUTION_BOX_NUMBER = 4;
    private static final int ITERATION_COUNT = 16;

    public String encode(String inputText, BitSet key){
        BitSet inputBitSet = DesConverter.convertFourCharactersLineToBitSet(inputText, BLOCK_LENGTH);
        BitSet initialRearrangedBitSet = initialPermutation(inputBitSet);

        BitSet leftPart = new BitSet(BLOCK_LENGTH / 2);
        BitSet rightPart = new BitSet(BLOCK_LENGTH / 2);
        initializeLeftAndRightParts(initialRearrangedBitSet, leftPart, rightPart);

        for(int i = 0; i < ITERATION_COUNT; i++){
            BitSet encryptedBitSet = encryption(rightPart, key);
            encryptedBitSet.xor(leftPart);
            leftPart = rightPart;
            rightPart = encryptedBitSet;
        }
        BitSet concatenatedParts = concatenation(leftPart, rightPart);
        BitSet finalBitSet = finalPermutation(concatenatedParts);
        return null;
    }

    private BitSet initialPermutation(BitSet inputBitSet){
        return transposition(inputBitSet, DesTables.initialPermutation);
    }

    private BitSet transposition(BitSet input, int[] table){
        BitSet result = new BitSet(table.length);
        for(int i = 0; i < table.length; i++){
            result.set(i, input.get(table[i]));
        }
        return result;
    }

    private void initializeLeftAndRightParts(BitSet source, BitSet leftPart, BitSet rightPart){
        int lengthOfOnePart = BLOCK_LENGTH / 2;
        for(int i = 0; i < lengthOfOnePart; i++){
            leftPart.set(i, source.get(i));
            rightPart.set(i, source.get(i + lengthOfOnePart));
        }
    }

    private BitSet encryption(BitSet rightPart, BitSet key){
        BitSet expansionRightPart = expansion(rightPart);
        expansionRightPart.xor(key);
        BitSet transformedBitSet = transformation(expansionRightPart);
        return permutation(transformedBitSet);
    }

    private BitSet expansion(BitSet rightPart){
        return transposition(rightPart, DesTables.expansionFunction);
    }

    private BitSet transformation(BitSet input){
        BitSet result = new BitSet(BLOCK_LENGTH / 2);
        for(int i = 0; i < DesTables.substitutionBoxes.length; i++){
            int ofsset = i * SUBSTITUTION_BLOCK_LENGTH;
            int row = getNumberFromBooleans(input.get(ofsset), input.get(ofsset + 5));
            int column = getNumberFromBooleans(input.get(ofsset + 1), input.get(ofsset + 2),
                                                input.get(ofsset + 3), input.get(ofsset + 4));
            setSubstitutionBlockNumberInBitSet(result, i * COUNT_OF_BIT_IN_SUBSTITUTION_BOX_NUMBER,
                                                DesTables.substitutionBoxes[i][row][column]);
        }
        return result;
    }

    private int getNumberFromBooleans(boolean... booleans){
        int result = 0;
        for(int i = booleans.length - 1; i >= 0; i--){
            if(booleans[i]) {
                result += (int) Math.pow(2, booleans.length - 1 - i);
            }
        }
        return result;
    }

    private void setSubstitutionBlockNumberInBitSet(BitSet bitSet, int startIndex, int number){
        for(int i = COUNT_OF_BIT_IN_SUBSTITUTION_BOX_NUMBER - 1; i >= 0; i--){
            bitSet.set(startIndex + i, number % 2 == 0);
            number /= 2;
        }
    }

    private BitSet permutation(BitSet input){
        return transposition(input, DesTables.permutation);
    }

    private BitSet concatenation(BitSet leftPart, BitSet rightPart){
        BitSet result = new BitSet(leftPart.size() + rightPart.size());
        for(int i = 0; i < leftPart.size(); i++){
            result.set(i, leftPart.get(i));
        }
        for(int i = 0; i < rightPart.size(); i++){
            result.set(i + leftPart.size(), rightPart.get(i));
        }
        return result;
    }

    private BitSet finalPermutation(BitSet input){
        return transposition(input, DesTables.finalPermutation);
    }
}
