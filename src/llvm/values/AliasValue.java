package llvm.values;

import java.util.Iterator;
import java.util.Map;

import llvm.types.PointerType;

/**
 * This represents an alias value, which has a type, an aliasee (function or global),
 * a linkage type and a visibility type.
 */
public class AliasValue extends Value implements LinkedAndVisible {
	protected final PointerType type;
	protected Value aliasee;
	protected final Linkage linkage;
	protected final Visibility visibility;
	
	public AliasValue(PointerType _type, Value _aliasee, Linkage _linkage, Visibility _visibility) {
		this.type = _type;
		this.aliasee = _aliasee;
		this.linkage = _linkage;
		this.visibility = _visibility;
	}
	
	public Value getAliaseeValue() {return this.aliasee;}
	public Linkage getLinkage() {return this.linkage;}
	public Visibility getVisibility() {return this.visibility;}

	public void ensureConstant() {this.aliasee.ensureConstant();}
	public PointerType getType() {return this.type;}
	public Iterator<? extends Value> getSubvalues() {
		return new ValueIterator(this.aliasee);
	}
	public boolean isAlias() {return true;}
	public AliasValue getAliasSelf() {return this;}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer(100);
		buffer.append("alias ").append(this.type);
		if (!this.linkage.equals(Linkage.ExternalLinkage))
			buffer.append(" linkage=").append(this.linkage.name());
		if (!this.visibility.equals(Visibility.DefaultVisibility))
			buffer.append(" visibility=").append(this.visibility.name());
		buffer.append(" @(").append(this.aliasee.toString()).append(")");
		return buffer.toString();
	}
	public boolean equalsValue(Value o) {
		return o.isAlias() && o.getAliasSelf() == this;
	}
	public int hashCode() {
		return 
			this.type.hashCode()*31 +
			this.aliasee.hashCode()*37 +
			this.linkage.hashCode()*41 +
			this.visibility.hashCode()*43;
	}

	public boolean equivalentAlias(AliasValue a) {
		return 
			this.type.equalsType(a.type) &&
			this.aliasee.equalsValue(a.aliasee) &&
			this.linkage.equals(a.linkage) && 
			this.visibility.equals(a.visibility);
	}
	
	protected AliasValue rewriteChildren(Map<Value,Value> old2new) {
		this.aliasee = this.aliasee.rewrite(old2new);
		return this;
	}
}
