package llvm.bitcode;

import static llvm.bitcode.LLVMUtils.TokenType.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import llvm.instructions.BasicBlock;
import llvm.instructions.CallInstruction;
import llvm.instructions.FunctionBody;
import llvm.instructions.Instruction;
import llvm.types.ArrayType;
import llvm.types.CompositeType;
import llvm.types.FloatingPointType;
import llvm.types.FunctionType;
import llvm.types.HolderType;
import llvm.types.OpaqueType;
import llvm.types.PointerType;
import llvm.types.StructureType;
import llvm.types.Type;
import llvm.types.VectorType;

/**
 * This class contains various methods that are of general use when dealing
 * with LLVM bitcode files and modules. 
 * Parent refs are 1-based
 */
public class LLVMUtils {
	private static boolean DEBUG = false;
	private static void debug(String message) {
		if (DEBUG)
			System.err.println("LLVMUtils: " + message);
	}
	
	protected static enum TokenType {
		LABEL("label"),
		METADATA("metadata"),
		VOID("void"),
		INTEGER("i"),
		FLOAT("float"),
		DOUBLE("double"),
		FP128("fp128"),
		X86_FP80("x86_fp80"),
		PPC_FP128("ppc_fp128"),
		ADDRSPACE("addrspace"),
		LITERAL("literal"),
		LANGLE("<"),
		RANGLE(">"),
		LBRACK("["),
		RBRACK("]"),
		LCURLY("{"),
		RCURLY("}"),
		STAR("*"),
		LPAREN("("),
		RPAREN(")"),
		CROSS("x"),
		QUESTION("?"),
		DOTDOTDOT("..."),
		PARENTREF("\\"), // 1-based
		COMMA(",");
	
		private final String value;
		private TokenType(String _value) {
			this.value = _value;
		}
		public String getValue() {return this.value;}
	}
	private static class Token {
		public final TokenType type;
		public final String value;
		
		Token(TokenType _type) {
			this.type = _type;
			this.value = this.type.getValue();
		}
		Token(TokenType _type, String _value) {
			this.type = _type;
			this.value = _value;
		}
		public String toString() {
			if (this.value != null) {
				return this.type.name() + "[" + this.value + "]";
			} else {
				return this.type.name();
			}
		}
	}
	
	public static Type parseType(String type) {
		List<Token> tokens = lex(type);
		
		debug(tokens.toString());
		
		int[] index = {0};
		Map<HolderType,Integer> map = new HashMap<HolderType,Integer>();
		Map<HolderType,Type> update = new HashMap<HolderType,Type>();
		Type result = parseType(tokens, index, map, update);
		if (index[0] != tokens.size())
			throw new IllegalArgumentException("Tokens remain at end of string");
		
		for (Map.Entry<HolderType,Type> entry : update.entrySet()) {
			entry.getKey().setInnerType(entry.getValue());
		}
		
		for (Map.Entry<HolderType,Integer> entry : map.entrySet()) {
			if (entry.getKey().isHolder())
				throw new IllegalArgumentException("Unknown ref " + entry.getKey());
		}
		
		return result;
	}
	private static void assignHolders(
			Type type, 
			Map<HolderType,Integer> map, 
			LinkedList<Type> parents, 
			Map<HolderType,Type> update) {
		if (type.isComposite()) {
			CompositeType ctype = type.getCompositeSelf();
			if (ctype.isPointer()) {
				PointerType ptype = ctype.getPointerSelf();
				parents.addFirst(ptype);
				if (ptype.getPointeeType().isHolder()) {
					HolderType holder = ptype.getPointeeType().getHolderSelf();
					int index = map.get(holder);
					if (index <= parents.size())
						update.put(holder, parents.get(index-1));
				} else {
					assignHolders(ptype.getPointeeType(), map, parents, update);
				}
				parents.removeFirst();
			} else if (ctype.isStructure()) {
				StructureType stype = ctype.getStructureSelf();
				parents.addFirst(stype);
				for (int i = 0; i < stype.getNumFields(); i++) {
					if (stype.getFieldType(i).isHolder()) {
						HolderType holder = stype.getFieldType(i).getHolderSelf();
						int index = map.get(holder);
						if (index <= parents.size())
							update.put(holder, parents.get(index-1));
					} else {
						assignHolders(stype.getFieldType(i), map, parents, update);
					}
				}
				parents.removeFirst();
			} else if (ctype.isArray()) {
				ArrayType atype = ctype.getArraySelf();
				parents.addFirst(atype);
				if (atype.getElementType().isHolder()) {
					HolderType holder = atype.getElementType().getHolderSelf();
					int index = map.get(holder);
					if (index <= parents.size()) 
						update.put(holder, parents.get(index-1));
				} else {
					assignHolders(atype.getElementType(), map, parents, update);
				}
				parents.removeFirst();
			}
		} else if (type.isFunction()) {
			FunctionType function = type.getFunctionSelf();
			parents.addFirst(function);
			if (function.getReturnType().isHolder()) {
				HolderType holder = function.getReturnType().getHolderSelf();
				int index = map.get(holder);
				if (index <= parents.size())
					update.put(holder, parents.get(index-1));
			} else {
				assignHolders(function.getReturnType(), map, parents, update);
			}
			for (int i = 0; i < function.getNumParams(); i++) {
				if (function.getParamType(i).isHolder()) {
					HolderType holder = function.getParamType(i).getHolderSelf();
					int index = map.get(holder);
					if (index <= parents.size())
						update.put(holder, parents.get(index-1));
				} else {
					assignHolders(function.getParamType(i), map, parents, update);
				}
			}
			parents.removeFirst();
		}
	}
	
	
	
