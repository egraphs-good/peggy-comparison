package peggy.represent.java;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import peggy.input.RuleParsingException;
import soot.ArrayType;
import soot.Body;
import soot.BooleanType;
import soot.ByteType;
import soot.CharType;
import soot.DoubleType;
import soot.FloatType;
import soot.IntType;
import soot.LongType;
import soot.PatchingChain;
import soot.PrimType;
import soot.RefLikeType;
import soot.RefType;
import soot.Scene;
import soot.ShortType;
import soot.SootClass;
import soot.SootFieldRef;
import soot.SootMethod;
import soot.SootResolver;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.VoidType;
import soot.jimple.DoubleConstant;
import soot.jimple.FloatConstant;
import soot.jimple.IntConstant;
import soot.jimple.JasminClass;
import soot.jimple.LongConstant;
import soot.jimple.NullConstant;
import soot.jimple.internal.JGotoStmt;
import soot.jimple.internal.JIfStmt;
import soot.jimple.internal.JLookupSwitchStmt;
import soot.jimple.internal.JTableSwitchStmt;
import soot.jimple.internal.JThrowStmt;
import soot.jimple.internal.JimpleLocal;
import soot.options.Options;
import soot.tagkit.CodeAttribute;
import soot.tagkit.JimpleLineNumberTag;
import soot.tagkit.LineNumberTag;
import soot.tagkit.Tag;
import soot.util.Chain;
import soot.util.JasminOutputStream;

/**
 * This is a utility class that has several convenient methods
 * when dealing with Soot classes.
 */
public abstract class SootUtils {
	/**
	 * If 'in' is a branch statement that points to 'oldTarget',
	 * then this method will make it point to 'newTarget' instead.
	 * Applies to JGotoStmt, JIfStmt, J{Lookup,Table}SwitchStmt.
	 */
	public static void redirectBranch(Unit in, Unit oldTarget, Unit newTarget) {
		if (in instanceof JGotoStmt) {
			JGotoStmt gotostmt = (JGotoStmt)in;
			if (gotostmt.getTarget() == oldTarget) {
				gotostmt.setTarget(newTarget);
			}
		} else if (in instanceof JIfStmt) {
			JIfStmt ifstmt = (JIfStmt)in;
			if (ifstmt.getTarget() == oldTarget) {
				ifstmt.setTarget(newTarget);
			}
		} else if (in instanceof JTableSwitchStmt) {
			JTableSwitchStmt stmt = (JTableSwitchStmt)in;
			if (stmt.getDefaultTarget() == oldTarget) {
				stmt.setDefaultTarget(newTarget);
			}
			List<Unit> targets = stmt.getTargets();
			for (int i = 0; i < targets.size(); i++) {
				if (targets.get(i) == oldTarget)
					stmt.setTarget(i, newTarget);
			}
		} else if (in instanceof JLookupSwitchStmt) {
			JLookupSwitchStmt stmt = (JLookupSwitchStmt)in;
			if (stmt.getDefaultTarget() == oldTarget) {
				stmt.setDefaultTarget(newTarget);
			}
			List<Unit> targets = stmt.getTargets();
			for (int i = 0; i < targets.size(); i++) {
				if (targets.get(i) == oldTarget)
					stmt.setTarget(i, newTarget);
			}
		}
	}
	
	
	

