package util.graph;

import java.util.Map;
import java.util.WeakHashMap;

import util.Tag;
import util.Taggable;

public abstract class TaggableVertex<G extends Graph<G,V>,V extends Vertex<G,V>>
		extends AbstractVertex<G,V> implements Taggable {
	protected Map<Tag,Object> mTags = null;
	
	protected void checkTags() {
		if (mTags == null)
			mTags = new WeakHashMap<Tag,Object>();
	}

	public boolean hasTag(Tag label) {
		return mTags != null && mTags.containsKey(label);
	}
	
	public <T> T getTag(Tag<T> label) {
		return mTags == null ? null : (T)mTags.get(label);
	}
	
	public void setTag(Tag<Void> label) {setTag(label, null);}

	public <T> T setTag(Tag<T> label, T tag) {
		checkTags();
		return (T)mTags.put(label, tag);
	}

	public <T> T removeTag(Tag<T> label) {
		return mTags == null ? null : (T)mTags.remove(label);
	}
	
	public String tagsToString() {
		return mTags == null ? "[]" : mTags.toString();
	}
}
