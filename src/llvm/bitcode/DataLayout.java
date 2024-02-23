package llvm.bitcode;

import static llvm.bitcode.DataLayout.AlignType.AGGREGATE;
import static llvm.bitcode.DataLayout.AlignType.FLOAT;
import static llvm.bitcode.DataLayout.AlignType.INTEGER;
import static llvm.bitcode.DataLayout.AlignType.STACK;
import static llvm.bitcode.DataLayout.AlignType.VECTOR;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import llvm.types.ArrayType;
import llvm.types.StructureType;
import llvm.types.Type;
import llvm.types.VectorType;

/**
 * This class describes the layout and alignment in memory of certain data types.
 * Every module has a data layout.
 * 
 * @author steppm
 */
public class DataLayout {
	public static enum AlignType {
		INTEGER,
		FLOAT,
		VECTOR,
		AGGREGATE,
		STACK;
	}
	
	public static class AlignmentInfo {
		public final AlignType type;
		public final int bitWidth;
		
		// these values are in BYTES!
		public final int abiSize;
		public final int prefSize;
		public AlignmentInfo(AlignType _type, int _bitWidth, int _abiSize, int _prefSize) {
			if (_bitWidth < 0)
				throw new IllegalArgumentException("Invalid bitWidth: " + _bitWidth);
			if (_abiSize < 0 || _prefSize < 0 || _abiSize > _prefSize)
				throw new IllegalArgumentException("Invalid sizes: " + _abiSize + ", " + _prefSize);
			this.type = _type;
			this.bitWidth = _bitWidth;
			this.abiSize = _abiSize;
			this.prefSize = _prefSize;
		}
	}
	
	public static class StructLayout {
		public final int structSize;
		public final int structAlignment;
		public final StructureType type;
		private final int[] memberOffsets;
		
		private StructLayout(
				int _structSize,
				int _structAlignment,
				StructureType _type,
				int[] _memberOffsets) {
			this.structSize = _structSize;
			this.structAlignment = _structAlignment;
			this.type = _type;
			this.memberOffsets = (int[])_memberOffsets.clone();
		}
		public int getMemberOffset(int index) {
			return this.memberOffsets[index];
		}
	}
	
	private boolean isBigEndian;
	// these values are in bytes!
	private int pointerMemSize;
	private int pointerABISize;
	private int pointerPrefSize;
	private final Map<AlignType,List<AlignmentInfo>> alignmap;
	private final Map<StructureType,StructLayout> structlayoutmap;
	
	public DataLayout() {
		this.alignmap = new EnumMap<AlignType,List<AlignmentInfo>>(AlignType.class);
		this.structlayoutmap = new HashMap<StructureType,StructLayout>();
		setDefaultAlignments();
	}
	
	public DataLayout(String datalayout) {
		this();
		setAlignments(datalayout);
	}
	
	public boolean isBigEndian() {return this.isBigEndian;}
	
	private void setDefaultAlignments() {
		this.isBigEndian = true;
		this.pointerMemSize = 8;
		this.pointerABISize = this.pointerPrefSize = 8;
		
		setAlignment(INTEGER, 1, 1, 1);
		setAlignment(INTEGER, 8, 1, 1);
		setAlignment(INTEGER, 16, 2, 2);
		setAlignment(INTEGER, 32, 4, 4);
		setAlignment(INTEGER, 64, 4, 8);
		setAlignment(FLOAT, 32, 4, 4);
		setAlignment(FLOAT, 64, 8, 8);
		setAlignment(VECTOR, 64, 8, 8);
		setAlignment(VECTOR, 128, 16, 16);
		setAlignment(AGGREGATE, 0, 0, 8);
	}
	
