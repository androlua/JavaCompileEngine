

package com.xiaoyv.dx.dex.code;

import com.xiaoyv.dx.rop.code.RegisterSpecList;
import com.xiaoyv.dx.rop.code.SourcePosition;
import com.xiaoyv.dx.util.AnnotatedOutput;

/**
 * Pseudo-instruction base class for zero-size (no code emitted)
 * instructions, which are generally used for tracking metainformation
 * about the code they are adjacent to.
 */
public abstract class ZeroSizeInsn extends DalvInsn {
    /**
     * Constructs an instance. The output address of this instance is initially
     * unknown ({@code -1}).
     *
     * @param position {@code non-null;} source position
     */
    public ZeroSizeInsn(SourcePosition position) {
        super(Dops.SPECIAL_FORMAT, position, RegisterSpecList.EMPTY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int codeSize() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void writeTo(AnnotatedOutput out) {
        // Nothing to do here, for this class.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final DalvInsn withOpcode(Dop opcode) {
        throw new RuntimeException("unsupported");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DalvInsn withRegisterOffset(int delta) {
        return withRegisters(getRegisters().withOffset(delta));
    }
}