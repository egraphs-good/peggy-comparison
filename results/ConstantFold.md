# ConstantFold
## Original
```java
public class ConstantFold {
    public int original() {
        int j = 1 + 1;
        int k = j * 3;
        return k - 10;
    }
}
```
## Run 

- axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml
- optimization_level: O2
- tmp_folder: tmp
- pb: glpk
- eto: 2

### Peggy output
```
+ Successfully added axiom file: axioms/java_operator_axioms.xml
+ Successfully added axiom file: axioms/java_util_axioms.xml
+ Successfully added axiom file: axioms/java_operator_costs.xml
+ Successfully added axiom file: axioms/java_arithmetic_axioms.xml
+ Loading class file ConstantFold
+ Optimizing class ConstantFold
   - Processing method <ConstantFold: void <init>()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2 after 4 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 10
GLPKFormulation: Number of values: 10
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <ConstantFold: void <init>()> SUCCESSFUL
      * Optimization took 563
      * PEG2PEGTIME 433
      * PBTIME 303
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <ConstantFold: void <init>()>
   - Processing method <ConstantFold: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2 after 0 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 9
GLPKFormulation: Number of values: 9
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <ConstantFold: int original()> SUCCESSFUL
      * Optimization took 274
      * PEG2PEGTIME 265
      * PBTIME 244
      * ENGINETIME 0
      * Optimization ratio 24/24 = 1.0
      * PEG-based Optimization ratio 24/24 = 1.0
   - Done processing method <ConstantFold: int original()>
+ Done optimizing ConstantFold
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <ConstantFold: void <init>()>
+ Fixing bytecode of method <ConstantFold: int original()>
+ Writing class back to optimized/ConstantFold.class
+ Total optimization time = 1271 milliseconds
```

### Optimized
```java
22:51:18.589 INFO  jd.cli.Main - Decompiling optimized/ConstantFold.class
public class ConstantFold
{
  public int original()
  {
    return -4;
  }
}

/* Location:
 * Qualified Name:     ConstantFold
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
## Run 

- axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml
- optimization_level: O2
- tmp_folder: tmp
- pb: glpk
- eto: 4

### Peggy output
```
+ Successfully added axiom file: axioms/java_operator_axioms.xml
+ Successfully added axiom file: axioms/java_util_axioms.xml
+ Successfully added axiom file: axioms/java_operator_costs.xml
+ Successfully added axiom file: axioms/java_arithmetic_axioms.xml
+ Loading class file ConstantFold
+ Optimizing class ConstantFold
   - Processing method <ConstantFold: void <init>()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 4 after 4 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 10
GLPKFormulation: Number of values: 10
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <ConstantFold: void <init>()> SUCCESSFUL
      * Optimization took 564
      * PEG2PEGTIME 439
      * PBTIME 299
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <ConstantFold: void <init>()>
   - Processing method <ConstantFold: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 4 after 0 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 9
GLPKFormulation: Number of values: 9
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <ConstantFold: int original()> SUCCESSFUL
      * Optimization took 269
      * PEG2PEGTIME 261
      * PBTIME 247
      * ENGINETIME 0
      * Optimization ratio 24/24 = 1.0
      * PEG-based Optimization ratio 24/24 = 1.0
   - Done processing method <ConstantFold: int original()>
+ Done optimizing ConstantFold
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <ConstantFold: void <init>()>
+ Fixing bytecode of method <ConstantFold: int original()>
+ Writing class back to optimized/ConstantFold.class
+ Total optimization time = 1218 milliseconds
```

### Optimized
```java
22:51:22.381 INFO  jd.cli.Main - Decompiling optimized/ConstantFold.class
public class ConstantFold
{
  public int original()
  {
    return -4;
  }
}

