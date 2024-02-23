package util.integer;

public abstract class AbstractIntListIterator extends AbstractIntIterator
		implements IntListIterator {
	public Integer previous() {return previousInt();}
	
	public void add(Integer element) {add(element.intValue());}
	
	public void set(Integer element) {set(element.intValue());}
}
