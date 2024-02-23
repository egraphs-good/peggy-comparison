package peggy.ilp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import llvm.bitcode.HashList;
import peggy.pb.CostModel;
import peggy.pb.CounterOutputStream;
import peggy.pb.EngineExpressionDigraph;
import peggy.represent.StickyPredicate;
import eqsat.meminfer.engine.peg.CPEGTerm;
import eqsat.meminfer.engine.peg.CPEGValue;

/**
 * This formulation defines ILP variables that specify the constraints for 
 * choosing the best PEG from an EPEG. It is meant to be given as input to 
 * GLPSOL.
 */
public abstract class GLPKFormulation<L,P> {
	private static final boolean DEBUG = true;
	private static void debug(String message) {
		if (DEBUG)
			System.err.println("GLPKFormulation: " + message);
	}
	
	public static final int MAX_THETA_VALUE = 1024;
	
	public static enum Operator {
		LESS_EQUAL("<="),
		GREATER_EQUAL(">="),
		EQUAL("=");
		private final String label;
		private Operator(String _label) {
			this.label = _label;
		}
		public String getLabel() {return this.label;}
	}
	
	private final File backingFile;
	private final int maxSize;
	private int ruleCounter;
	private final EngineExpressionDigraph<CPEGValue<L,P>,CPEGTerm<L,P>> graph;
	private final HashList<CPEGTerm<L,P>> orderedTerms;
	private final boolean hasThetas;
	
	private class WM {
		private final Map<String,Integer> map = 
			new HashMap<String,Integer>();
		WM(Object... pairs) {
			if ((pairs.length&1) != 0)
				throw new IllegalArgumentException("Need an even number");
			for (int i = 0; i < pairs.length; i+=2) {
				if (!(pairs[i] instanceof String && pairs[i+1] instanceof Integer))
					throw new IllegalArgumentException("Need pair of String and Integer");
				this.map.put((String)pairs[i], (Integer)pairs[i+1]);
			}
		}
		public void put(String s, int i) {this.map.put(s, i);}
		public int get(String s) {return this.map.get(s);}
		public Iterable<String> keys() {return this.map.keySet();}
	}
	
	public GLPKFormulation(File _backingFile, EngineExpressionDigraph<CPEGValue<L,P>,CPEGTerm<L,P>> _graph) {
		this(_backingFile, -1, _graph);
	}
	public GLPKFormulation(File _backingFile, int _maxSize, EngineExpressionDigraph<CPEGValue<L,P>,CPEGTerm<L,P>> _graph) {
		this.backingFile = _backingFile;
		this.maxSize = _maxSize;
		this.graph = _graph;
		this.orderedTerms = new HashList<CPEGTerm<L,P>>();
		boolean thetas = false;
		for (CPEGTerm<L,P> term : this.graph.getNodes()) {
			if (term.getOp().isTheta())
				thetas = true;
			this.orderedTerms.add(term);
		}
		this.hasThetas = thetas;
	}
	
	public File getBackingFile() {return this.backingFile;}
	protected abstract CostModel<CPEGTerm<L,P>,Integer> getCostModel(); 
	public CPEGTerm<L,P> getTerm(int index) {return this.orderedTerms.getValue(index);}
	public int getIndex(CPEGTerm<L,P> term) {return this.orderedTerms.getIndex(term);}
	
	public static String escape(String str) {
		StringBuffer result = new StringBuffer(str.length()*2);
		char[] hex = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
		for (int i=0; i<str.length(); i++) {
			char c = str.charAt(i);
			if (c == '\n') {
				result.append("\\n");
			} else if (c == '\r') {
				result.append("\\r");
			} else if (c < 32 || c > 127) {
				result.append("\\u" + hex[(c>>12)&0xF] + hex[(c>>8)&0xF] + hex[(c>>4)&0xF] + hex[c&0xF]);
			} else {
				result.append(c);
			}
		}
		return result.toString();
	}
	
	private String N(int n) {return "N[" + n + "]";}
	private String V(int v) {return "V[" + v + "]";}
	private String TI(int n, int i) {return "TI_" + n + "[" + i + "]";} 
	private String TO(int v) {return "TO[" + v + "]";}
	private String T(int v) {return "T[" + v + "]";}
	