	/**
	 * For the given set of Units, this will return a map
	 * from each instruction to the set of other instructions
	 * that have it as a branch target.
	 */
	public static Map<Unit,Set<Unit>> buildTargetterMap(Body body) {
		return buildTargetterMap(body.getUnits().iterator());
	}
	public static Map<Unit,Set<Unit>> buildTargetterMap(PatchingChain units) {
		return buildTargetterMap(units.iterator());
	}
	public static Map<Unit,Set<Unit>> buildTargetterMap(Iterator<? extends Unit> unititer) {
		Map<Unit,Set<Unit>> result = new HashMap<Unit,Set<Unit>>();
		
		while (unititer.hasNext()) {
			Unit current = unititer.next();
			if (current instanceof JGotoStmt) {
				Unit target = ((JGotoStmt)current).getTarget();
				Set<Unit> parents = result.get(target);
				if (parents == null) {
					parents = new HashSet<Unit>();
					result.put(target, parents);
				}
				parents.add(current);
			} else if (current instanceof JIfStmt) {
				Unit target = ((JIfStmt)current).getTarget();
				Set<Unit> parents = result.get(target);
				if (parents == null) {
					parents = new HashSet<Unit>();
					result.put(target, parents);
				}
				parents.add(current);
			}
		}
		
		return result;
	}
	

	
	/** Removes some of the annotations or "Tags" on the code.
	 *  This is an important step because some annotations can have
	 *  references to units that may have been removed. The tags
	 *  themselves are worthless. 
	 */
	public static void removeTags(Body body){
		boolean progress = true;
		while(progress){
			progress = false;
			
			for (Iterator iter = body.getTags().iterator(); iter.hasNext(); ){
				Tag tag = (Tag)iter.next();
				boolean shouldRemove = false;
				shouldRemove |= tag instanceof LineNumberTag; 
				shouldRemove |= tag instanceof JimpleLineNumberTag;
				shouldRemove |= tag instanceof CodeAttribute;
				
				if (shouldRemove){
					body.getTags().remove(tag);
					progress = true;
					break;
				}
			}
		}
	}
	


	/** This converts a soot Type object to its internal Java string form.
	 */
	public static String typeToTypeName(Type type) {
		StringBuffer result = new StringBuffer(100);
		boolean wasArray = false;
		while (type instanceof soot.ArrayType) {
			wasArray = true;
			result.append('[');
			type = ((soot.ArrayType)type).getElementType();
		}
		
		if (type instanceof RefType) {
			String classname = ((RefType)type).getClassName();
			if (wasArray) {
				result.append('L');
				result.append(classname.replace('.', '/'));
				result.append(';');
			} else {
				result.append(classname);
			}
		} else if (type instanceof soot.ByteType) {
			result.append('B');
		} else if (type instanceof soot.CharType) {
			result.append('C');
		} else if (type instanceof soot.DoubleType) {
			result.append('D');
		} else if (type instanceof soot.FloatType) {
			result.append('F');
		} else if (type instanceof soot.IntType) {
			result.append('I');
		} else if (type instanceof soot.LongType) {
			result.append('J');
		} else if (type instanceof soot.ShortType) {
			result.append('S');
		} else if (type instanceof soot.BooleanType) {
			result.append('Z');
		} else {
			throw new RuntimeException("Invalid type: " + type);
		}
		return result.toString();
	}

	
	