	/**
	 * ABI and pref should be in terms of BYTES, not bits!
	 * 
	 * @param type
	 * @param bitWidth
	 * @param abi
	 * @param pref
	 */
	private void setAlignment(AlignType type, int bitWidth, int abi, int pref) {
		List<AlignmentInfo> list = this.alignmap.get(type);
		if (list == null) {
			list = new ArrayList<AlignmentInfo>();
			this.alignmap.put(type, list);
		}
		
		int index = -1;
		for (int i = 0; i < list.size(); i++) {
			AlignmentInfo info = list.get(i);
			if (info.bitWidth == bitWidth) {
				index = i;
				break;
			}
		}
		
		if (index == -1) {
			AlignmentInfo newinfo = new AlignmentInfo(type, bitWidth, abi, pref);
			list.add(newinfo);
		} else {
			AlignmentInfo newinfo = new AlignmentInfo(type, bitWidth, abi, pref);
			list.set(index, newinfo);
		}
	}
	
	
	private static final Pattern pointer1 = Pattern.compile("^p:(\\d+):(\\d+):(\\d+)$");
	private static final Pattern pointer2 = Pattern.compile("^p:(\\d+):(\\d+)$");
	private static final Pattern alignmentPattern1 = Pattern.compile("^[ivfas](\\d+):(\\d+):(\\d+)$");
	private static final Pattern alignmentPattern2 = Pattern.compile("^[ivfas](\\d+):(\\d+)$");
	private void setAlignments(String datalayout) {
		String[] tokens = datalayout.split("-");
		for (String token : tokens) {
			if (token.length() == 0)
				throw new IllegalArgumentException("Invalid data layout piece: " + token);
			char first = token.charAt(0);

			AlignType type = null;
			
			switch(first) {
			case 'e':
				this.isBigEndian = false;
				break;
			case 'E':
				this.isBigEndian = true;
				break;
			case 'p': {
				Matcher matcher1 = pointer1.matcher(token);
				Matcher matcher2 = pointer2.matcher(token);
				if (matcher1.matches()) {
					int pms = Integer.parseInt(matcher1.group(1));
					int pas = Integer.parseInt(matcher1.group(2));
					int pps = Integer.parseInt(matcher1.group(3));
					
					if (pms <= 0)
						throw new IllegalArgumentException("Pointer memory size must be positive: " + pms);
					if (pas < 0 || pps < 0)
						throw new IllegalArgumentException("Invalid pointer ABI and pref sizes: " + pas + ", " + pps);

					this.pointerMemSize = pms>>3;
					this.pointerABISize = pas>>3;
					this.pointerPrefSize = pps>>3;
				} else if (matcher2.matches()) {
					int pms = Integer.parseInt(matcher2.group(1));
					int pas = Integer.parseInt(matcher2.group(2));
					int pps = pas;
					
					if (pms <= 0)
						throw new IllegalArgumentException("Pointer memory size must be positive: " + pms);
					if (pas < 0 || pps < 0)
						throw new IllegalArgumentException("Invalid pointer ABI and pref sizes: " + pas + ", " + pps);

					this.pointerMemSize = pms>>3;
					this.pointerABISize = pas>>3;
					this.pointerPrefSize = pps>>3;
				} else {
					throw new IllegalArgumentException("Bad pointer alignment string: " + token);
				}
				break;
			} 
			case 'i':
				type = INTEGER;
			case 'v':
				if (type == null) type = VECTOR;
			case 'f':
				if (type == null) type = FLOAT;
			case 'a':
				if (type == null) type = AGGREGATE;
			case 's': {
				if (type == null) type = STACK;
				Matcher matcher1 = alignmentPattern1.matcher(token);
				Matcher matcher2 = alignmentPattern2.matcher(token);
				if (matcher1.matches()) {
					int bitWidth = Integer.parseInt(matcher1.group(1));
					int abiSize = Integer.parseInt(matcher1.group(2))>>3;
					int prefSize = Integer.parseInt(matcher1.group(3))>>3;
					setAlignment(type, bitWidth, abiSize, prefSize);
				} else if (matcher2.matches()) {
					int bitWidth = Integer.parseInt(matcher2.group(1))>>3;
					int abiSize = Integer.parseInt(matcher2.group(2))>>3;
					setAlignment(type, bitWidth, abiSize, abiSize);
				} else {
					throw new IllegalArgumentException("Invalid integer alignment string: " + token);
				}
				break;
			}
			default:
				throw new IllegalArgumentException("Invalid data layout piece: " + token);
			}
		}
	}

	public StructLayout getStructLayout(StructureType type) {
		StructLayout layout = this.structlayoutmap.get(type);
		if (layout == null) {
			int structSize = 0;
			int structAlignment = 0;
			int[] memberOffsets = new int[type.getNumFields()];
			
			for (int i = 0; i < type.getNumFields(); i++) {
				Type elementType = type.getFieldType(i);
				
				int typeAlign = type.isPacked() ? 1 : getAlignment(elementType, true);
				int typeSize = type.isPacked() ? getTypeStoreSize(elementType) : getABITypeSize(elementType);
				
				structSize = ((structSize + typeAlign - 1)/typeAlign) * typeAlign;
				
				if (typeAlign > structAlignment)
					structAlignment = typeAlign;
				
				memberOffsets[i] = structSize;
				structSize += typeSize;
			}
			
			if (structAlignment == 0)
				structAlignment = 1;
			
			if ((structSize % structAlignment) != 0) 
				structSize = (structSize/structAlignment + 1) * structAlignment;
			
			layout = new StructLayout(structSize, structAlignment, type, memberOffsets);
			this.structlayoutmap.put(type, layout);
		}
		return layout;
	}
	
	public int getAlignment(Type type, boolean abi) {
		if (type.isLabel() || (type.isComposite() && type.getCompositeSelf().isPointer())) {
			return (abi ? this.pointerABISize : this.pointerPrefSize);
		} else if (type.isComposite() && type.getCompositeSelf().isArray()) {
			return getAlignment(type.getCompositeSelf().getArraySelf().getElementType(), abi);
		} else if (type.isComposite() && type.getCompositeSelf().isStructure()) {
			StructureType stype = type.getCompositeSelf().getStructureSelf();
			if (stype.isPacked() && abi)
				return 1;
			StructLayout layout = getStructLayout(stype);
			int Align = getAlignmentFromInfo(AGGREGATE, 0, abi, type);
			return Math.max(Align, layout.structAlignment);
		} else if (type.isInteger() || type.isVoid()) {
			return getAlignmentFromInfo(INTEGER, getTypeSizeInBits(type), abi, type);
		} else if (type.isFloatingPoint()) {
			return getAlignmentFromInfo(FLOAT, getTypeSizeInBits(type), abi, type);
		} else if (type.isComposite() && type.getCompositeSelf().isVector()) {
			return getAlignmentFromInfo(VECTOR, getTypeSizeInBits(type), abi, type);
		} else {
			throw new IllegalArgumentException("Type has no size: " + type);
		}
	}
	