	private static List<Token> lex(String type) {
		int index = 0;
		List<Token> tokens = new ArrayList<Token>();
		
		while (index < type.length()) {
			index = skipWS(type, index);
			if (index == type.length())
				break;

			if (startsWith(type, index, "label")) {
				index += 5;
				tokens.add(new Token(LABEL));
			} else if (startsWith(type, index, "?")) {
				index += 1;
				tokens.add(new Token(QUESTION));
			} else if (startsWith(type, index, "void")) {
				index += 4;
				tokens.add(new Token(VOID));
			} else if (startsWith(type, index, "metadata")) {
				index += 8;
				tokens.add(new Token(METADATA));
			} else if (startsWith(type, index, "addrspace")) {
				index += 9;
				tokens.add(new Token(ADDRSPACE));
			} else if (startsWith(type, index, "float")) {
				index += 5;
				tokens.add(new Token(FLOAT));
			} else if (startsWith(type, index, "double")) {
				index += 6;
				tokens.add(new Token(DOUBLE));
			} else if (startsWith(type, index, "fp128")) {
				index += 5;
				tokens.add(new Token(FP128));
			} else if (startsWith(type, index, "x86_fp80")) {
				index += 8;
				tokens.add(new Token(X86_FP80));
			} else if (startsWith(type, index, "ppc_fp128")) {
				index += 9;
				tokens.add(new Token(PPC_FP128));
			} else if (startsWith(type, index, "i")) {
				// integer
				int num = index+1;
				while (num < type.length() && Character.isDigit(type.charAt(num)))
					num++;
				if (num == index+1)
					throw new IllegalArgumentException("Integer has invalid width");
				
				String value = type.substring(index, num);
				index = num;
				tokens.add(new Token(INTEGER, value));
			} else if (startsWith(type, index, "\\")) {
				// parentref
				int num = index+1;
				while (num < type.length() && Character.isDigit(type.charAt(num)))
					num++;
				if (num == index+1)
					throw new IllegalArgumentException("Integer has invalid width");
				
				String value = type.substring(index, num);
				index = num;
				tokens.add(new Token(PARENTREF, value));
			} else if (Character.isDigit(type.charAt(index))) {
				// literal
				int num = index;
				while (num < type.length() && Character.isDigit(type.charAt(num)))
					num++;
				String value = type.substring(index, num);
				index = num;
				tokens.add(new Token(LITERAL, value));
			} else if (startsWith(type, index, "<")) {
				index++;
				tokens.add(new Token(LANGLE));
			} else if (startsWith(type, index, ">")) {
				index++;
				tokens.add(new Token(RANGLE));
			} else if (startsWith(type, index, "[")) {
				index++;
				tokens.add(new Token(LBRACK));
			} else if (startsWith(type, index, "]")) {
				index++;
				tokens.add(new Token(RBRACK));
			} else if (startsWith(type, index, "{")) {
				index++;
				tokens.add(new Token(LCURLY));
			} else if (startsWith(type, index, "}")) {
				index++;
				tokens.add(new Token(RCURLY));
			} else if (startsWith(type, index, "(")) {
				index++;
				tokens.add(new Token(LPAREN));
			} else if (startsWith(type, index, ")")) {
				index++;
				tokens.add(new Token(RPAREN));
			} else if (startsWith(type, index, "*")) {
				index++;
				tokens.add(new Token(STAR));
			} else if (startsWith(type, index, "x")) {
				index++;
				tokens.add(new Token(CROSS));
			} else if (startsWith(type, index, "...")) {
				index+=3;
				tokens.add(new Token(DOTDOTDOT));
			} else if (startsWith(type, index, ",")) {
				index++;
				tokens.add(new Token(COMMA));
			} else {
				throw new IllegalArgumentException("Invalid character at index " + index + " in type string: " + type);
			}
		}
		
		return tokens;
	}

