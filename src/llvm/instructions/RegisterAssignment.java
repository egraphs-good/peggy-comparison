package llvm.instructions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import llvm.instructions.BasicBlock.Handle;
import llvm.values.VirtualRegister;

/**
 * This represents the assignment of virtual registers to instructions
 * within a method body. This is the only thing that defines the value
 * of a virtual register.
 */
public class RegisterAssignment {
	protected final Map<VirtualRegister,Handle> assignment;
	protected final Map<Handle,VirtualRegister> inverse;
	
	public RegisterAssignment() {
		this.assignment = new HashMap<VirtualRegister, Handle>();
		this.inverse = new HashMap<Handle,VirtualRegister>();
	}
	
	public void clear() {
		this.assignment.clear();
		this.inverse.clear();
	}
	
	public Handle remove(VirtualRegister reg) {
		if (reg == null)
			throw new NullPointerException();
		Handle handle = this.assignment.remove(reg);
		if (handle != null)
			this.inverse.remove(handle);
		return handle;
	}
	
	public VirtualRegister remove(Handle handle) {
		if (handle == null)
			throw new NullPointerException();
		VirtualRegister reg = this.inverse.remove(handle);
		if (reg != null)
			this.assignment.remove(reg);
		return reg;
	}
	
	public void set(VirtualRegister reg, Handle inst) {
		if (reg == null || inst == null)
			throw new NullPointerException();
		if (!reg.getType().equalsType(inst.getInstruction().getType()))
			throw new IllegalArgumentException("Reg and instruction have different types");
		
		if (!this.assignment.containsKey(reg) && !this.inverse.containsKey(inst)) {
			// neither set, go ahead
			this.assignment.put(reg, inst);
			this.inverse.put(inst, reg);
		} else if (this.assignment.containsKey(reg) && this.inverse.containsKey(inst)) {
			if (this.assignment.get(reg).equals(inst) && this.inverse.get(inst).equals(reg))
				return; // already set
			else
				throw new IllegalArgumentException("Inconsistent state");
		} else {
			// one or the other set
			throw new IllegalArgumentException("Inconsistent state");
		}
	}
	public Handle getHandle(VirtualRegister reg) {
		if (reg == null)
			throw new NullPointerException();
		return this.assignment.get(reg);
	}
	public VirtualRegister getRegister(Handle handle) {
		if (handle == null)
			throw new NullPointerException();
		return this.inverse.get(handle);
	}
	public boolean isAssigned(VirtualRegister reg) {
		if (reg == null)
			throw new NullPointerException();
		return this.assignment.containsKey(reg);
	}
	public boolean isAssigned(Handle handle) {
		if (handle == null)
			throw new NullPointerException();
		return this.inverse.containsKey(handle);
	}
	public Set<VirtualRegister> getRegisters() {
		return Collections.unmodifiableSet(this.assignment.keySet());
	}
	public Set<Handle> getHandles() {
		return Collections.unmodifiableSet(this.inverse.keySet());
	}
}
