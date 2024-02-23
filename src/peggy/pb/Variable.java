package peggy.pb;

public class Variable<N>{
	public final N node;
	public final String name;
	
	protected Variable(String _name){
		this(_name, null);
	}
	
	protected Variable(String _name, N _node){
		name = _name;
		node = _node;
	}
	
	public boolean equals(Object o){
		if (o==null || !(o instanceof Variable))
			return false;
		Variable<N> v = (Variable<N>)o;
		return v.name.equals(name);
	}
	
	public int hashCode(){
		return name.hashCode();
	}
	
	public String toString(){
		return "("+name+":"+node+")";
	}
}
