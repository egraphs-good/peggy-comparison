# DeadLoopDeletion
## Original
```java
public class DeadLoopDeletion {
    public int original() {
        int j = 3;
        for (int i = 0; i < 4; i++) {
            j++;
        }
        j = 2;
        return j;
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
+ Loading class file DeadLoopDeletion
+ Optimizing class DeadLoopDeletion
   - Processing method <DeadLoopDeletion: void <init>()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2 after 3 milliseconds
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
      * Optimization of method <DeadLoopDeletion: void <init>()> SUCCESSFUL
      * Optimization took 562
      * PEG2PEGTIME 437
      * PBTIME 301
      * ENGINETIME 3
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <DeadLoopDeletion: void <init>()>
   - Processing method <DeadLoopDeletion: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 2 after 0 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 5
GLPKFormulation: Number of values: 5
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <DeadLoopDeletion: int original()> SUCCESSFUL
      * Optimization took 256
      * PEG2PEGTIME 245
      * PBTIME 227
      * ENGINETIME 0
   - Done processing method <DeadLoopDeletion: int original()>
+ Done optimizing DeadLoopDeletion
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <DeadLoopDeletion: void <init>()>
+ Fixing bytecode of method <DeadLoopDeletion: int original()>
+ Writing class back to optimized/DeadLoopDeletion.class
+ Total optimization time = 1218 milliseconds
```

### Optimized
```java
22:59:18.150 INFO  jd.cli.Main - Decompiling optimized/DeadLoopDeletion.class
public class DeadLoopDeletion
{
  public int original()
  {
    return 2;
  }
}

/* Location:
 * Qualified Name:     DeadLoopDeletion
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
+ Loading class file DeadLoopDeletion
+ Optimizing class DeadLoopDeletion
   - Processing method <DeadLoopDeletion: void <init>()>
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
      * Optimization of method <DeadLoopDeletion: void <init>()> SUCCESSFUL
      * Optimization took 558
      * PEG2PEGTIME 436
      * PBTIME 301
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <DeadLoopDeletion: void <init>()>
   - Processing method <DeadLoopDeletion: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 4 after 2 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 5
GLPKFormulation: Number of values: 5
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <DeadLoopDeletion: int original()> SUCCESSFUL
      * Optimization took 260
      * PEG2PEGTIME 250
      * PBTIME 231
      * ENGINETIME 2
   - Done processing method <DeadLoopDeletion: int original()>
+ Done optimizing DeadLoopDeletion
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <DeadLoopDeletion: void <init>()>
+ Fixing bytecode of method <DeadLoopDeletion: int original()>
+ Writing class back to optimized/DeadLoopDeletion.class
+ Total optimization time = 1209 milliseconds
```

### Optimized
```java
22:59:22.059 INFO  jd.cli.Main - Decompiling optimized/DeadLoopDeletion.class
public class DeadLoopDeletion
{
  public int original()
  {
    return 2;
  }
}

/* Location:
 * Qualified Name:     DeadLoopDeletion
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
+ Loading class file DeadLoopDeletion
+ Optimizing class DeadLoopDeletion
   - Processing method <DeadLoopDeletion: void <init>()>
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
      * Optimization of method <DeadLoopDeletion: void <init>()> SUCCESSFUL
      * Optimization took 555
      * PEG2PEGTIME 433
      * PBTIME 299
      * ENGINETIME 6
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <DeadLoopDeletion: void <init>()>
   - Processing method <DeadLoopDeletion: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 5 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 5
GLPKFormulation: Number of values: 5
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <DeadLoopDeletion: int original()> SUCCESSFUL
      * Optimization took 252
      * PEG2PEGTIME 242
      * PBTIME 223
      * ENGINETIME 1
   - Done processing method <DeadLoopDeletion: int original()>
+ Done optimizing DeadLoopDeletion
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <DeadLoopDeletion: void <init>()>
+ Fixing bytecode of method <DeadLoopDeletion: int original()>
+ Writing class back to optimized/DeadLoopDeletion.class
+ Total optimization time = 1223 milliseconds
```

### Optimized
```java
22:59:25.878 INFO  jd.cli.Main - Decompiling optimized/DeadLoopDeletion.class
public class DeadLoopDeletion
{
  public int original()
  {
    return 2;
  }
}

/* Location:
 * Qualified Name:     DeadLoopDeletion
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
+ Loading class file DeadLoopDeletion
+ Optimizing class DeadLoopDeletion
   - Processing method <DeadLoopDeletion: void <init>()>
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
      * Optimization of method <DeadLoopDeletion: void <init>()> SUCCESSFUL
      * Optimization took 584
      * PEG2PEGTIME 459
      * PBTIME 326
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <DeadLoopDeletion: void <init>()>
   - Processing method <DeadLoopDeletion: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 5 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 5
GLPKFormulation: Number of values: 5
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <DeadLoopDeletion: int original()> SUCCESSFUL
      * Optimization took 267
      * PEG2PEGTIME 256
      * PBTIME 228
      * ENGINETIME 0
   - Done processing method <DeadLoopDeletion: int original()>
+ Done optimizing DeadLoopDeletion
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <DeadLoopDeletion: void <init>()>
+ Fixing bytecode of method <DeadLoopDeletion: int original()>
+ Writing class back to optimized/DeadLoopDeletion.class
+ Total optimization time = 1241 milliseconds
```

