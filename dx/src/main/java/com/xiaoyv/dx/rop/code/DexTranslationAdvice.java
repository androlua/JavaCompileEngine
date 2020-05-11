

package com.xiaoyv.dx.rop.code;

import com.xiaoyv.dx.rop.cst.CstInteger;
import com.xiaoyv.dx.rop.type.Type;

/**
 * Implementation of {@link TranslationAdvice} which represents what
 * the dex format will be able to represent.
 */
public final class DexTranslationAdvice
        implements TranslationAdvice {
    /**
     * {@code non-null;} standard instance of this class
     */
    public static final DexTranslationAdvice THE_ONE =
            new DexTranslationAdvice();

    /**
     * debug advice for disabling invoke-range optimization
     */
    public static final DexTranslationAdvice NO_SOURCES_IN_ORDER =
            new DexTranslationAdvice(true);

    /**
     * The minimum source width, in register units, for an invoke
     * instruction that requires its sources to be in order and contiguous.
     */
    private static final int MIN_INVOKE_IN_ORDER = 6;

    /**
     * when true: always returns false for requiresSourcesInOrder
     */
    private final boolean disableSourcesInOrder;

    /**
     * This class is not publicly instantiable. Use {@link #THE_ONE}.
     */
    private DexTranslationAdvice() {
        disableSourcesInOrder = false;
    }

    private DexTranslationAdvice(boolean disableInvokeRange) {
        this.disableSourcesInOrder = disableInvokeRange;
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasConstantOperation(Rop opcode,
                                        RegisterSpec sourceA, RegisterSpec sourceB) {
        if (sourceA.getType() != Type.INT) {
            return false;
        }

        // Return false if second source isn't a constant
        if (!(sourceB.getTypeBearer() instanceof CstInteger)) {
            // Except for rsub-int (reverse sub) where first source is constant
            if (sourceA.getTypeBearer() instanceof CstInteger &&
                    opcode.getOpcode() == RegOps.SUB) {
                CstInteger cst = (CstInteger) sourceA.getTypeBearer();
                return cst.fitsIn16Bits();
            } else {
                return false;
            }
        }

        CstInteger cst = (CstInteger) sourceB.getTypeBearer();

        switch (opcode.getOpcode()) {
            // These have 8 and 16 bit cst representations
            case RegOps.REM:
            case RegOps.ADD:
            case RegOps.MUL:
            case RegOps.DIV:
            case RegOps.AND:
            case RegOps.OR:
            case RegOps.XOR:
                return cst.fitsIn16Bits();
            // These only have 8 bit cst reps
            case RegOps.SHL:
            case RegOps.SHR:
            case RegOps.USHR:
                return cst.fitsIn8Bits();
            // No sub-const insn, so check if equivalent add-const fits
            case RegOps.SUB:
                CstInteger cst2 = CstInteger.make(-cst.getValue());
                return cst2.fitsIn16Bits();
            default:
                return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean requiresSourcesInOrder(Rop opcode,
                                          RegisterSpecList sources) {

        return !disableSourcesInOrder && opcode.isCallLike()
                && totalRopWidth(sources) >= MIN_INVOKE_IN_ORDER;
    }

    /**
     * Calculates the total rop width of the list of SSA registers
     *
     * @param sources {@code non-null;} list of SSA registers
     * @return {@code >= 0;} rop-form width in register units
     */
    private int totalRopWidth(RegisterSpecList sources) {
        int sz = sources.size();
        int total = 0;

        for (int i = 0; i < sz; i++) {
            total += sources.get(i).getCategory();
        }

        return total;
    }

    /**
     * {@inheritDoc}
     */
    public int getMaxOptimalRegisterCount() {
        return 16;
    }
}