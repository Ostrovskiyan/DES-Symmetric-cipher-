package com.ostrovskyi;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.BitSet;

/**
 * Created by Альберт on 20.02.2016.
 */
public class Des {

    public enum Mode{
        ENCODE, DECODE
    }

    private static final int BLOCK_LENGTH = 64;
    private static final int SUBSTITUTION_BLOCK_LENGTH = 6;
    private static final int COUNT_OF_BIT_IN_SUBSTITUTION_BOX_NUMBER = 4;
    private static final int COUNT_OF_ROUNDS = 16;

    private static final int LENGTH_OF_KEY_WITHOUT_CHECK_BITS = 56;
    private static final int COUNT_OF_UNICODE_CHARACTERS_IN_BLOCK = 4;

    public String encode(String inputText, String key){
        return cipherAlgorithm(inputText, key, Mode.ENCODE);
    }

    public String decode(String inputText, String key){
        return cipherAlgorithm(inputText, key, Mode.DECODE);
    }

    private String cipherAlgorithm(String inputText, String key, Mode mode){
        char[] inputChars = inputText.toCharArray();
        if(inputChars.length % COUNT_OF_UNICODE_CHARACTERS_IN_BLOCK != 0){
            inputChars = addMissingCharacters(inputChars);
        }

        BitSet bitSetKey = DesConverter.stringHexKeyToBitSet(key);
        BitSet[] roundKeys = getRoundKeys(bitSetKey);
        if(mode.equals(Mode.DECODE)){
            reverse(roundKeys);
        }

        StringBuilder result = new StringBuilder();
        for(int i = 0; i < inputChars.length; i += COUNT_OF_UNICODE_CHARACTERS_IN_BLOCK){
            result.append(encodeFourCharacterString(inputChars, i, roundKeys));
        }
        return result.toString();
    }

    private char[] addMissingCharacters(char[] inputChars){
        int countMissingCharacters = COUNT_OF_UNICODE_CHARACTERS_IN_BLOCK -
                (inputChars.length % COUNT_OF_UNICODE_CHARACTERS_IN_BLOCK);
        char[] tempArray = Arrays.copyOf(inputChars, inputChars.length + countMissingCharacters);
        for(int i = 0; i < countMissingCharacters; i++){
            tempArray[i + inputChars.length] = ' ';
        }
        return tempArray;
    }

    private void reverse(BitSet[] bitSetArray){
        for(int i = 0; i < bitSetArray.length / 2; i++){
            BitSet temp = bitSetArray[i];
            bitSetArray[i] = bitSetArray[bitSetArray.length - i - 1];
            bitSetArray[bitSetArray.length - i - 1] = temp;
        }
    }

    private String encodeFourCharacterString(char[] inputText, int startIndex, BitSet[] roundKeys){
        BitSet inputBitSet = DesConverter.convertFourCharactersLineToBitSet(inputText, startIndex, BLOCK_LENGTH);
        BitSet initialRearrangedBitSet = initialPermutation(inputBitSet);

        BitSet leftPart = new BitSet(BLOCK_LENGTH / 2);
        BitSet rightPart = new BitSet(BLOCK_LENGTH / 2);
        initializeLeftAndRightParts(initialRearrangedBitSet, leftPart, rightPart);

        for(int i = 0; i < COUNT_OF_ROUNDS; i++){
            BitSet encryptedBitSet = encryption(rightPart, roundKeys[i]);
            encryptedBitSet.xor(leftPart);
            leftPart = rightPart;
            rightPart = encryptedBitSet;
        }

        BitSet concatenatedParts = concatenation(rightPart, leftPart);
        BitSet finalBitSet = finalPermutation(concatenatedParts);
        return DesConverter.BitSetToString(finalBitSet);
    }

    private BitSet initialPermutation(BitSet inputBitSet){
        return transposition(inputBitSet, DesTables.initialPermutation);
    }

    private BitSet transposition(BitSet input, int[] table){
        BitSet result = new BitSet(table.length);
        for(int i = 0; i < table.length; i++){
            result.set(i, input.get(table[i] - 1));
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
            bitSet.set(startIndex + i, number % 2 == 1);
            number /= 2;
        }
    }

    private BitSet permutation(BitSet input){
        return transposition(input, DesTables.permutation);
    }

    private BitSet concatenation(BitSet leftPart, BitSet rightPart){
        BitSet result = new BitSet(BLOCK_LENGTH);
        for(int i = 0; i < BLOCK_LENGTH / 2; i++){
            result.set(i, leftPart.get(i));
        }
        for(int i = 0; i < BLOCK_LENGTH / 2; i++){
            result.set(i + BLOCK_LENGTH / 2, rightPart.get(i));
        }
        return result;
    }

    private BitSet finalPermutation(BitSet input){
        return transposition(input, DesTables.finalPermutation);
    }

    private BitSet[] getRoundKeys(BitSet key){
        BitSet keyWithoutCheckBits = removeCheckBits(key);
        BitSet[] shiftedKeys = new BitSet[COUNT_OF_ROUNDS];
        BitSet[] roundKeys = new BitSet[COUNT_OF_ROUNDS];

        shiftedKeys[0] = desShiftKey(keyWithoutCheckBits, DesTables.keyRotations[0]);
        roundKeys[0] = finalKeyProcessing(shiftedKeys[0]);
        for(int i = 1; i < COUNT_OF_ROUNDS; i++){
            shiftedKeys[i] = desShiftKey(shiftedKeys[i - 1], DesTables.keyRotations[i]);
            roundKeys[i] = finalKeyProcessing(shiftedKeys[i]);
        }
        return roundKeys;
    }

    private BitSet removeCheckBits(BitSet key){
        return transposition(key, DesTables.initialKeyPreparing);
    }

    private BitSet desShiftKey(BitSet key, int shiftSize){
        BitSet newKey = new BitSet(LENGTH_OF_KEY_WITHOUT_CHECK_BITS);
        int halfLength = LENGTH_OF_KEY_WITHOUT_CHECK_BITS / 2;
        for(int i = 0; i < halfLength; i++){
            newKey.set(i, key.get((i + shiftSize) % halfLength));
            newKey.set(i + halfLength, key.get(((i + shiftSize) % halfLength) + halfLength));
        }
        return newKey;
    }

    private BitSet finalKeyProcessing(BitSet key){
        return transposition(key, DesTables.finalKeyProcessing);
    }
}