	private class State {
		private final PrintStream out;
		private final CounterOutputStream cout;
		private final int maxSize;
		State(OutputStream _out, int _maxSize) {
			this.cout = new CounterOutputStream(_out);
			this.out = new PrintStream(this.cout);
			this.maxSize = _maxSize;
		}
		public void print(String msg) throws IOException {
			this.out.print(msg);
			if (this.maxSize > 0 && 
				this.cout.getWrittenByteCount() > this.maxSize)
				throw new IOException("Maximum file size exceeded");
		}
		public void println(String msg) throws IOException {
			this.out.println(msg);
			if (this.maxSize > 0 && 
				this.cout.getWrittenByteCount() > this.maxSize)
				throw new IOException("Maximum file size exceeded");
		}
		public void close() {
			this.out.close();
		}
	}
	
	public void writeFormulation() 
	throws IOException {
		this.ruleCounter = 0;
		
		CostModel<CPEGTerm<L,P>, Integer> costModel = this.getCostModel();
		
		HashList<CPEGValue<L,P>> orderedValues = new HashList<CPEGValue<L,P>>();
		for (CPEGValue<L,P> value : this.graph.getValues()) {
			orderedValues.add(value);
		}

		debug("Number of nodes: " + this.orderedTerms.size());
		debug("Number of values: " + orderedValues.size());
		
		State state = new State(new FileOutputStream(this.backingFile), this.maxSize);
		this.makeVariables(state, orderedValues);
		
		this.rule_nodeImpliesChildValues(state, orderedValues);
		this.rule_valueImpliesOneMember(state, orderedValues);
		this.rule_rootValuesUsed(state, orderedValues);
		if (this.hasThetas) {
			this.rule_thetaFlowPropagation(state, orderedValues);
			this.rule_edgeConnectsThetaFlow(state, orderedValues);
		} else {
			this.rule_thetaFlowLoopless(state, orderedValues);
		}
		
		WM objwm = new WM();
		for (int k = 0; k < this.orderedTerms.size(); k++) {
			CPEGTerm<L,P> nk = this.orderedTerms.getValue(k);
			int cost = costModel.cost(nk);
			if (cost > 0)
				objwm.put(N(k), cost);
		}
		this.writeObjectiveFunction(state, true, objwm);
		
		state.println("solve;");
		state.println("for {i in 0.." + (this.orderedTerms.size()-1) + ": N[i]>0.0}");
		state.println("{");
		state.println("   printf \"OUTPUT N[%d]\\n\", i;");
		state.println("}");
		state.println("end;");
		
		state.close();
	}

	private void B_implies_X1geX2plusD(
			State out,
			String B, String X1, String X2, int D,
			String comment) throws IOException {
		/* B => (X1 >= X2+D)
		 * ~B | (X1 >= X2+D)
		 * ==>
		 * X1 - X2 - D + (1-B)*M >= 0
		 * X1 - X2 - M*B >= D-M
		 */
		int M = MAX_THETA_VALUE + Math.abs(D) + 3;
		this.writeConstraint(
				out, 
				new WM(X1, 1, X2, -1, B, -M), Operator.GREATER_EQUAL, D-M, 
				comment);
	}
	
	private void rule_thetaFlowLoopless(
			State out,
			HashList<CPEGValue<L,P>> values) throws IOException {
		// N_k => (T_cvi >= T_v + 1)
		for (int k = 0; k < this.orderedTerms.size(); k++) {
			CPEGTerm<L,P> nk = this.orderedTerms.getValue(k);
			int v = values.getIndex(nk.getValue());
			for (int i = 0; i < nk.getArity(); i++) {
				int cvi = values.getIndex(this.graph.getChildValue(nk, i));
				this.B_implies_X1geX2plusD(out, N(k), T(cvi), T(v), 1,
						"Theta flow rule: loopless");
			}
		}
		
		// for roots: T_vr = 0
		for (CPEGValue<L,P> Vr : this.graph.getRootValues()) {
			int vr = values.getIndex(Vr);
			this.writeConstraint(
					out, 
					new WM(T(vr), 1), Operator.EQUAL, 0, 
					"Theta flow of root is 0");
		}
	}
	
	private void B_implies_X1geX2(
			State out, 
			String B, String X1, String X2, 
			String comment) throws IOException {
		/* B => (X1>=X2)
		 * ~B | (X1>=X2)
		 * ==>
		 * X1-X2 + (1-B)*M >= 0
		 * X1 - X2 - M*B >= -M
		 */
		int M = MAX_THETA_VALUE;
		this.writeConstraint(
				out, 
				new WM(X1, 1, X2, -1, B, -M), Operator.GREATER_EQUAL, -M, 
				comment);
	}
	
