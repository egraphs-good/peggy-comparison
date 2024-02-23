package llvm.values;

import java.util.Iterator;
import java.util.Map;

import llvm.types.PointerType;

/**
 * Represents a global variable. Globals have a type (must be a pointer),
 * may be constant, have an initial value, have a linkage, have an alignment,
 * have a section, visibility, and may be thread local. 
 */
public class GlobalVariable extends Value implements LinkedAndVisible {
	protected final PointerType type;
	protected final boolean isConstant;
	protected Value initialValue; // can be null!
	protected final Linkage linkage;
	protected final int alignment;
	protected final int sectionIndex;
	protected final Visibility visibility;
	protected final boolean isThreadLocal;

	public GlobalVariable(
			PointerType _type, boolean _isConstant,
			Value _initialValue, Linkage _linkage, 
			int _alignment, int _sectionIndex, 
			Visibility _visibility, boolean _isThreadLocal) {
		if (_initialValue != null && !_initialValue.getType().equalsType(_type.getPointeeType()))
			throw new IllegalArgumentException("Initial value for global var has wrong type");
		this.type = _type;
		this.isConstant = _isConstant;
		this.initialValue = _initialValue;
		this.linkage = _linkage;
		this.alignment = _alignment;
		this.sectionIndex = _sectionIndex;
		this.visibility = _visibility;
		this.isThreadLocal = _isThreadLocal;
	}
	
	public void ensureConstant() {}
	public boolean isConstant() {return this.isConstant;}
	public Value getInitialValue() {return this.initialValue;}
	public Linkage getLinkage() {return this.linkage;}
	public int getAlignment() {return this.alignment;}
	public int getSectionIndex() {return this.sectionIndex;}
	public Visibility getVisibility() {return this.visibility;}
	public boolean isThreadLocal() {return this.isThreadLocal;}
	
	public PointerType getType() {return this.type;}
	public Iterator<? extends Value> getSubvalues() {
		if (this.initialValue != null)
			return new ValueIterator(this.initialValue);
		else
			return new ValueIterator();
	}
	public boolean isGlobalVariable() {return true;}
	public GlobalVariable getGlobalVariableSelf() {return this;}

	public String toString() {
		StringBuffer buffer = new StringBuffer(100);
		if (this.isConstant)
			buffer.append("constant ");
		buffer.append("global ").append(this.type.toString());
		if (this.sectionIndex != 0)
			buffer.append(" section=").append(this.sectionIndex);
		if (this.alignment != 0)
			buffer.append(" align=").append(this.alignment);
		if (!this.linkage.equals(Linkage.ExternalLinkage))
			buffer.append(" linkage=").append(this.linkage.name());
		if (!this.visibility.equals(Visibility.DefaultVisibility))
			buffer.append(" visibility=").append(this.visibility.name());
		if (this.isThreadLocal)
			buffer.append(" threadlocal");
		return buffer.toString();
	}
	public boolean equalsValue(Value o) {
		return o.isGlobalVariable() && o.getGlobalVariableSelf() == this;
	}
	public int hashCode() {
		return 
			this.type.hashCode()*3 +
			(this.isConstant ? 5 : 7) +
			this.linkage.hashCode()*11 +
			this.alignment*13 + 
			this.sectionIndex*17 +
			this.visibility.hashCode()*19 +
			(this.isThreadLocal ? 23 : 29);
	}
	
	public boolean equivalentGlobal(GlobalVariable g) {
		return 
			this.type.equalsType(g.type) &&
			this.isConstant == g.isConstant &&
			this.linkage.equals(g.linkage) &&
			this.alignment == g.alignment &&
			this.sectionIndex == g.sectionIndex &&
			this.visibility.equals(g.visibility) &&
			this.isThreadLocal == g.isThreadLocal;
	}
	
	protected GlobalVariable rewriteChildren(Map<Value,Value> old2new) {
		if (this.initialValue != null) {
			this.initialValue = this.initialValue.rewrite(old2new);
		}
		return this;
	}
}
