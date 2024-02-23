package llvm.bitcode;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import llvm.instructions.AllocaInstruction;
import llvm.instructions.BasicBlock;
import llvm.instructions.BinopInstruction;
import llvm.instructions.BrInstruction;
import llvm.instructions.CallInstruction;
import llvm.instructions.Cast;
import llvm.instructions.CastInstruction;
import llvm.instructions.CmpInstruction;
import llvm.instructions.ExtractEltInstruction;
import llvm.instructions.ExtractValueInstruction;
import llvm.instructions.FreeInstruction;
import llvm.instructions.FunctionBody;
import llvm.instructions.GEPInstruction;
import llvm.instructions.GetResultInstruction;
import llvm.instructions.IndirectBRInstruction;
import llvm.instructions.InsertEltInstruction;
import llvm.instructions.InsertValueInstruction;
import llvm.instructions.Instruction;
import llvm.instructions.InvokeInstruction;
import llvm.instructions.LoadInstruction;
import llvm.instructions.MallocInstruction;
import llvm.instructions.PhiInstruction;
import llvm.instructions.RegisterAssignment;
import llvm.instructions.RetInstruction;
import llvm.instructions.SelectInstruction;
import llvm.instructions.ShuffleVec2_8Instruction;
import llvm.instructions.ShuffleVecInstruction;
import llvm.instructions.StoreInstruction;
import llvm.instructions.SwitchInstruction;
import llvm.instructions.TerminatorInstruction;
import llvm.instructions.VSelectInstruction;
import llvm.instructions.VaargInstruction;
import llvm.instructions.BasicBlock.Handle;
import llvm.types.Type;
import llvm.values.AliasValue;
import llvm.values.BlockAddressValue;
import llvm.values.ConstantArrayValue;
import llvm.values.ConstantExpr;
import llvm.values.ConstantStructureValue;
import llvm.values.FunctionValue;
import llvm.values.GlobalVariable;
import llvm.values.IntegerValue;
import llvm.values.LabelValue;
import llvm.values.Module;
import llvm.values.ParameterAttributeMap;
import llvm.values.Value;
import llvm.values.VirtualRegister;
import llvm.values.FunctionValue.ArgumentValue;
import util.pair.Pair;

/**
 * This class is used to determine when two function bodies are equivalent.
 * We say they are equivalent when they have the same number of basic blocks,
 * and there is a simple graph isomorphism between the blocks, preserving adjacency
 * and instruction lists. The two functions are allowed to have different 
 * names, but their signatures must be exactly the same. The local value-name maps 
 * will be ignored.
 */
public class FunctionComparator {
	private static final boolean DEBUG = false;
	private static void debug(String message) {
		if (DEBUG)
			System.err.println("FunctionComparator: " + message);
	}
	
	private static final IntegerValue I32_0 = IntegerValue.getZero(32);
	
	private final DoubleMap<BasicBlock> mapB = 
		new DoubleMap<BasicBlock>();
	private final DoubleMap<VirtualRegister> mapV = 
		new DoubleMap<VirtualRegister>();
	private final FunctionBody body1, body2;
	private final Module module1, module2;
	
	public FunctionComparator(
			Module m1, Module m2,
			FunctionBody _b1, FunctionBody _b2) {
		this.module1 = m1;
		this.module2 = m2;
		this.body1 = _b1;
		this.body2 = _b2;
	}
	
