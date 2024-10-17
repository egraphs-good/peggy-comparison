#
# javac $@.java && 
java -Xmx2000m -cp .:peggy_1.0.jar:polybench:benchmark:benchmark/passing:benchmark/failing peggy.optimize.java.Main -axioms axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml -O2 $@ -tmpFolder tmp  -pb glpk -activate "inlineall:livsr:binop:constant" -glpkPath "/glpk-5.0/examples/glpsol"