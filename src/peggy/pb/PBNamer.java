package peggy.pb;

public interface PBNamer<N> {
	public String getName(N node);
	public String getTransitiveName(N a, N b);
}