	// %"alloca point" = bitcast i32 0 to i32
	public void removeAllocaPoint() {
		removeAllocaPoint(body1);
		removeAllocaPoint(body2);
	}
	private boolean isBitcast(Instruction inst) {
		if (inst.isCast()) {
			CastInstruction cast = inst.getCastSelf();
			if (cast.getCast().equals(Cast.Bitcast)) {
				Type destType = cast.getDestinationType();
				if (destType.isInteger() && destType.getIntegerSelf().getWidth() == 32) {
					return cast.getCastee().equalsValue(I32_0);
				}
			}
		}
		return false;
	}
	private boolean hasUses(BasicBlock bb, Value target) {
		for (int i = 0; i < bb.getNumInstructions(); i++) {
			for (Iterator<? extends Value> iter = bb.getInstruction(i).getValues(); iter.hasNext(); ) {
				if (iter.next().equalsValue(target))
					return true;
			}
		}
		return false;
	}
	private void removeAllocaPoint(FunctionBody body) {
		debug("removing alloca point...");
		
		final BasicBlock start = body.getStart();
		final RegisterAssignment regs = body.getRegisterAssignment();
		toploop:
		for (int i = 0; i < start.getNumInstructions(); i++) {
			Handle handle = start.getHandle(i);
			if (isBitcast(handle.getInstruction())) {
				debug("Found a bitcast");
				
				// look for uses
				if (regs.isAssigned(handle)) {
					debug("Assigned to reg");
					
					final VirtualRegister allocaPoint = regs.getRegister(handle);
					for (int j = 0; j < body.getNumBlocks(); j++) {
						if (hasUses(body.getBlock(j), allocaPoint)) {
							// has uses, continue
							debug("Has uses, skipping");
							continue toploop;
						}
					}
					
					debug("Has no uses, removing");
					
					// no uses, can remove
					start.removeInstruction(i);
					return;
				} else {
					debug("Not assigned to reg, removing");

					// remove and break
					start.removeInstruction(i);
					return;
				}
			}
		}
	}
	
	public boolean areEquivalent() {
		if (!doHeaders(body1.getHeader(), body2.getHeader()))
			return false;
		if (!doBlocks())
			return false;
		return true;
	}
	
	private static class DoubleMap<K> {
		private final Map<K,K> left2right = new HashMap<K,K>();
		private final Map<K,K> right2left = new HashMap<K,K>();
		public void put(K left, K right) {
			left2right.put(left, right);
			right2left.put(right, left);
		}
		public boolean hasEither(K left, K right) {
			return left2right.containsKey(left) || 
				right2left.containsKey(right);
		}
		public boolean isMatchingPair(K left, K right) {
			return 
				left2right.containsKey(left) &&
				left2right.get(left).equals(right) &&
				right2left.containsKey(right) &&
				right2left.get(right).equals(left);
		}
	}
	
	private boolean doBlocks() {
		LinkedList<Pair<BasicBlock,BasicBlock>> worklist = 
			new LinkedList<Pair<BasicBlock,BasicBlock>>();
		worklist.add(new Pair<BasicBlock,BasicBlock>(body1.getStart(), body2.getStart()));
		Set<Pair<BasicBlock,BasicBlock>> seen = 
			new HashSet<Pair<BasicBlock,BasicBlock>>();
		
		while (worklist.size() > 0) {
			final Pair<BasicBlock,BasicBlock> next = worklist.removeFirst();
			if (seen.contains(next))
				continue;
			seen.add(next);
			
			final BasicBlock left = next.getFirst();
			final BasicBlock right = next.getSecond();
			
			if (mapB.hasEither(left, right)) {
				if (!mapB.isMatchingPair(left, right))
					return false;
			}
			mapB.put(left, right);
			
			if (left.getNumSuccs() != right.getNumSuccs())
				return false;
			
			// do instructions
			if (!doInstructions(
					left, right,
					body1.getRegisterAssignment(), body2.getRegisterAssignment()))
				return false;
			
			for (int i = 0; i < left.getNumSuccs(); i++) {
				worklist.addLast(new Pair<BasicBlock,BasicBlock>(
						left.getSucc(i), right.getSucc(i)));
			}
		}
		
		return true;
	}
	
	private String bbToString(BasicBlock bb) {
		StringBuilder builder = new StringBuilder();
		builder.append("Block[\n");
		for (int i = 0; i < bb.getNumInstructions(); i++) {
			builder.append(bb.getInstruction(i)).append('\n');
		}
		builder.append("]");
		return builder.toString();
	}
	
