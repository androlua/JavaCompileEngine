

package com.xiaoyv.dx.dex.file;

import com.xiaoyv.dex.SizeOf;
import com.xiaoyv.dx.rop.cst.CstString;
import com.xiaoyv.dx.rop.cst.CstType;
import com.xiaoyv.dx.util.AnnotatedOutput;
import com.xiaoyv.dx.util.Hex;

/**
 * Representation of a type reference inside a Dalvik file.
 */
public final class TypeIdItem extends IdItem {
    /**
     * Constructs an instance.
     *
     * @param type {@code non-null;} the constant for the type
     */
    public TypeIdItem(CstType type) {
        super(type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ItemType itemType() {
        return ItemType.TYPE_TYPE_ID_ITEM;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int writeSize() {
        return SizeOf.TYPE_ID_ITEM;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addContents(DexFile file) {
        file.getStringIds().intern(getDefiningClass().getDescriptor());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeTo(DexFile file, AnnotatedOutput out) {
        CstType type = getDefiningClass();
        CstString descriptor = type.getDescriptor();
        int idx = file.getStringIds().indexOf(descriptor);

        if (out.annotates()) {
            out.annotate(0, indexString() + ' ' + descriptor.toHuman());
            out.annotate(4, "  descriptor_idx: " + Hex.u4(idx));
        }

        out.writeInt(idx);
    }
}