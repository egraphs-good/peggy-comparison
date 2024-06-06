#
# javac $@.java && 
java -Xmx2000m -cp .:peggy_1.0.jar:benchmark:benchmark/peggy_example:benchmark/peggy_unoptimized peggy.optimize.java.Main -axioms axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml -O1 $@ -tmpFolder tmp  -pb glpk