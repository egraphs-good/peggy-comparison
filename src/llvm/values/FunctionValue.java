package llvm.values;

import java.util.*;

import llvm.types.FunctionType;
import llvm.types.PointerType;
import llvm.types.Type;

/**
 * This value denotes a function HEADER, not the body.
 * Only function headers go into the value table.
 * 
 * @author steppm
 */
public class FunctionValue extends Value implements LinkedAndVisible {
	public class ArgumentValue extends Value {
		protected final int index;
		protected final Type type;
		
		private ArgumentValue(int _index) {
			this.index = _index;
			FunctionType outerType = FunctionValue.this.type.getPointeeType().getFunctionSelf();
			this.type = outerType.getParamType(this.index);
		}
		
		public int getIndex() {return this.index;}
		
		public boolean isFunctionLocal() {return true;}
		
		public Type getType() {return this.type;}
		public Iterator<? extends Value> getSubvalues() {
			return new ValueIterator();
		}
		public boolean isArgument() {return true;}
		public ArgumentValue getArgumentSelf() {return this;}
		
		public String toString() {
			return this.type + " param" + this.index;
		}
		public boolean equalsValue(Value v) {
			if (!v.isArgument())
				return false;
			ArgumentValue a = v.getArgumentSelf();
			return this.index == a.index && this.getParent() == a.getParent();
		}
		public int hashCode() {
			return this.index*3 + this.getParent().hashCode()*59;
		}
		public FunctionValue getParent() {return FunctionValue.this;}
		
		protected ArgumentValue rewriteChildren(Map<Value,Value> old2new) {
			return this;
		}
	}
	

	protected final PointerType type;
	protected final int cc;
	protected final boolean isPrototype;
	protected final Linkage linkage;
	protected final ParameterAttributeMap paramAttrs;
	protected final int alignment;
	protected final int sectionIndex;
	protected final Visibility visibility;
	protected final int collectorIndex;
	protected final List<ArgumentValue> arguments;
	
	public FunctionValue(PointerType _type, int _cc, 
			 			 boolean _isProto, Linkage _linkage,
			 			 ParameterAttributeMap _paramAttrs, int _align,
			 			 int _sectionIndex, Visibility _visibility, 
			 			 int _collectorIndex) {
		if (!(_type.isComposite() && 
			  _type.getCompositeSelf().isPointer() && 
			  _type.getCompositeSelf().getPointerSelf().getPointeeType().isFunction()))
			throw new IllegalArgumentException("Type must be pointer to function");
		
		this.type = _type;
		this.cc = _cc;
		this.isPrototype = _isProto;
		this.linkage = _linkage;
		this.paramAttrs = _paramAttrs;
		this.alignment = _align;
		this.sectionIndex = _sectionIndex;
		this.visibility = _visibility;
		this.collectorIndex = _collectorIndex;
		
		FunctionType funcType = this.type.getPointeeType().getFunctionSelf();
		this.arguments = new ArrayList<ArgumentValue>(funcType.getNumParams());
		for (int i = 0; i < funcType.getNumParams(); i++) {
			this.arguments.add(new ArgumentValue(i));
		}
	}
	
	// index of 0 means the function, params are 1-based
	public ParameterAttributeMap getParameterAttributeMap() {return this.paramAttrs;}
	public int getCallingConvention() {return this.cc;}
	public boolean isPrototype() {return this.isPrototype;}
	public Linkage getLinkage() {return this.linkage;}
	public int getAlignment() {return this.alignment;}
	public int getSectionIndex() {return this.sectionIndex;}
	public Visibility getVisibility() {return this.visibility;}
	public int getCollectorIndex() {return this.collectorIndex;}
	public int getNumArguments() {return this.arguments.size();}
	public ArgumentValue getArgument(int i) {return this.arguments.get(i);}
	
	public void ensureConstant() {}
	public FunctionType getFunctionType() {
		return this.type.getPointeeType().getFunctionSelf();
	}
	public PointerType getType() {return this.type;}
	public Iterator<? extends Value> getSubvalues() {
		return Collections.unmodifiableList(this.arguments).iterator();
	}
	public boolean isFunction() {return true;}
	public FunctionValue getFunctionSelf() {return this;}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer(100);
		buffer.append("declare FUNC ");
		if (this.cc != 0)
			buffer.append(" cc=").append(this.cc);
		if (!this.linkage.equals(Linkage.ExternalLinkage)) 
			buffer.append(" linkage=").append(this.linkage.name());
		if (this.alignment != 0)
			buffer.append(" alignment=").append(this.alignment);
		if (!this.visibility.equals(Visibility.DefaultVisibility))
			buffer.append(" visibility=").append(this.visibility.name());
		if (this.collectorIndex != 0)
			buffer.append(" collector=").append(this.collectorIndex);
		if (this.sectionIndex != 0)
			buffer.append(" section=").append(this.sectionIndex);
		buffer.append(" (");
		for (int i = 0; i < this.arguments.size(); i++) {
			if (i > 0) buffer.append(", ");
			buffer.append(this.arguments.get(i));
		}
		FunctionType funcType = this.getType().getPointeeType().getFunctionSelf();
		if (funcType.isVararg())
			buffer.append(",...");
		buffer.append(") ").append(funcType.getReturnType());
		return buffer.toString();
	}
	public boolean equalsValue(Value o) {
		return o.isFunction() && o.getFunctionSelf() == this;
	}
	public int hashCode() {
		int result = 
			this.type.hashCode()*2 + 
			this.cc*3 +
			(this.isPrototype ? 5 : 7) +
			this.linkage.hashCode()*11 +
			this.alignment*13 +
			this.visibility.hashCode()*17 +
			this.collectorIndex*19 +
			this.paramAttrs.hashCode()*23;
		return result;
	}
	
	public boolean equivalentFunction(FunctionValue f) {
		if (!(this.type.equalsType(f.type) &&
			  this.cc == f.cc &&
			  this.isPrototype == f.isPrototype &&
			  this.linkage.equals(f.linkage) && 
			  this.alignment == f.alignment &&
			  this.visibility.equals(f.visibility)) &&
			  this.collectorIndex == f.collectorIndex &&
			  this.paramAttrs.equals(f.paramAttrs))
			return false;
		return true;
	}
	
	protected FunctionValue rewriteChildren(Map<Value,Value> old2new) {
		return this;
	}
}
