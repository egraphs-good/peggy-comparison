package peggy.represent.java;

/**
 * This enum encodes the 4 different Java invocation types.
 * @author amie
 *
 */
public enum InvokeType {
   INVOKE_VIRTUAL(JavaOperator.INVOKEVIRTUAL),
   INVOKE_STATIC(JavaOperator.INVOKESTATIC),
   INVOKE_INTERFACE(JavaOperator.INVOKEINTERFACE),
   INVOKE_SPECIAL(JavaOperator.INVOKESPECIAL);
   
   private JavaOperator operator;
   private InvokeType(JavaOperator _operator) {
	   this.operator = _operator;
   }
   
   public JavaOperator getOperator() {
	   return this.operator;
   }
}