/* Location:
 * Qualified Name:     ConstantFold
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
## Run 

- axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml
- optimization_level: O2
- tmp_folder: tmp
- pb: glpk
- eto: 8

### Peggy output
```
+ Successfully added axiom file: axioms/java_operator_axioms.xml
+ Successfully added axiom file: axioms/java_util_axioms.xml
+ Successfully added axiom file: axioms/java_operator_costs.xml
+ Successfully added axiom file: axioms/java_arithmetic_axioms.xml
+ Loading class file ConstantFold
+ Optimizing class ConstantFold
   - Processing method <ConstantFold: void <init>()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 8 after 6 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 10
GLPKFormulation: Number of values: 10
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <ConstantFold: void <init>()> SUCCESSFUL
      * Optimization took 558
      * PEG2PEGTIME 436
      * PBTIME 300
      * ENGINETIME 6
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <ConstantFold: void <init>()>
   - Processing method <ConstantFold: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 8 after 15 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 9
GLPKFormulation: Number of values: 9
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <ConstantFold: int original()> SUCCESSFUL
      * Optimization took 281
      * PEG2PEGTIME 273
      * PBTIME 243
      * ENGINETIME 15
      * Optimization ratio 4/24 = 0.16666666666666666
      * PEG-based Optimization ratio 4/24 = 0.16666666666666666
   - Done processing method <ConstantFold: int original()>
+ Done optimizing ConstantFold
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <ConstantFold: void <init>()>
+ Fixing bytecode of method <ConstantFold: int original()>
+ Writing class back to optimized/ConstantFold.class
+ Total optimization time = 1215 milliseconds
```

### Optimized
```java
22:51:26.151 INFO  jd.cli.Main - Decompiling optimized/ConstantFold.class
public class ConstantFold
{
  public int original()
  {
    return -4;
  }
}

/* Location:
 * Qualified Name:     ConstantFold
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
## Run 

- axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml
- optimization_level: O2
- tmp_folder: tmp
- pb: glpk
- eto: 16

### Peggy output
```
+ Successfully added axiom file: axioms/java_operator_axioms.xml
+ Successfully added axiom file: axioms/java_util_axioms.xml
+ Successfully added axiom file: axioms/java_operator_costs.xml
+ Successfully added axiom file: axioms/java_arithmetic_axioms.xml
+ Loading class file ConstantFold
+ Optimizing class ConstantFold
   - Processing method <ConstantFold: void <init>()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 10 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 10
GLPKFormulation: Number of values: 10
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <ConstantFold: void <init>()> SUCCESSFUL
      * Optimization took 564
      * PEG2PEGTIME 441
      * PBTIME 298
      * ENGINETIME 8
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <ConstantFold: void <init>()>
   - Processing method <ConstantFold: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 12 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 9
GLPKFormulation: Number of values: 9
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <ConstantFold: int original()> SUCCESSFUL
      * Optimization took 277
      * PEG2PEGTIME 269
      * PBTIME 236
      * ENGINETIME 17
   - Done processing method <ConstantFold: int original()>
+ Done optimizing ConstantFold
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <ConstantFold: void <init>()>
+ Fixing bytecode of method <ConstantFold: int original()>
+ Writing class back to optimized/ConstantFold.class
+ Total optimization time = 1257 milliseconds
```

### Optimized
```java
22:51:30.045 INFO  jd.cli.Main - Decompiling optimized/ConstantFold.class
public class ConstantFold
{
  public int original()
  {
    return -4;
  }
}

/* Location:
 * Qualified Name:     ConstantFold
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
## Run 

- axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml
- optimization_level: O2
- tmp_folder: tmp
- pb: glpk
- eto: 32

### Peggy output
```
+ Successfully added axiom file: axioms/java_operator_axioms.xml
+ Successfully added axiom file: axioms/java_util_axioms.xml
+ Successfully added axiom file: axioms/java_operator_costs.xml
+ Successfully added axiom file: axioms/java_arithmetic_axioms.xml
+ Loading class file ConstantFold
+ Optimizing class ConstantFold
   - Processing method <ConstantFold: void <init>()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 10 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 10
GLPKFormulation: Number of values: 10
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <ConstantFold: void <init>()> SUCCESSFUL
      * Optimization took 562
      * PEG2PEGTIME 441
      * PBTIME 300
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <ConstantFold: void <init>()>
   - Processing method <ConstantFold: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 12 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 9
GLPKFormulation: Number of values: 9
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <ConstantFold: int original()> SUCCESSFUL
      * Optimization took 272
      * PEG2PEGTIME 261
      * PBTIME 231
      * ENGINETIME 16
   - Done processing method <ConstantFold: int original()>
+ Done optimizing ConstantFold
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <ConstantFold: void <init>()>
+ Fixing bytecode of method <ConstantFold: int original()>
+ Writing class back to optimized/ConstantFold.class
+ Total optimization time = 1220 milliseconds
```

### Optimized
```java
22:51:33.837 INFO  jd.cli.Main - Decompiling optimized/ConstantFold.class
public class ConstantFold
{
  public int original()
  {
    return -4;
  }
}

/* Location:
 * Qualified Name:     ConstantFold
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
## Run 

- axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml
- optimization_level: O2
- tmp_folder: tmp
- pb: glpk
- eto: 64

### Peggy output
```
+ Successfully added axiom file: axioms/java_operator_axioms.xml
+ Successfully added axiom file: axioms/java_util_axioms.xml
+ Successfully added axiom file: axioms/java_operator_costs.xml
+ Successfully added axiom file: axioms/java_arithmetic_axioms.xml
+ Loading class file ConstantFold
+ Optimizing class ConstantFold
   - Processing method <ConstantFold: void <init>()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 10 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 10
GLPKFormulation: Number of values: 10
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <ConstantFold: void <init>()> SUCCESSFUL
      * Optimization took 580
      * PEG2PEGTIME 437
      * PBTIME 304
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <ConstantFold: void <init>()>
   - Processing method <ConstantFold: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 12 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 9
GLPKFormulation: Number of values: 9
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <ConstantFold: int original()> SUCCESSFUL
      * Optimization took 288
      * PEG2PEGTIME 277
      * PBTIME 238
      * ENGINETIME 17
   - Done processing method <ConstantFold: int original()>
+ Done optimizing ConstantFold
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <ConstantFold: void <init>()>
+ Fixing bytecode of method <ConstantFold: int original()>
+ Writing class back to optimized/ConstantFold.class
+ Total optimization time = 1265 milliseconds
```

### Optimized
```java
22:51:37.693 INFO  jd.cli.Main - Decompiling optimized/ConstantFold.class
public class ConstantFold
{
  public int original()
  {
    return -4;
  }
}

/* Location:
 * Qualified Name:     ConstantFold
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
## Run 

- axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml
- optimization_level: O2
- tmp_folder: tmp
- pb: glpk
- eto: 128

### Peggy output
```
+ Successfully added axiom file: axioms/java_operator_axioms.xml
+ Successfully added axiom file: axioms/java_util_axioms.xml
+ Successfully added axiom file: axioms/java_operator_costs.xml
+ Successfully added axiom file: axioms/java_arithmetic_axioms.xml
+ Loading class file ConstantFold
+ Optimizing class ConstantFold
   - Processing method <ConstantFold: void <init>()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 10 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 10
GLPKFormulation: Number of values: 10
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <ConstantFold: void <init>()> SUCCESSFUL
      * Optimization took 556
      * PEG2PEGTIME 435
      * PBTIME 298
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <ConstantFold: void <init>()>
   - Processing method <ConstantFold: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 12 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 9
GLPKFormulation: Number of values: 9
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <ConstantFold: int original()> SUCCESSFUL
      * Optimization took 273
      * PEG2PEGTIME 263
      * PBTIME 231
      * ENGINETIME 17
   - Done processing method <ConstantFold: int original()>
+ Done optimizing ConstantFold
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <ConstantFold: void <init>()>
+ Fixing bytecode of method <ConstantFold: int original()>
+ Writing class back to optimized/ConstantFold.class
+ Total optimization time = 1204 milliseconds
```

### Optimized
```java
22:51:41.496 INFO  jd.cli.Main - Decompiling optimized/ConstantFold.class
public class ConstantFold
{
  public int original()
  {
    return -4;
  }
}

/* Location:
 * Qualified Name:     ConstantFold
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
## Run 

- axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml
- optimization_level: O2
- tmp_folder: tmp
- pb: glpk
- eto: 256

### Peggy output
```
+ Successfully added axiom file: axioms/java_operator_axioms.xml
+ Successfully added axiom file: axioms/java_util_axioms.xml
+ Successfully added axiom file: axioms/java_operator_costs.xml
+ Successfully added axiom file: axioms/java_arithmetic_axioms.xml
+ Loading class file ConstantFold
+ Optimizing class ConstantFold
   - Processing method <ConstantFold: void <init>()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 10 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 10
GLPKFormulation: Number of values: 10
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <ConstantFold: void <init>()> SUCCESSFUL
      * Optimization took 555
      * PEG2PEGTIME 436
      * PBTIME 299
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <ConstantFold: void <init>()>
   - Processing method <ConstantFold: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 12 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 9
GLPKFormulation: Number of values: 9
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <ConstantFold: int original()> SUCCESSFUL
      * Optimization took 277
      * PEG2PEGTIME 267
      * PBTIME 235
      * ENGINETIME 18
   - Done processing method <ConstantFold: int original()>
+ Done optimizing ConstantFold
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <ConstantFold: void <init>()>
+ Fixing bytecode of method <ConstantFold: int original()>
+ Writing class back to optimized/ConstantFold.class
+ Total optimization time = 1214 milliseconds
```

### Optimized
```java
22:51:45.257 INFO  jd.cli.Main - Decompiling optimized/ConstantFold.class
public class ConstantFold
{
  public int original()
  {
    return -4;
  }
}

/* Location:
 * Qualified Name:     ConstantFold
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
## Run 

- axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml
- optimization_level: O2
- tmp_folder: tmp
- pb: glpk
- eto: 512

### Peggy output
```
+ Successfully added axiom file: axioms/java_operator_axioms.xml
+ Successfully added axiom file: axioms/java_util_axioms.xml
+ Successfully added axiom file: axioms/java_operator_costs.xml
+ Successfully added axiom file: axioms/java_arithmetic_axioms.xml
+ Loading class file ConstantFold
+ Optimizing class ConstantFold
   - Processing method <ConstantFold: void <init>()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 10 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 10
GLPKFormulation: Number of values: 10
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <ConstantFold: void <init>()> SUCCESSFUL
      * Optimization took 567
      * PEG2PEGTIME 447
      * PBTIME 300
      * ENGINETIME 8
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <ConstantFold: void <init>()>
   - Processing method <ConstantFold: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 12 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 9
GLPKFormulation: Number of values: 9
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <ConstantFold: int original()> SUCCESSFUL
      * Optimization took 275
      * PEG2PEGTIME 265
      * PBTIME 234
      * ENGINETIME 15
   - Done processing method <ConstantFold: int original()>
+ Done optimizing ConstantFold
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <ConstantFold: void <init>()>
+ Fixing bytecode of method <ConstantFold: int original()>
+ Writing class back to optimized/ConstantFold.class
+ Total optimization time = 1223 milliseconds
```

### Optimized
```java
22:51:49.052 INFO  jd.cli.Main - Decompiling optimized/ConstantFold.class
public class ConstantFold
{
  public int original()
  {
    return -4;
  }
}

/* Location:
 * Qualified Name:     ConstantFold
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
## Run 

- axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml
- optimization_level: O2
- tmp_folder: tmp
- pb: glpk
- eto: 1024

### Peggy output
```
+ Successfully added axiom file: axioms/java_operator_axioms.xml
+ Successfully added axiom file: axioms/java_util_axioms.xml
+ Successfully added axiom file: axioms/java_operator_costs.xml
+ Successfully added axiom file: axioms/java_arithmetic_axioms.xml
+ Loading class file ConstantFold
+ Optimizing class ConstantFold
   - Processing method <ConstantFold: void <init>()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 10 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 10
GLPKFormulation: Number of values: 10
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <ConstantFold: void <init>()> SUCCESSFUL
      * Optimization took 558
      * PEG2PEGTIME 438
      * PBTIME 301
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <ConstantFold: void <init>()>
   - Processing method <ConstantFold: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 12 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 9
GLPKFormulation: Number of values: 9
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <ConstantFold: int original()> SUCCESSFUL
      * Optimization took 285
      * PEG2PEGTIME 275
      * PBTIME 237
      * ENGINETIME 18
   - Done processing method <ConstantFold: int original()>
+ Done optimizing ConstantFold
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <ConstantFold: void <init>()>
+ Fixing bytecode of method <ConstantFold: int original()>
+ Writing class back to optimized/ConstantFold.class
+ Total optimization time = 1228 milliseconds
```

### Optimized
```java
22:51:52.848 INFO  jd.cli.Main - Decompiling optimized/ConstantFold.class
public class ConstantFold
{
  public int original()
  {
    return -4;
  }
}

/* Location:
 * Qualified Name:     ConstantFold
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
## Run 

- axioms: axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml
- optimization_level: O2
- tmp_folder: tmp
- pb: glpk
- eto: 2048

### Peggy output
```
+ Successfully added axiom file: axioms/java_operator_axioms.xml
+ Successfully added axiom file: axioms/java_util_axioms.xml
+ Successfully added axiom file: axioms/java_operator_costs.xml
+ Successfully added axiom file: axioms/java_arithmetic_axioms.xml
+ Loading class file ConstantFold
+ Optimizing class ConstantFold
   - Processing method <ConstantFold: void <init>()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 10 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 10
GLPKFormulation: Number of values: 10
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <ConstantFold: void <init>()> SUCCESSFUL
      * Optimization took 559
      * PEG2PEGTIME 438
      * PBTIME 303
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <ConstantFold: void <init>()>
   - Processing method <ConstantFold: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 12 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 9
GLPKFormulation: Number of values: 9
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <ConstantFold: int original()> SUCCESSFUL
      * Optimization took 274
      * PEG2PEGTIME 264
      * PBTIME 231
      * ENGINETIME 17
   - Done processing method <ConstantFold: int original()>
+ Done optimizing ConstantFold
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <ConstantFold: void <init>()>
+ Fixing bytecode of method <ConstantFold: int original()>
+ Writing class back to optimized/ConstantFold.class
+ Total optimization time = 1223 milliseconds
```

### Optimized
```java
22:51:56.648 INFO  jd.cli.Main - Decompiling optimized/ConstantFold.class
public class ConstantFold
{
  public int original()
  {
    return -4;
  }
}

/* Location:
 * Qualified Name:     ConstantFold
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
