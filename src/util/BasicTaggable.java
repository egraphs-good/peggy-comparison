package util;

import util.pair.LinkedPairedList;
import util.pair.PairedList;

public class BasicTaggable implements Taggable {
	protected PairedList<Tag,Object> mTags = null;
	
	protected void checkTags() {mTags = new LinkedPairedList<Tag,Object>();}

	public boolean hasTag(Tag label) {
		return mTags != null && mTags.indexOfFirst(label) != -1;
	}
	
	public <T> T getTag(Tag<T> label) {
		return mTags == null ? null : (T)mTags.findSecond(label);
	}
	
	public void setTag(Tag<Void> label) {setTag(label, null);}

	public <T> T setTag(Tag<T> label, T tag) {
		if (mTags == null) {
			checkTags();
			mTags.add(label, tag);
			return null;
		}
		int index = mTags.indexOfFirst(label);
		if (index != -1)
			return (T)mTags.setSecond(index, tag);
		mTags.add(label, tag);
		return null;
	}

	public <T> T removeTag(Tag<T> label) {
		return mTags == null ? null : (T)mTags.removeFirst(label);
	}
	
	public String tagsToString() {
		return mTags == null ? "[]" : mTags.toString();
	}
}