	private static int skipWS(String type, int index) {
		loop:
		while (index < type.length()) {
			switch(type.charAt(index)) {
			case '\n':
			case '\r':
			case '\t':
			case ' ':
				index++;
				break;
			default:
				break loop;
			}
		}
		return index;
	}
	
	private static boolean startsWith(String type, int index, String tofind) {
		return (index+tofind.length() <= type.length()) && 
			type.substring(index).startsWith(tofind);
	}
	
	private static Type parseType(
			List<Token> tokens, 
			int[] index, 
			Map<HolderType,Integer> map,
			Map<HolderType,Type> update) {
		if (index[0] >= tokens.size())
			throw new IllegalArgumentException("Premature EOF");

		Token first = tokens.get(index[0]);
		Type base;
		switch (first.type) {
		case LANGLE:
		case LCURLY:
		case LBRACK:
			base = parseArrayVectorStructure(tokens, index, false, map, update);
			break;
		default:
			base = parseBasicType(tokens, index, map);
			break;
		}
		
		debug("base type = " + base);
		debug("index[0] = " + index[0]);
		
		while (index[0] < tokens.size()) {
			first = tokens.get(index[0]);
			if (first.type.equals(LPAREN)) {
				List<Type> args = new ArrayList<Type>();
				boolean varargs = parseArgumentTypes(tokens, index, args, map, update);
				base = new FunctionType(base, args, varargs);
			} else if (first.type.equals(STAR)) {
				index[0]++;
				base = new PointerType(base);
			} else if (first.type.equals(ADDRSPACE)) {
				if (index[0]+4 >= tokens.size())
					throw new IllegalArgumentException("Premature EOF after addrspace");
				if (!(tokens.get(index[0]+1).type.equals(LPAREN) &&
					  tokens.get(index[0]+2).type.equals(LITERAL) &&
					  tokens.get(index[0]+3).type.equals(RPAREN) &&
					  tokens.get(index[0]+4).type.equals(STAR))) {
					throw new IllegalArgumentException("Invalid addrspace directive");
				}
				
				String literal = tokens.get(index[0]+2).value;
				int addrspace = Integer.parseInt(literal);
				index[0] += 5;
				base = new PointerType(base, addrspace);
			} else {
				break;
			}
		}
		
		assignHolders(base, map, new LinkedList<Type>(), update);
		return base;
	}
	
	
	private static boolean parseArgumentTypes(
			List<Token> tokens, 
			int[] index, 
			List<? super Type> args,
			Map<HolderType,Integer> map,
			Map<HolderType,Type> update) {
		if (index[0] >= tokens.size())
			throw new IllegalArgumentException("Premature EOF for argument types");
		if (!tokens.get(index[0]).type.equals(LPAREN))
			throw new IllegalArgumentException("Expecting '('");
		index[0]++;
		
		boolean varargs = false;
		
		while (index[0] < tokens.size()) {
			if (tokens.get(index[0]).type.equals(RPAREN)) {
				index[0]++;
				break;
			} else if (tokens.get(index[0]).type.equals(DOTDOTDOT)) {
				varargs = true;
				index[0]++;
				if (index[0] >= tokens.size() || !tokens.get(index[0]).type.equals(RPAREN))
					throw new IllegalArgumentException("'...' must be followed by ')'");
				index[0]++;
				break;
			} else {
				Type arg = parseType(tokens, index, map, update);
				if (index[0] >= tokens.size())
					throw new IllegalArgumentException("Premature EOF");
				args.add(arg);
				
				if (tokens.get(index[0]).type.equals(RPAREN)) {
					index[0]++;
					break;
				} else if (tokens.get(index[0]).type.equals(COMMA)) {
					index[0]++;
					continue;
				} else {
					throw new IllegalArgumentException("Expecting ',' or ')'");
				}
			}
		}
		
		return varargs;
	}
	