	private boolean doInstructions(
			BasicBlock left,
			BasicBlock right,
			RegisterAssignment reg1,
			RegisterAssignment reg2) {
		if (left.getNumInstructions() != right.getNumInstructions())
			return false;
		final int length = left.getNumInstructions();
		for (int i = 0; i < length; i++) {
			final Handle hl = left.getHandle(i);
			final Handle hr = right.getHandle(i);
			// both or neither should have registers
			if (reg1.isAssigned(hl) != reg2.isAssigned(hr))
				return false;
			// if it has a register...
			if (reg1.isAssigned(hl)) {
				final VirtualRegister vl = reg1.getRegister(hl);
				final VirtualRegister vr = reg2.getRegister(hr);
				if (mapV.hasEither(vl, vr) && !mapV.isMatchingPair(vl, vr))
					return false;
				mapV.put(vl, vr);
			}
			
			final Instruction il = hl.getInstruction();
			final Instruction ir = hr.getInstruction();
			if (!doInstruction(il, ir))
				return false;
		}
		return true;
	}

	private boolean doValue(Value left, Value right) {
		if (left.isInteger() ||
			left.isFloatingPoint() || 
			left.isUndef() ||
			left.isConstantNullPointer() ||
			left.isConstantVector() ||
			left.isInlineASM() ||
			left.isMetadataNode() ||
			left.isMetadataString()) {
			return left.equalsValue(right);
		}
		else if (left.isBlockAddress()) {
			if (!right.isBlockAddress()) return false;
			BlockAddressValue bl = left.getBlockAddressSelf();
			BlockAddressValue br = right.getBlockAddressSelf();
			if (mapB.hasEither(bl.getBlock(), br.getBlock()) && 
				!mapB.isMatchingPair(bl.getBlock(), br.getBlock())) {
				return false;
			}
			mapB.put(bl.getBlock(), br.getBlock());
			return doHeaders(bl.getFunction(), br.getFunction());
		}
		else if (left.isFunction()) {
			if (!right.isFunction()) return false;
			FunctionValue fl = left.getFunctionSelf();
			FunctionValue fr = right.getFunctionSelf();
			if (fl.equalsValue(body1.getHeader()) && fr.equalsValue(body2.getHeader()))
				return true;
			else if (fl.equalsValue(body1.getHeader()) || fr.equalsValue(body2.getHeader()))
				return false;
			
			// not this function
			String name1 = module1.lookupValueName(fl);
			String name2 = module2.lookupValueName(fr);
			// functions must have names
			if (name1 == null || name2 == null && !name1.equals(name2))
				return false;
			return doHeaders(fl, fr);
		}
		else if (left.isConstantStructure()) {
			if (!right.isConstantStructure()) return false;
			ConstantStructureValue cl = left.getConstantStructureSelf();
			ConstantStructureValue cr = right.getConstantStructureSelf();
			if (!(cl.getType().equalsType(cr.getType()) &&
				  cl.getNumFields() == cr.getNumFields()))
				return false;
			for (int i = 0; i < cl.getNumFields(); i++) {
				if (!doValue(cl.getFieldValue(i), cr.getFieldValue(i)))
					return false;
			}
			return true;
		}
		else if (left.isConstantArray()) {
			if (!right.isConstantArray()) return false;
			ConstantArrayValue cl = left.getConstantArraySelf();
			ConstantArrayValue cr = right.getConstantArraySelf();
			if (!(cl.getType().equalsType(cr.getType()) &&
				  cl.getNumElements() == cr.getNumElements()))
				return false;
			long num = cl.getNumElements().signedValue();
			for (long i = 0L; i < num; i++) {
				if (!doValue(cl.getElement((int)i), cr.getElement((int)i)))
					return false;
			}
			return true;
		}
		else if (left.isGlobalVariable()) {
			if (!right.isGlobalVariable()) return false;
			GlobalVariable gl = left.getGlobalVariableSelf();
			GlobalVariable gr = right.getGlobalVariableSelf();
			if (!(gl.getType().equalsType(gr.getType()) &&
				  gl.isConstant() == gr.isConstant() &&
				  gl.getLinkage().equals(gr.getLinkage()) &&
				  gl.getAlignment() == gr.getAlignment() &&
				  gl.getSectionIndex() == gr.getSectionIndex() &&
				  gl.getVisibility().equals(gr.getVisibility()) &&
				  gl.isThreadLocal() == gr.isThreadLocal()))
				return false;
			if (gl.getInitialValue() != null && gr.getInitialValue() != null) {
				if (!doValue(gl.getInitialValue(), gr.getInitialValue()))
					return false;
			}
			else if (gl.getInitialValue() != null || gr.getInitialValue() != null) 
				return false;
			return true;
		}
		else if (left.isAlias()) {
			if (!right.isAlias()) return false;
			AliasValue al = left.getAliasSelf();
			AliasValue ar = right.getAliasSelf();
			return al.getType().equalsType(ar.getType()) &&
				doValue(al.getAliaseeValue(), ar.getAliaseeValue()) &&
				al.getLinkage().equals(ar.getLinkage()) &&
				al.getVisibility().equals(ar.getVisibility());
		}
		else if (left.isArgument()) {
			if (!right.isArgument()) return false;
			ArgumentValue al = left.getArgumentSelf();
			ArgumentValue ar = right.getArgumentSelf();
			return al.getIndex() == ar.getIndex() &&
				al.getType().equalsType(ar.getType());
		}
		else if (left.isRegister()) {
			if (!right.isRegister()) return false;
			VirtualRegister vl = left.getRegisterSelf();
			VirtualRegister vr = right.getRegisterSelf();
			if (!vl.getType().equalsType(vr.getType()))
				return false;
			if (mapV.hasEither(vl, vr)) {
				if (!mapV.isMatchingPair(vl, vr))
					return false;
			}
			else 
				mapV.put(vl, vr);
			return true;
		}
		else if (left.isConstantExpr()) {
			if (!right.isConstantExpr()) return false;
			ConstantExpr cl = left.getConstantExprSelf();
			ConstantExpr cr = right.getConstantExprSelf();
			return doInstruction(cl.getInstruction(), cr.getInstruction());
		}
		else if (left.isLabel()) {
			if (!right.isLabel()) return false;
			LabelValue ll = left.getLabelSelf();
			LabelValue lr = right.getLabelSelf();
			if (mapB.hasEither(ll.getBlock(), lr.getBlock())) {
				if (!mapB.isMatchingPair(ll.getBlock(), lr.getBlock()))
					return false;
			} 
			else
				mapB.put(ll.getBlock(), lr.getBlock());
			return true;
		}
		else
			throw new RuntimeException("Forgot to implement: " + left);
	}
	