	private void rule_edgeConnectsThetaFlow(
			State out,
			HashList<CPEGValue<L,P>> values) throws IOException {
		// N_k => (TO_cvi >= TI_k_i)
		for (int k = 0; k < this.orderedTerms.size(); k++) {
			CPEGTerm<L,P> nk = this.orderedTerms.getValue(k);
			for (int i = 0; i < nk.getArity(); i++) {
				int cvi = values.getIndex(nk.getChild(i).getValue());
				this.B_implies_X1geX2(out, N(k), TO(cvi), TI(k, i), 
						"Edge connects theta flows");
			}
		}
	}
	
	private void B_implies_X1eqX2plusD(
			State out,
			String B, String X1, String X2, int D,
			String comment) throws IOException {
		/* B => (X1 = X2+D)
		 * ~B | ((X1 <= X2+D) & (X1 >= X2+D))
		 * (~B | (X1 <= X2+D))   &   (~B | (X1 >= X2+D))
		 * ==>
		 * X1-X2-D - (1-B)*M <= 0
		 * X1 - X2 + M*B <= D+M
		 * 
		 * X1-X2-D + (1-B)*M >= 0
		 * X1 - X2 - M*B >= D-M
		 */
		int M = MAX_THETA_VALUE;
		this.writeConstraint(
				out, 
				new WM(X1, 1, X2, -1, B, M), Operator.LESS_EQUAL, D+M, 
				comment);

		this.writeConstraint(
				out, 
				new WM(X1, 1, X2, -1, B, -M), Operator.GREATER_EQUAL, D-M, 
				comment);
	}
	
	private void rule_thetaFlowPropagation(
			State out,
			HashList<CPEGValue<L,P>> values) throws IOException {
		for (int k = 0 ; k < this.orderedTerms.size(); k++) {
			CPEGTerm<L,P> nk = this.orderedTerms.getValue(k);
			final int v = values.getIndex(nk.getValue());
			if (nk.getOp().isTheta()) {
				// N_k => (TI_k_0 = TO_v + 1), where node k in value v
				this.B_implies_X1eqX2plusD(out, N(k), TI(k,0), TO(v), 1,
						"Theta flow propagation rule: theta");
				
				{// TI_k_1 = 0
					this.writeConstraint(
							out, 
							new WM(TI(k,1), 1), Operator.EQUAL, 0, 
							"Theta flow propagation rule: theta");
				}
			} else if (this.graph.isRoot(nk.getValue())) {
				// TI_k_i = 0, for all i
				for (int i = 0; i < nk.getArity(); i++) {
					this.writeConstraint(
							out, 
							new WM(TI(k,i), 1), Operator.EQUAL, 0, 
							"Theta flow propagation rule: root");
				}
			} else {
				// Nk => (TI_k_i = TO_v + 1), where node k in value v
				for (int i = 0; i < nk.getArity(); i++) {
					this.B_implies_X1eqX2plusD(out, N(k), TI(k,i), TO(v), 1,
							"Theta flow propagation rule: default");
				}
			}
		}
		
//		for (CPEGValue<L,P> Vv : this.graph.getRootValues()) {
//			int v = values.getIndex(Vv);
//			// TO_v = 0, where node k in value v
//			this.writeConstraint(
//					out, 
//					new WM(TO(v), 1), Operator.EQUAL, 0, 
//					"Theta flow propagation rule: root");
//		}
	}
	
	private void rule_rootValuesUsed(
			State out,
			HashList<CPEGValue<L,P>> values) throws IOException {
		// for root value Vr, Vr = 1
		for (CPEGValue<L,P> Vr : this.graph.getRootValues()) {
			WM wm = new WM(V(values.getIndex(Vr)), 1);
			writeConstraint(out, wm, Operator.EQUAL, 1, 
					"Root value must be used");
		}
	}
	
	private void rule_valueImpliesOneMember(
			State out,
			HashList<CPEGValue<L,P>> values) throws IOException {
		// V = N1 + N2 + ... + Nm
		for (int v = 0; v < values.size(); v++) {
			CPEGValue<L,P> Vv = values.getValue(v);
			WM wm = new WM(V(v), -1);
			for (CPEGTerm<L,P> nk : this.graph.getValueElements(Vv)) {
				wm.put(N(this.orderedTerms.getIndex(nk)), 1);
			}
			writeConstraint(out, wm, Operator.EQUAL, 0, 
					"Value implies one member");
		}
	}
	