### Optimized
```java
22:59:29.673 INFO  jd.cli.Main - Decompiling optimized/DeadLoopDeletion.class
public class DeadLoopDeletion
{
  public int original()
  {
    return 2;
  }
}

/* Location:
 * Qualified Name:     DeadLoopDeletion
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
+ Loading class file DeadLoopDeletion
+ Optimizing class DeadLoopDeletion
   - Processing method <DeadLoopDeletion: void <init>()>
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
      * Optimization of method <DeadLoopDeletion: void <init>()> SUCCESSFUL
      * Optimization took 565
      * PEG2PEGTIME 442
      * PBTIME 305
      * ENGINETIME 8
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <DeadLoopDeletion: void <init>()>
   - Processing method <DeadLoopDeletion: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 5 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 5
GLPKFormulation: Number of values: 5
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <DeadLoopDeletion: int original()> SUCCESSFUL
      * Optimization took 274
      * PEG2PEGTIME 263
      * PBTIME 228
      * ENGINETIME 1
   - Done processing method <DeadLoopDeletion: int original()>
+ Done optimizing DeadLoopDeletion
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <DeadLoopDeletion: void <init>()>
+ Fixing bytecode of method <DeadLoopDeletion: int original()>
+ Writing class back to optimized/DeadLoopDeletion.class
+ Total optimization time = 1232 milliseconds
```

### Optimized
```java
22:59:33.445 INFO  jd.cli.Main - Decompiling optimized/DeadLoopDeletion.class
public class DeadLoopDeletion
{
  public int original()
  {
    return 2;
  }
}

/* Location:
 * Qualified Name:     DeadLoopDeletion
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
+ Loading class file DeadLoopDeletion
+ Optimizing class DeadLoopDeletion
   - Processing method <DeadLoopDeletion: void <init>()>
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
      * Optimization of method <DeadLoopDeletion: void <init>()> SUCCESSFUL
      * Optimization took 558
      * PEG2PEGTIME 437
      * PBTIME 300
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <DeadLoopDeletion: void <init>()>
   - Processing method <DeadLoopDeletion: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 5 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 5
GLPKFormulation: Number of values: 5
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <DeadLoopDeletion: int original()> SUCCESSFUL
      * Optimization took 253
      * PEG2PEGTIME 242
      * PBTIME 224
      * ENGINETIME 0
   - Done processing method <DeadLoopDeletion: int original()>
+ Done optimizing DeadLoopDeletion
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <DeadLoopDeletion: void <init>()>
+ Fixing bytecode of method <DeadLoopDeletion: int original()>
+ Writing class back to optimized/DeadLoopDeletion.class
+ Total optimization time = 1196 milliseconds
```

### Optimized
```java
22:59:37.223 INFO  jd.cli.Main - Decompiling optimized/DeadLoopDeletion.class
public class DeadLoopDeletion
{
  public int original()
  {
    return 2;
  }
}

/* Location:
 * Qualified Name:     DeadLoopDeletion
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
+ Loading class file DeadLoopDeletion
+ Optimizing class DeadLoopDeletion
   - Processing method <DeadLoopDeletion: void <init>()>
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
      * Optimization of method <DeadLoopDeletion: void <init>()> SUCCESSFUL
      * Optimization took 567
      * PEG2PEGTIME 441
      * PBTIME 300
      * ENGINETIME 9
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <DeadLoopDeletion: void <init>()>
   - Processing method <DeadLoopDeletion: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 5 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 5
GLPKFormulation: Number of values: 5
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <DeadLoopDeletion: int original()> SUCCESSFUL
      * Optimization took 264
      * PEG2PEGTIME 253
      * PBTIME 231
      * ENGINETIME 2
   - Done processing method <DeadLoopDeletion: int original()>
+ Done optimizing DeadLoopDeletion
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <DeadLoopDeletion: void <init>()>
+ Fixing bytecode of method <DeadLoopDeletion: int original()>
+ Writing class back to optimized/DeadLoopDeletion.class
+ Total optimization time = 1225 milliseconds
```

### Optimized
```java
22:59:41.146 INFO  jd.cli.Main - Decompiling optimized/DeadLoopDeletion.class
public class DeadLoopDeletion
{
  public int original()
  {
    return 2;
  }
}

/* Location:
 * Qualified Name:     DeadLoopDeletion
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
+ Loading class file DeadLoopDeletion
+ Optimizing class DeadLoopDeletion
   - Processing method <DeadLoopDeletion: void <init>()>
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
      * Optimization of method <DeadLoopDeletion: void <init>()> SUCCESSFUL
      * Optimization took 556
      * PEG2PEGTIME 436
      * PBTIME 299
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <DeadLoopDeletion: void <init>()>
   - Processing method <DeadLoopDeletion: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 5 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 5
GLPKFormulation: Number of values: 5
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <DeadLoopDeletion: int original()> SUCCESSFUL
      * Optimization took 265
      * PEG2PEGTIME 254
      * PBTIME 227
      * ENGINETIME 1
   - Done processing method <DeadLoopDeletion: int original()>
+ Done optimizing DeadLoopDeletion
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <DeadLoopDeletion: void <init>()>
+ Fixing bytecode of method <DeadLoopDeletion: int original()>
+ Writing class back to optimized/DeadLoopDeletion.class
+ Total optimization time = 1211 milliseconds
```

