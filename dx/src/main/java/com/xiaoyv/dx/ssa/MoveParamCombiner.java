

package com.xiaoyv.dx.ssa;

import com.xiaoyv.dx.rop.code.CstInsn;
import com.xiaoyv.dx.rop.code.LocalItem;
import com.xiaoyv.dx.rop.code.RegOps;
import com.xiaoyv.dx.rop.code.RegisterSpec;
import com.xiaoyv.dx.rop.cst.CstInteger;

import java.util.HashSet;
import java.util.List;

/**
 * Combine identical move-param insns, which may result from Ropper's
 * handling of synchronized methods.
 */
public class MoveParamCombiner {

    /**
     * method to process
     */
    private final SsaMethod ssaMeth;

    /**
     * Processes a method with this optimization step.
     *
     * @param ssaMethod method to process
     */
    public static void process(SsaMethod ssaMethod) {
        new MoveParamCombiner(ssaMethod).run();
    }

    private MoveParamCombiner(SsaMethod ssaMeth) {
        this.ssaMeth = ssaMeth;
    }

    /**
     * Runs this optimization step.
     */
    private void run() {
        // This will contain the definition specs for each parameter
        final RegisterSpec[] paramSpecs
                = new RegisterSpec[ssaMeth.getParamWidth()];

        // Insns to delete when all done
        final HashSet<SsaInsn> deletedInsns = new HashSet();

        ssaMeth.forEachInsn(new SsaInsn.Visitor() {
            public void visitMoveInsn(NormalSsaInsn insn) {
            }

            public void visitPhiInsn(PhiInsn phi) {
            }

            public void visitNonMoveInsn(NormalSsaInsn insn) {
                if (insn.getOpcode().getOpcode() != RegOps.MOVE_PARAM) {
                    return;
                }

                int param = getParamIndex(insn);

                if (paramSpecs[param] == null) {
                    paramSpecs[param] = insn.getResult();
                } else {
                    final RegisterSpec specA = paramSpecs[param];
                    final RegisterSpec specB = insn.getResult();
                    LocalItem localA = specA.getLocalItem();
                    LocalItem localB = specB.getLocalItem();
                    LocalItem newLocal;

                    /*
                     * Is there local information to preserve?
                     */

                    if (localA == null) {
                        newLocal = localB;
                    } else if (localB == null) {
                        newLocal = localA;
                    } else if (localA.equals(localB)) {
                        newLocal = localA;
                    } else {
                        /*
                         * Oddly, these two identical move-params have distinct
                         * debug info. We'll just keep them distinct.
                         */
                        return;
                    }

                    ssaMeth.getDefinitionForRegister(specA.getReg())
                            .setResultLocal(newLocal);

                    /*
                     * Map all uses of specB to specA
                     */

                    RegisterMapper mapper = new RegisterMapper() {
                        /** @inheritDoc */
                        public int getNewRegisterCount() {
                            return ssaMeth.getRegCount();
                        }

                        /** @inheritDoc */
                        public RegisterSpec map(RegisterSpec registerSpec) {
                            if (registerSpec.getReg() == specB.getReg()) {
                                return specA;
                            }

                            return registerSpec;
                        }
                    };

                    List<SsaInsn> uses
                            = ssaMeth.getUseListForRegister(specB.getReg());

                    // Use list is modified by mapSourceRegisters
                    for (int i = uses.size() - 1; i >= 0; i--) {
                        SsaInsn use = uses.get(i);
                        use.mapSourceRegisters(mapper);
                    }

                    deletedInsns.add(insn);
                }

            }
        });

        ssaMeth.deleteInsns(deletedInsns);
    }

    /**
     * Returns the parameter index associated with a move-param insn. Does
     * not verify that the insn is a move-param insn.
     *
     * @param insn {@code non-null;} a move-param insn
     * @return {@code >=0;} parameter index
     */
    private int getParamIndex(NormalSsaInsn insn) {
        CstInsn cstInsn = (CstInsn) (insn.getOriginalRopInsn());

        int param = ((CstInteger) cstInsn.getConstant()).getValue();
        return param;
    }

}