	private boolean doInstruction(Instruction left, Instruction right) {
		if (left.isTerminator()) {
			if (!right.isTerminator()) return false;
			return doTerminator(left.getTerminatorSelf(), right.getTerminatorSelf());
		}
		else if (left.isInsertValue()) {
			if (!right.isInsertValue()) return false;
			InsertValueInstruction il = left.getInsertValueSelf();
			InsertValueInstruction ir = right.getInsertValueSelf();
			return il.getIndexes().equals(ir.getIndexes()) &&
				doValue(il.getAggregate(), ir.getAggregate()) &&
				doValue(il.getElement(), ir.getElement());
		}
		else if (left.isExtractValue()) {
			if (!right.isExtractValue()) return false;
			ExtractValueInstruction el = left.getExtractValueSelf();
			ExtractValueInstruction er = right.getExtractValueSelf();
			return el.getIndexes().equals(er.getIndexes()) &&
				doValue(el.getAggregate(), er.getAggregate());
		}
		
		else if (left.isBinop()) {
			if (!right.isBinop()) return false;
			BinopInstruction bl = left.getBinopSelf();
			BinopInstruction br = right.getBinopSelf();
			return bl.getBinop().equals(br.getBinop()) &&
				doValue(bl.getLHS(), br.getLHS()) &&
				doValue(bl.getRHS(), br.getRHS());
		} 
		else if (left.isCast()) {
			if (!right.isCast()) return false;
			CastInstruction cl = left.getCastSelf();
			CastInstruction cr = right.getCastSelf();
			return cl.getCast().equals(cr.getCast()) &&
				cl.getDestinationType().equalsType(cr.getDestinationType()) &&
				doValue(cl.getCastee(), cr.getCastee());
		}
		else if (left.isShuffleVec2_8()) {
			if (!right.isShuffleVec2_8()) return false;
			ShuffleVec2_8Instruction sl = left.getShuffleVec2_8Self();
			ShuffleVec2_8Instruction sr = right.getShuffleVec2_8Self();
			return doValue(sl.getOperand1(), sr.getOperand1()) &&
				doValue(sl.getOperand2(), sr.getOperand2()) &&
				doValue(sl.getMask(), sr.getMask());
		}
		else if (left.isShuffleVec()) {
			if (!right.isShuffleVec()) return false;
			ShuffleVecInstruction sl = left.getShuffleVecSelf();
			ShuffleVecInstruction sr = right.getShuffleVecSelf();
			return doValue(sl.getVector1(), sr.getVector1()) &&
				doValue(sl.getVector2(), sr.getVector2()) &&
				doValue(sl.getShuffleVector(), sr.getShuffleVector());
		}
		else if (left.isInsertElt()) {
			if (!right.isInsertElt()) return false;
			InsertEltInstruction il = left.getInsertEltSelf();
			InsertEltInstruction ir = right.getInsertEltSelf();
			return doValue(il.getVector(), ir.getVector()) &&
				doValue(il.getElement(), ir.getElement()) &&
				doValue(il.getIndex(), ir.getIndex());
		}
		else if (left.isGEP()) {
			if (!right.isGEP()) return false;
			GEPInstruction gl = left.getGEPSelf();
			GEPInstruction gr = right.getGEPSelf();
			if (!(doValue(gl.getBaseValue(), gr.getBaseValue()) &&
				  gl.getType().equalsType(gr.getType()) &&
				  gl.getNumIndexes() == gr.getNumIndexes() &&
				  gl.isInbounds() == gr.isInbounds()))
				return false;
			for (int i = 0; i < gl.getNumIndexes(); i++) {
				if (!doValue(gl.getIndex(i), gr.getIndex(i)))
					return false;
			}
			return true;
		}
		else if (left.isVSelect()) {
			if (!right.isVSelect()) return false;
			VSelectInstruction sl = left.getVSelectSelf();
			VSelectInstruction sr = right.getVSelectSelf();
			return doValue(sl.getCondition(), sr.getCondition()) &&
				doValue(sl.getTrueValue(), sr.getTrueValue()) &&
				doValue(sl.getFalseValue(), sr.getFalseValue());
		}
		else if (left.isSelect()) {
			if (!right.isSelect()) return false;
			SelectInstruction sl = left.getSelectSelf();
			SelectInstruction sr = right.getSelectSelf();
			return doValue(sl.getCondition(), sr.getCondition()) &&
				doValue(sl.getTrueValue(), sr.getTrueValue()) &&
				doValue(sl.getFalseValue(), sr.getFalseValue());
		}
		else if (left.isExtractElt()) {
			if (!right.isExtractElt()) return false;
			ExtractEltInstruction el = left.getExtractEltSelf();
			ExtractEltInstruction er = right.getExtractEltSelf();
			return doValue(el.getVector(), er.getVector()) &&
				doValue(el.getIndex(), er.getIndex());
		}
		else if (left.isCmp()) {
			if (!right.isCmp()) return false;
			CmpInstruction cl = left.getCmpSelf();
			CmpInstruction cr = right.getCmpSelf();
			return cl.getPredicate().equals(cr.getPredicate()) &&
				doValue(cl.getLHS(), cr.getLHS()) &&
				doValue(cl.getRHS(), cr.getRHS());
		}
		else if (left.isPhi()) {
			if (!right.isPhi()) return false;
			PhiInstruction pl = left.getPhiSelf();
			PhiInstruction pr = right.getPhiSelf();
			if (!(pl.getType().equalsType(pr.getType()) &&
				  pl.getNumPairs() == pr.getNumPairs()))
				return false;
			for (int i = 0; i < pl.getNumPairs(); i++) {
				Pair<? extends Value,BasicBlock> pairL = pl.getPair(i);
				Pair<? extends Value,BasicBlock> pairR = pr.getPair(i);
				if (!doValue(pairL.getFirst(), pairR.getFirst()))
					return false;
				if (mapB.hasEither(pairL.getSecond(), pairR.getSecond())) {
					if (!mapB.isMatchingPair(pairL.getSecond(), pairR.getSecond()))
						return false;
				}
				else
					mapB.put(pairL.getSecond(), pairR.getSecond());
			}
			return true;
		}
		else if (left.isGetResult()) {
			if (!right.isGetResult()) return false;
			GetResultInstruction gl = left.getGetResultSelf();
			GetResultInstruction gr = right.getGetResultSelf();
			return doValue(gl.getBase(), gr.getBase()) &&
				gl.getIndex() == gr.getIndex();
		}
		else if (left.isMalloc()) {
			if (!right.isMalloc()) return false;
			MallocInstruction ml = left.getMallocSelf();
			MallocInstruction mr = right.getMallocSelf();
			return ml.getElementType().equalsType(mr.getElementType()) &&
				doValue(ml.getNumElementsValue(), mr.getNumElementsValue()) &&
				ml.getAlignment() == mr.getAlignment();
		}
		else if (left.isFree()) {
			if (!right.isFree()) return false;
			FreeInstruction fl = left.getFreeSelf();
			FreeInstruction fr = right.getFreeSelf();
			return doValue(fl.getFreedValue(), fr.getFreedValue());
		}
		else if (left.isAlloca()) {
			if (!right.isAlloca()) return false;
			AllocaInstruction al = left.getAllocaSelf();
			AllocaInstruction ar = right.getAllocaSelf();
			return al.getElementType().equalsType(ar.getElementType()) &&
				doValue(al.getNumElementsValue(), ar.getNumElementsValue()) &&
				al.getAlignment() == ar.getAlignment();
		}
		else if (left.isLoad()) {
			if (!right.isLoad()) return false;
			LoadInstruction ll = left.getLoadSelf();
			LoadInstruction lr = right.getLoadSelf();
			return doValue(ll.getLoadee(), lr.getLoadee()) &&
				ll.getAlignment() == lr.getAlignment() &&
				ll.isVolatile() == lr.isVolatile();
		}
		else if (left.isStore()) {
			if (!right.isStore()) return false;
			StoreInstruction sl = left.getStoreSelf();
			StoreInstruction sr = right.getStoreSelf();
			return doValue(sl.getAddress(), sr.getAddress()) &&
				doValue(sl.getValue(), sr.getValue()) &&
				sl.getAlignment() == sr.getAlignment() &&
				sl.isVolatile() == sr.isVolatile();
		}
		else if (left.isCall()) {
			if (!right.isCall()) return false;
			CallInstruction cl = left.getCallSelf();
			CallInstruction cr = right.getCallSelf();
			if (!(doParamattrMap(cl.getParameterAttributeMap(), cr.getParameterAttributeMap()) &&
				  doValue(cl.getFunctionPointer(), cr.getFunctionPointer()) &&
				  cl.getCallingConvention() == cr.getCallingConvention() &&
				  cl.isTailCall() == cr.isTailCall() &&
				  cl.getNumActuals() == cr.getNumActuals()))
				return false;
			for (int i = 0; i < cl.getNumActuals(); i++) {
				if (!doValue(cl.getActual(i), cr.getActual(i)))
					return false;
			}
			return true;
		}
		else if (left.isVaarg()) {
			if (!right.isVaarg()) return false;
			VaargInstruction vl = left.getVaargSelf();
			VaargInstruction vr = right.getVaargSelf();
			return doValue(vl.getVAList(), vr.getVAList()) &&
				vl.getResultType().equalsType(vr.getResultType());
		}
		else
			throw new RuntimeException("Forgot to implement: " + left);
	}
	
