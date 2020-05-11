

package com.xiaoyv.dx.rop.type;

import com.xiaoyv.dx.util.ToHuman;

/**
 * Object which has an associated type, possibly itself.
 */
public interface TypeBearer
        extends ToHuman {
    /**
     * Gets the type associated with this instance.
     *
     * @return {@code non-null;} the type
     */
    Type getType();

    /**
     * Gets the frame type corresponding to this type. This method returns
     * {@code this}, except if {@link Type#isIntlike} on the underlying
     * type returns {@code true} but the underlying type is not in
     * fact {@link Type#INT}, in which case this method returns an instance
     * whose underlying type <i>is</i> {@code INT}.
     *
     * @return {@code non-null;} the frame type for this instance
     */
    TypeBearer getFrameType();

    /**
     * Gets the basic type corresponding to this instance.
     *
     * @return the basic type; one of the {@code BT_*} constants
     * defined by {@link Type}
     */
    int getBasicType();

    /**
     * Gets the basic type corresponding to this instance's frame type. This
     * is equivalent to {@code getFrameType().getBasicType()}, and
     * is the same as calling {@code getFrameType()} unless this
     * instance is an int-like type, in which case this method returns
     * {@code BT_INT}.
     *
     * @return the basic frame type; one of the {@code BT_*} constants
     * defined by {@link Type}
     * @see #getBasicType
     * @see #getFrameType
     */
    int getBasicFrameType();

    /**
     * Returns whether this instance represents a constant value.
     *
     * @return {@code true} if this instance represents a constant value
     * and {@code false} if not
     */
    boolean isConstant();
}