	private void rule_nodeImpliesChildValues(
			State out,
			HashList<CPEGValue<L,P>> values) throws IOException {
		// N_k => V_ci, for all i
		StickyPredicate<CPEGTerm<L,P>> stickyP = this.graph.getStickyPredicate();
		for (int k = 0; k < this.orderedTerms.size(); k++) {
			CPEGTerm<L,P> nk = this.orderedTerms.getValue(k);
			Set<Integer> vs = new HashSet<Integer>();
			for (int i = 0; i < nk.getArity(); i++) {
				// N_k <= V_ci,
				// N_k - V_ci <= 0
				int v = values.getIndex(nk.getChild(i).getValue());
				vs.add(v);
			}
			
			for (int v : vs) {
				WM wm = new WM(N(k), 1, V(v), -1);
				writeConstraint(out, wm, Operator.LESS_EQUAL, 0, 
						"Node implies child value");
			}
			
			// now do stickyness
			for (int i = 0; i < nk.getArity(); i++) {
				if (!stickyP.isSticky(nk, i)) continue;
				Set<CPEGTerm<L,P>> allowed = new HashSet<CPEGTerm<L,P>>();
				for (CPEGTerm<L,P> child : this.graph.getValueElements(this.graph.getChildValue(nk, i))) {
					if (stickyP.allowsChild(nk, i, child))
						allowed.add(child);
				}
				
				if (allowed.size() == 0) {
					// can't use node, N_k = 0
					this.writeConstraint(
							out, 
							new WM(N(k), 1), Operator.EQUAL, 0, 
							"Can't use node: sticky but no allowable children along input " + i);
				} else {
					// node implies exactly one allowable child
					// N_k => (N_a1 + ... + N_am = 1)
					// (already can't sum to >1)
					// (1-N_k) + (N_a1 + ... + N_am) >= 1
					// -N_k + N_a1 + ... + N_am >= 0
					WM wm = new WM(N(k),-1);
					for (CPEGTerm<L,P> Na : allowed) {
						wm.put(N(this.orderedTerms.getIndex(Na)), 1);
					}
					this.writeConstraint(
							out,
							wm, Operator.GREATER_EQUAL, 0,
							"Node is sticky, can only use certain children along input " + i);
				}
			}
		}
		
	}
	
	private void makeVariables(
			State out,
			HashList<CPEGValue<L,P>> values) throws IOException {
		out.println("var N {i in 0.." + (this.orderedTerms.size()-1) + "}, binary;");
		out.println("var V {i in 0.." + (values.size()-1) + "}, binary;");
		
		if (this.hasThetas) {
			out.println("var TO {i in 0.." + (values.size()-1) + "}, integer, >=0, <=" + MAX_THETA_VALUE + ";");
			for (int n = 0; n < this.orderedTerms.size(); n++) {
				final int inputs = this.orderedTerms.getValue(n).getArity();
				out.println("var TI_" + n + " {i in 0.." + (inputs-1) + "}, integer, >=0, <=" + MAX_THETA_VALUE + ";");
			}
		} else {
			out.println("var T {i in 0.." + (values.size()-1) + "}, integer, >=0, <=" + MAX_THETA_VALUE + ";");
		}
	}
	
	private String aN() {return "a" + (this.ruleCounter++);}
	
	private void writeConstraint(
			State out, 
			WM map,
			Operator op, 
			int RHS, 
			String comment) throws IOException {
		if (comment!=null) {
			comment = escape(comment);
			out.println("/* " + comment + " */");
		}
		
		out.print("s.t. " + aN() + ": ");
		for (String var : map.keys()) {
			int weight = map.get(var);
			if (weight > 0) out.print("+" + weight + "*" + var + " ");
			else out.print(weight + "*" + var + " ");
		}
		out.print(op.getLabel());
		out.print(" ");
		out.print("" + RHS);
		out.println(";");
	}
	private void writeObjectiveFunction(
			State out,
			boolean isMin,
			WM wm) throws IOException {
		out.print(isMin ? "minimize obj: " : "maximize obj: ");
		for (String var : wm.keys()) {
			int weight = wm.get(var);
			if (weight>0) out.print("+" + weight + "*" + var + " ");
			else out.print(weight + "*" + var + " ");
		}
		out.println(";");
	}
}