	private boolean doTerminator(
			TerminatorInstruction left,
			TerminatorInstruction right ){ 
		if (left.isRet()) {
			if (!right.isRet()) return false;
			RetInstruction rl = left.getRetSelf();
			RetInstruction rr = right.getRetSelf();
			if (!(rl.getReturnValueType().equalsType(rr.getReturnValueType()) &&
				  rl.getNumReturnValues() == rr.getNumReturnValues()))
				return false;
			for (int i = 0; i < rl.getNumReturnValues(); i++) {
				if (!doValue(rl.getReturnValue(i), rr.getReturnValue(i)))
					return false;
			}
			return true;
		}
		else if (left.isBr()) {
			if (!right.isBr()) return false;
			BrInstruction bl = left.getBrSelf();
			BrInstruction br = right.getBrSelf();
			if (bl.getCondition() != null && br.getCondition() != null) {
				if (!doValue(bl.getCondition(), br.getCondition()))
					return false;
			} else if (bl.getCondition() != null || br.getCondition() != null)
				return false;
			
			if (bl.getTrueTarget() != null && br.getTrueTarget() != null) {
				if (mapB.hasEither(bl.getTrueTarget(), br.getTrueTarget()) && 
					!mapB.isMatchingPair(bl.getTrueTarget(), br.getTrueTarget()))
					return false;
			} 
			else if (bl.getTrueTarget() != null || br.getTrueTarget() != null) 
				return false;
			else
				mapB.put(bl.getTrueTarget(), br.getTrueTarget());
			
			if (bl.getFalseTarget() != null && br.getFalseTarget() != null) {
				if (mapB.hasEither(bl.getFalseTarget(), br.getFalseTarget()) && 
					!mapB.isMatchingPair(bl.getFalseTarget(), br.getFalseTarget()))
					return false;
			} 
			else if (bl.getFalseTarget() != null || br.getFalseTarget() != null) 
				return false;
			else
				mapB.put(bl.getFalseTarget(), br.getFalseTarget());
			return true;
		}
		else if (left.isSwitch()) {
			if (!right.isSwitch()) return false;
			SwitchInstruction sl = left.getSwitchSelf();
			SwitchInstruction sr = right.getSwitchSelf();
			if (!(doValue(sl.getInputValue(), sr.getInputValue()) &&
				  sl.getNumCaseLabels() == sr.getNumCaseLabels()))
				return false;
			if (mapB.hasEither(sl.getDefaultTarget(), sr.getDefaultTarget())) {
				if (!mapB.isMatchingPair(sl.getDefaultTarget(), sr.getDefaultTarget()))
					return false;
			}
			else
				mapB.put(sl.getDefaultTarget(), sr.getDefaultTarget());
			
			for (int i = 0; i < sl.getNumCaseLabels(); i++) {
				IntegerValue tl = sl.getCaseLabel(i);
				IntegerValue tr = null;
				int j;
				for (j = 0; j < sr.getNumCaseLabels(); j++) {
					tr = sr.getCaseLabel(j);
					if (doValue(tl, tr))
						break;
					tr = null;
				}
				if (tr == null) // could not find matching case
					return false;
				
				if (mapB.hasEither(sl.getCaseTarget(i), sr.getCaseTarget(j))) {
					if (!mapB.isMatchingPair(sl.getCaseTarget(i), sr.getCaseTarget(j)))
						return false;
				}
				else
					mapB.put(sl.getCaseTarget(i), sr.getCaseTarget(j));
			}
			
			return true;
		}
		else if (left.isInvoke()) {
			if (!right.isInvoke()) return false;
			InvokeInstruction il = left.getInvokeSelf();
			InvokeInstruction ir = right.getInvokeSelf();
			if (!(doParamattrMap(il.getParameterAttributeMap(), ir.getParameterAttributeMap()) &&
				  doValue(il.getFunctionPointer(), ir.getFunctionPointer()) &&
				  il.getCallingConvention() == ir.getCallingConvention() &&
				  il.getNumActuals() == ir.getNumActuals()))
				return false;
			if (mapB.hasEither(il.getReturnBlock(), ir.getReturnBlock())) {
				if (!mapB.isMatchingPair(il.getReturnBlock(), ir.getReturnBlock()))
					return false;
			}
			else
				mapB.put(il.getReturnBlock(), ir.getReturnBlock());
			
			if (mapB.hasEither(il.getUnwindBlock(), ir.getUnwindBlock())) {
				if (!mapB.isMatchingPair(il.getUnwindBlock(), ir.getUnwindBlock()))
					return false;
			}
			else
				mapB.put(il.getUnwindBlock(), ir.getUnwindBlock());
			
			for (int i = 0; i < il.getNumActuals(); i++) {
				if (!doValue(il.getActual(i), ir.getActual(i)))
					return false;
			}
			return true;
		}
		else if (left.isUnwind()) {
			if (!right.isUnwind()) return false;
			return true;
		}
		else if (left.isUnreachable()) {
			if (!right.isUnreachable()) return false;
			return true;
		}
		else if (left.isIndirectBR()) {
			if (!right.isIndirectBR()) return false;
			IndirectBRInstruction il = left.getIndirectBRSelf();
			IndirectBRInstruction ir = right.getIndirectBRSelf();
			return doValue(il.getAddress(), ir.getAddress());
		}
		else
			throw new RuntimeException("Forgot to implement: " + left);
	}