	/**
	 * Converts a string representing a Java type in internal form
	 * into the corresponding Soot Type object.
	 */
	public static Type typeNameToType(String name) {
		if (name.startsWith("[")) {
			// array type, signature style
			int dims = 0;
			int index = 0;
			while (index < name.length() && name.charAt(index) == '[') {
				index++;
				dims++;
			}
			
			Type type;
			switch (name.charAt(index)) {
			case 'B': type = ByteType.v(); break;
			case 'C': type = CharType.v(); break;
			case 'D': type = DoubleType.v(); break;
			case 'F': type = FloatType.v(); break;
			case 'I': type = IntType.v(); break;
			case 'J': type = LongType.v(); break;
			case 'S': type = ShortType.v(); break;
			case 'Z': type = BooleanType.v(); break;
				
			case 'L': {
				String classname = name.substring(index + 1, name.length() - 1);
				classname = classname.replace('/', '.');
				type = RefType.v(classname);
				break;
			}
			default: throw new RuntimeException("Bad element type: " + name);
			}
			type = ArrayType.v(type, dims);
			return type;
		} else {
			// plain object name
			return RefType.v(name);
		}
	}
	

	
	/**
	 * Parses a single type from an input string.
	 * @param typeString
	 * @return the Soot Type that corresponds to the input string
	 */
	public static Type parseType(String typeString) {
		int openBrack = typeString.indexOf('[');
		int dims = 0;
		
		if (openBrack >= 0) {
			// has array dimensions
			String arrayDimensions = typeString.substring(openBrack);
			if (!arrayDimensions.matches("^(\\[\\])+$"))
				throw new RuleParsingException("Invalid type string: " + typeString);
			dims = arrayDimensions.length()/2;
			typeString = typeString.substring(0, openBrack);
		}
		
		Type elementType = null;
		if (typeString.equals("boolean")) elementType = BooleanType.v();
		else if (typeString.equals("byte")) elementType = ByteType.v();
		else if (typeString.equals("char")) elementType = CharType.v();
		else if (typeString.equals("double")) elementType = DoubleType.v();
		else if (typeString.equals("float")) elementType = FloatType.v();
		else if (typeString.equals("int")) elementType = IntType.v();
		else if (typeString.equals("long")) elementType = LongType.v();
		else if (typeString.equals("short")) elementType = ShortType.v();
		else if (typeString.equals("void")) elementType = VoidType.v();
		else {
			try {
				elementType = RefType.v(typeString);
			} catch (Throwable t) {
				throw new RuleParsingException("Cannot parse as RefType: " + typeString);
			}
		}
		
		if (dims > 0)
			return ArrayType.v(elementType, dims);
		else
			return elementType;
	}
	
	
	
	/**
	 * Parses a string that is the inside of a method signature's parameter list.
	 * For instance, one valid input to this method is "int,int,java.lang.String[]".
	 * The returned result is a List of the corresponding Soot Types.
	 * @param paramString
	 * @return a List of the corresponding Soot Types
	 */
	public static List<Type> parseParameterTypes(String paramString) {
		String[] tokens = paramString.split(",");
		List<Type> result = new ArrayList<Type>(tokens.length);
		for (String token : tokens) {
			token = token.trim();
			if (token.equals(""))
				continue;
			result.add(SootUtils.parseType(token));
		}
		return result;
	}
	
	
	