### Optimized
```java
22:59:44.940 INFO  jd.cli.Main - Decompiling optimized/DeadLoopDeletion.class
public class DeadLoopDeletion
{
  public int original()
  {
    return 2;
  }
}

/* Location:
 * Qualified Name:     DeadLoopDeletion
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
+ Loading class file DeadLoopDeletion
+ Optimizing class DeadLoopDeletion
   - Processing method <DeadLoopDeletion: void <init>()>
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
      * Optimization of method <DeadLoopDeletion: void <init>()> SUCCESSFUL
      * Optimization took 548
      * PEG2PEGTIME 428
      * PBTIME 300
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <DeadLoopDeletion: void <init>()>
   - Processing method <DeadLoopDeletion: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 5 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 5
GLPKFormulation: Number of values: 5
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <DeadLoopDeletion: int original()> SUCCESSFUL
      * Optimization took 267
      * PEG2PEGTIME 258
      * PBTIME 229
      * ENGINETIME 1
   - Done processing method <DeadLoopDeletion: int original()>
+ Done optimizing DeadLoopDeletion
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <DeadLoopDeletion: void <init>()>
+ Fixing bytecode of method <DeadLoopDeletion: int original()>
+ Writing class back to optimized/DeadLoopDeletion.class
+ Total optimization time = 1249 milliseconds
```

### Optimized
```java
22:59:48.721 INFO  jd.cli.Main - Decompiling optimized/DeadLoopDeletion.class
public class DeadLoopDeletion
{
  public int original()
  {
    return 2;
  }
}

/* Location:
 * Qualified Name:     DeadLoopDeletion
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
+ Loading class file DeadLoopDeletion
+ Optimizing class DeadLoopDeletion
   - Processing method <DeadLoopDeletion: void <init>()>
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
      * Optimization of method <DeadLoopDeletion: void <init>()> SUCCESSFUL
      * Optimization took 571
      * PEG2PEGTIME 449
      * PBTIME 312
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <DeadLoopDeletion: void <init>()>
   - Processing method <DeadLoopDeletion: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 5 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 5
GLPKFormulation: Number of values: 5
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <DeadLoopDeletion: int original()> SUCCESSFUL
      * Optimization took 260
      * PEG2PEGTIME 250
      * PBTIME 231
      * ENGINETIME 1
   - Done processing method <DeadLoopDeletion: int original()>
+ Done optimizing DeadLoopDeletion
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <DeadLoopDeletion: void <init>()>
+ Fixing bytecode of method <DeadLoopDeletion: int original()>
+ Writing class back to optimized/DeadLoopDeletion.class
+ Total optimization time = 1214 milliseconds
```

### Optimized
```java
22:59:52.463 INFO  jd.cli.Main - Decompiling optimized/DeadLoopDeletion.class
public class DeadLoopDeletion
{
  public int original()
  {
    return 2;
  }
}

/* Location:
 * Qualified Name:     DeadLoopDeletion
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
+ Loading class file DeadLoopDeletion
+ Optimizing class DeadLoopDeletion
   - Processing method <DeadLoopDeletion: void <init>()>
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
      * Optimization of method <DeadLoopDeletion: void <init>()> SUCCESSFUL
      * Optimization took 561
      * PEG2PEGTIME 438
      * PBTIME 300
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <DeadLoopDeletion: void <init>()>
   - Processing method <DeadLoopDeletion: int original()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 5 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 5
GLPKFormulation: Number of values: 5
         @ Running solver
         @ No result from ILP solver
      * Original PEG chosen as output
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <DeadLoopDeletion: int original()> SUCCESSFUL
      * Optimization took 274
      * PEG2PEGTIME 259
      * PBTIME 225
      * ENGINETIME 1
   - Done processing method <DeadLoopDeletion: int original()>
+ Done optimizing DeadLoopDeletion
+ Final results:
   - Skipped methods = 0
   - Buggy methods = 0
   - Total methods = 2
+ Fixing bytecode of method <DeadLoopDeletion: void <init>()>
+ Fixing bytecode of method <DeadLoopDeletion: int original()>
+ Writing class back to optimized/DeadLoopDeletion.class
+ Total optimization time = 1339 milliseconds
```

### Optimized
```java
22:59:56.539 INFO  jd.cli.Main - Decompiling optimized/DeadLoopDeletion.class
public class DeadLoopDeletion
{
  public int original()
  {
    return 2;
  }
}

/* Location:
 * Qualified Name:     DeadLoopDeletion
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