	private boolean doParamattrMap(
			ParameterAttributeMap leftmap,
			ParameterAttributeMap rightmap) {
		if (leftmap.getMaxIndex() != rightmap.getMaxIndex())
			return false;
		if (leftmap.hasFunctionAttributes() && rightmap.hasFunctionAttributes()) {
			if (!leftmap.getFunctionAttributes().equals(rightmap.getFunctionAttributes()))
				return false;
		} else if (leftmap.hasFunctionAttributes() || rightmap.hasFunctionAttributes())
			return false;
		
		for (int i = 0; i <= leftmap.getMaxIndex(); i++) {
			if (leftmap.hasParamAttributes(i) && rightmap.hasParamAttributes(i)) {
				if (!leftmap.getParamAttributes(i).equals(rightmap.getParamAttributes(i)))
					return false;
			} else if (leftmap.hasParamAttributes(i) || rightmap.hasParamAttributes(i))
				return false;
		}
		return true;
	}
	
	private boolean doHeaders(FunctionValue left, FunctionValue right) {
		if (!left.getType().equalsType(right.getType()))
			return false;
		if (left.getCallingConvention() != right.getCallingConvention())
			return false;
		if (left.isPrototype() != right.isPrototype())
			return false;
		if (!left.getLinkage().equals(right.getLinkage()))
			return false;
		if (!doParamattrMap(
				left.getParameterAttributeMap(), 
				right.getParameterAttributeMap()))
			return false;
		if (left.getAlignment() != right.getAlignment())
			return false;
		if (left.getSectionIndex() != right.getSectionIndex())
			return false;
		if (!left.getVisibility().equals(right.getVisibility()))
			return false;
		if (left.getCollectorIndex() != right.getCollectorIndex())
			return false;

		// do arguments
		if (left.getNumArguments() != right.getNumArguments())
			return false;
		for (int i = 0; i < left.getNumArguments(); i++) {
			ArgumentValue al = left.getArgument(i);
			ArgumentValue ar = right.getArgument(i);
			if (al.getIndex() != ar.getIndex())
				return false;
			if (!al.getType().equalsType(ar.getType()))
				return false;
		}
		return true;
	}
	
