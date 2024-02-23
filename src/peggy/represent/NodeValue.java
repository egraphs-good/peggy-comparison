package peggy.represent;

// Disjoint triple class
public final class NodeValue<V,L,P> {
	private V vValue;
	private L lValue;
	private P pValue;
	private int which;
	
	public static <V,L,P> NodeValue<V,L,P> makeV(V _v){
		return new NodeValue<V,L,P>(_v, null, null, 0);
	}
	public static <V,L,P> NodeValue<V,L,P> makeL(L _l){
		return new NodeValue<V,L,P>(null, _l, null, 1);
	}
	public static <V,L,P> NodeValue<V,L,P> makeP(P _p){
		return new NodeValue<V,L,P>(null, null, _p, 2);
	}
	
	private NodeValue(V _v, L _l, P _p, int _which){
		vValue = _v;
		lValue = _l;
		pValue = _p;
		which = _which;
	}
	
	public boolean isV(){return which==0;}
	public boolean isL(){return which==1;}
	public boolean isP(){return which==2;}
	
	public V getV(){
		if (!isV())
			throw new UnsupportedOperationException();
		return vValue;
	}
	public L getL(){
		if (!isL())
			throw new UnsupportedOperationException();
		return lValue;
	}
	public P getP(){
		if (!isP())
			throw new UnsupportedOperationException();
		return pValue;
	}
	
	
	
	public boolean equals(Object o){
		if (o==null || !(o instanceof NodeValue))
			return false;
		
		NodeValue<V,L,P> n = (NodeValue<V,L,P>)o;
		if (n.which!=which)
			return false;
		switch(which){
		case 0: return n.vValue.equals(vValue);
		case 1: return n.lValue.equals(lValue);
		case 2: return n.pValue.equals(pValue);
		}
		return false;
	}
	
	
	public int hashCode(){
		int result = which*37;
		switch(which){
		case 0: result += (vValue==null ? 101 : vValue.hashCode()); break;
		case 1: result += (lValue==null ? 43 : lValue.hashCode()); break;
		case 2: result += (pValue==null ? 79 : pValue.hashCode()); break;
		}
		return result;
	}
	
	
	public String toString(){
		String result = "NodeValue[";
		switch(which){
		case 0: result += "0]{"+vValue+"}"; break;
		case 1: result += "1]{"+lValue+"}"; break;
		case 2: result += "2]{"+pValue+"}"; break;
		}
		return result;
	}
}
