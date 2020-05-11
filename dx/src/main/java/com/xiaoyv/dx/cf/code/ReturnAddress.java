

package com.xiaoyv.dx.cf.code;

import com.xiaoyv.dx.rop.type.Type;
import com.xiaoyv.dx.rop.type.TypeBearer;
import com.xiaoyv.dx.util.Hex;

/**
 * Representation of a subroutine return address. In Java verification,
 * somewhat counterintuitively, the salient bit of information you need to
 * know about a return address is the <i>start address</i> of the subroutine
 * being returned from, not the address being returned <i>to</i>, so that's
 * what instances of this class hang onto.
 */
public final class ReturnAddress implements TypeBearer {
    /**
     * {@code >= 0;} the start address of the subroutine being returned from
     */
    private final int subroutineAddress;

    /**
     * Constructs an instance.
     *
     * @param subroutineAddress {@code >= 0;} the start address of the
     *                          subroutine being returned from
     */
    public ReturnAddress(int subroutineAddress) {
        if (subroutineAddress < 0) {
            throw new IllegalArgumentException("subroutineAddress < 0");
        }

        this.subroutineAddress = subroutineAddress;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ("<addr:" + Hex.u2(subroutineAddress) + ">");
    }

    /**
     * {@inheritDoc}
     */
    public String toHuman() {
        return toString();
    }

    /**
     * {@inheritDoc}
     */
    public Type getType() {
        return Type.RETURN_ADDRESS;
    }

    /**
     * {@inheritDoc}
     */
    public TypeBearer getFrameType() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public int getBasicType() {
        return Type.RETURN_ADDRESS.getBasicType();
    }

    /**
     * {@inheritDoc}
     */
    public int getBasicFrameType() {
        return Type.RETURN_ADDRESS.getBasicFrameType();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isConstant() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof ReturnAddress)) {
            return false;
        }

        return subroutineAddress == ((ReturnAddress) other).subroutineAddress;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return subroutineAddress;
    }

    /**
     * Gets the subroutine address.
     *
     * @return {@code >= 0;} the subroutine address
     */
    public int getSubroutineAddress() {
        return subroutineAddress;
    }
}