	private static CompositeType parseArrayVectorStructure(
			List<Token> tokens, 
			int[] index, 
			boolean packed,
			Map<HolderType,Integer> map,
			Map<HolderType,Type> update) {
		if (index[0] >= tokens.size())
			throw new IllegalArgumentException("Premature EOF for composite type");

		Token first = tokens.get(index[0]);
		switch (first.type) {
		case LANGLE: {
			// vector or packed structure
			index[0]++;
			if (index[0] >= tokens.size())
				throw new IllegalArgumentException("Premature EOF for composite type");
			first = tokens.get(index[0]);
			if (first.type.equals(LCURLY)) {
				// packed structure
				CompositeType result = parseArrayVectorStructure(tokens, index, true, map, update);
				if (index[0] >= tokens.size() ||
					!tokens.get(index[0]).type.equals(RANGLE))
					throw new IllegalArgumentException("Expecting '>' for packed structure");
				index[0]++;
				
				return result;
			} else if (first.type.equals(LITERAL)) {
				// vector
				int literalIndex = index[0];
				index[0]++;
				if (index[0] >= tokens.size() || 
					!tokens.get(index[0]).type.equals(CROSS))
					throw new IllegalArgumentException("Expecting 'x' after vector size literal");
				index[0]++;
				Type elementType = parseBasicType(tokens, index, map);
				if (index[0] >= tokens.size() ||
					!tokens.get(index[0]).type.equals(RANGLE))
					throw new IllegalArgumentException("Expecting '>' for vector");
				index[0]++;
				int numElements = Integer.parseInt(tokens.get(literalIndex).value);
				
				return new VectorType(elementType, numElements);
			} else {
				throw new IllegalArgumentException("Expecting '{' or literal");
			}
		}
			
		case LCURLY: {
			index[0]++;
			
			List<Type> fields = new ArrayList<Type>();
			while (index[0] < tokens.size()) {
				if (tokens.get(index[0]).type.equals(RCURLY)) {
					index[0]++;
					break;
				}
				
				Type field = parseType(tokens, index, map, update);
				if (index[0] >= tokens.size())
					throw new IllegalArgumentException("Premature EOF in structure field list");
				fields.add(field);
				
				if (tokens.get(index[0]).type.equals(COMMA)) {
					index[0]++;
					continue;
				} else if (tokens.get(index[0]).type.equals(RCURLY)) {
					index[0]++;
					break;
				} else {
					throw new IllegalArgumentException("Expecting ',' or '}'");
				}
			}

			StructureType result = new StructureType(packed, fields);
			assignHolders(result, map, new LinkedList<Type>(), update);
			return result;
		}
			
		case LBRACK: {
			index[0]++;
			if (index[0]+1 >= tokens.size())
				throw new IllegalArgumentException("Premature EOF in array");
			if (!(tokens.get(index[0]).type.equals(LITERAL) &&
				  tokens.get(index[0]+1).type.equals(CROSS)))
				throw new IllegalArgumentException("Expecting literal and cross for array type");
			int literalIndex = index[0];
			index[0]+=2;
			
			Type elementType = parseType(tokens, index, map, update);
			if (index[0] >= tokens.size())
				throw new IllegalArgumentException("Premature EOF in array type");
			if (!tokens.get(index[0]).type.equals(RBRACK))
				throw new IllegalArgumentException("Expecting ']'");
			index[0]++;
			
			int numElements = Integer.parseInt(tokens.get(literalIndex).value);
			ArrayType result = new ArrayType(elementType, numElements);
			assignHolders(result, map, new LinkedList<Type>(), update);
			return result;
		}
			
		default:
			throw new IllegalArgumentException("Expecting '<' or '{' or '['");
		}
	}
	