	private int getAlignmentFromInfo(AlignType aligntype, int bitWidth, boolean abi, Type type) {
		AlignmentInfo bestMatch = null;
		AlignmentInfo largestInt = null;
		
		List<AlignmentInfo> list = this.alignmap.get(aligntype);
		if (list == null) {
			list = new ArrayList<AlignmentInfo>();
			this.alignmap.put(aligntype, list);
		}
		
		for (AlignmentInfo info : list) {
			if (info.bitWidth == bitWidth)
				return (abi ? info.abiSize : info.prefSize);
			
			if (aligntype.equals(VECTOR)) {
				if (info.bitWidth < bitWidth) {
					if (bestMatch == null || bestMatch.bitWidth < info.bitWidth)
						bestMatch = info;
				}
			} else if (aligntype.equals(INTEGER)) {
				if (info.bitWidth > bitWidth && 
					(bestMatch == null || info.bitWidth < bestMatch.bitWidth))
					bestMatch = info;

				if (largestInt == null ||
					(info.bitWidth > largestInt.bitWidth))
					largestInt = info;
			}
		}

		
		if (bestMatch == null) {
			if (aligntype.equals(INTEGER)) {
				bestMatch = largestInt;
			} else if (aligntype.equals(VECTOR)) {
				return getAlignment(type.getCompositeSelf().getVectorSelf().getElementType(), abi);
			} else {
				throw new RuntimeException("Unknown alignment type: " + aligntype);
			}
		}
		
		return (abi ? bestMatch.abiSize : bestMatch.prefSize);
	}
	
	public int getTypeSizeInBits(Type type) {
		if (type.isLabel() || (type.isComposite() && type.getCompositeSelf().isPointer())) {
			return this.pointerMemSize*8;
		} else if (type.isComposite() && type.getCompositeSelf().isArray()) {
			ArrayType array = type.getCompositeSelf().getArraySelf();
			return 8 * getABITypeSize(array.getElementType()) * (int)array.getNumElements().signedValue();
		} else if (type.isComposite() && type.getCompositeSelf().isStructure()) {
			StructureType stype = type.getCompositeSelf().getStructureSelf();
			StructLayout layout = getStructLayout(stype);
			return layout.structSize*8;
		} else if (type.isInteger()) {
			return type.getIntegerSelf().getWidth();
		} else if (type.isVoid()) {
			return 8;
		} else if (type.isFloatingPoint()) {
			switch (type.getFloatingPointSelf().getKind()) {
			case FLOAT:
				return 32;
			case DOUBLE:
				return 64;
			case X86_FP80:
				return 80;
			case FP128:
			case PPC_FP128:
				return 128; 
			default:
				throw new RuntimeException("Mike didn't handle: " + type.getFloatingPointSelf().getKind());
			}
		} else if (type.isComposite() && type.getCompositeSelf().isVector()) {
			VectorType vector = type.getCompositeSelf().getVectorSelf();
			int eltSize = getTypeSizeInBits(vector.getElementType());
			return eltSize * (int)vector.getNumElements().signedValue();
		} else {
			throw new IllegalArgumentException("Type has no size: " + type);
		}
	}
	
	public int getTypeStoreSize(Type type) {
		return (getTypeSizeInBits(type)+7)/8;
	}
	
	public int getABITypeSize(Type type) {
		int Align = getAlignment(type, true);
		return ((getTypeStoreSize(type) + Align - 1)/Align) * Align;
	}


	
	private void dump() {
		System.out.println("DataLayout:");
		System.out.println("  isBigEndian = " + this.isBigEndian);
		System.out.println("  pointerMemSize = " + this.pointerMemSize);
		System.out.println("  pointerABISize = " + this.pointerABISize);
		System.out.println("  pointerPrefSize = " + this.pointerPrefSize);

		for (AlignType type : this.alignmap.keySet()) {
			System.out.println("  " + type.name() + " = {");
			for (AlignmentInfo info : this.alignmap.get(type)) {
				System.out.println("    " + info.bitWidth + " " + info.abiSize + " " + info.prefSize);
			}
			System.out.println("  }");
		}
	}
	
	
	
	
	public static void main(String args[]) {
		DataLayout layout = new DataLayout();
		layout.dump();
		
		layout.setAlignments("e-i1:64-i8:64:64-i16:64:64-s8:64:64-a8:64:64");
		layout.dump();
		
		StructureType stype = new StructureType(false, Arrays.<Type>asList(Type.getIntegerType(8), Type.getIntegerType(8)));
		System.out.println(layout.getABITypeSize(stype));
	}
}
