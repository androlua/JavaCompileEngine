

package com.xiaoyv.dx.rop.code;

import com.xiaoyv.dx.rop.type.Type;
import com.xiaoyv.dx.rop.type.TypeList;

/**
 * Instruction which possibly throws. The {@code successors} list in the
 * basic block an instance of this class is inside corresponds in-order to
 * the list of exceptions handled by this instruction, with the
 * no-exception case appended as the final target.
 */
public final class ThrowingInsn
        extends Insn {
    /**
     * {@code non-null;} list of exceptions caught
     */
    private final TypeList catches;

    /**
     * Gets the string form of a register spec list to be used as a catches
     * list.
     *
     * @param catches {@code non-null;} the catches list
     * @return {@code non-null;} the string form
     */
    public static String toCatchString(TypeList catches) {
        StringBuffer sb = new StringBuffer(100);

        sb.append("catch");

        int sz = catches.size();
        for (int i = 0; i < sz; i++) {
            sb.append(" ");
            sb.append(catches.getType(i).toHuman());
        }

        return sb.toString();
    }

    /**
     * Constructs an instance.
     *
     * @param opcode   {@code non-null;} the opcode
     * @param position {@code non-null;} source position
     * @param sources  {@code non-null;} specs for all the sources
     * @param catches  {@code non-null;} list of exceptions caught
     */
    public ThrowingInsn(Rop opcode, SourcePosition position,
                        RegisterSpecList sources,
                        TypeList catches) {
        super(opcode, position, null, sources);

        if (opcode.getBranchingness() != Rop.BRANCH_THROW) {
            throw new IllegalArgumentException("bogus branchingness");
        }

        if (catches == null) {
            throw new NullPointerException("catches == null");
        }

        this.catches = catches;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getInlineString() {
        return toCatchString(catches);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeList getCatches() {
        return catches;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void accept(Visitor visitor) {
        visitor.visitThrowingInsn(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Insn withAddedCatch(Type type) {
        return new ThrowingInsn(getOpcode(), getPosition(),
                getSources(), catches.withAddedType(type));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Insn withRegisterOffset(int delta) {
        return new ThrowingInsn(getOpcode(), getPosition(),
                getSources().withOffset(delta),
                catches);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Insn withNewRegisters(RegisterSpec result,
                                 RegisterSpecList sources) {

        return new ThrowingInsn(getOpcode(), getPosition(),
                sources,
                catches);
    }
}