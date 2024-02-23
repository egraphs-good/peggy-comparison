package util;

public interface Taggable {
	public boolean hasTag(Tag label);
	public <T> T getTag(Tag<T> label);
	public void setTag(Tag<Void> label);
	public <T> T setTag(Tag<T> label, T tag);
	public <T> T removeTag(Tag<T> label);
	public String tagsToString();
}
