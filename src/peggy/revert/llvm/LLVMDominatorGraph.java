package peggy.revert.llvm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import llvm.instructions.BasicBlock;
import llvm.instructions.BasicBlock.Handle;
import llvm.values.VirtualRegister;
import peggy.revert.DominatorGraph;
import peggy.revert.DominatorVertex;
import util.graph.AbstractGraph;

/**
 * This class implements DominatorGraph over a list of BasicBlocks.
 * It is assumed that the BasicBlocks given in the constructor are well-formed,
 * except possibly for not being in SSA form. The main usage of this class is
 * to help get the BasicBlocks into SSA form.
 * 
 * @author steppm
 */
public class LLVMDominatorGraph extends AbstractGraph<LLVMDominatorGraph,LLVMDominatorGraph.Vertex> 
implements DominatorGraph<LLVMDominatorGraph,LLVMDominatorGraph.Vertex> {
	private static final boolean DEBUG = false;
	private static void debug(String message) {
		if (DEBUG)
			System.err.println("LLVMDominatorGraph: " + message);
	}
	
	public class Vertex implements DominatorVertex<LLVMDominatorGraph,Vertex> {
		protected final BasicBlock block;
		protected Vertex(BasicBlock _block) {
			if (_block == null)
				throw new NullPointerException();
			this.block = _block;
		}
		
		public BasicBlock getBlock() {return this.block;}
		public LLVMDominatorGraph getGraph() {return LLVMDominatorGraph.this;}
		public Vertex getSelf() {return this;}
		public Collection<? extends Vertex> getChildren() {
			return Collections.unmodifiableCollection(block2succs.get(this));
		}
		public Collection<? extends Vertex> getParents() {
			return Collections.unmodifiableCollection(block2preds.get(this));
		}
		public boolean hasChildren() {return block2succs.get(this).size() > 0;}
		public boolean hasParent(Vertex v) {return block2preds.get(this).contains(v);}
		public boolean hasChild(Vertex v) {return block2succs.get(this).contains(v);}
		public boolean isLeaf() {return block2succs.get(this).size() == 0;}
		public boolean hasParents() {return block2preds.get(this).size() > 0;}
		public boolean isRoot() {return block2preds.get(this).size() == 0;}
		public int getChildCount() {return block2succs.get(this).size();}
		public int getParentCount() {return block2preds.get(this).size();}

		public Collection<? extends Vertex> getDominated() {
			Set<Vertex> dominated = new HashSet<Vertex>();
			for (Vertex v : vertices) {
				if (block2doms.get(v).contains(this))
					dominated.add(v);
			}
			return dominated;
		}
		public Collection<? extends Vertex> getDominators() {
			return block2doms.get(this);
		}
		public int getDominatedCount() {
			return getDominated().size();
		}
		public int getDominatorCount() {
			return block2doms.get(this).size();
		}
		public boolean isStart() {
			return this.equals(start);
		}
		
		public String toString() {
			StringBuilder builder = new StringBuilder(100);
			for (int i = 0; i < this.block.getNumInstructions(); i++) {
				builder.append(this.block.getInstruction(i).toString());
				builder.append("\n");
			}
			return builder.toString();
		}
		public String toString(Map<Handle,VirtualRegister> regmap) {
			StringBuilder builder = new StringBuilder(100);
			for (int i = 0; i < this.block.getNumInstructions(); i++) {
				Handle handle = this.block.getHandle(i);
				if (regmap.containsKey(handle))
					builder.append(regmap.get(handle)).append(" = ");
				builder.append(handle.getInstruction().toString());
				builder.append("\n");
			}
			return builder.toString();
		}
	}
	
	protected final List<Vertex> vertices;
	protected Vertex start;
	protected final Map<Vertex,List<Vertex>> block2preds;
	protected final Map<Vertex,List<Vertex>> block2succs;
	protected final Map<Vertex,Set<Vertex>> block2doms;
	
	public LLVMDominatorGraph(
			BasicBlock startBlock, 
			List<BasicBlock> blockList) {
		if (!blockList.contains(startBlock))
			throw new IllegalArgumentException("Start block not contained in block list");
		
		this.vertices = new ArrayList<Vertex>();
		this.block2preds = new HashMap<Vertex,List<Vertex>>();
		this.block2succs = new HashMap<Vertex,List<Vertex>>();
		this.block2doms = new HashMap<Vertex,Set<Vertex>>();
		
		Map<BasicBlock,Vertex> blockmap = 
			new HashMap<BasicBlock,Vertex>();
		// build blocks
		for (BasicBlock bb : blockList) {
			Vertex vertex = new Vertex(bb);
			this.vertices.add(vertex);
			this.block2preds.put(vertex, new ArrayList<Vertex>());
			this.block2succs.put(vertex, new ArrayList<Vertex>());
			blockmap.put(bb, vertex);
		}
		this.start = blockmap.get(startBlock);
		
		// build block2preds and block2succs
		for (BasicBlock bb : blockList) {
			Vertex vv = blockmap.get(bb);
			for (int i = 0; i < bb.getNumSuccs(); i++) {
				BasicBlock succ = bb.getSucc(i);
				Vertex succV = blockmap.get(succ);
				this.block2succs.get(vv).add(succV);
				this.block2preds.get(succV).add(vv);
			}
		}
		
		buildDominators();
		
		
		if (DEBUG) {
			debug("Dominators:");
			for (Vertex v : this.vertices) {
				debug(v.getBlock() + " --> [");
				for (Vertex dom : this.block2doms.get(v))
					debug("   " + dom.getBlock());
				debug("]");
			}
		}
	}
	
	private void buildDominators() {
		// initialize
		// D[start] = {start}
		// D[n] = all nodes, (n != start)
		for (Vertex v : this.vertices) {
			if (v.isStart()) {
				Set<Vertex> doms = new HashSet<Vertex>();
				doms.add(v);
				this.block2doms.put(v, doms);
			} else {
				Set<Vertex> doms = new HashSet<Vertex>(this.vertices);
				this.block2doms.put(v, doms);
			}
		}
		
		
		// iterate and solve equation:
		// D[n] = {n} union {x : for all pred p of n, x in D[p]}
		for (boolean progress = true; progress; ) {
			progress = false;
			for (Vertex n : this.vertices) {
				if (n.isStart())
					continue;
				Set<Vertex> olddoms = this.block2doms.get(n);
				Set<Vertex> newdoms = new HashSet<Vertex>();
				newdoms.add(n);
				
				Set<Vertex> inter = new HashSet<Vertex>();
				Iterator<? extends Vertex> parents = n.getParents().iterator();
				if (parents.hasNext())
					inter.addAll(this.block2doms.get(parents.next()));
				while (parents.hasNext()) {
					inter.retainAll(this.block2doms.get(parents.next()));
				}
				newdoms.addAll(inter);
				
				if (!olddoms.equals(newdoms)) {
					progress = true;
					this.block2doms.put(n, newdoms);
				}
			}
		}
	}
	
	public Vertex getStart() {return this.start;}
	public LLVMDominatorGraph getSelf() {return this;}
	public Collection<? extends Vertex> getVertices() {return Collections.unmodifiableList(this.vertices);}
	
	/**
	 * Special form of toString that can show the register assignments.
	 */
	public String toString(Map<Handle,VirtualRegister> regmap) {
		StringBuilder string = new StringBuilder("digraph {\nordering=out;\n");
		for (Vertex vertex : getVertices()) {
			string.append(vertex.hashCode());
			string.append(" [label=\"");
			string.append("Block ").append(vertex.getBlock().toString()).append("\\n");
			string.append(vertex.toString(regmap).replace("\n", "\\n"));
			string.append("\"];\n");
		}
		for (Vertex vertex : getVertices()) {
			for (Object child : vertex.getChildren()) {
				string.append(vertex.hashCode());
				string.append(" -> ");
				string.append(child.hashCode());
				string.append(";\n");
			}
		}
		string.append("}\n");
		return string.toString();
	}
}