	private static Type parseBasicType(
			List<Token> tokens, 
			int[] index, 
			Map<HolderType,Integer> map) {
		if (index[0] >= tokens.size())
			throw new IllegalArgumentException("Premature EOF");
		
		Token first = tokens.get(index[0]);
		switch (first.type) {
		case QUESTION: {
			index[0]++;
			return new OpaqueType();
		}
 		case LABEL: {
 			index[0]++;
 			return Type.LABEL_TYPE;
 		}
 		case METADATA: {
 			index[0]++;
 			return Type.METADATA_TYPE;
 		}
		case VOID: {
			index[0]++;
			return Type.VOID_TYPE;
		}
		case INTEGER: {
			int width = Integer.parseInt(first.value.substring(1));
			index[0]++;
			return Type.getIntegerType(width);
		}
		case FLOAT: {
			index[0]++;
			return Type.getFloatingPointType(FloatingPointType.Kind.FLOAT);
		}
		case DOUBLE: {
			index[0]++;
			return Type.getFloatingPointType(FloatingPointType.Kind.DOUBLE);
		}
		case FP128: {
			index[0]++;
			return Type.getFloatingPointType(FloatingPointType.Kind.FP128);
		}
		case X86_FP80: {
			index[0]++;
			return Type.getFloatingPointType(FloatingPointType.Kind.X86_FP80);
		}
		case PPC_FP128: {
			index[0]++;
			return Type.getFloatingPointType(FloatingPointType.Kind.PPC_FP128);
		}
		case PARENTREF: {
			int ref = Integer.parseInt(first.value.substring(1));
			if (ref < 1)
				throw new IllegalArgumentException("parent ref " + first.value + " is invalid");
			index[0]++;
			
			HolderType result = new HolderType();
			map.put(result, ref);
			return result;
		}

		default:
			throw new IllegalArgumentException("Invalid token for basic type: " + first.type.getValue());
		}
	}
	
	
	/**
	 * Returns true if the given function body has any call instructions
	 * that use BB labels as parameters. We can't represent that in a PEG,
	 * so we will not try to build a CFG for those functions.
	 */
	public static boolean containsLabelParameters(FunctionBody body) {
		for (int i = 0; i < body.getNumBlocks(); i++) {
			BasicBlock bb = body.getBlock(i);
			for (int j = 0; j < bb.getNumInstructions(); j++) {
				Instruction inst = bb.getInstruction(j);
				if (!inst.isCall()) continue;
				CallInstruction call = inst.getCallSelf();
				for (int k = 0; k < call.getNumActuals(); k++) {
					if (call.getActual(k).isLabel())
						return true;
				}
			}
		}
		return false;
	}

	
	/**
	 * Returns true if this function contains any indirectbr instructions.
	 * We cannot represent those in PEG form. 
	 */
	public static boolean containsIndirectBranches(FunctionBody body) {
		for (int i = 0; i < body.getNumBlocks(); i++) {
			BasicBlock bb = body.getBlock(i);
			for (int j = 0; j < bb.getNumInstructions(); j++) {
				Instruction inst = bb.getInstruction(j);
				if (inst.isTerminator() && inst.getTerminatorSelf().isIndirectBR())
					return true;
			}
		}
		return false;
	}
	
	
	public static void main(String args[]) throws Throwable {
		Type type1 = LLVMUtils.parseType(args[0]);
		Type type2 = LLVMUtils.parseType(args[1]);
		System.out.println(type1 + " = " + type1.hashCode());
		System.out.println(type2 + " = " + type2.hashCode());
		System.out.println(type1.equalsType(type2));
	}
}