	/**
	 * Resolves the given SootFieldRef so that it will name the actual parent class of the field.
	 */
	public static SootFieldRef normalizeFieldRef(SootFieldRef fieldref) {
		SootClass clazz = fieldref.declaringClass();
		SootClass result = searchForField(clazz, fieldref);
		if (result == null)
			throw new IllegalArgumentException("Cannot resolve field: " + fieldref);
		SootFieldRef returnvalue = Scene.v().makeFieldRef(result, fieldref.name(), fieldref.type(), fieldref.isStatic());
		
		return returnvalue;
	}
	private static SootClass searchForField(SootClass clazz, SootFieldRef fieldref) {
		if (clazz == null || clazz.getName().equals("java.lang.Object"))
			return null;
		if (clazz.declaresField(fieldref.name(), fieldref.type())) {
			return clazz;
		} else {
			SootClass superclass = clazz.getSuperclass();
			SootClass result = searchForField(superclass, fieldref);
			if (result != null)
				return result;
			for (Iterator iter=clazz.getInterfaces().iterator(); iter.hasNext(); ) {
				SootClass superinterface = (SootClass) iter.next();
				result = searchForField(superinterface, fieldref);
				if (result != null)
					return result;
			}
			return null;
		}
	}
	
	
	/**
	 * JimpleLocals don't override equals, so they will use 
	 * reference equality.
	 * It saddens me that this method is necessary.
	 * Shame on you, Soot. Shame on you.
	 * 
	 *  @return true iff (the two locals are both null) OR (are both not null
	 *  	AND have same names AND (both have null types OR they have equal types))
	 */
	public static boolean jimpleLocalsEqual(JimpleLocal lhs, JimpleLocal rhs) {
		if (lhs == null && rhs == null)
			return true;
		else if (lhs == null || rhs == null) 
			return false;
		
		if (!lhs.getName().equals(rhs.getName()))
			return false;
		if (lhs.getType() == null && rhs.getType() == null)
			return true;
		else if (lhs.getType() == null || rhs.getType() == null)
			return false;
		else
			return lhs.getType().equals(rhs.getType());
	}
	
	
	public static void printUnits(SootMethod method, PrintStream out) {
		printUnits(method.retrieveActiveBody(), out);
	}
	public static void printUnits(Body body, PrintStream out) {
		printUnits(body.getUnits(), out);
	}
	public static void printUnits(PatchingChain units, PrintStream out) {
		for (Unit unit : (Collection<Unit>)units) {
			out.println(unit);
		}
	}
	
	
	public static void printLocals(Chain locals, PrintStream out) {
		for (JimpleLocal local : (Collection<JimpleLocal>)locals) {
			out.println(local.getName() + ": " + local.getType());
		}
	}
	
	
	/**
	 * Returns true iff the given method has a body and has any exception
	 * handlers or throw instructions.
	 */
	public static boolean hasExceptions(SootMethod method) {
		if (!method.isConcrete())
			return false;

		SootResolver.v().resolveClass(method.getDeclaringClass().getName(), SootClass.SIGNATURES);
		SootResolver.v().resolveClass(method.getDeclaringClass().getName(), SootClass.BODIES);
		
		Body body = method.retrieveActiveBody();
		if (body.getTraps().size() > 0)
			return true;
		for (Iterator<Unit> it = body.getUnits().iterator(); it.hasNext(); ) {
			Unit unit = it.next();
			if (unit instanceof JThrowStmt)
				return true;
		}
		return false;
	}
	
	
	/**
	 * Causes Soot to parse and rewrite a classfile.
	 * This is useful to establish our baseline, cuz Soot
	 * writes out shitty code.
	 */
	public static void readAndWrite(String classname, File outputFolder) throws Exception {
		SootClass clazz = Scene.v().loadClassAndSupport(classname);
		Scene.v().loadBasicClasses();

		for (SootMethod method : (List<SootMethod>)clazz.getMethods()) {
			if (method.isConcrete())
				method.retrieveActiveBody();
		}
		
		String pathToClassFile = clazz.getJavaPackageName().replace('.','/');
		File myOutputFolder = new File(outputFolder, pathToClassFile);
		File outputFile = new File(myOutputFolder, clazz.getShortName() + ".class");
		outputFile.getParentFile().mkdirs();
		Options.v().set_output_dir(outputFolder.getAbsolutePath());
		JasminClass jasmin = new JasminClass(clazz);
		FileOutputStream fout = new FileOutputStream(outputFile);
		PrintWriter writer = new PrintWriter(new JasminOutputStream(fout));
		jasmin.print(writer);
		writer.flush();
		fout.close();
	}

	/**
	 * Returns a null constant for the given type.
	 * For primitive types, this is the constant equivalent to 0.
	 * For reference types, this is null.
	 */
	public static Value getNullByType(Type type) {
		if (type instanceof PrimType) {
			if (type instanceof IntType ||
				type instanceof ShortType ||
				type instanceof ByteType ||
				type instanceof BooleanType ||
				type instanceof CharType) {
				return IntConstant.v(0);
			} 
			else if (type instanceof FloatType) {
				return FloatConstant.v(0.0f);
			}
			else if (type instanceof DoubleType) {
				return DoubleConstant.v(0.0);
			}
			else if (type instanceof LongType) {
				return LongConstant.v(0L);
			}
			else {
				throw new RuntimeException("Unknown primtype: " + type);
			}
		} else if (type instanceof RefLikeType) {
			return NullConstant.v();
		} else {
			throw new IllegalArgumentException("This type has no null value: " + type);
		}
	}
	
	
	
	public static void main(String args[]) throws Throwable {
		if (args.length < 2) {
			System.err.println("USAGE: SootUtils <classname> <outputFolder>");
			System.exit(1);
		}
		
		File outputFolder = new File(args[1]);
		readAndWrite(args[0], outputFolder);
	}
}
