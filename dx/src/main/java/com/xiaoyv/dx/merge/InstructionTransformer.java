

package com.xiaoyv.dx.merge;

import com.xiaoyv.dex.DexException;
import com.xiaoyv.dex.DexIndexOverflowException;
import com.xiaoyv.dx.io.CodeReader;
import com.xiaoyv.dx.io.Opcodes;
import com.xiaoyv.dx.io.instructions.DecodedInstruction;
import com.xiaoyv.dx.io.instructions.ShortArrayCodeOutput;

final class InstructionTransformer {
    private final CodeReader reader;

    private DecodedInstruction[] mappedInstructions;
    private int mappedAt;
    private IndexMap indexMap;

    public InstructionTransformer() {
        this.reader = new CodeReader();
        this.reader.setAllVisitors(new GenericVisitor());
        this.reader.setStringVisitor(new StringVisitor());
        this.reader.setTypeVisitor(new TypeVisitor());
        this.reader.setFieldVisitor(new FieldVisitor());
        this.reader.setMethodVisitor(new MethodVisitor());
    }

    public short[] transform(IndexMap indexMap, short[] encodedInstructions) throws DexException {
        DecodedInstruction[] decodedInstructions =
                DecodedInstruction.decodeAll(encodedInstructions);
        int size = decodedInstructions.length;

        this.indexMap = indexMap;
        mappedInstructions = new DecodedInstruction[size];
        mappedAt = 0;
        reader.visitAll(decodedInstructions);

        ShortArrayCodeOutput out = new ShortArrayCodeOutput(size);
        for (DecodedInstruction instruction : mappedInstructions) {
            if (instruction != null) {
                instruction.encode(out);
            }
        }

        this.indexMap = null;
        return out.getArray();
    }

    private class GenericVisitor implements CodeReader.Visitor {
        public void visit(DecodedInstruction[] all, DecodedInstruction one) {
            mappedInstructions[mappedAt++] = one;
        }
    }

    private class StringVisitor implements CodeReader.Visitor {
        public void visit(DecodedInstruction[] all, DecodedInstruction one) {
            int stringId = one.getIndex();
            int mappedId = indexMap.adjustString(stringId);
            boolean isJumbo = (one.getOpcode() == Opcodes.CONST_STRING_JUMBO);
            jumboCheck(isJumbo, mappedId);
            mappedInstructions[mappedAt++] = one.withIndex(mappedId);
        }
    }

    private class FieldVisitor implements CodeReader.Visitor {
        public void visit(DecodedInstruction[] all, DecodedInstruction one) {
            int fieldId = one.getIndex();
            int mappedId = indexMap.adjustField(fieldId);
            boolean isJumbo = (one.getOpcode() == Opcodes.CONST_STRING_JUMBO);
            jumboCheck(isJumbo, mappedId);
            mappedInstructions[mappedAt++] = one.withIndex(mappedId);
        }
    }

    private class TypeVisitor implements CodeReader.Visitor {
        public void visit(DecodedInstruction[] all, DecodedInstruction one) {
            int typeId = one.getIndex();
            int mappedId = indexMap.adjustType(typeId);
            boolean isJumbo = (one.getOpcode() == Opcodes.CONST_STRING_JUMBO);
            jumboCheck(isJumbo, mappedId);
            mappedInstructions[mappedAt++] = one.withIndex(mappedId);
        }
    }

    private class MethodVisitor implements CodeReader.Visitor {
        public void visit(DecodedInstruction[] all, DecodedInstruction one) {
            int methodId = one.getIndex();
            int mappedId = indexMap.adjustMethod(methodId);
            boolean isJumbo = (one.getOpcode() == Opcodes.CONST_STRING_JUMBO);
            jumboCheck(isJumbo, mappedId);
            mappedInstructions[mappedAt++] = one.withIndex(mappedId);
        }
    }

    private static void jumboCheck(boolean isJumbo, int newIndex) {
        if (!isJumbo && (newIndex > 0xffff)) {
            throw new DexIndexOverflowException("Cannot merge new index " + newIndex +
                    " into a non-jumbo instruction!");
        }
    }
}