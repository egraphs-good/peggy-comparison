#
# javac $@.java && 
java -Xmx2000m -cp /peggy-comparison/peggy:/peggy-comparison/peggy/peggy_1.0.jar:/peggy-comparison/java/benchmark \
    peggy.optimize.java.Main \
    -axioms /peggy-comparison/peggy/axioms/java_arithmetic_axioms.xml:/peggy-comparison/peggy/axioms/java_operator_axioms.xml:/peggy-comparison/peggy/axioms/java_operator_costs.xml:/peggy-comparison/peggy/axioms/java_util_axioms.xml \
    -O2 $@ \
    -tmpFolder tmp \
    -pb glpk \
    -activate "inlineall:livsr:binop:constant"