	// args: func1 module1 func2 module2
	public static void main(String args[]) throws Throwable {
		if (args.length < 5) {
			System.err.println("USAGE: FunctionComparator func1 module1 func2 module2 is2.8 [removealloca]");
			System.exit(1);
		}
		boolean is28 = args[4].equals("true");
		
		String func1 = args[0];
		Module module1;
		if (is28) {
			BitcodeReader2_8 reader = new BitcodeReader2_8(new FileInputStream(args[1]));
			module1 = ModuleDecoder2_8.decode(reader.readBitcode(), reader.LLVM2_7MetadataDetected());
		} else {
			module1 = ModuleDecoder.decode(new BitcodeReader(new FileInputStream(args[1])).readBitcode());
		}
		String func2 = args[2];
		Module module2;
		if (is28) {
			BitcodeReader2_8 reader = new BitcodeReader2_8(new FileInputStream(args[3]));
			module2 = ModuleDecoder2_8.decode(reader.readBitcode(), reader.LLVM2_7MetadataDetected());
		} else {
			module2 = ModuleDecoder.decode(new BitcodeReader(new FileInputStream(args[3])).readBitcode());
		}
		
		Value v1 = module1.getValueByName(func1);
		Value v2 = module2.getValueByName(func2);
		if (v1 == null || v2 == null || !v1.isFunction() || !v2.isFunction())
			throw new RuntimeException("Not functions");
		FunctionBody b1 = module1.getBodyByHeader(v1.getFunctionSelf());
		FunctionBody b2 = module2.getBodyByHeader(v2.getFunctionSelf());
		if (b1==null || b2==null)
			throw new RuntimeException("No bodies");
		
		FunctionComparator fc = new FunctionComparator(module1, module2, b1, b2);
		if (args.length >= 6)
			fc.removeAllocaPoint();
		System.out.println(fc.areEquivalent());
	}
}
