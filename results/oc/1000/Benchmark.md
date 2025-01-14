# Benchmark
## Original
```java
public class Benchmark {

    public static int branchHoistingOriginal(int n) {
        int y = 0;
        int x = 0;
        while (y < 500) {
            y++;
            if (n == 0) {
                x = y * 2;
            } else {
                x = y * 3;
            }
        }
        return x;
    }

    public static int branchHoistingExpected(int n) {
        int y = 0;
        int x = 0;
        while (y < 500)
            y++;

        if (n == 0) {
            x = y * 2;
        } else {
            x = y * 3;
        }

        return x;
    }

    public static int conditionalConstantFoldingOriginal(int x) {
        if (x == 5) {
            return 4 * x;
        } else if (x == 4) {
            return 5 * x;
        } else {
            return 20;
        }
    }

    public static int conditionalConstantFoldingExpected() {
        return 20;
    }

    public static int constantFoldOriginal() {
        int j = 1 + 1;
        int k = j * 3;
        return k - 10;
    }

    public static int deadLoopDeletionOriginal() {
        int j = 3;
        for (int i = 0; i < 4; i++) {
            j++;
        }
        j = 2;
        return j;
    }

    public static int functionInliningFoo() {
        return 1;
    }

    public static int functionInliningOriginal(int x) {
        return functionInliningFoo() + 1;
    }

    public static int functionInliningExpected(int x) {
        return x + 2;
    }

    public static int ifTrueOriginal(int x) {
        if (true) {
            return x;
        } else {
            return x - 1;
        }
    }

    public static int ifTrueExpected(int x) {
        return x;
    }

    public static int infiniteEffectfulLoopOriginal() {
        int j = 0;
        for (int i = 5; i == 5;) {
            System.out.println(j);
        }
        j = 2;
        return j;
    }

    public static int infiniteLoopOriginal() {
        int j = 0;
        for (int i = 5; i == 5;) {
            j++;
        }
        return j;
    }

    // Modified from the paper example (see loop strength reduction)
    public static int loopBasedCodeMotionOriginal() {
        int x = 0;
        while (x < 3) {
            x += 1;
        }
        return x * 50;
    }

    public static int loopBasedCodeMotionExpected() {
        int x = 0;
        while (x < 150) {
            x += 50;
        }
        return x;
    }

    public static void loopInvariantCodeMotionOriginal(int n, int m) {
        for (int i = 0; i < 20; i++) {
            int j = n * 20;
            if (j < m) {
                j++;
            }
            System.out.println(i * j);
        }
    }

    public static void loopInvariantCodeMotionExpected(int n, int m) {
        int j = n * 20;
        if (j < m) {
            j++;
        }
        for (int i = 0; i < 20; i++) {
            System.out.println(i * j);
        }
    }

    public static int loopPeelingOriginal(int n) {
        int x = 0;
        int i = 0;
        while (i < n) {
            x += 5;
            i++;
        }
        return x;
    }

    public static int loopPeelingExpected(int n) {
        int x = 0;
        if (0 >= n) {
            x = 0;
        } else {
            x = 5;
            int i = 1;
            while (i < n) {
                x += 5;
                i++;
            }
        }
        return x;
    }

    public static void loopStrengthReductionOriginal() {
        int i = 0;
        int d = 0;
        while (d < 300) {
            System.out.println(i * 5);
            i = i + 1;
            d++;
        }
    }

    public static void loopStrengthReductionExpected() {
        int i = 0;
        int d = 0;
        while (d < 300) {
            System.out.println(i);
            i = i + 5;
            d++;
        }
    }

    // Modified from the paper example, as multiplying by 5 makes
    // the optimizer just turn it into five additions.
    public static void loopStrengthReductionModifiedOriginal() {
        int i = 0;
        int d = 0;
        while (d < 300) {
            System.out.println(i * 50);
            i = i + 1;
            if (d == 150) {
                i = i + 3;
            }
            d++;
        }
    }

    public static void loopStrengthReductionModifiedExpected() {
        int i = 0;
        int d = 0;
        while (d < 300) {
            System.out.println(i);
            i = i + 50;
            if (d == 150) {
                i = i + 150;
            }
            d++;
        }
    }

    public static int loopUnrollOriginal(int n) {
        int i = 0;
        while (i < 1) {
            i++;
        }
        return i;
    }

    public static int loopUnrollExpected(int n) {
        return 1;
    }

    public static int simpleLoopUnswitchOriginal(int n) {
        int j = 0;
        for (int i = 0; i < n; i++) {
            System.out.println(i);
            if (n < 0) {
                j = 2;
            }
            j++;
        }
        return j;
    }

    // The expected code for simple loop unswitch causes encoding
    // of the output CFG to never terminate, so it is in Failing.java.
}

```
## Run 

{'axioms': 'peggy/axioms/java_arithmetic_axioms.xml:peggy/axioms/java_operator_axioms.xml:peggy/axioms/java_operator_costs.xml:peggy/axioms/java_util_axioms.xml', 'optimization_level': 'O2', 'tmp_folder': 'tmp', 'pb': 'glpk', 'eto': '1000'}

### Peggy output
```
+ !!! CRITICAL ERROR: Error parsing command line: Unrecognized option: tmp_folder !!!
```

### Optimized
```java
18:03:43.337 INFO  jd.cli.Main - Decompiling optimized/Benchmark.class
import java.io.PrintStream;

public class Benchmark
{
  public static int branchHoistingOriginal(int paramInt)
  {
    int i = 0;
    int j = 0;
    while (i < 500)
    {
      i++;
      if (paramInt == 0) {
        j = i * 2;
      } else {
        j = i * 3;
      }
    }
    return j;
  }
  
  public static int branchHoistingExpected(int paramInt)
  {
    int i = 0;
    while (500 > i) {
      i += 1;
    }
    int j = i + i;
    if (paramInt == 0) {}
    for (paramInt = j;; paramInt = j + i) {
      return paramInt;
    }
  }
  
  public static int conditionalConstantFoldingOriginal(int paramInt)
  {
    if (paramInt == 5)
    {
      paramInt = 4 * paramInt;
      return paramInt;
    }
    if (paramInt == 4) {
      paramInt *= 5;
    }
    for (;;)
    {
      paramInt = paramInt;
      break;
      paramInt = 20;
    }
  }
  
  public static int conditionalConstantFoldingExpected()
  {
    return 20;
  }
  
  public static int constantFoldOriginal()
  {
    return -4;
  }
  
  public static int deadLoopDeletionOriginal()
  {
    return 2;
  }
  
  public static int functionInliningFoo()
  {
    return 1;
  }
  
  public static int functionInliningOriginal(int paramInt)
  {
    return 1 + functionInliningFoo();
  }
  
  public static int functionInliningExpected(int paramInt)
  {
    return paramInt + 2;
  }
  
  public static int ifTrueOriginal(int paramInt)
  {
    return paramInt;
  }
  
  public static int ifTrueExpected(int paramInt)
  {
    return paramInt;
  }
  
  public static int infiniteEffectfulLoopOriginal()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      System.out.println(i);
    }
    return 2;
  }
  
  public static int infiniteLoopOriginal()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      i++;
    }
    return i;
  }
  
  public static int loopBasedCodeMotionOriginal()
  {
    for (int i = 0; 150 > i; i = 50 + i) {}
    return i;
  }
  
  public static int loopBasedCodeMotionExpected()
  {
    for (int i = 0; i < 150; i = 50 + i) {}
    return i;
  }
  
  public static void loopInvariantCodeMotionOriginal(int paramInt1, int paramInt2)
  {
    int i = paramInt1 * 20;
    if (i < paramInt2) {}
    for (paramInt2 = i + 1;; paramInt2 = i)
    {
      i = 0;
      for (paramInt1 = 0; 20 > i; paramInt1 = paramInt2 + paramInt1)
      {
        System.out.println(paramInt1);
        i += 1;
      }
      return;
    }
  }
  
  public static void loopInvariantCodeMotionExpected(int paramInt1, int paramInt2)
  {
    int i = paramInt1 * 20;
    if (paramInt2 > i) {}
    for (paramInt1 = 1 + i;; paramInt1 = i)
    {
      i = 0;
      paramInt2 = 0;
      while (20 > paramInt2)
      {
        System.out.println(i);
        i = paramInt1 + i;
        paramInt2 += 1;
      }
      return;
    }
  }
  
  public static int loopPeelingOriginal(int paramInt)
  {
    int i = 0;
    int j = 0;
    while (j < paramInt)
    {
      i += 5;
      j += 1;
    }
    return i;
  }
  
  public static int loopPeelingExpected(int paramInt)
  {
    if (paramInt <= 0) {}
    int i;
    for (paramInt = 0;; paramInt = i)
    {
      return paramInt;
      i = 5;
      int j = 1;
      while (j < paramInt)
      {
        i += 5;
        j += 1;
      }
    }
  }
  
  public static void loopStrengthReductionOriginal()
  {
    int i = 0;
    while (i < 1500)
    {
      System.out.println(i);
      i += 5;
    }
  }
  
  public static void loopStrengthReductionExpected()
  {
    int i = 0;
    int j = 0;
    while (i < 300)
    {
      i += 1;
      System.out.println(j);
      j += 5;
    }
  }
  
  public static void loopStrengthReductionModifiedOriginal()
  {
    int j = 0;
    int i = 0;
    if (i < 300)
    {
      if (i == 150) {}
      for (int k = j + 4;; k = 1 + j)
      {
        System.out.println(j * 50);
        j = k;
        i += 1;
        break;
      }
    }
  }
  
  public static void loopStrengthReductionModifiedExpected()
  {
    int k = 0;
    int j = 0;
    if (j < 300)
    {
      if (j == 150) {}
      for (int i = k + 200;; i = 50 + k)
      {
        k = i;
        System.out.println(k);
        j = 1 + j;
        break;
      }
    }
  }
  
  public static int loopUnrollOriginal(int paramInt)
  {
    paramInt = 0;
    while (1 > paramInt) {
      paramInt += 1;
    }
    return paramInt;
  }
  
  public static int loopUnrollExpected(int paramInt)
  {
    return 1;
  }
  
  public static int simpleLoopUnswitchOriginal(int paramInt)
  {
    int j = 0;
    int i = 0;
    if (paramInt < 0) {
      while (paramInt > i)
      {
        j = 2 + 1;
        System.out.println(i);
        i += 1;
      }
    }
    while (paramInt > i)
    {
      j += 1;
      System.out.println(i);
      i += 1;
    }
    return j;
  }
}

/* Location:
 * Qualified Name:     Benchmark
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
## Run 

{'axioms': 'peggy/axioms/java_arithmetic_axioms.xml:peggy/axioms/java_operator_axioms.xml:peggy/axioms/java_operator_costs.xml:peggy/axioms/java_util_axioms.xml', 'optimization_level': 'O2', 'tmp_folder': 'tmp', 'pb': 'glpk', 'eto': '1000'}

### Peggy output
```
Cannot connect to the Docker daemon at unix:///Users/kirsten/.docker/run/docker.sock. Is the docker daemon running?
```

### Optimized
```java
13:49:04.245 INFO  jd.cli.Main - Decompiling optimized/Benchmark.class
import java.io.PrintStream;

public class Benchmark
{
  public static int branchHoistingOriginal(int paramInt)
  {
    int i = 0;
    int j = 0;
    while (i < 500)
    {
      i++;
      if (paramInt == 0) {
        j = i * 2;
      } else {
        j = i * 3;
      }
    }
    return j;
  }
  
  public static int branchHoistingExpected(int paramInt)
  {
    int i = 0;
    while (500 > i) {
      i += 1;
    }
    int j = i + i;
    if (paramInt == 0) {}
    for (paramInt = j;; paramInt = j + i) {
      return paramInt;
    }
  }
  
  public static int conditionalConstantFoldingOriginal(int paramInt)
  {
    if (paramInt == 5)
    {
      paramInt = 4 * paramInt;
      return paramInt;
    }
    if (paramInt == 4) {
      paramInt *= 5;
    }
    for (;;)
    {
      paramInt = paramInt;
      break;
      paramInt = 20;
    }
  }
  
  public static int conditionalConstantFoldingExpected()
  {
    return 20;
  }
  
  public static int constantFoldOriginal()
  {
    return -4;
  }
  
  public static int deadLoopDeletionOriginal()
  {
    return 2;
  }
  
  public static int functionInliningFoo()
  {
    return 1;
  }
  
  public static int functionInliningOriginal(int paramInt)
  {
    return 1 + functionInliningFoo();
  }
  
  public static int functionInliningExpected(int paramInt)
  {
    return paramInt + 2;
  }
  
  public static int ifTrueOriginal(int paramInt)
  {
    return paramInt;
  }
  
  public static int ifTrueExpected(int paramInt)
  {
    return paramInt;
  }
  
  public static int infiniteEffectfulLoopOriginal()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      System.out.println(i);
    }
    return 2;
  }
  
  public static int infiniteLoopOriginal()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      i++;
    }
    return i;
  }
  
  public static int loopBasedCodeMotionOriginal()
  {
    for (int i = 0; 150 > i; i = 50 + i) {}
    return i;
  }
  
  public static int loopBasedCodeMotionExpected()
  {
    for (int i = 0; i < 150; i = 50 + i) {}
    return i;
  }
  
  public static void loopInvariantCodeMotionOriginal(int paramInt1, int paramInt2)
  {
    int i = paramInt1 * 20;
    if (i < paramInt2) {}
    for (paramInt2 = i + 1;; paramInt2 = i)
    {
      i = 0;
      for (paramInt1 = 0; 20 > i; paramInt1 = paramInt2 + paramInt1)
      {
        System.out.println(paramInt1);
        i += 1;
      }
      return;
    }
  }
  
  public static void loopInvariantCodeMotionExpected(int paramInt1, int paramInt2)
  {
    int i = paramInt1 * 20;
    if (paramInt2 > i) {}
    for (paramInt1 = 1 + i;; paramInt1 = i)
    {
      i = 0;
      paramInt2 = 0;
      while (20 > paramInt2)
      {
        System.out.println(i);
        i = paramInt1 + i;
        paramInt2 += 1;
      }
      return;
    }
  }
  
  public static int loopPeelingOriginal(int paramInt)
  {
    int i = 0;
    int j = 0;
    while (j < paramInt)
    {
      i += 5;
      j += 1;
    }
    return i;
  }
  
  public static int loopPeelingExpected(int paramInt)
  {
    if (paramInt <= 0) {}
    int i;
    for (paramInt = 0;; paramInt = i)
    {
      return paramInt;
      i = 5;
      int j = 1;
      while (j < paramInt)
      {
        i += 5;
        j += 1;
      }
    }
  }
  
  public static void loopStrengthReductionOriginal()
  {
    int i = 0;
    while (i < 1500)
    {
      System.out.println(i);
      i += 5;
    }
  }
  
  public static void loopStrengthReductionExpected()
  {
    int i = 0;
    int j = 0;
    while (i < 300)
    {
      i += 1;
      System.out.println(j);
      j += 5;
    }
  }
  
  public static void loopStrengthReductionModifiedOriginal()
  {
    int j = 0;
    int i = 0;
    if (i < 300)
    {
      if (i == 150) {}
      for (int k = j + 4;; k = 1 + j)
      {
        System.out.println(j * 50);
        j = k;
        i += 1;
        break;
      }
    }
  }
  
  public static void loopStrengthReductionModifiedExpected()
  {
    int k = 0;
    int j = 0;
    if (j < 300)
    {
      if (j == 150) {}
      for (int i = k + 200;; i = 50 + k)
      {
        k = i;
        System.out.println(k);
        j = 1 + j;
        break;
      }
    }
  }
  
  public static int loopUnrollOriginal(int paramInt)
  {
    paramInt = 0;
    while (1 > paramInt) {
      paramInt += 1;
    }
    return paramInt;
  }
  
  public static int loopUnrollExpected(int paramInt)
  {
    return 1;
  }
  
  public static int simpleLoopUnswitchOriginal(int paramInt)
  {
    int j = 0;
    int i = 0;
    if (paramInt < 0) {
      while (paramInt > i)
      {
        j = 2 + 1;
        System.out.println(i);
        i += 1;
      }
    }
    while (paramInt > i)
    {
      j += 1;
      System.out.println(i);
      i += 1;
    }
    return j;
  }
}

/* Location:
 * Qualified Name:     Benchmark
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
## Run 

{'axioms': 'peggy/axioms/java_arithmetic_axioms.xml:peggy/axioms/java_operator_axioms.xml:peggy/axioms/java_operator_costs.xml:peggy/axioms/java_util_axioms.xml', 'optimization_level': 'O2', 'tmpFolder': 'tmp', 'pb': 'glpk', 'eto': '1000'}

### Peggy output
```
+ Successfully added axiom file: peggy/axioms/java_operator_axioms.xml
+ Successfully added axiom file: peggy/axioms/java_util_axioms.xml
+ Successfully added axiom file: peggy/axioms/java_operator_costs.xml
+ Successfully added axiom file: peggy/axioms/java_arithmetic_axioms.xml
+ Loading class file Benchmark
+ Optimizing class Benchmark
   - Processing method <Benchmark: void <init>()>
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
      * Optimization of method <Benchmark: void <init>()> SUCCESSFUL
      * Optimization took 553
      * PEG2PEGTIME 419
      * PBTIME 304
      * ENGINETIME 6
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <Benchmark: void <init>()>
   - Processing method <Benchmark: int branchHoistingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1000 after 1490 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1858
GLPKFormulation: Number of values: 617
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingOriginal(int)> SUCCESSFUL
      * Optimization took 20742
      * PEG2PEGTIME 20697
      * PBTIME 19180
      * ENGINETIME 1491
      * Optimization ratio 375/695 = 0.539568345323741
      * PEG-based Optimization ratio 375/695 = 0.539568345323741
   - Done processing method <Benchmark: int branchHoistingOriginal(int)>
   - Processing method <Benchmark: int branchHoistingExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1000 after 1000 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1746
GLPKFormulation: Number of values: 744
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingExpected(int)> SUCCESSFUL
      * Optimization took 21299
      * PEG2PEGTIME 21281
      * PBTIME 20249
      * ENGINETIME 1000
      * Optimization ratio 203/235 = 0.8638297872340426
      * PEG-based Optimization ratio 203/235 = 0.8638297872340426
   - Done processing method <Benchmark: int branchHoistingExpected(int)>
   - Processing method <Benchmark: int conditionalConstantFoldingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 16 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 16
GLPKFormulation: Number of values: 14
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int conditionalConstantFoldingOriginal(int)> SUCCESSFUL
      * Optimization took 320
      * PEG2PEGTIME 310
      * PBTIME 292
      * ENGINETIME 6
      * Optimization ratio 50/50 = 1.0
      * PEG-based Optimization ratio 50/50 = 1.0
   - Done processing method <Benchmark: int conditionalConstantFoldingOriginal(int)>
   - Processing method <Benchmark: int conditionalConstantFoldingExpected()>
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
      * Optimization of method <Benchmark: int conditionalConstantFoldingExpected()> SUCCESSFUL
      * Optimization took 239
      * PEG2PEGTIME 236
      * PBTIME 223
      * ENGINETIME 1
   - Done processing method <Benchmark: int conditionalConstantFoldingExpected()>
   - Processing method <Benchmark: int constantFoldOriginal()>
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
      * Optimization of method <Benchmark: int constantFoldOriginal()> SUCCESSFUL
      * Optimization took 235
      * PEG2PEGTIME 233
      * PBTIME 225
      * ENGINETIME 3
   - Done processing method <Benchmark: int constantFoldOriginal()>
   - Processing method <Benchmark: int deadLoopDeletionOriginal()>
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
      * Optimization of method <Benchmark: int deadLoopDeletionOriginal()> SUCCESSFUL
      * Optimization took 227
      * PEG2PEGTIME 225
      * PBTIME 219
      * ENGINETIME 0
   - Done processing method <Benchmark: int deadLoopDeletionOriginal()>
   - Processing method <Benchmark: int functionInliningFoo()>
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
      * Optimization of method <Benchmark: int functionInliningFoo()> SUCCESSFUL
      * Optimization took 230
      * PEG2PEGTIME 229
      * PBTIME 225
      * ENGINETIME 0
   - Done processing method <Benchmark: int functionInliningFoo()>
   - Processing method <Benchmark: int functionInliningOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 12 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 12
GLPKFormulation: Number of values: 11
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int functionInliningOriginal(int)> SUCCESSFUL
      * Optimization took 266
      * PEG2PEGTIME 261
      * PBTIME 254
      * ENGINETIME 2
      * Optimization ratio 2004/2004 = 1.0
      * PEG-based Optimization ratio 2004/2004 = 1.0
   - Done processing method <Benchmark: int functionInliningOriginal(int)>
   - Processing method <Benchmark: int functionInliningExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 8 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 8
GLPKFormulation: Number of values: 7
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int functionInliningExpected(int)> SUCCESSFUL
      * Optimization took 238
      * PEG2PEGTIME 235
      * PBTIME 229
      * ENGINETIME 1
      * Optimization ratio 4/4 = 1.0
      * PEG-based Optimization ratio 4/4 = 1.0
   - Done processing method <Benchmark: int functionInliningExpected(int)>
   - Processing method <Benchmark: int ifTrueOriginal(int)>
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
      * Optimization of method <Benchmark: int ifTrueOriginal(int)> SUCCESSFUL
      * Optimization took 310
      * PEG2PEGTIME 307
      * PBTIME 298
      * ENGINETIME 1
   - Done processing method <Benchmark: int ifTrueOriginal(int)>
   - Processing method <Benchmark: int ifTrueExpected(int)>
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
      * Optimization of method <Benchmark: int ifTrueExpected(int)> SUCCESSFUL
      * Optimization took 240
      * PEG2PEGTIME 237
      * PBTIME 229
      * ENGINETIME 0
   - Done processing method <Benchmark: int ifTrueExpected(int)>
   - Processing method <Benchmark: int infiniteEffectfulLoopOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1000 after 161 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1043
GLPKFormulation: Number of values: 716
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      ! Error processing method <Benchmark: int infiniteEffectfulLoopOriginal()> [
         java.lang.RuntimeException: Bad CFG: Should never happen
         	at eqsat.revert.RevertCFG.removeEmptyFallBlocks(RevertCFG.java:401)
         	at eqsat.revert.RevertCFG.simplify(RevertCFG.java:311)
         	at eqsat.revert.CFGReverter.<init>(CFGReverter.java:38)
         	at peggy.optimize.SingleStageOptimizer.optimize(SingleStageOptimizer.java:104)
         	at peggy.optimize.java.Main.optimizeAll(Main.java:875)
         	at peggy.optimize.java.Main.optimizeClass(Main.java:704)
         	at peggy.optimize.java.Main.main(Main.java:2881)
         
      ]
      * Reverting to original method body
      * Optimization of method <Benchmark: int infiniteEffectfulLoopOriginal()> FAILED
   - Processing method <Benchmark: int infiniteLoopOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1000 after 863 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 787
GLPKFormulation: Number of values: 134
```

### Optimized
```java
13:56:30.707 INFO  jd.cli.Main - Decompiling optimized/Benchmark.class
import java.io.PrintStream;

public class Benchmark
{
  public static int branchHoistingOriginal(int paramInt)
  {
    int i;
    for (int j = 0;; j = i)
    {
      i = j + 1;
      if (i >= 500) {
        break;
      }
    }
    j = i + i;
    if (paramInt == 0) {
      i = j;
    }
    for (;;)
    {
      return i;
      i += j;
    }
  }
  
  public static int branchHoistingExpected(int paramInt)
  {
    int i = 0;
    while (500 > i) {
      i += 1;
    }
    int j = i + i;
    if (paramInt == 0) {}
    for (paramInt = j;; paramInt = i + j) {
      return paramInt;
    }
  }
  
  public static int conditionalConstantFoldingOriginal(int paramInt)
  {
    if (paramInt == 5)
    {
      paramInt = 4 * paramInt;
      return paramInt;
    }
    if (paramInt == 4) {}
    for (paramInt = 5 * paramInt;; paramInt = 20)
    {
      paramInt = paramInt;
      break;
    }
  }
  
  public static int conditionalConstantFoldingExpected()
  {
    return 20;
  }
  
  public static int constantFoldOriginal()
  {
    return -4;
  }
  
  public static int deadLoopDeletionOriginal()
  {
    return 2;
  }
  
  public static int functionInliningFoo()
  {
    return 1;
  }
  
  public static int functionInliningOriginal(int paramInt)
  {
    return 1 + functionInliningFoo();
  }
  
  public static int functionInliningExpected(int paramInt)
  {
    return paramInt + 2;
  }
  
  public static int ifTrueOriginal(int paramInt)
  {
    return paramInt;
  }
  
  public static int ifTrueExpected(int paramInt)
  {
    return paramInt;
  }
  
  public static int infiniteEffectfulLoopOriginal()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      System.out.println(i);
    }
    return 2;
  }
  
  public static int infiniteLoopOriginal()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      i++;
    }
    return i;
  }
  
  public static int loopBasedCodeMotionOriginal()
  {
    for (int i = 0; 3 > i; i = 1 + i) {}
    return i * 50;
  }
  
  public static int loopBasedCodeMotionExpected()
  {
    int i = 0;
    while (150 > i) {
      i += 50;
    }
    return i;
  }
  
  public static void loopInvariantCodeMotionOriginal(int paramInt1, int paramInt2)
  {
    int i = paramInt1 * 20;
    if (i < paramInt2) {}
    for (paramInt2 = i + 1;; paramInt2 = i)
    {
      i = 0;
      paramInt1 = 0;
      while (20 > i)
      {
        System.out.println(paramInt1);
        i = 1 + i;
        paramInt1 += paramInt2;
      }
      return;
    }
  }
  
  public static void loopInvariantCodeMotionExpected(int paramInt1, int paramInt2)
  {
    paramInt1 *= 20;
    if (paramInt1 < paramInt2) {}
    for (paramInt2 = 1 + paramInt1;; paramInt2 = paramInt1)
    {
      paramInt1 = 0;
      int i = 0;
      while (i < 20)
      {
        paramInt1 += paramInt2;
        i += 1;
        System.out.println(paramInt1);
      }
      return;
    }
  }
  
  public static int loopPeelingOriginal(int paramInt)
  {
    int i = 0;
    for (int j = 0; i < paramInt; j = 5 + j) {
      i += 1;
    }
    return j;
  }
  
  public static int loopPeelingExpected(int paramInt)
  {
    if (paramInt <= 0) {}
    for (int j = 0;; j = j)
    {
      return j;
      int i = 1;
      for (j = 5; i < paramInt; j = 5 + j) {
        i = 1 + i;
      }
    }
  }
  
  public static void loopStrengthReductionOriginal()
  {
    for (int i = 0; i < 300; i = 1 + i)
    {
      int tmp16_15 = (i + i);
      System.out.println(i + (tmp16_15 + tmp16_15));
    }
  }
  
  public static void loopStrengthReductionExpected()
  {
    int j = 0;
    for (int i = 0; 300 > j; i = 5 + i)
    {
      System.out.println(i);
      j = 1 + j;
    }
  }
  
  public static void loopStrengthReductionModifiedOriginal()
  {
    int j = 0;
    int i = 0;
    if (300 > j)
    {
      int k = 1 + i;
      if (j == 150) {
        k += 3;
      }
      for (;;)
      {
        System.out.println(50 * i);
        j = 1 + j;
        i = k;
        break;
        k = k;
      }
    }
  }
  
  public static void loopStrengthReductionModifiedExpected()
  {
    int j = 0;
    int i = 0;
    if (300 > j)
    {
      int k = i + 50;
      if (j == 150) {
        k += 150;
      }
      for (;;)
      {
        j = 1 + j;
        i = k;
        System.out.println(i);
        break;
        k = k;
      }
    }
  }
  
  public static int loopUnrollOriginal(int paramInt)
  {
    paramInt = 0;
    while (paramInt < 1) {
      paramInt += 1;
    }
    return paramInt;
  }
  
  public static int loopUnrollExpected(int paramInt)
  {
    return 1;
  }
  
  public static int simpleLoopUnswitchOriginal(int paramInt)
  {
    int k = 0;
    int j = 0;
    if (paramInt < 0)
    {
      while (j < paramInt)
      {
        int i = j;
        k = 2 + 1;
        j = 1 + j;
        System.out.println(i);
      }
      return k;
    }
    for (;;)
    {
      if (j < paramInt)
      {
        j = 1 + j;
        System.out.println(j);
      }
    }
  }
}

/* Location:
 * Qualified Name:     Benchmark
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
## Run 

{'axioms': 'peggy/axioms/java_arithmetic_axioms.xml:peggy/axioms/java_operator_axioms.xml:peggy/axioms/java_operator_costs.xml:peggy/axioms/java_util_axioms.xml', 'optimization_level': 'O2', 'tmpFolder': 'tmp', 'pb': 'glpk', 'eto': '1000'}

### Peggy output
```
+ Successfully added axiom file: peggy/axioms/java_operator_axioms.xml
+ Successfully added axiom file: peggy/axioms/java_util_axioms.xml
+ Successfully added axiom file: peggy/axioms/java_operator_costs.xml
+ Successfully added axiom file: peggy/axioms/java_arithmetic_axioms.xml
+ Loading class file Benchmark
+ Optimizing class Benchmark
   - Processing method <Benchmark: void <init>()>
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
      * Optimization of method <Benchmark: void <init>()> SUCCESSFUL
      * Optimization took 474
      * PEG2PEGTIME 374
      * PBTIME 289
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <Benchmark: void <init>()>
   - Processing method <Benchmark: int branchHoistingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1000 after 1306 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1753
GLPKFormulation: Number of values: 613
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingOriginal(int)> SUCCESSFUL
      * Optimization took 19342
      * PEG2PEGTIME 19300
      * PBTIME 17963
      * ENGINETIME 1306
      * Optimization ratio 375/695 = 0.539568345323741
      * PEG-based Optimization ratio 375/695 = 0.539568345323741
   - Done processing method <Benchmark: int branchHoistingOriginal(int)>
   - Processing method <Benchmark: int branchHoistingExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1000 after 672 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1635
GLPKFormulation: Number of values: 753
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingExpected(int)> SUCCESSFUL
      * Optimization took 19034
      * PEG2PEGTIME 19014
      * PBTIME 18320
      * ENGINETIME 672
      * Optimization ratio 203/235 = 0.8638297872340426
      * PEG-based Optimization ratio 203/235 = 0.8638297872340426
   - Done processing method <Benchmark: int branchHoistingExpected(int)>
   - Processing method <Benchmark: int conditionalConstantFoldingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 16 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 16
GLPKFormulation: Number of values: 14
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int conditionalConstantFoldingOriginal(int)> SUCCESSFUL
      * Optimization took 334
      * PEG2PEGTIME 325
      * PBTIME 305
      * ENGINETIME 5
      * Optimization ratio 50/50 = 1.0
      * PEG-based Optimization ratio 50/50 = 1.0
   - Done processing method <Benchmark: int conditionalConstantFoldingOriginal(int)>
   - Processing method <Benchmark: int conditionalConstantFoldingExpected()>
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
      * Optimization of method <Benchmark: int conditionalConstantFoldingExpected()> SUCCESSFUL
      * Optimization took 249
      * PEG2PEGTIME 247
      * PBTIME 240
      * ENGINETIME 0
   - Done processing method <Benchmark: int conditionalConstantFoldingExpected()>
   - Processing method <Benchmark: int constantFoldOriginal()>
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
      * Optimization of method <Benchmark: int constantFoldOriginal()> SUCCESSFUL
      * Optimization took 251
      * PEG2PEGTIME 248
      * PBTIME 240
      * ENGINETIME 1
   - Done processing method <Benchmark: int constantFoldOriginal()>
   - Processing method <Benchmark: int deadLoopDeletionOriginal()>
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
      * Optimization of method <Benchmark: int deadLoopDeletionOriginal()> SUCCESSFUL
      * Optimization took 250
      * PEG2PEGTIME 247
      * PBTIME 237
      * ENGINETIME 0
   - Done processing method <Benchmark: int deadLoopDeletionOriginal()>
   - Processing method <Benchmark: int functionInliningFoo()>
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
      * Optimization of method <Benchmark: int functionInliningFoo()> SUCCESSFUL
      * Optimization took 238
      * PEG2PEGTIME 236
      * PBTIME 229
      * ENGINETIME 1
   - Done processing method <Benchmark: int functionInliningFoo()>
   - Processing method <Benchmark: int functionInliningOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 12 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 12
GLPKFormulation: Number of values: 11
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int functionInliningOriginal(int)> SUCCESSFUL
      * Optimization took 373
      * PEG2PEGTIME 365
      * PBTIME 349
      * ENGINETIME 3
      * Optimization ratio 2004/2004 = 1.0
      * PEG-based Optimization ratio 2004/2004 = 1.0
   - Done processing method <Benchmark: int functionInliningOriginal(int)>
   - Processing method <Benchmark: int functionInliningExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 8 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 8
GLPKFormulation: Number of values: 7
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int functionInliningExpected(int)> SUCCESSFUL
      * Optimization took 251
      * PEG2PEGTIME 249
      * PBTIME 239
      * ENGINETIME 1
      * Optimization ratio 4/4 = 1.0
      * PEG-based Optimization ratio 4/4 = 1.0
   - Done processing method <Benchmark: int functionInliningExpected(int)>
   - Processing method <Benchmark: int ifTrueOriginal(int)>
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
      * Optimization of method <Benchmark: int ifTrueOriginal(int)> SUCCESSFUL
      * Optimization took 245
      * PEG2PEGTIME 243
      * PBTIME 235
      * ENGINETIME 1
   - Done processing method <Benchmark: int ifTrueOriginal(int)>
   - Processing method <Benchmark: int ifTrueExpected(int)>
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
      * Optimization of method <Benchmark: int ifTrueExpected(int)> SUCCESSFUL
      * Optimization took 325
      * PEG2PEGTIME 321
      * PBTIME 316
      * ENGINETIME 0
   - Done processing method <Benchmark: int ifTrueExpected(int)>
   - Processing method <Benchmark: int infiniteEffectfulLoopOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1000 after 177 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1043
GLPKFormulation: Number of values: 716
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      ! Error processing method <Benchmark: int infiniteEffectfulLoopOriginal()> [
         java.lang.RuntimeException: Bad CFG: Should never happen
         	at eqsat.revert.RevertCFG.removeEmptyFallBlocks(RevertCFG.java:401)
         	at eqsat.revert.RevertCFG.simplify(RevertCFG.java:311)
         	at eqsat.revert.CFGReverter.<init>(CFGReverter.java:38)
         	at peggy.optimize.SingleStageOptimizer.optimize(SingleStageOptimizer.java:104)
         	at peggy.optimize.java.Main.optimizeAll(Main.java:875)
         	at peggy.optimize.java.Main.optimizeClass(Main.java:704)
         	at peggy.optimize.java.Main.main(Main.java:2881)
         
      ]
      * Reverting to original method body
      * Optimization of method <Benchmark: int infiniteEffectfulLoopOriginal()> FAILED
   - Processing method <Benchmark: int infiniteLoopOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1000 after 901 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 787
GLPKFormulation: Number of values: 134
```

### Optimized
```java
14:03:25.194 INFO  jd.cli.Main - Decompiling optimized/Benchmark.class
import java.io.PrintStream;

public class Benchmark
{
  public static int branchHoistingOriginal(int paramInt)
  {
    int i;
    for (int j = 0;; j = i)
    {
      i = j + 1;
      if (i >= 500) {
        break;
      }
    }
    j = i + i;
    if (paramInt == 0) {
      i = j;
    }
    for (;;)
    {
      return i;
      i += j;
    }
  }
  
  public static int branchHoistingExpected(int paramInt)
  {
    int i = 0;
    while (500 > i) {
      i += 1;
    }
    int j = i + i;
    if (paramInt == 0) {}
    for (paramInt = j;; paramInt = i + j) {
      return paramInt;
    }
  }
  
  public static int conditionalConstantFoldingOriginal(int paramInt)
  {
    if (paramInt == 5)
    {
      paramInt *= 4;
      return paramInt;
    }
    if (paramInt == 4) {
      paramInt *= 5;
    }
    for (;;)
    {
      paramInt = paramInt;
      break;
      paramInt = 20;
    }
  }
  
  public static int conditionalConstantFoldingExpected()
  {
    return 20;
  }
  
  public static int constantFoldOriginal()
  {
    return -4;
  }
  
  public static int deadLoopDeletionOriginal()
  {
    return 2;
  }
  
  public static int functionInliningFoo()
  {
    return 1;
  }
  
  public static int functionInliningOriginal(int paramInt)
  {
    return functionInliningFoo() + 1;
  }
  
  public static int functionInliningExpected(int paramInt)
  {
    return paramInt + 2;
  }
  
  public static int ifTrueOriginal(int paramInt)
  {
    return paramInt;
  }
  
  public static int ifTrueExpected(int paramInt)
  {
    return paramInt;
  }
  
  public static int infiniteEffectfulLoopOriginal()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      System.out.println(i);
    }
    return 2;
  }
  
  public static int infiniteLoopOriginal()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      i++;
    }
    return i;
  }
  
  public static int loopBasedCodeMotionOriginal()
  {
    int i = 0;
    while (i < 3) {
      i += 1;
    }
    return i * 50;
  }
  
  public static int loopBasedCodeMotionExpected()
  {
    for (int i = 0; 150 > i; i = 50 + i) {}
    return i;
  }
  
  public static void loopInvariantCodeMotionOriginal(int paramInt1, int paramInt2)
  {
    paramInt1 *= 20;
    if (paramInt2 > paramInt1) {
      paramInt1 += 1;
    }
    for (;;)
    {
      paramInt2 = 0;
      int i = 0;
      while (20 > i)
      {
        paramInt2 = paramInt1 + paramInt2;
        i = 1 + i;
        System.out.println(paramInt2);
      }
      return;
      paramInt1 = paramInt1;
    }
  }
  
  public static void loopInvariantCodeMotionExpected(int paramInt1, int paramInt2)
  {
    paramInt1 = 20 * paramInt1;
    if (paramInt2 > paramInt1) {}
    for (paramInt2 = 1 + paramInt1;; paramInt2 = paramInt1)
    {
      int i = 0;
      paramInt1 = 0;
      while (i < 20)
      {
        System.out.println(paramInt1);
        i += 1;
        paramInt1 += paramInt2;
      }
      return;
    }
  }
  
  public static int loopPeelingOriginal(int paramInt)
  {
    int i = 0;
    for (int j = 0; paramInt > i; j = 5 + j) {
      i += 1;
    }
    return j;
  }
  
  public static int loopPeelingExpected(int paramInt)
  {
    if (paramInt <= 0) {}
    int i;
    for (paramInt = 0;; paramInt = i)
    {
      return paramInt;
      i = 5;
      for (int j = 1; j < paramInt; j = 1 + j) {
        i += 5;
      }
    }
  }
  
  public static void loopStrengthReductionOriginal()
  {
    int i = 0;
    while (i < 300)
    {
      int j = i;
      i = 1 + i;
      int tmp21_20 = (j + j);
      System.out.println(tmp21_20 + tmp21_20 + j);
    }
  }
  
  public static void loopStrengthReductionExpected()
  {
    int j = 0;
    int i = 0;
    while (300 > i)
    {
      j = 5 + j;
      System.out.println(j);
      i += 1;
    }
  }
  
  public static void loopStrengthReductionModifiedOriginal()
  {
    int k = 0;
    int j = 0;
    if (j < 300)
    {
      int i = k + 1;
      if (j == 150) {
        i += 3;
      }
      for (;;)
      {
        System.out.println(50 * k);
        k = i;
        j += 1;
        break;
        i = i;
      }
    }
  }
  
  public static void loopStrengthReductionModifiedExpected()
  {
    int j = 0;
    int i = 0;
    if (j < 300)
    {
      int k = 50 + i;
      if (j == 150) {
        k += 150;
      }
      for (;;)
      {
        j = 1 + j;
        System.out.println(i);
        i = k;
        break;
        k = k;
      }
    }
  }
  
  public static int loopUnrollOriginal(int paramInt)
  {
    paramInt = 0;
    while (paramInt < 1) {
      paramInt += 1;
    }
    return paramInt;
  }
  
  public static int loopUnrollExpected(int paramInt)
  {
    return 1;
  }
  
  public static int simpleLoopUnswitchOriginal(int paramInt)
  {
    int i = 0;
    int j = 0;
    if (paramInt < 0) {
      while (paramInt > j)
      {
        System.out.println(j);
        i = 2 + 1;
        j = 1 + j;
      }
    }
    while (paramInt > j)
    {
      System.out.println(j);
      i += 1;
      j = 1 + j;
    }
    return i;
  }
}

/* Location:
 * Qualified Name:     Benchmark
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
## Run 

{'axioms': 'peggy/axioms/java_arithmetic_axioms.xml:peggy/axioms/java_operator_axioms.xml:peggy/axioms/java_operator_costs.xml:peggy/axioms/java_util_axioms.xml', 'optimization_level': 'O2', 'tmpFolder': 'tmp', 'pb': 'glpk', 'eto': '1000'}

### Peggy output
```
+ Successfully added axiom file: peggy/axioms/java_operator_axioms.xml
+ Successfully added axiom file: peggy/axioms/java_util_axioms.xml
+ Successfully added axiom file: peggy/axioms/java_operator_costs.xml
+ Successfully added axiom file: peggy/axioms/java_arithmetic_axioms.xml
+ Loading class file Benchmark
+ Optimizing class Benchmark
   - Processing method <Benchmark: void <init>()>
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
      * Optimization of method <Benchmark: void <init>()> SUCCESSFUL
      * Optimization took 494
      * PEG2PEGTIME 397
      * PBTIME 283
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <Benchmark: void <init>()>
   - Processing method <Benchmark: int branchHoistingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1000 after 1389 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1903
GLPKFormulation: Number of values: 627
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingOriginal(int)> SUCCESSFUL
      * Optimization took 20103
      * PEG2PEGTIME 20036
      * PBTIME 18614
      * ENGINETIME 1390
      * Optimization ratio 375/695 = 0.539568345323741
      * PEG-based Optimization ratio 375/695 = 0.539568345323741
   - Done processing method <Benchmark: int branchHoistingOriginal(int)>
   - Processing method <Benchmark: int branchHoistingExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1000 after 1070 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1812
GLPKFormulation: Number of values: 793
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingExpected(int)> SUCCESSFUL
      * Optimization took 21845
      * PEG2PEGTIME 21830
      * PBTIME 20743
      * ENGINETIME 1071
      * Optimization ratio 203/235 = 0.8638297872340426
      * PEG-based Optimization ratio 203/235 = 0.8638297872340426
   - Done processing method <Benchmark: int branchHoistingExpected(int)>
   - Processing method <Benchmark: int conditionalConstantFoldingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 16 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 16
GLPKFormulation: Number of values: 14
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int conditionalConstantFoldingOriginal(int)> SUCCESSFUL
      * Optimization took 301
      * PEG2PEGTIME 294
      * PBTIME 279
      * ENGINETIME 4
      * Optimization ratio 50/50 = 1.0
      * PEG-based Optimization ratio 50/50 = 1.0
   - Done processing method <Benchmark: int conditionalConstantFoldingOriginal(int)>
   - Processing method <Benchmark: int conditionalConstantFoldingExpected()>
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
      * Optimization of method <Benchmark: int conditionalConstantFoldingExpected()> SUCCESSFUL
      * Optimization took 233
      * PEG2PEGTIME 230
      * PBTIME 222
      * ENGINETIME 0
   - Done processing method <Benchmark: int conditionalConstantFoldingExpected()>
   - Processing method <Benchmark: int constantFoldOriginal()>
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
      * Optimization of method <Benchmark: int constantFoldOriginal()> SUCCESSFUL
      * Optimization took 250
      * PEG2PEGTIME 245
      * PBTIME 234
      * ENGINETIME 2
   - Done processing method <Benchmark: int constantFoldOriginal()>
   - Processing method <Benchmark: int deadLoopDeletionOriginal()>
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
      * Optimization of method <Benchmark: int deadLoopDeletionOriginal()> SUCCESSFUL
      * Optimization took 242
      * PEG2PEGTIME 239
      * PBTIME 231
      * ENGINETIME 1
   - Done processing method <Benchmark: int deadLoopDeletionOriginal()>
   - Processing method <Benchmark: int functionInliningFoo()>
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
      * Optimization of method <Benchmark: int functionInliningFoo()> SUCCESSFUL
      * Optimization took 250
      * PEG2PEGTIME 246
      * PBTIME 239
      * ENGINETIME 0
   - Done processing method <Benchmark: int functionInliningFoo()>
   - Processing method <Benchmark: int functionInliningOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 12 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 12
GLPKFormulation: Number of values: 11
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int functionInliningOriginal(int)> SUCCESSFUL
      * Optimization took 279
      * PEG2PEGTIME 271
      * PBTIME 261
      * ENGINETIME 3
      * Optimization ratio 2004/2004 = 1.0
      * PEG-based Optimization ratio 2004/2004 = 1.0
   - Done processing method <Benchmark: int functionInliningOriginal(int)>
   - Processing method <Benchmark: int functionInliningExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 8 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 8
GLPKFormulation: Number of values: 7
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int functionInliningExpected(int)> SUCCESSFUL
      * Optimization took 249
      * PEG2PEGTIME 247
      * PBTIME 241
      * ENGINETIME 1
      * Optimization ratio 4/4 = 1.0
      * PEG-based Optimization ratio 4/4 = 1.0
   - Done processing method <Benchmark: int functionInliningExpected(int)>
   - Processing method <Benchmark: int ifTrueOriginal(int)>
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
      * Optimization of method <Benchmark: int ifTrueOriginal(int)> SUCCESSFUL
      * Optimization took 328
      * PEG2PEGTIME 326
      * PBTIME 235
      * ENGINETIME 1
   - Done processing method <Benchmark: int ifTrueOriginal(int)>
   - Processing method <Benchmark: int ifTrueExpected(int)>
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
      * Optimization of method <Benchmark: int ifTrueExpected(int)> SUCCESSFUL
      * Optimization took 232
      * PEG2PEGTIME 230
      * PBTIME 224
      * ENGINETIME 1
   - Done processing method <Benchmark: int ifTrueExpected(int)>
   - Processing method <Benchmark: int infiniteEffectfulLoopOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1000 after 158 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1043
GLPKFormulation: Number of values: 716
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      ! Error processing method <Benchmark: int infiniteEffectfulLoopOriginal()> [
         java.lang.RuntimeException: Bad CFG: Should never happen
         	at eqsat.revert.RevertCFG.removeEmptyFallBlocks(RevertCFG.java:401)
         	at eqsat.revert.RevertCFG.simplify(RevertCFG.java:311)
         	at eqsat.revert.CFGReverter.<init>(CFGReverter.java:38)
         	at peggy.optimize.SingleStageOptimizer.optimize(SingleStageOptimizer.java:104)
         	at peggy.optimize.java.Main.optimizeAll(Main.java:875)
         	at peggy.optimize.java.Main.optimizeClass(Main.java:704)
         	at peggy.optimize.java.Main.main(Main.java:2881)
         
      ]
      * Reverting to original method body
      * Optimization of method <Benchmark: int infiniteEffectfulLoopOriginal()> FAILED
   - Processing method <Benchmark: int infiniteLoopOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1000 after 994 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 787
GLPKFormulation: Number of values: 134
```

### Optimized
```java
14:25:42.612 INFO  jd.cli.Main - Decompiling optimized/Benchmark.class
import java.io.PrintStream;

public class Benchmark
{
  public static int branchHoistingOriginal(int paramInt)
  {
    int i;
    for (int j = 0;; j = i)
    {
      i = j + 1;
      if (i >= 500) {
        break;
      }
    }
    j = i + i;
    if (paramInt == 0) {
      i = j;
    }
    for (;;)
    {
      return i;
      i += j;
    }
  }
  
  public static int branchHoistingExpected(int paramInt)
  {
    int i = 0;
    while (500 > i) {
      i += 1;
    }
    int j = i + i;
    if (paramInt == 0) {}
    for (paramInt = j;; paramInt = i + j) {
      return paramInt;
    }
  }
  
  public static int conditionalConstantFoldingOriginal(int paramInt)
  {
    if (paramInt == 5)
    {
      paramInt *= 4;
      return paramInt;
    }
    if (paramInt == 4) {
      paramInt *= 5;
    }
    for (;;)
    {
      paramInt = paramInt;
      break;
      paramInt = 20;
    }
  }
  
  public static int conditionalConstantFoldingExpected()
  {
    return 20;
  }
  
  public static int constantFoldOriginal()
  {
    return -4;
  }
  
  public static int deadLoopDeletionOriginal()
  {
    return 2;
  }
  
  public static int functionInliningFoo()
  {
    return 1;
  }
  
  public static int functionInliningOriginal(int paramInt)
  {
    return 1 + functionInliningFoo();
  }
  
  public static int functionInliningExpected(int paramInt)
  {
    return 2 + paramInt;
  }
  
  public static int ifTrueOriginal(int paramInt)
  {
    return paramInt;
  }
  
  public static int ifTrueExpected(int paramInt)
  {
    return paramInt;
  }
  
  public static int infiniteEffectfulLoopOriginal()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      System.out.println(i);
    }
    return 2;
  }
  
  public static int infiniteLoopOriginal()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      i++;
    }
    return i;
  }
  
  public static int loopBasedCodeMotionOriginal()
  {
    for (int i = 0; 3 > i; i = 1 + i) {}
    return i * 50;
  }
  
  public static int loopBasedCodeMotionExpected()
  {
    for (int i = 0; 150 > i; i = 50 + i) {}
    return i;
  }
  
  public static void loopInvariantCodeMotionOriginal(int paramInt1, int paramInt2)
  {
    paramInt1 *= 20;
    if (paramInt2 > paramInt1) {}
    for (paramInt2 = paramInt1 + 1;; paramInt2 = paramInt1)
    {
      int i = 0;
      paramInt1 = 0;
      while (i < 20)
      {
        System.out.println(paramInt1);
        i += 1;
        paramInt1 += paramInt2;
      }
      return;
    }
  }
  
  public static void loopInvariantCodeMotionExpected(int paramInt1, int paramInt2)
  {
    paramInt1 *= 20;
    if (paramInt2 > paramInt1) {}
    for (paramInt2 = paramInt1 + 1;; paramInt2 = paramInt1)
    {
      int i = 0;
      paramInt1 = 0;
      while (20 > i)
      {
        System.out.println(paramInt1);
        i += 1;
        paramInt1 += paramInt2;
      }
      return;
    }
  }
  
  public static int loopPeelingOriginal(int paramInt)
  {
    int i = 0;
    for (int j = 0; i < paramInt; j = 5 + j) {
      i = 1 + i;
    }
    return j;
  }
  
  public static int loopPeelingExpected(int paramInt)
  {
    if (paramInt <= 0) {}
    int j;
    for (paramInt = 0;; paramInt = j)
    {
      return paramInt;
      j = 5;
      int i = 1;
      while (paramInt > i)
      {
        j = 5 + j;
        i += 1;
      }
    }
  }
  
  public static void loopStrengthReductionOriginal()
  {
    int i = 0;
    while (i < 300)
    {
      int tmp16_15 = (i + i);
      System.out.println(i + (tmp16_15 + tmp16_15));
      i += 1;
    }
  }
  
  public static void loopStrengthReductionExpected()
  {
    int j = 0;
    for (int i = 0; 300 > j; i = 5 + i)
    {
      j = 1 + j;
      System.out.println(i);
    }
  }
  
  public static void loopStrengthReductionModifiedOriginal()
  {
    int j = 0;
    int k = 0;
    if (k < 300)
    {
      int i = j + 1;
      if (k == 150) {}
      for (i = 3 + i;; i = i)
      {
        j = i;
        k += 1;
        System.out.println(j * 50);
        break;
      }
    }
  }
  
  public static void loopStrengthReductionModifiedExpected()
  {
    int k = 0;
    int j = 0;
    if (k < 300)
    {
      int i = j + 50;
      if (k == 150) {}
      for (i = 150 + i;; i = i)
      {
        System.out.println(j);
        k += 1;
        j = i;
        break;
      }
    }
  }
  
  public static int loopUnrollOriginal(int paramInt)
  {
    for (paramInt = 0; paramInt < 1; paramInt = 1 + paramInt) {}
    return paramInt;
  }
  
  public static int loopUnrollExpected(int paramInt)
  {
    return 1;
  }
  
  public static int simpleLoopUnswitchOriginal(int paramInt)
  {
    int k = 0;
    int j = 0;
    if (paramInt < 0)
    {
      while (paramInt > j)
      {
        int i = j;
        k = 2 + 1;
        j += 1;
        System.out.println(i);
      }
      return k;
    }
    for (;;)
    {
      if (paramInt > j)
      {
        j += 1;
        System.out.println(j);
      }
    }
  }
}

/* Location:
 * Qualified Name:     Benchmark
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
## Run 

{'axioms': 'peggy/axioms/java_arithmetic_axioms.xml:peggy/axioms/java_operator_axioms.xml:peggy/axioms/java_operator_costs.xml:peggy/axioms/java_util_axioms.xml', 'optimization_level': 'O2', 'tmpFolder': 'tmp', 'pb': 'glpk', 'eto': '1000'}

### Peggy output
```
+ Successfully added axiom file: peggy/axioms/java_operator_axioms.xml
+ Successfully added axiom file: peggy/axioms/java_util_axioms.xml
+ Successfully added axiom file: peggy/axioms/java_operator_costs.xml
+ Successfully added axiom file: peggy/axioms/java_arithmetic_axioms.xml
+ Loading class file Benchmark
+ Optimizing class Benchmark
   - Processing method <Benchmark: void <init>()>
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
      * Optimization of method <Benchmark: void <init>()> SUCCESSFUL
      * Optimization took 491
      * PEG2PEGTIME 398
      * PBTIME 294
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <Benchmark: void <init>()>
   - Processing method <Benchmark: int branchHoistingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1000 after 1270 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1755
GLPKFormulation: Number of values: 614
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingOriginal(int)> SUCCESSFUL
      * Optimization took 21399
      * PEG2PEGTIME 21337
      * PBTIME 20033
      * ENGINETIME 1271
      * Optimization ratio 375/695 = 0.539568345323741
      * PEG-based Optimization ratio 375/695 = 0.539568345323741
   - Done processing method <Benchmark: int branchHoistingOriginal(int)>
   - Processing method <Benchmark: int branchHoistingExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1000 after 1099 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1750
GLPKFormulation: Number of values: 746
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingExpected(int)> SUCCESSFUL
      * Optimization took 20744
      * PEG2PEGTIME 20714
      * PBTIME 19596
      * ENGINETIME 1100
      * Optimization ratio 203/235 = 0.8638297872340426
      * PEG-based Optimization ratio 203/235 = 0.8638297872340426
   - Done processing method <Benchmark: int branchHoistingExpected(int)>
   - Processing method <Benchmark: int conditionalConstantFoldingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 16 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 16
GLPKFormulation: Number of values: 14
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int conditionalConstantFoldingOriginal(int)> SUCCESSFUL
      * Optimization took 436
      * PEG2PEGTIME 424
      * PBTIME 400
      * ENGINETIME 9
      * Optimization ratio 50/50 = 1.0
      * PEG-based Optimization ratio 50/50 = 1.0
   - Done processing method <Benchmark: int conditionalConstantFoldingOriginal(int)>
   - Processing method <Benchmark: int conditionalConstantFoldingExpected()>
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
      * Optimization of method <Benchmark: int conditionalConstantFoldingExpected()> SUCCESSFUL
      * Optimization took 239
      * PEG2PEGTIME 237
      * PBTIME 229
      * ENGINETIME 1
   - Done processing method <Benchmark: int conditionalConstantFoldingExpected()>
   - Processing method <Benchmark: int constantFoldOriginal()>
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
      * Optimization of method <Benchmark: int constantFoldOriginal()> SUCCESSFUL
      * Optimization took 255
      * PEG2PEGTIME 248
      * PBTIME 239
      * ENGINETIME 2
   - Done processing method <Benchmark: int constantFoldOriginal()>
   - Processing method <Benchmark: int deadLoopDeletionOriginal()>
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
      * Optimization of method <Benchmark: int deadLoopDeletionOriginal()> SUCCESSFUL
      * Optimization took 273
      * PEG2PEGTIME 269
      * PBTIME 263
      * ENGINETIME 0
   - Done processing method <Benchmark: int deadLoopDeletionOriginal()>
   - Processing method <Benchmark: int functionInliningFoo()>
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
      * Optimization of method <Benchmark: int functionInliningFoo()> SUCCESSFUL
      * Optimization took 252
      * PEG2PEGTIME 248
      * PBTIME 242
      * ENGINETIME 1
   - Done processing method <Benchmark: int functionInliningFoo()>
   - Processing method <Benchmark: int functionInliningOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 12 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 12
GLPKFormulation: Number of values: 11
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int functionInliningOriginal(int)> SUCCESSFUL
      * Optimization took 293
      * PEG2PEGTIME 287
      * PBTIME 274
      * ENGINETIME 7
      * Optimization ratio 2004/2004 = 1.0
      * PEG-based Optimization ratio 2004/2004 = 1.0
   - Done processing method <Benchmark: int functionInliningOriginal(int)>
   - Processing method <Benchmark: int functionInliningExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 8 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 8
GLPKFormulation: Number of values: 7
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int functionInliningExpected(int)> SUCCESSFUL
      * Optimization took 268
      * PEG2PEGTIME 263
      * PBTIME 251
      * ENGINETIME 5
      * Optimization ratio 4/4 = 1.0
      * PEG-based Optimization ratio 4/4 = 1.0
   - Done processing method <Benchmark: int functionInliningExpected(int)>
   - Processing method <Benchmark: int ifTrueOriginal(int)>
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
      * Optimization of method <Benchmark: int ifTrueOriginal(int)> SUCCESSFUL
      * Optimization took 253
      * PEG2PEGTIME 251
      * PBTIME 242
      * ENGINETIME 0
   - Done processing method <Benchmark: int ifTrueOriginal(int)>
   - Processing method <Benchmark: int ifTrueExpected(int)>
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
      * Optimization of method <Benchmark: int ifTrueExpected(int)> SUCCESSFUL
      * Optimization took 324
      * PEG2PEGTIME 320
      * PBTIME 312
      * ENGINETIME 0
   - Done processing method <Benchmark: int ifTrueExpected(int)>
   - Processing method <Benchmark: int infiniteEffectfulLoopOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1000 after 214 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1043
GLPKFormulation: Number of values: 716
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      ! Error processing method <Benchmark: int infiniteEffectfulLoopOriginal()> [
         java.lang.RuntimeException: Bad CFG: Should never happen
         	at eqsat.revert.RevertCFG.removeEmptyFallBlocks(RevertCFG.java:401)
         	at eqsat.revert.RevertCFG.simplify(RevertCFG.java:311)
         	at eqsat.revert.CFGReverter.<init>(CFGReverter.java:38)
         	at peggy.optimize.SingleStageOptimizer.optimize(SingleStageOptimizer.java:104)
         	at peggy.optimize.java.Main.optimizeAll(Main.java:875)
         	at peggy.optimize.java.Main.optimizeClass(Main.java:704)
         	at peggy.optimize.java.Main.main(Main.java:2881)
         
      ]
      * Reverting to original method body
      * Optimization of method <Benchmark: int infiniteEffectfulLoopOriginal()> FAILED
   - Processing method <Benchmark: int infiniteLoopOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1000 after 1170 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 787
GLPKFormulation: Number of values: 134
```

### Optimized
```java
14:30:10.103 INFO  jd.cli.Main - Decompiling optimized/Benchmark.class
import java.io.PrintStream;

public class Benchmark
{
  public static int branchHoistingOriginal(int paramInt)
  {
    int i;
    for (int j = 0;; j = i)
    {
      i = 1 + j;
      if (i >= 500) {
        break;
      }
    }
    j = i + i;
    if (paramInt == 0) {}
    for (paramInt = j;; paramInt = i + j) {
      return paramInt;
    }
  }
  
  public static int branchHoistingExpected(int paramInt)
  {
    for (int i = 0; 500 > i; i = 1 + i) {}
    int j = i + i;
    if (paramInt == 0) {}
    for (paramInt = j;; paramInt = j + i) {
      return paramInt;
    }
  }
  
  public static int conditionalConstantFoldingOriginal(int paramInt)
  {
    if (paramInt == 5)
    {
      paramInt = 4 * paramInt;
      return paramInt;
    }
    if (paramInt == 4) {
      paramInt *= 5;
    }
    for (;;)
    {
      paramInt = paramInt;
      break;
      paramInt = 20;
    }
  }
  
  public static int conditionalConstantFoldingExpected()
  {
    return 20;
  }
  
  public static int constantFoldOriginal()
  {
    return -4;
  }
  
  public static int deadLoopDeletionOriginal()
  {
    return 2;
  }
  
  public static int functionInliningFoo()
  {
    return 1;
  }
  
  public static int functionInliningOriginal(int paramInt)
  {
    return 1 + functionInliningFoo();
  }
  
  public static int functionInliningExpected(int paramInt)
  {
    return paramInt + 2;
  }
  
  public static int ifTrueOriginal(int paramInt)
  {
    return paramInt;
  }
  
  public static int ifTrueExpected(int paramInt)
  {
    return paramInt;
  }
  
  public static int infiniteEffectfulLoopOriginal()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      System.out.println(i);
    }
    return 2;
  }
  
  public static int infiniteLoopOriginal()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      i++;
    }
    return i;
  }
  
  public static int loopBasedCodeMotionOriginal()
  {
    for (int i = 0; 3 > i; i = 1 + i) {}
    return 50 * i;
  }
  
  public static int loopBasedCodeMotionExpected()
  {
    int i = 0;
    while (150 > i) {
      i += 50;
    }
    return i;
  }
  
  public static void loopInvariantCodeMotionOriginal(int paramInt1, int paramInt2)
  {
    paramInt1 = 20 * paramInt1;
    if (paramInt2 > paramInt1) {}
    for (paramInt2 = 1 + paramInt1;; paramInt2 = paramInt1)
    {
      int i = 0;
      for (paramInt1 = 0; i < 20; paramInt1 = paramInt2 + paramInt1)
      {
        i = 1 + i;
        System.out.println(paramInt1);
      }
      return;
    }
  }
  
  public static void loopInvariantCodeMotionExpected(int paramInt1, int paramInt2)
  {
    int i = paramInt1 * 20;
    if (paramInt2 > i) {}
    for (paramInt2 = i + 1;; paramInt2 = i)
    {
      i = 0;
      for (paramInt1 = 0; 20 > i; paramInt1 = paramInt2 + paramInt1)
      {
        System.out.println(paramInt1);
        i += 1;
      }
      return;
    }
  }
  
  public static int loopPeelingOriginal(int paramInt)
  {
    int i = 0;
    for (int j = 0; i < paramInt; j = 5 + j) {
      i += 1;
    }
    return j;
  }
  
  public static int loopPeelingExpected(int paramInt)
  {
    if (paramInt <= 0) {}
    int i;
    for (paramInt = 0;; paramInt = i)
    {
      return paramInt;
      i = 5;
      int j = 1;
      while (j < paramInt)
      {
        i = 5 + i;
        j += 1;
      }
    }
  }
  
  public static void loopStrengthReductionOriginal()
  {
    for (int i = 0; i < 300; i = 1 + i)
    {
      int tmp15_14 = (i + i);
      System.out.println(tmp15_14 + tmp15_14 + i);
    }
  }
  
  public static void loopStrengthReductionExpected()
  {
    int j = 0;
    for (int i = 0; 300 > j; i = 5 + i)
    {
      System.out.println(i);
      j = 1 + j;
    }
  }
  
  public static void loopStrengthReductionModifiedOriginal()
  {
    int j = 0;
    int i = 0;
    if (j < 300)
    {
      if (j == 150) {}
      for (int k = 4 + i;; k = 1 + i)
      {
        j += 1;
        System.out.println(50 * i);
        i = k;
        break;
      }
    }
  }
  
  public static void loopStrengthReductionModifiedExpected()
  {
    int j = 0;
    int k = 0;
    if (300 > j)
    {
      if (j == 150) {}
      for (int i = k + 200;; i = 50 + k)
      {
        j += 1;
        k = i;
        System.out.println(k);
        break;
      }
    }
  }
  
  public static int loopUnrollOriginal(int paramInt)
  {
    paramInt = 0;
    while (paramInt < 1) {
      paramInt += 1;
    }
    return paramInt;
  }
  
  public static int loopUnrollExpected(int paramInt)
  {
    return 1;
  }
  
  public static int simpleLoopUnswitchOriginal(int paramInt)
  {
    int i = 0;
    int j = 0;
    if (paramInt < 0) {
      while (paramInt > j)
      {
        System.out.println(j);
        j += 1;
        i = 1 + 2;
      }
    }
    while (paramInt > j)
    {
      System.out.println(j);
      i = 1 + i;
      j += 1;
    }
    return i;
  }
}

/* Location:
 * Qualified Name:     Benchmark
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
## Run 

{'axioms': 'peggy/axioms/java_arithmetic_axioms.xml:peggy/axioms/java_operator_axioms.xml:peggy/axioms/java_operator_costs.xml:peggy/axioms/java_util_axioms.xml', 'optimization_level': 'O2', 'tmpFolder': 'tmp', 'pb': 'glpk', 'eto': '1000'}

### Peggy output
```
+ Successfully added axiom file: peggy/axioms/java_operator_axioms.xml
+ Successfully added axiom file: peggy/axioms/java_util_axioms.xml
+ Successfully added axiom file: peggy/axioms/java_operator_costs.xml
+ Successfully added axiom file: peggy/axioms/java_arithmetic_axioms.xml
+ Loading class file Benchmark
+ Optimizing class Benchmark
   - Processing method <Benchmark: void <init>()>
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
      * Optimization of method <Benchmark: void <init>()> SUCCESSFUL
      * Optimization took 497
      * PEG2PEGTIME 402
      * PBTIME 293
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <Benchmark: void <init>()>
   - Processing method <Benchmark: int branchHoistingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1000 after 2239 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1876
GLPKFormulation: Number of values: 632
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingOriginal(int)> SUCCESSFUL
      * Optimization took 22564
      * PEG2PEGTIME 22516
      * PBTIME 20254
      * ENGINETIME 2240
      * Optimization ratio 375/695 = 0.539568345323741
      * PEG-based Optimization ratio 375/695 = 0.539568345323741
   - Done processing method <Benchmark: int branchHoistingOriginal(int)>
   - Processing method <Benchmark: int branchHoistingExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1000 after 615 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1712
GLPKFormulation: Number of values: 730
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingExpected(int)> SUCCESSFUL
      * Optimization took 18599
      * PEG2PEGTIME 18581
      * PBTIME 17948
      * ENGINETIME 615
      * Optimization ratio 203/235 = 0.8638297872340426
      * PEG-based Optimization ratio 203/235 = 0.8638297872340426
   - Done processing method <Benchmark: int branchHoistingExpected(int)>
   - Processing method <Benchmark: int conditionalConstantFoldingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 16 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 16
GLPKFormulation: Number of values: 14
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int conditionalConstantFoldingOriginal(int)> SUCCESSFUL
      * Optimization took 326
      * PEG2PEGTIME 317
      * PBTIME 301
      * ENGINETIME 6
      * Optimization ratio 50/50 = 1.0
      * PEG-based Optimization ratio 50/50 = 1.0
   - Done processing method <Benchmark: int conditionalConstantFoldingOriginal(int)>
   - Processing method <Benchmark: int conditionalConstantFoldingExpected()>
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
      * Optimization of method <Benchmark: int conditionalConstantFoldingExpected()> SUCCESSFUL
      * Optimization took 256
      * PEG2PEGTIME 254
      * PBTIME 246
      * ENGINETIME 1
   - Done processing method <Benchmark: int conditionalConstantFoldingExpected()>
   - Processing method <Benchmark: int constantFoldOriginal()>
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
      * Optimization of method <Benchmark: int constantFoldOriginal()> SUCCESSFUL
      * Optimization took 251
      * PEG2PEGTIME 249
      * PBTIME 239
      * ENGINETIME 1
   - Done processing method <Benchmark: int constantFoldOriginal()>
   - Processing method <Benchmark: int deadLoopDeletionOriginal()>
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
      * Optimization of method <Benchmark: int deadLoopDeletionOriginal()> SUCCESSFUL
      * Optimization took 251
      * PEG2PEGTIME 242
      * PBTIME 235
      * ENGINETIME 0
   - Done processing method <Benchmark: int deadLoopDeletionOriginal()>
   - Processing method <Benchmark: int functionInliningFoo()>
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
      * Optimization of method <Benchmark: int functionInliningFoo()> SUCCESSFUL
      * Optimization took 257
      * PEG2PEGTIME 256
      * PBTIME 244
      * ENGINETIME 0
   - Done processing method <Benchmark: int functionInliningFoo()>
   - Processing method <Benchmark: int functionInliningOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 12 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 12
GLPKFormulation: Number of values: 11
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int functionInliningOriginal(int)> SUCCESSFUL
      * Optimization took 387
      * PEG2PEGTIME 370
      * PBTIME 363
      * ENGINETIME 2
      * Optimization ratio 2004/2004 = 1.0
      * PEG-based Optimization ratio 2004/2004 = 1.0
   - Done processing method <Benchmark: int functionInliningOriginal(int)>
   - Processing method <Benchmark: int functionInliningExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 8 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 8
GLPKFormulation: Number of values: 7
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int functionInliningExpected(int)> SUCCESSFUL
      * Optimization took 275
      * PEG2PEGTIME 269
      * PBTIME 258
      * ENGINETIME 2
      * Optimization ratio 4/4 = 1.0
      * PEG-based Optimization ratio 4/4 = 1.0
   - Done processing method <Benchmark: int functionInliningExpected(int)>
   - Processing method <Benchmark: int ifTrueOriginal(int)>
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
      * Optimization of method <Benchmark: int ifTrueOriginal(int)> SUCCESSFUL
      * Optimization took 441
      * PEG2PEGTIME 433
      * PBTIME 424
      * ENGINETIME 1
   - Done processing method <Benchmark: int ifTrueOriginal(int)>
   - Processing method <Benchmark: int ifTrueExpected(int)>
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
      * Optimization of method <Benchmark: int ifTrueExpected(int)> SUCCESSFUL
      * Optimization took 266
      * PEG2PEGTIME 261
      * PBTIME 249
      * ENGINETIME 1
   - Done processing method <Benchmark: int ifTrueExpected(int)>
   - Processing method <Benchmark: int infiniteEffectfulLoopOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1000 after 233 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1043
GLPKFormulation: Number of values: 716
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      ! Error processing method <Benchmark: int infiniteEffectfulLoopOriginal()> [
         java.lang.RuntimeException: Bad CFG: Should never happen
         	at eqsat.revert.RevertCFG.removeEmptyFallBlocks(RevertCFG.java:401)
         	at eqsat.revert.RevertCFG.simplify(RevertCFG.java:311)
         	at eqsat.revert.CFGReverter.<init>(CFGReverter.java:38)
         	at peggy.optimize.SingleStageOptimizer.optimize(SingleStageOptimizer.java:104)
         	at peggy.optimize.java.Main.optimizeAll(Main.java:875)
         	at peggy.optimize.java.Main.optimizeClass(Main.java:704)
         	at peggy.optimize.java.Main.main(Main.java:2881)
         
      ]
      * Reverting to original method body
      * Optimization of method <Benchmark: int infiniteEffectfulLoopOriginal()> FAILED
   - Processing method <Benchmark: int infiniteLoopOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1000 after 1059 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 787
GLPKFormulation: Number of values: 134
```

### Optimized
```java
14:44:24.770 INFO  jd.cli.Main - Decompiling optimized/Benchmark.class
import java.io.PrintStream;

public class Benchmark
{
  public static int branchHoistingOriginal(int paramInt)
  {
    int i;
    for (int j = 0;; j = i)
    {
      i = j + 1;
      if (i >= 500) {
        break;
      }
    }
    j = i + i;
    if (paramInt == 0) {
      i = j;
    }
    for (;;)
    {
      return i;
      i += j;
    }
  }
  
  public static int branchHoistingExpected(int paramInt)
  {
    for (int j = 0; 500 > j; j = 1 + j) {}
    int i = j + j;
    if (paramInt == 0) {}
    for (paramInt = i;; paramInt = j + i) {
      return paramInt;
    }
  }
  
  public static int conditionalConstantFoldingOriginal(int paramInt)
  {
    if (paramInt == 5)
    {
      paramInt *= 4;
      return paramInt;
    }
    if (paramInt == 4) {
      paramInt *= 5;
    }
    for (;;)
    {
      paramInt = paramInt;
      break;
      paramInt = 20;
    }
  }
  
  public static int conditionalConstantFoldingExpected()
  {
    return 20;
  }
  
  public static int constantFoldOriginal()
  {
    return -4;
  }
  
  public static int deadLoopDeletionOriginal()
  {
    return 2;
  }
  
  public static int functionInliningFoo()
  {
    return 1;
  }
  
  public static int functionInliningOriginal(int paramInt)
  {
    return functionInliningFoo() + 1;
  }
  
  public static int functionInliningExpected(int paramInt)
  {
    return 2 + paramInt;
  }
  
  public static int ifTrueOriginal(int paramInt)
  {
    return paramInt;
  }
  
  public static int ifTrueExpected(int paramInt)
  {
    return paramInt;
  }
  
  public static int infiniteEffectfulLoopOriginal()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      System.out.println(i);
    }
    return 2;
  }
  
  public static int infiniteLoopOriginal()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      i++;
    }
    return i;
  }
  
  public static int loopBasedCodeMotionOriginal()
  {
    int i = 0;
    while (i < 3) {
      i += 1;
    }
    return 50 * i;
  }
  
  public static int loopBasedCodeMotionExpected()
  {
    for (int i = 0; i < 150; i = 50 + i) {}
    return i;
  }
  
  public static void loopInvariantCodeMotionOriginal(int paramInt1, int paramInt2)
  {
    int i = paramInt1 * 20;
    if (paramInt2 > i) {
      i += 1;
    }
    for (;;)
    {
      paramInt2 = 0;
      paramInt1 = 0;
      while (20 > paramInt2)
      {
        paramInt2 = 1 + paramInt2;
        System.out.println(paramInt1);
        paramInt1 += i;
      }
      return;
      i = i;
    }
  }
  
  public static void loopInvariantCodeMotionExpected(int paramInt1, int paramInt2)
  {
    paramInt1 = 20 * paramInt1;
    if (paramInt2 > paramInt1) {}
    for (paramInt2 = paramInt1 + 1;; paramInt2 = paramInt1)
    {
      paramInt1 = 0;
      int i = 0;
      while (i < 20)
      {
        System.out.println(paramInt1);
        paramInt1 += paramInt2;
        i += 1;
      }
      return;
    }
  }
  
  public static int loopPeelingOriginal(int paramInt)
  {
    int j = 0;
    int i = 0;
    while (paramInt > i)
    {
      j += 5;
      i += 1;
    }
    return j;
  }
  
  public static int loopPeelingExpected(int paramInt)
  {
    if (paramInt <= 0) {}
    int j;
    for (paramInt = 0;; paramInt = j)
    {
      return paramInt;
      int i = 1;
      j = 5;
      while (i < paramInt)
      {
        i = 1 + i;
        j += 5;
      }
    }
  }
  
  public static void loopStrengthReductionOriginal()
  {
    int i = 0;
    while (i < 300)
    {
      int j = i;
      i = 1 + i;
      int tmp22_21 = (j + j);
      System.out.println(j + (tmp22_21 + tmp22_21));
    }
  }
  
  public static void loopStrengthReductionExpected()
  {
    int i = 0;
    int j = 0;
    while (300 > j)
    {
      i += 5;
      j = 1 + j;
      System.out.println(i);
    }
  }
  
  public static void loopStrengthReductionModifiedOriginal()
  {
    int i = 0;
    int j = 0;
    if (300 > j)
    {
      int k = 1 + i;
      if (j == 150) {}
      for (k = 3 + k;; k = k)
      {
        System.out.println(50 * i);
        i = k;
        j = 1 + j;
        break;
      }
    }
  }
  
  public static void loopStrengthReductionModifiedExpected()
  {
    int k = 0;
    int j = 0;
    if (j < 300)
    {
      int i = 50 + k;
      if (j == 150) {}
      for (i = 150 + i;; i = i)
      {
        k = i;
        j = 1 + j;
        System.out.println(k);
        break;
      }
    }
  }
  
  public static int loopUnrollOriginal(int paramInt)
  {
    paramInt = 0;
    while (1 > paramInt) {
      paramInt += 1;
    }
    return paramInt;
  }
  
  public static int loopUnrollExpected(int paramInt)
  {
    return 1;
  }
  
  public static int simpleLoopUnswitchOriginal(int paramInt)
  {
    int i = 0;
    int j = 0;
    if (paramInt < 0) {
      while (paramInt > i)
      {
        System.out.println(i);
        i += 1;
        j = 2 + 1;
      }
    }
    while (paramInt > i)
    {
      System.out.println(i);
      i += 1;
      j += 1;
    }
    return j;
  }
}

/* Location:
 * Qualified Name:     Benchmark
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
## Run 

{'axioms': 'peggy/axioms/java_arithmetic_axioms.xml:peggy/axioms/java_operator_axioms.xml:peggy/axioms/java_operator_costs.xml:peggy/axioms/java_util_axioms.xml', 'optimization_level': 'O2', 'tmpFolder': 'tmp', 'pb': 'glpk', 'eto': '1000'}

### Peggy output
```
+ Successfully added axiom file: peggy/axioms/java_operator_axioms.xml
+ Successfully added axiom file: peggy/axioms/java_util_axioms.xml
+ Successfully added axiom file: peggy/axioms/java_operator_costs.xml
+ Successfully added axiom file: peggy/axioms/java_arithmetic_axioms.xml
+ Loading class file Benchmark
+ Optimizing class Benchmark
   - Processing method <Benchmark: void <init>()>
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
      * Optimization of method <Benchmark: void <init>()> SUCCESSFUL
      * Optimization took 459
      * PEG2PEGTIME 357
      * PBTIME 276
      * ENGINETIME 5
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <Benchmark: void <init>()>
   - Processing method <Benchmark: int branchHoistingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1000 after 1368 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1897
GLPKFormulation: Number of values: 621
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingOriginal(int)> SUCCESSFUL
      * Optimization took 20260
      * PEG2PEGTIME 20213
      * PBTIME 18808
      * ENGINETIME 1368
      * Optimization ratio 375/695 = 0.539568345323741
      * PEG-based Optimization ratio 375/695 = 0.539568345323741
   - Done processing method <Benchmark: int branchHoistingOriginal(int)>
   - Processing method <Benchmark: int branchHoistingExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1000 after 658 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1685
GLPKFormulation: Number of values: 722
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingExpected(int)> SUCCESSFUL
      * Optimization took 19040
      * PEG2PEGTIME 19020
      * PBTIME 18345
      * ENGINETIME 659
      * Optimization ratio 203/235 = 0.8638297872340426
      * PEG-based Optimization ratio 203/235 = 0.8638297872340426
   - Done processing method <Benchmark: int branchHoistingExpected(int)>
   - Processing method <Benchmark: int conditionalConstantFoldingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 16 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 16
GLPKFormulation: Number of values: 14
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int conditionalConstantFoldingOriginal(int)> SUCCESSFUL
      * Optimization took 328
      * PEG2PEGTIME 319
      * PBTIME 307
      * ENGINETIME 5
      * Optimization ratio 50/50 = 1.0
      * PEG-based Optimization ratio 50/50 = 1.0
   - Done processing method <Benchmark: int conditionalConstantFoldingOriginal(int)>
   - Processing method <Benchmark: int conditionalConstantFoldingExpected()>
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
      * Optimization of method <Benchmark: int conditionalConstantFoldingExpected()> SUCCESSFUL
      * Optimization took 251
      * PEG2PEGTIME 250
      * PBTIME 242
      * ENGINETIME 1
   - Done processing method <Benchmark: int conditionalConstantFoldingExpected()>
   - Processing method <Benchmark: int constantFoldOriginal()>
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
      * Optimization of method <Benchmark: int constantFoldOriginal()> SUCCESSFUL
      * Optimization took 264
      * PEG2PEGTIME 260
      * PBTIME 250
      * ENGINETIME 2
   - Done processing method <Benchmark: int constantFoldOriginal()>
   - Processing method <Benchmark: int deadLoopDeletionOriginal()>
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
      * Optimization of method <Benchmark: int deadLoopDeletionOriginal()> SUCCESSFUL
      * Optimization took 246
      * PEG2PEGTIME 242
      * PBTIME 237
      * ENGINETIME 0
   - Done processing method <Benchmark: int deadLoopDeletionOriginal()>
   - Processing method <Benchmark: int functionInliningFoo()>
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
      * Optimization of method <Benchmark: int functionInliningFoo()> SUCCESSFUL
      * Optimization took 239
      * PEG2PEGTIME 235
      * PBTIME 231
      * ENGINETIME 0
   - Done processing method <Benchmark: int functionInliningFoo()>
   - Processing method <Benchmark: int functionInliningOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 12 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 12
GLPKFormulation: Number of values: 11
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int functionInliningOriginal(int)> SUCCESSFUL
      * Optimization took 269
      * PEG2PEGTIME 265
      * PBTIME 258
      * ENGINETIME 2
      * Optimization ratio 2004/2004 = 1.0
      * PEG-based Optimization ratio 2004/2004 = 1.0
   - Done processing method <Benchmark: int functionInliningOriginal(int)>
   - Processing method <Benchmark: int functionInliningExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 8 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 8
GLPKFormulation: Number of values: 7
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int functionInliningExpected(int)> SUCCESSFUL
      * Optimization took 266
      * PEG2PEGTIME 263
      * PBTIME 256
      * ENGINETIME 1
      * Optimization ratio 4/4 = 1.0
      * PEG-based Optimization ratio 4/4 = 1.0
   - Done processing method <Benchmark: int functionInliningExpected(int)>
   - Processing method <Benchmark: int ifTrueOriginal(int)>
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
      * Optimization of method <Benchmark: int ifTrueOriginal(int)> SUCCESSFUL
      * Optimization took 325
      * PEG2PEGTIME 323
      * PBTIME 317
      * ENGINETIME 0
   - Done processing method <Benchmark: int ifTrueOriginal(int)>
   - Processing method <Benchmark: int ifTrueExpected(int)>
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
      * Optimization of method <Benchmark: int ifTrueExpected(int)> SUCCESSFUL
      * Optimization took 257
      * PEG2PEGTIME 248
      * PBTIME 236
      * ENGINETIME 0
   - Done processing method <Benchmark: int ifTrueExpected(int)>
   - Processing method <Benchmark: int infiniteEffectfulLoopOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1000 after 152 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1043
GLPKFormulation: Number of values: 716
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      ! Error processing method <Benchmark: int infiniteEffectfulLoopOriginal()> [
         java.lang.RuntimeException: Bad CFG: Should never happen
         	at eqsat.revert.RevertCFG.removeEmptyFallBlocks(RevertCFG.java:401)
         	at eqsat.revert.RevertCFG.simplify(RevertCFG.java:311)
         	at eqsat.revert.CFGReverter.<init>(CFGReverter.java:38)
         	at peggy.optimize.SingleStageOptimizer.optimize(SingleStageOptimizer.java:104)
         	at peggy.optimize.java.Main.optimizeAll(Main.java:875)
         	at peggy.optimize.java.Main.optimizeClass(Main.java:704)
         	at peggy.optimize.java.Main.main(Main.java:2881)
         
      ]
      * Reverting to original method body
      * Optimization of method <Benchmark: int infiniteEffectfulLoopOriginal()> FAILED
   - Processing method <Benchmark: int infiniteLoopOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1000 after 958 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 787
GLPKFormulation: Number of values: 134
```

### Optimized
```java
14:54:29.500 INFO  jd.cli.Main - Decompiling optimized/Benchmark.class
import java.io.PrintStream;

public class Benchmark
{
  public static int branchHoistingOriginal(int paramInt)
  {
    int i;
    for (int j = 0;; j = i)
    {
      i = j + 1;
      if (i >= 500) {
        break;
      }
    }
    j = i + i;
    if (paramInt == 0) {
      i = j;
    }
    for (;;)
    {
      return i;
      i += j;
    }
  }
  
  public static int branchHoistingExpected(int paramInt)
  {
    for (int j = 0; 500 > j; j = 1 + j) {}
    int i = j + j;
    if (paramInt == 0) {}
    for (paramInt = i;; paramInt = j + i) {
      return paramInt;
    }
  }
  
  public static int conditionalConstantFoldingOriginal(int paramInt)
  {
    if (paramInt == 5)
    {
      paramInt *= 4;
      return paramInt;
    }
    if (paramInt == 4) {
      paramInt *= 5;
    }
    for (;;)
    {
      paramInt = paramInt;
      break;
      paramInt = 20;
    }
  }
  
  public static int conditionalConstantFoldingExpected()
  {
    return 20;
  }
  
  public static int constantFoldOriginal()
  {
    return -4;
  }
  
  public static int deadLoopDeletionOriginal()
  {
    return 2;
  }
  
  public static int functionInliningFoo()
  {
    return 1;
  }
  
  public static int functionInliningOriginal(int paramInt)
  {
    return functionInliningFoo() + 1;
  }
  
  public static int functionInliningExpected(int paramInt)
  {
    return paramInt + 2;
  }
  
  public static int ifTrueOriginal(int paramInt)
  {
    return paramInt;
  }
  
  public static int ifTrueExpected(int paramInt)
  {
    return paramInt;
  }
  
  public static int infiniteEffectfulLoopOriginal()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      System.out.println(i);
    }
    return 2;
  }
  
  public static int infiniteLoopOriginal()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      i++;
    }
    return i;
  }
  
  public static int loopBasedCodeMotionOriginal()
  {
    int i = 0;
    while (3 > i) {
      i += 1;
    }
    return i * 50;
  }
  
  public static int loopBasedCodeMotionExpected()
  {
    for (int i = 0; 150 > i; i = 50 + i) {}
    return i;
  }
  
  public static void loopInvariantCodeMotionOriginal(int paramInt1, int paramInt2)
  {
    int i = paramInt1 * 20;
    if (paramInt2 > i) {}
    for (paramInt1 = i + 1;; paramInt1 = i)
    {
      i = 0;
      paramInt2 = 0;
      while (i < 20)
      {
        i += 1;
        paramInt2 = paramInt1 + paramInt2;
        System.out.println(paramInt2);
      }
      return;
    }
  }
  
  public static void loopInvariantCodeMotionExpected(int paramInt1, int paramInt2)
  {
    paramInt1 = 20 * paramInt1;
    if (paramInt1 < paramInt2) {}
    for (paramInt1 = 1 + paramInt1;; paramInt1 = paramInt1)
    {
      int i = 0;
      paramInt2 = 0;
      while (i < 20)
      {
        i += 1;
        System.out.println(paramInt2);
        paramInt2 += paramInt1;
      }
      return;
    }
  }
  
  public static int loopPeelingOriginal(int paramInt)
  {
    int i = 0;
    int j = 0;
    while (paramInt > j)
    {
      i = 5 + i;
      j += 1;
    }
    return i;
  }
  
  public static int loopPeelingExpected(int paramInt)
  {
    if (paramInt <= 0) {}
    int j;
    for (int i = 0;; i = j)
    {
      return i;
      j = 5;
      i = 1;
      while (paramInt > i)
      {
        j = 5 + j;
        i += 1;
      }
    }
  }
  
  public static void loopStrengthReductionOriginal()
  {
    int i = 0;
    while (i < 300)
    {
      int j = i;
      i = 1 + i;
      int tmp22_21 = (j + j);
      System.out.println(j + (tmp22_21 + tmp22_21));
    }
  }
  
  public static void loopStrengthReductionExpected()
  {
    int j = 0;
    int i = 0;
    while (j < 300)
    {
      j += 1;
      i = 5 + i;
      System.out.println(i);
    }
  }
  
  public static void loopStrengthReductionModifiedOriginal()
  {
    int j = 0;
    int i = 0;
    if (j < 300)
    {
      int k = i + 1;
      if (j == 150) {
        k += 3;
      }
      for (;;)
      {
        j += 1;
        System.out.println(i * 50);
        i = k;
        break;
        k = k;
      }
    }
  }
  
  public static void loopStrengthReductionModifiedExpected()
  {
    int k = 0;
    int j = 0;
    if (j < 300)
    {
      int i = 50 + k;
      if (j == 150) {}
      for (i = 150 + i;; i = i)
      {
        k = i;
        System.out.println(k);
        j = 1 + j;
        break;
      }
    }
  }
  
  public static int loopUnrollOriginal(int paramInt)
  {
    for (paramInt = 0; 1 > paramInt; paramInt = 1 + paramInt) {}
    return paramInt;
  }
  
  public static int loopUnrollExpected(int paramInt)
  {
    return 1;
  }
  
  public static int simpleLoopUnswitchOriginal(int paramInt)
  {
    int j = 0;
    int i = 0;
    if (paramInt < 0)
    {
      while (j < paramInt)
      {
        j = 1 + j;
        System.out.println(j);
        i = 2 + 1;
      }
      return i;
    }
    for (;;)
    {
      if (j < paramInt)
      {
        j = 1 + j;
        System.out.println(j);
      }
    }
  }
}

/* Location:
 * Qualified Name:     Benchmark
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
## Run 

{'axioms': 'peggy/axioms/java_arithmetic_axioms.xml:peggy/axioms/java_operator_axioms.xml:peggy/axioms/java_operator_costs.xml:peggy/axioms/java_util_axioms.xml', 'optimization_level': 'O2', 'tmpFolder': 'tmp', 'pb': 'glpk', 'eto': '1000'}

### Peggy output
```
+ Successfully added axiom file: peggy/axioms/java_operator_axioms.xml
+ Successfully added axiom file: peggy/axioms/java_util_axioms.xml
+ Successfully added axiom file: peggy/axioms/java_operator_costs.xml
+ Successfully added axiom file: peggy/axioms/java_arithmetic_axioms.xml
+ Loading class file Benchmark
+ Optimizing class Benchmark
   - Processing method <Benchmark: void <init>()>
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
      * Optimization of method <Benchmark: void <init>()> SUCCESSFUL
      * Optimization took 454
      * PEG2PEGTIME 372
      * PBTIME 284
      * ENGINETIME 4
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <Benchmark: void <init>()>
   - Processing method <Benchmark: int branchHoistingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1000 after 1279 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1755
GLPKFormulation: Number of values: 614
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingOriginal(int)> SUCCESSFUL
      * Optimization took 18839
      * PEG2PEGTIME 18790
      * PBTIME 17484
      * ENGINETIME 1279
      * Optimization ratio 375/695 = 0.539568345323741
      * PEG-based Optimization ratio 375/695 = 0.539568345323741
   - Done processing method <Benchmark: int branchHoistingOriginal(int)>
   - Processing method <Benchmark: int branchHoistingExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1000 after 696 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1750
GLPKFormulation: Number of values: 746
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingExpected(int)> SUCCESSFUL
      * Optimization took 20576
      * PEG2PEGTIME 20561
      * PBTIME 19844
      * ENGINETIME 696
      * Optimization ratio 203/235 = 0.8638297872340426
      * PEG-based Optimization ratio 203/235 = 0.8638297872340426
   - Done processing method <Benchmark: int branchHoistingExpected(int)>
   - Processing method <Benchmark: int conditionalConstantFoldingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 16 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 16
GLPKFormulation: Number of values: 14
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int conditionalConstantFoldingOriginal(int)> SUCCESSFUL
      * Optimization took 358
      * PEG2PEGTIME 346
      * PBTIME 320
      * ENGINETIME 5
      * Optimization ratio 50/50 = 1.0
      * PEG-based Optimization ratio 50/50 = 1.0
   - Done processing method <Benchmark: int conditionalConstantFoldingOriginal(int)>
   - Processing method <Benchmark: int conditionalConstantFoldingExpected()>
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
      * Optimization of method <Benchmark: int conditionalConstantFoldingExpected()> SUCCESSFUL
      * Optimization took 239
      * PEG2PEGTIME 237
      * PBTIME 229
      * ENGINETIME 0
   - Done processing method <Benchmark: int conditionalConstantFoldingExpected()>
   - Processing method <Benchmark: int constantFoldOriginal()>
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
      * Optimization of method <Benchmark: int constantFoldOriginal()> SUCCESSFUL
      * Optimization took 243
      * PEG2PEGTIME 241
      * PBTIME 233
      * ENGINETIME 1
   - Done processing method <Benchmark: int constantFoldOriginal()>
   - Processing method <Benchmark: int deadLoopDeletionOriginal()>
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
      * Optimization of method <Benchmark: int deadLoopDeletionOriginal()> SUCCESSFUL
      * Optimization took 236
      * PEG2PEGTIME 231
      * PBTIME 222
      * ENGINETIME 1
   - Done processing method <Benchmark: int deadLoopDeletionOriginal()>
   - Processing method <Benchmark: int functionInliningFoo()>
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
      * Optimization of method <Benchmark: int functionInliningFoo()> SUCCESSFUL
      * Optimization took 239
      * PEG2PEGTIME 237
      * PBTIME 230
      * ENGINETIME 1
   - Done processing method <Benchmark: int functionInliningFoo()>
   - Processing method <Benchmark: int functionInliningOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 12 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 12
GLPKFormulation: Number of values: 11
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int functionInliningOriginal(int)> SUCCESSFUL
      * Optimization took 281
      * PEG2PEGTIME 276
      * PBTIME 269
      * ENGINETIME 2
      * Optimization ratio 2004/2004 = 1.0
      * PEG-based Optimization ratio 2004/2004 = 1.0
   - Done processing method <Benchmark: int functionInliningOriginal(int)>
   - Processing method <Benchmark: int functionInliningExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 8 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 8
GLPKFormulation: Number of values: 7
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int functionInliningExpected(int)> SUCCESSFUL
      * Optimization took 247
      * PEG2PEGTIME 244
      * PBTIME 235
      * ENGINETIME 1
      * Optimization ratio 4/4 = 1.0
      * PEG-based Optimization ratio 4/4 = 1.0
   - Done processing method <Benchmark: int functionInliningExpected(int)>
   - Processing method <Benchmark: int ifTrueOriginal(int)>
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
      * Optimization of method <Benchmark: int ifTrueOriginal(int)> SUCCESSFUL
      * Optimization took 237
      * PEG2PEGTIME 236
      * PBTIME 229
      * ENGINETIME 0
   - Done processing method <Benchmark: int ifTrueOriginal(int)>
   - Processing method <Benchmark: int ifTrueExpected(int)>
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
      * Optimization of method <Benchmark: int ifTrueExpected(int)> SUCCESSFUL
      * Optimization took 295
      * PEG2PEGTIME 290
      * PBTIME 283
      * ENGINETIME 1
   - Done processing method <Benchmark: int ifTrueExpected(int)>
   - Processing method <Benchmark: int infiniteEffectfulLoopOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1000 after 158 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1043
GLPKFormulation: Number of values: 716
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      ! Error processing method <Benchmark: int infiniteEffectfulLoopOriginal()> [
         java.lang.RuntimeException: Bad CFG: Should never happen
         	at eqsat.revert.RevertCFG.removeEmptyFallBlocks(RevertCFG.java:401)
         	at eqsat.revert.RevertCFG.simplify(RevertCFG.java:311)
         	at eqsat.revert.CFGReverter.<init>(CFGReverter.java:38)
         	at peggy.optimize.SingleStageOptimizer.optimize(SingleStageOptimizer.java:104)
         	at peggy.optimize.java.Main.optimizeAll(Main.java:875)
         	at peggy.optimize.java.Main.optimizeClass(Main.java:704)
         	at peggy.optimize.java.Main.main(Main.java:2881)
         
      ]
      * Reverting to original method body
      * Optimization of method <Benchmark: int infiniteEffectfulLoopOriginal()> FAILED
   - Processing method <Benchmark: int infiniteLoopOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1000 after 826 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 787
GLPKFormulation: Number of values: 134
```

### Optimized
```java
15:00:45.294 INFO  jd.cli.Main - Decompiling optimized/Benchmark.class
import java.io.PrintStream;

public class Benchmark
{
  public static int branchHoistingOriginal(int paramInt)
  {
    int j;
    for (int i = 0;; i = j)
    {
      j = 1 + i;
      if (j >= 500) {
        break;
      }
    }
    i = j + j;
    if (paramInt == 0) {
      i = i;
    }
    for (;;)
    {
      return i;
      i += j;
    }
  }
  
  public static int branchHoistingExpected(int paramInt)
  {
    int j = 0;
    while (j < 500) {
      j += 1;
    }
    int i = j + j;
    if (paramInt == 0) {}
    for (paramInt = i;; paramInt = j + i) {
      return paramInt;
    }
  }
  
  public static int conditionalConstantFoldingOriginal(int paramInt)
  {
    if (paramInt == 5)
    {
      paramInt = 4 * paramInt;
      return paramInt;
    }
    if (paramInt == 4) {}
    for (paramInt = 5 * paramInt;; paramInt = 20)
    {
      paramInt = paramInt;
      break;
    }
  }
  
  public static int conditionalConstantFoldingExpected()
  {
    return 20;
  }
  
  public static int constantFoldOriginal()
  {
    return -4;
  }
  
  public static int deadLoopDeletionOriginal()
  {
    return 2;
  }
  
  public static int functionInliningFoo()
  {
    return 1;
  }
  
  public static int functionInliningOriginal(int paramInt)
  {
    return 1 + functionInliningFoo();
  }
  
  public static int functionInliningExpected(int paramInt)
  {
    return 2 + paramInt;
  }
  
  public static int ifTrueOriginal(int paramInt)
  {
    return paramInt;
  }
  
  public static int ifTrueExpected(int paramInt)
  {
    return paramInt;
  }
  
  public static int infiniteEffectfulLoopOriginal()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      System.out.println(i);
    }
    return 2;
  }
  
  public static int infiniteLoopOriginal()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      i++;
    }
    return i;
  }
  
  public static int loopBasedCodeMotionOriginal()
  {
    int i = 0;
    while (i < 3) {
      i += 1;
    }
    return 50 * i;
  }
  
  public static int loopBasedCodeMotionExpected()
  {
    for (int i = 0; 150 > i; i = 50 + i) {}
    return i;
  }
  
  public static void loopInvariantCodeMotionOriginal(int paramInt1, int paramInt2)
  {
    paramInt1 = 20 * paramInt1;
    if (paramInt2 > paramInt1) {}
    for (paramInt2 = 1 + paramInt1;; paramInt2 = paramInt1)
    {
      int i = 0;
      paramInt1 = 0;
      while (20 > i)
      {
        i += 1;
        paramInt1 = paramInt2 + paramInt1;
        System.out.println(paramInt1);
      }
      return;
    }
  }
  
  public static void loopInvariantCodeMotionExpected(int paramInt1, int paramInt2)
  {
    int i = 20 * paramInt1;
    if (paramInt2 > i) {}
    for (paramInt2 = i + 1;; paramInt2 = i)
    {
      i = 0;
      for (paramInt1 = 0; 20 > i; paramInt1 = paramInt2 + paramInt1)
      {
        i = 1 + i;
        System.out.println(paramInt1);
      }
      return;
    }
  }
  
  public static int loopPeelingOriginal(int paramInt)
  {
    int i = 0;
    for (int j = 0; j < paramInt; j = 1 + j) {
      i += 5;
    }
    return i;
  }
  
  public static int loopPeelingExpected(int paramInt)
  {
    if (paramInt <= 0) {}
    int i;
    for (paramInt = 0;; paramInt = i)
    {
      return paramInt;
      i = 5;
      for (int j = 1; paramInt > j; j = 1 + j) {
        i = 5 + i;
      }
    }
  }
  
  public static void loopStrengthReductionOriginal()
  {
    int i = 0;
    while (300 > i)
    {
      int j = i;
      i = 1 + i;
      int tmp21_20 = (j + j);
      System.out.println(tmp21_20 + tmp21_20 + j);
    }
  }
  
  public static void loopStrengthReductionExpected()
  {
    int j = 0;
    for (int i = 0; 300 > i; i = 1 + i)
    {
      j += 5;
      System.out.println(j);
    }
  }
  
  public static void loopStrengthReductionModifiedOriginal()
  {
    int j = 0;
    int k = 0;
    if (j < 300)
    {
      int i = k + 1;
      if (j == 150) {}
      for (i = 3 + i;; i = i)
      {
        System.out.println(50 * k);
        j += 1;
        k = i;
        break;
      }
    }
  }
  
  public static void loopStrengthReductionModifiedExpected()
  {
    int j = 0;
    int i = 0;
    if (300 > j)
    {
      if (j == 150) {}
      for (int k = 200 + i;; k = i + 50)
      {
        System.out.println(i);
        j += 1;
        i = k;
        break;
      }
    }
  }
  
  public static int loopUnrollOriginal(int paramInt)
  {
    for (paramInt = 0; paramInt < 1; paramInt = 1 + paramInt) {}
    return paramInt;
  }
  
  public static int loopUnrollExpected(int paramInt)
  {
    return 1;
  }
  
  public static int simpleLoopUnswitchOriginal(int paramInt)
  {
    int j = 0;
    int i = 0;
    if (paramInt < 0) {
      while (i < paramInt)
      {
        System.out.println(i);
        j = 2 + 1;
        i = 1 + i;
      }
    }
    while (i < paramInt)
    {
      System.out.println(i);
      j += 1;
      i = 1 + i;
    }
    return j;
  }
}

/* Location:
 * Qualified Name:     Benchmark
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
## Run 

{'axioms': 'peggy/axioms/java_arithmetic_axioms.xml:peggy/axioms/java_operator_axioms.xml:peggy/axioms/java_operator_costs.xml:peggy/axioms/java_util_axioms.xml', 'optimization_level': 'O2', 'tmpFolder': 'tmp', 'pb': 'glpk', 'eto': '1000'}

### Peggy output
```
+ Successfully added axiom file: peggy/axioms/java_operator_axioms.xml
+ Successfully added axiom file: peggy/axioms/java_util_axioms.xml
+ Successfully added axiom file: peggy/axioms/java_operator_costs.xml
+ Successfully added axiom file: peggy/axioms/java_arithmetic_axioms.xml
+ Loading class file Benchmark
+ Optimizing class Benchmark
   - Processing method <Benchmark: void <init>()>
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
      * Optimization of method <Benchmark: void <init>()> SUCCESSFUL
      * Optimization took 472
      * PEG2PEGTIME 396
      * PBTIME 307
      * ENGINETIME 8
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <Benchmark: void <init>()>
   - Processing method <Benchmark: int branchHoistingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1000 after 1350 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1897
GLPKFormulation: Number of values: 621
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingOriginal(int)> SUCCESSFUL
      * Optimization took 19065
      * PEG2PEGTIME 19020
      * PBTIME 17643
      * ENGINETIME 1350
      * Optimization ratio 375/695 = 0.539568345323741
      * PEG-based Optimization ratio 375/695 = 0.539568345323741
   - Done processing method <Benchmark: int branchHoistingOriginal(int)>
   - Processing method <Benchmark: int branchHoistingExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1000 after 601 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1766
GLPKFormulation: Number of values: 752
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingExpected(int)> SUCCESSFUL
      * Optimization took 19563
      * PEG2PEGTIME 19537
      * PBTIME 18914
      * ENGINETIME 601
      * Optimization ratio 203/235 = 0.8638297872340426
      * PEG-based Optimization ratio 203/235 = 0.8638297872340426
   - Done processing method <Benchmark: int branchHoistingExpected(int)>
   - Processing method <Benchmark: int conditionalConstantFoldingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 16 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 16
GLPKFormulation: Number of values: 14
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int conditionalConstantFoldingOriginal(int)> SUCCESSFUL
      * Optimization took 320
      * PEG2PEGTIME 306
      * PBTIME 292
      * ENGINETIME 4
      * Optimization ratio 50/50 = 1.0
      * PEG-based Optimization ratio 50/50 = 1.0
   - Done processing method <Benchmark: int conditionalConstantFoldingOriginal(int)>
   - Processing method <Benchmark: int conditionalConstantFoldingExpected()>
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
      * Optimization of method <Benchmark: int conditionalConstantFoldingExpected()> SUCCESSFUL
      * Optimization took 247
      * PEG2PEGTIME 246
      * PBTIME 235
      * ENGINETIME 1
   - Done processing method <Benchmark: int conditionalConstantFoldingExpected()>
   - Processing method <Benchmark: int constantFoldOriginal()>
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
      * Optimization of method <Benchmark: int constantFoldOriginal()> SUCCESSFUL
      * Optimization took 253
      * PEG2PEGTIME 252
      * PBTIME 245
      * ENGINETIME 1
   - Done processing method <Benchmark: int constantFoldOriginal()>
   - Processing method <Benchmark: int deadLoopDeletionOriginal()>
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
      * Optimization of method <Benchmark: int deadLoopDeletionOriginal()> SUCCESSFUL
      * Optimization took 241
      * PEG2PEGTIME 239
      * PBTIME 233
      * ENGINETIME 0
   - Done processing method <Benchmark: int deadLoopDeletionOriginal()>
   - Processing method <Benchmark: int functionInliningFoo()>
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
      * Optimization of method <Benchmark: int functionInliningFoo()> SUCCESSFUL
      * Optimization took 244
      * PEG2PEGTIME 242
      * PBTIME 237
      * ENGINETIME 0
   - Done processing method <Benchmark: int functionInliningFoo()>
   - Processing method <Benchmark: int functionInliningOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 12 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 12
GLPKFormulation: Number of values: 11
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int functionInliningOriginal(int)> SUCCESSFUL
      * Optimization took 271
      * PEG2PEGTIME 263
      * PBTIME 255
      * ENGINETIME 2
      * Optimization ratio 2004/2004 = 1.0
      * PEG-based Optimization ratio 2004/2004 = 1.0
   - Done processing method <Benchmark: int functionInliningOriginal(int)>
   - Processing method <Benchmark: int functionInliningExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 8 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 8
GLPKFormulation: Number of values: 7
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int functionInliningExpected(int)> SUCCESSFUL
      * Optimization took 241
      * PEG2PEGTIME 238
      * PBTIME 232
      * ENGINETIME 1
      * Optimization ratio 4/4 = 1.0
      * PEG-based Optimization ratio 4/4 = 1.0
   - Done processing method <Benchmark: int functionInliningExpected(int)>
   - Processing method <Benchmark: int ifTrueOriginal(int)>
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
      * Optimization of method <Benchmark: int ifTrueOriginal(int)> SUCCESSFUL
      * Optimization took 317
      * PEG2PEGTIME 314
      * PBTIME 308
      * ENGINETIME 0
   - Done processing method <Benchmark: int ifTrueOriginal(int)>
   - Processing method <Benchmark: int ifTrueExpected(int)>
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
      * Optimization of method <Benchmark: int ifTrueExpected(int)> SUCCESSFUL
      * Optimization took 239
      * PEG2PEGTIME 236
      * PBTIME 228
      * ENGINETIME 1
   - Done processing method <Benchmark: int ifTrueExpected(int)>
   - Processing method <Benchmark: int infiniteEffectfulLoopOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1000 after 147 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1043
GLPKFormulation: Number of values: 716
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      ! Error processing method <Benchmark: int infiniteEffectfulLoopOriginal()> [
         java.lang.RuntimeException: Bad CFG: Should never happen
         	at eqsat.revert.RevertCFG.removeEmptyFallBlocks(RevertCFG.java:401)
         	at eqsat.revert.RevertCFG.simplify(RevertCFG.java:311)
         	at eqsat.revert.CFGReverter.<init>(CFGReverter.java:38)
         	at peggy.optimize.SingleStageOptimizer.optimize(SingleStageOptimizer.java:104)
         	at peggy.optimize.java.Main.optimizeAll(Main.java:875)
         	at peggy.optimize.java.Main.optimizeClass(Main.java:704)
         	at peggy.optimize.java.Main.main(Main.java:2881)
         
      ]
      * Reverting to original method body
      * Optimization of method <Benchmark: int infiniteEffectfulLoopOriginal()> FAILED
   - Processing method <Benchmark: int infiniteLoopOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1000 after 905 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 787
GLPKFormulation: Number of values: 134
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      ! Error processing method <Benchmark: int infiniteLoopOriginal()> [
         java.lang.RuntimeException: Bad CFG: Should never happen
         	at eqsat.revert.RevertCFG.removeEmptyFallBlocks(RevertCFG.java:401)
         	at eqsat.revert.RevertCFG.simplify(RevertCFG.java:311)
         	at eqsat.revert.CFGReverter.<init>(CFGReverter.java:38)
         	at peggy.optimize.SingleStageOptimizer.optimize(SingleStageOptimizer.java:104)
         	at peggy.optimize.java.Main.optimizeAll(Main.java:875)
         	at peggy.optimize.java.Main.optimizeClass(Main.java:704)
         	at peggy.optimize.java.Main.main(Main.java:2881)
         
      ]
      * Reverting to original method body
      * Optimization of method <Benchmark: int infiniteLoopOriginal()> FAILED
   - Processing method <Benchmark: int loopBasedCodeMotionOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
```

### Optimized
```java
15:10:44.393 INFO  jd.cli.Main - Decompiling optimized/Benchmark.class
import java.io.PrintStream;

public class Benchmark
{
  public static int branchHoistingOriginal(int paramInt)
  {
    int i;
    for (int j = 0;; j = i)
    {
      i = j + 1;
      if (i >= 500) {
        break;
      }
    }
    j = i + i;
    if (paramInt == 0) {
      i = j;
    }
    for (;;)
    {
      return i;
      i += j;
    }
  }
  
  public static int branchHoistingExpected(int paramInt)
  {
    for (int j = 0; 500 > j; j = 1 + j) {}
    int i = j + j;
    if (paramInt == 0) {}
    for (paramInt = i;; paramInt = j + i) {
      return paramInt;
    }
  }
  
  public static int conditionalConstantFoldingOriginal(int paramInt)
  {
    if (paramInt == 5)
    {
      paramInt *= 4;
      return paramInt;
    }
    if (paramInt == 4) {
      paramInt *= 5;
    }
    for (;;)
    {
      paramInt = paramInt;
      break;
      paramInt = 20;
    }
  }
  
  public static int conditionalConstantFoldingExpected()
  {
    return 20;
  }
  
  public static int constantFoldOriginal()
  {
    return -4;
  }
  
  public static int deadLoopDeletionOriginal()
  {
    return 2;
  }
  
  public static int functionInliningFoo()
  {
    return 1;
  }
  
  public static int functionInliningOriginal(int paramInt)
  {
    return 1 + functionInliningFoo();
  }
  
  public static int functionInliningExpected(int paramInt)
  {
    return paramInt + 2;
  }
  
  public static int ifTrueOriginal(int paramInt)
  {
    return paramInt;
  }
  
  public static int ifTrueExpected(int paramInt)
  {
    return paramInt;
  }
  
  public static int infiniteEffectfulLoopOriginal()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      System.out.println(i);
    }
    return 2;
  }
  
  public static int infiniteLoopOriginal()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      i++;
    }
    return i;
  }
  
  public static int loopBasedCodeMotionOriginal()
  {
    for (int i = 0; 3 > i; i = 1 + i) {}
    return 50 * i;
  }
  
  public static int loopBasedCodeMotionExpected()
  {
    int i = 0;
    while (i < 150) {
      i += 50;
    }
    return i;
  }
  
  public static void loopInvariantCodeMotionOriginal(int paramInt1, int paramInt2)
  {
    paramInt1 = 20 * paramInt1;
    if (paramInt2 > paramInt1) {}
    for (int i = 1 + paramInt1;; i = paramInt1)
    {
      paramInt2 = 0;
      for (paramInt1 = 0; paramInt2 < 20; paramInt1 = i + paramInt1)
      {
        paramInt2 = 1 + paramInt2;
        System.out.println(paramInt1);
      }
      return;
    }
  }
  
  public static void loopInvariantCodeMotionExpected(int paramInt1, int paramInt2)
  {
    paramInt1 = 20 * paramInt1;
    if (paramInt2 > paramInt1) {}
    for (int i = paramInt1 + 1;; i = paramInt1)
    {
      paramInt2 = 0;
      paramInt1 = 0;
      while (20 > paramInt1)
      {
        paramInt2 = i + paramInt2;
        paramInt1 = 1 + paramInt1;
        System.out.println(paramInt2);
      }
      return;
    }
  }
  
  public static int loopPeelingOriginal(int paramInt)
  {
    int j = 0;
    for (int i = 0; i < paramInt; i = 1 + i) {
      j += 5;
    }
    return j;
  }
  
  public static int loopPeelingExpected(int paramInt)
  {
    if (paramInt <= 0) {}
    for (int i = 0;; i = i)
    {
      return i;
      int j = 1;
      for (i = 5; paramInt > j; i = 5 + i) {
        j += 1;
      }
    }
  }
  
  public static void loopStrengthReductionOriginal()
  {
    for (int i = 0; 300 > i; i = 1 + i)
    {
      int tmp15_14 = (i + i);
      System.out.println(tmp15_14 + (tmp15_14 + i));
    }
  }
  
  public static void loopStrengthReductionExpected()
  {
    int j = 0;
    int i = 0;
    while (300 > i)
    {
      j += 5;
      i += 1;
      System.out.println(j);
    }
  }
  
  public static void loopStrengthReductionModifiedOriginal()
  {
    int i = 0;
    int k = 0;
    if (300 > k)
    {
      if (k == 150) {}
      for (int j = i + 4;; j = 1 + i)
      {
        System.out.println(50 * i);
        i = j;
        k = 1 + k;
        break;
      }
    }
  }
  
  public static void loopStrengthReductionModifiedExpected()
  {
    int j = 0;
    int k = 0;
    if (300 > j)
    {
      int i = 50 + k;
      if (j == 150) {}
      for (i = 150 + i;; i = i)
      {
        j += 1;
        System.out.println(k);
        k = i;
        break;
      }
    }
  }
  
  public static int loopUnrollOriginal(int paramInt)
  {
    for (paramInt = 0; 1 > paramInt; paramInt = 1 + paramInt) {}
    return paramInt;
  }
  
  public static int loopUnrollExpected(int paramInt)
  {
    return 1;
  }
  
  public static int simpleLoopUnswitchOriginal(int paramInt)
  {
    int j = 0;
    int i = 0;
    if (paramInt < 0) {
      while (j < paramInt)
      {
        j += 1;
        System.out.println(j);
        i = 2 + 1;
      }
    }
    while (j < paramInt)
    {
      j += 1;
      System.out.println(j);
      i += 1;
    }
    return i;
  }
}

/* Location:
 * Qualified Name:     Benchmark
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
## Run 

{'axioms': 'peggy/axioms/java_arithmetic_axioms.xml:peggy/axioms/java_operator_axioms.xml:peggy/axioms/java_operator_costs.xml:peggy/axioms/java_util_axioms.xml', 'optimization_level': 'O2', 'tmpFolder': 'tmp', 'pb': 'glpk', 'eto': '1000'}

### Peggy output
```
+ Successfully added axiom file: peggy/axioms/java_operator_axioms.xml
+ Successfully added axiom file: peggy/axioms/java_util_axioms.xml
+ Successfully added axiom file: peggy/axioms/java_operator_costs.xml
+ Successfully added axiom file: peggy/axioms/java_arithmetic_axioms.xml
+ Loading class file Benchmark
^C```

### Optimized
```java
15:12:59.611 INFO  jd.cli.Main - Decompiling optimized/Benchmark.class
import java.io.PrintStream;

public class Benchmark
{
  public static int branchHoistingOriginal(int paramInt)
  {
    int i;
    for (int j = 0;; j = i)
    {
      i = j + 1;
      if (i >= 500) {
        break;
      }
    }
    j = i + i;
    if (paramInt == 0) {
      i = j;
    }
    for (;;)
    {
      return i;
      i += j;
    }
  }
  
  public static int branchHoistingExpected(int paramInt)
  {
    for (int j = 0; 500 > j; j = 1 + j) {}
    int i = j + j;
    if (paramInt == 0) {}
    for (paramInt = i;; paramInt = j + i) {
      return paramInt;
    }
  }
  
  public static int conditionalConstantFoldingOriginal(int paramInt)
  {
    if (paramInt == 5)
    {
      paramInt *= 4;
      return paramInt;
    }
    if (paramInt == 4) {
      paramInt *= 5;
    }
    for (;;)
    {
      paramInt = paramInt;
      break;
      paramInt = 20;
    }
  }
  
  public static int conditionalConstantFoldingExpected()
  {
    return 20;
  }
  
  public static int constantFoldOriginal()
  {
    return -4;
  }
  
  public static int deadLoopDeletionOriginal()
  {
    return 2;
  }
  
  public static int functionInliningFoo()
  {
    return 1;
  }
  
  public static int functionInliningOriginal(int paramInt)
  {
    return 1 + functionInliningFoo();
  }
  
  public static int functionInliningExpected(int paramInt)
  {
    return paramInt + 2;
  }
  
  public static int ifTrueOriginal(int paramInt)
  {
    return paramInt;
  }
  
  public static int ifTrueExpected(int paramInt)
  {
    return paramInt;
  }
  
  public static int infiniteEffectfulLoopOriginal()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      System.out.println(i);
    }
    return 2;
  }
  
  public static int infiniteLoopOriginal()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      i++;
    }
    return i;
  }
  
  public static int loopBasedCodeMotionOriginal()
  {
    for (int i = 0; 3 > i; i = 1 + i) {}
    return 50 * i;
  }
  
  public static int loopBasedCodeMotionExpected()
  {
    int i = 0;
    while (i < 150) {
      i += 50;
    }
    return i;
  }
  
  public static void loopInvariantCodeMotionOriginal(int paramInt1, int paramInt2)
  {
    paramInt1 = 20 * paramInt1;
    if (paramInt2 > paramInt1) {}
    for (int i = 1 + paramInt1;; i = paramInt1)
    {
      paramInt2 = 0;
      for (paramInt1 = 0; paramInt2 < 20; paramInt1 = i + paramInt1)
      {
        paramInt2 = 1 + paramInt2;
        System.out.println(paramInt1);
      }
      return;
    }
  }
  
  public static void loopInvariantCodeMotionExpected(int paramInt1, int paramInt2)
  {
    paramInt1 = 20 * paramInt1;
    if (paramInt2 > paramInt1) {}
    for (int i = paramInt1 + 1;; i = paramInt1)
    {
      paramInt2 = 0;
      paramInt1 = 0;
      while (20 > paramInt1)
      {
        paramInt2 = i + paramInt2;
        paramInt1 = 1 + paramInt1;
        System.out.println(paramInt2);
      }
      return;
    }
  }
  
  public static int loopPeelingOriginal(int paramInt)
  {
    int j = 0;
    for (int i = 0; i < paramInt; i = 1 + i) {
      j += 5;
    }
    return j;
  }
  
  public static int loopPeelingExpected(int paramInt)
  {
    if (paramInt <= 0) {}
    for (int i = 0;; i = i)
    {
      return i;
      int j = 1;
      for (i = 5; paramInt > j; i = 5 + i) {
        j += 1;
      }
    }
  }
  
  public static void loopStrengthReductionOriginal()
  {
    for (int i = 0; 300 > i; i = 1 + i)
    {
      int tmp15_14 = (i + i);
      System.out.println(tmp15_14 + (tmp15_14 + i));
    }
  }
  
  public static void loopStrengthReductionExpected()
  {
    int j = 0;
    int i = 0;
    while (300 > i)
    {
      j += 5;
      i += 1;
      System.out.println(j);
    }
  }
  
  public static void loopStrengthReductionModifiedOriginal()
  {
    int i = 0;
    int k = 0;
    if (300 > k)
    {
      if (k == 150) {}
      for (int j = i + 4;; j = 1 + i)
      {
        System.out.println(50 * i);
        i = j;
        k = 1 + k;
        break;
      }
    }
  }
  
  public static void loopStrengthReductionModifiedExpected()
  {
    int j = 0;
    int k = 0;
    if (300 > j)
    {
      int i = 50 + k;
      if (j == 150) {}
      for (i = 150 + i;; i = i)
      {
        j += 1;
        System.out.println(k);
        k = i;
        break;
      }
    }
  }
  
  public static int loopUnrollOriginal(int paramInt)
  {
    for (paramInt = 0; 1 > paramInt; paramInt = 1 + paramInt) {}
    return paramInt;
  }
  
  public static int loopUnrollExpected(int paramInt)
  {
    return 1;
  }
  
  public static int simpleLoopUnswitchOriginal(int paramInt)
  {
    int j = 0;
    int i = 0;
    if (paramInt < 0) {
      while (j < paramInt)
      {
        j += 1;
        System.out.println(j);
        i = 2 + 1;
      }
    }
    while (j < paramInt)
    {
      j += 1;
      System.out.println(j);
      i += 1;
    }
    return i;
  }
}

/* Location:
 * Qualified Name:     Benchmark
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
## Run 

{'axioms': 'peggy/axioms/java_arithmetic_axioms.xml:peggy/axioms/java_operator_axioms.xml:peggy/axioms/java_operator_costs.xml:peggy/axioms/java_util_axioms.xml', 'optimization_level': 'O2', 'tmpFolder': 'tmp', 'pb': 'glpk', 'eto': '1000'}

### Peggy output
```
+ Successfully added axiom file: peggy/axioms/java_operator_axioms.xml
+ Successfully added axiom file: peggy/axioms/java_util_axioms.xml
+ Successfully added axiom file: peggy/axioms/java_operator_costs.xml
+ Successfully added axiom file: peggy/axioms/java_arithmetic_axioms.xml
+ Loading class file Benchmark
+ Optimizing class Benchmark
   - Processing method <Benchmark: void <init>()>
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
      * Optimization of method <Benchmark: void <init>()> SUCCESSFUL
      * Optimization took 650
      * PEG2PEGTIME 556
      * PBTIME 435
      * ENGINETIME 6
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <Benchmark: void <init>()>
   - Processing method <Benchmark: int branchHoistingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1000 after 1265 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1897
GLPKFormulation: Number of values: 621
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingOriginal(int)> SUCCESSFUL
      * Optimization took 23198
      * PEG2PEGTIME 23158
      * PBTIME 21864
      * ENGINETIME 1265
      * Optimization ratio 375/695 = 0.539568345323741
      * PEG-based Optimization ratio 375/695 = 0.539568345323741
   - Done processing method <Benchmark: int branchHoistingOriginal(int)>
   - Processing method <Benchmark: int branchHoistingExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1000 after 656 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1670
GLPKFormulation: Number of values: 759
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingExpected(int)> SUCCESSFUL
      * Optimization took 18286
      * PEG2PEGTIME 18264
      * PBTIME 17592
      * ENGINETIME 656
      * Optimization ratio 203/235 = 0.8638297872340426
      * PEG-based Optimization ratio 203/235 = 0.8638297872340426
   - Done processing method <Benchmark: int branchHoistingExpected(int)>
   - Processing method <Benchmark: int conditionalConstantFoldingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 16 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 16
GLPKFormulation: Number of values: 14
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int conditionalConstantFoldingOriginal(int)> SUCCESSFUL
      * Optimization took 448
      * PEG2PEGTIME 434
      * PBTIME 415
      * ENGINETIME 5
      * Optimization ratio 50/50 = 1.0
      * PEG-based Optimization ratio 50/50 = 1.0
   - Done processing method <Benchmark: int conditionalConstantFoldingOriginal(int)>
   - Processing method <Benchmark: int conditionalConstantFoldingExpected()>
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
      * Optimization of method <Benchmark: int conditionalConstantFoldingExpected()> SUCCESSFUL
      * Optimization took 578
      * PEG2PEGTIME 571
      * PBTIME 558
      * ENGINETIME 1
   - Done processing method <Benchmark: int conditionalConstantFoldingExpected()>
   - Processing method <Benchmark: int constantFoldOriginal()>
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
      * Optimization of method <Benchmark: int constantFoldOriginal()> SUCCESSFUL
      * Optimization took 386
      * PEG2PEGTIME 379
      * PBTIME 352
      * ENGINETIME 3
   - Done processing method <Benchmark: int constantFoldOriginal()>
   - Processing method <Benchmark: int deadLoopDeletionOriginal()>
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
      * Optimization of method <Benchmark: int deadLoopDeletionOriginal()> SUCCESSFUL
      * Optimization took 264
      * PEG2PEGTIME 257
      * PBTIME 250
      * ENGINETIME 0
   - Done processing method <Benchmark: int deadLoopDeletionOriginal()>
   - Processing method <Benchmark: int functionInliningFoo()>
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
      * Optimization of method <Benchmark: int functionInliningFoo()> SUCCESSFUL
      * Optimization took 263
      * PEG2PEGTIME 262
      * PBTIME 253
      * ENGINETIME 0
   - Done processing method <Benchmark: int functionInliningFoo()>
   - Processing method <Benchmark: int functionInliningOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 12 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 12
GLPKFormulation: Number of values: 11
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int functionInliningOriginal(int)> SUCCESSFUL
      * Optimization took 312
      * PEG2PEGTIME 307
      * PBTIME 299
      * ENGINETIME 3
      * Optimization ratio 2004/2004 = 1.0
      * PEG-based Optimization ratio 2004/2004 = 1.0
   - Done processing method <Benchmark: int functionInliningOriginal(int)>
   - Processing method <Benchmark: int functionInliningExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 8 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 8
GLPKFormulation: Number of values: 7
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int functionInliningExpected(int)> SUCCESSFUL
      * Optimization took 253
      * PEG2PEGTIME 246
      * PBTIME 240
      * ENGINETIME 1
      * Optimization ratio 4/4 = 1.0
      * PEG-based Optimization ratio 4/4 = 1.0
   - Done processing method <Benchmark: int functionInliningExpected(int)>
   - Processing method <Benchmark: int ifTrueOriginal(int)>
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
      * Optimization of method <Benchmark: int ifTrueOriginal(int)> SUCCESSFUL
      * Optimization took 245
      * PEG2PEGTIME 242
      * PBTIME 235
      * ENGINETIME 0
   - Done processing method <Benchmark: int ifTrueOriginal(int)>
   - Processing method <Benchmark: int ifTrueExpected(int)>
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
      * Optimization of method <Benchmark: int ifTrueExpected(int)> SUCCESSFUL
      * Optimization took 354
      * PEG2PEGTIME 350
      * PBTIME 243
      * ENGINETIME 1
   - Done processing method <Benchmark: int ifTrueExpected(int)>
   - Processing method <Benchmark: int infiniteEffectfulLoopOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1000 after 206 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1043
GLPKFormulation: Number of values: 716
         @ Running solver
```

### Optimized
```java
15:16:23.050 INFO  jd.cli.Main - Decompiling optimized/Benchmark.class
import java.io.PrintStream;

public class Benchmark
{
  public static int branchHoistingOriginal(int paramInt)
  {
    int j;
    for (int i = 0;; i = j)
    {
      j = i + 1;
      if (500 <= j) {
        break;
      }
    }
    i = j + j;
    if (paramInt == 0) {
      i = i;
    }
    for (;;)
    {
      return i;
      i += j;
    }
  }
  
  public static int branchHoistingExpected(int paramInt)
  {
    int i = 0;
    while (500 > i) {
      i += 1;
    }
    int j = i + i;
    if (paramInt == 0) {}
    for (paramInt = j;; paramInt = i + j) {
      return paramInt;
    }
  }
  
  public static int conditionalConstantFoldingOriginal(int paramInt)
  {
    if (paramInt == 5)
    {
      paramInt *= 4;
      return paramInt;
    }
    if (paramInt == 4) {}
    for (paramInt = 5 * paramInt;; paramInt = 20)
    {
      paramInt = paramInt;
      break;
    }
  }
  
  public static int conditionalConstantFoldingExpected()
  {
    return 20;
  }
  
  public static int constantFoldOriginal()
  {
    return -4;
  }
  
  public static int deadLoopDeletionOriginal()
  {
    return 2;
  }
  
  public static int functionInliningFoo()
  {
    return 1;
  }
  
  public static int functionInliningOriginal(int paramInt)
  {
    return 1 + functionInliningFoo();
  }
  
  public static int functionInliningExpected(int paramInt)
  {
    return paramInt + 2;
  }
  
  public static int ifTrueOriginal(int paramInt)
  {
    return paramInt;
  }
  
  public static int ifTrueExpected(int paramInt)
  {
    return paramInt;
  }
  
  public static int infiniteEffectfulLoopOriginal()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      System.out.println(i);
    }
    return 2;
  }
  
  public static int infiniteLoopOriginal()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      i++;
    }
    return i;
  }
  
  public static int loopBasedCodeMotionOriginal()
  {
    for (int i = 0; 3 > i; i = 1 + i) {}
    return 50 * i;
  }
  
  public static int loopBasedCodeMotionExpected()
  {
    int i = 0;
    while (i < 150) {
      i += 50;
    }
    return i;
  }
  
  public static void loopInvariantCodeMotionOriginal(int paramInt1, int paramInt2)
  {
    paramInt1 = 20 * paramInt1;
    if (paramInt2 > paramInt1) {}
    for (paramInt1 = 1 + paramInt1;; paramInt1 = paramInt1)
    {
      int i = 0;
      for (paramInt2 = 0; 20 > i; paramInt2 = paramInt1 + paramInt2)
      {
        i = 1 + i;
        System.out.println(paramInt2);
      }
      return;
    }
  }
  
  public static void loopInvariantCodeMotionExpected(int paramInt1, int paramInt2)
  {
    int i = paramInt1 * 20;
    if (i < paramInt2) {}
    for (paramInt1 = i + 1;; paramInt1 = i)
    {
      paramInt2 = 0;
      i = 0;
      while (20 > i)
      {
        paramInt2 += paramInt1;
        System.out.println(paramInt2);
        i += 1;
      }
      return;
    }
  }
  
  public static int loopPeelingOriginal(int paramInt)
  {
    int i = 0;
    int j = 0;
    while (paramInt > i)
    {
      i = 1 + i;
      j += 5;
    }
    return j;
  }
  
  public static int loopPeelingExpected(int paramInt)
  {
    if (paramInt <= 0) {}
    for (int j = 0;; j = j)
    {
      return j;
      int i = 1;
      j = 5;
      while (paramInt > i)
      {
        i += 1;
        j += 5;
      }
    }
  }
  
  public static void loopStrengthReductionOriginal()
  {
    int j = 0;
    int i = 0;
    while (j < 300)
    {
      System.out.println(i);
      j += 1;
      i += 5;
    }
  }
  
  public static void loopStrengthReductionExpected()
  {
    int i = 0;
    int j = 0;
    while (j < 300)
    {
      i += 5;
      j = 1 + j;
      System.out.println(i);
    }
  }
  
  public static void loopStrengthReductionModifiedOriginal()
  {
    int j = 0;
    int i = 0;
    if (j < 300)
    {
      int k = i + 1;
      if (j == 150) {}
      for (k = 3 + k;; k = k)
      {
        j += 1;
        System.out.println(50 * i);
        i = k;
        break;
      }
    }
  }
  
  public static void loopStrengthReductionModifiedExpected()
  {
    int j = 0;
    int k = 0;
    if (300 > j)
    {
      int i = 50 + k;
      if (j == 150) {
        i += 150;
      }
      for (;;)
      {
        System.out.println(k);
        j += 1;
        k = i;
        break;
        i = i;
      }
    }
  }
  
  public static int loopUnrollOriginal(int paramInt)
  {
    for (paramInt = 0; 1 > paramInt; paramInt = 1 + paramInt) {}
    return paramInt;
  }
  
  public static int loopUnrollExpected(int paramInt)
  {
    return 1;
  }
  
  public static int simpleLoopUnswitchOriginal(int paramInt)
  {
    int i = 0;
    int j = 0;
    if (paramInt < 0) {
      while (paramInt > j)
      {
        System.out.println(j);
        i = 1 + 2;
        j += 1;
      }
    }
    while (paramInt > j)
    {
      System.out.println(j);
      i = 1 + i;
      j += 1;
    }
    return i;
  }
}

/* Location:
 * Qualified Name:     Benchmark
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
## Run 

{'axioms': 'peggy/axioms/java_arithmetic_axioms.xml:peggy/axioms/java_operator_axioms.xml:peggy/axioms/java_operator_costs.xml:peggy/axioms/java_util_axioms.xml', 'optimization_level': 'O2', 'tmpFolder': 'tmp', 'pb': 'glpk', 'eto': '1000'}

### Peggy output
```
+ Successfully added axiom file: peggy/axioms/java_operator_axioms.xml
+ Successfully added axiom file: peggy/axioms/java_util_axioms.xml
+ Successfully added axiom file: peggy/axioms/java_operator_costs.xml
+ Successfully added axiom file: peggy/axioms/java_arithmetic_axioms.xml
+ Loading class file Benchmark
+ Optimizing class Benchmark
   - Processing method <Benchmark: void <init>()>
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
      * Optimization of method <Benchmark: void <init>()> SUCCESSFUL
      * Optimization took 694
      * PEG2PEGTIME 560
      * PBTIME 418
      * ENGINETIME 16
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <Benchmark: void <init>()>
   - Processing method <Benchmark: int branchHoistingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1000 after 1302 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1897
GLPKFormulation: Number of values: 621
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingOriginal(int)> SUCCESSFUL
      * Optimization took 21405
      * PEG2PEGTIME 21361
      * PBTIME 20029
      * ENGINETIME 1303
      * Optimization ratio 375/695 = 0.539568345323741
      * PEG-based Optimization ratio 375/695 = 0.539568345323741
   - Done processing method <Benchmark: int branchHoistingOriginal(int)>
   - Processing method <Benchmark: int branchHoistingExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1000 after 933 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1672
GLPKFormulation: Number of values: 760
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingExpected(int)> SUCCESSFUL
      * Optimization took 20618
      * PEG2PEGTIME 20605
      * PBTIME 19651
      * ENGINETIME 933
      * Optimization ratio 203/235 = 0.8638297872340426
      * PEG-based Optimization ratio 203/235 = 0.8638297872340426
   - Done processing method <Benchmark: int branchHoistingExpected(int)>
   - Processing method <Benchmark: int conditionalConstantFoldingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 16 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 16
GLPKFormulation: Number of values: 14
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int conditionalConstantFoldingOriginal(int)> SUCCESSFUL
      * Optimization took 325
      * PEG2PEGTIME 319
      * PBTIME 296
      * ENGINETIME 5
      * Optimization ratio 50/50 = 1.0
      * PEG-based Optimization ratio 50/50 = 1.0
   - Done processing method <Benchmark: int conditionalConstantFoldingOriginal(int)>
   - Processing method <Benchmark: int conditionalConstantFoldingExpected()>
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
      * Optimization of method <Benchmark: int conditionalConstantFoldingExpected()> SUCCESSFUL
      * Optimization took 262
      * PEG2PEGTIME 260
      * PBTIME 253
      * ENGINETIME 0
   - Done processing method <Benchmark: int conditionalConstantFoldingExpected()>
   - Processing method <Benchmark: int constantFoldOriginal()>
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
      * Optimization of method <Benchmark: int constantFoldOriginal()> SUCCESSFUL
      * Optimization took 237
      * PEG2PEGTIME 233
      * PBTIME 225
      * ENGINETIME 1
   - Done processing method <Benchmark: int constantFoldOriginal()>
   - Processing method <Benchmark: int deadLoopDeletionOriginal()>
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
      * Optimization of method <Benchmark: int deadLoopDeletionOriginal()> SUCCESSFUL
      * Optimization took 234
      * PEG2PEGTIME 231
      * PBTIME 225
      * ENGINETIME 0
   - Done processing method <Benchmark: int deadLoopDeletionOriginal()>
   - Processing method <Benchmark: int functionInliningFoo()>
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
      * Optimization of method <Benchmark: int functionInliningFoo()> SUCCESSFUL
      * Optimization took 238
      * PEG2PEGTIME 234
      * PBTIME 230
      * ENGINETIME 0
   - Done processing method <Benchmark: int functionInliningFoo()>
   - Processing method <Benchmark: int functionInliningOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 12 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 12
GLPKFormulation: Number of values: 11
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int functionInliningOriginal(int)> SUCCESSFUL
      * Optimization took 288
      * PEG2PEGTIME 282
      * PBTIME 274
      * ENGINETIME 3
      * Optimization ratio 2004/2004 = 1.0
      * PEG-based Optimization ratio 2004/2004 = 1.0
   - Done processing method <Benchmark: int functionInliningOriginal(int)>
   - Processing method <Benchmark: int functionInliningExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 8 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 8
GLPKFormulation: Number of values: 7
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int functionInliningExpected(int)> SUCCESSFUL
      * Optimization took 282
      * PEG2PEGTIME 273
      * PBTIME 266
      * ENGINETIME 1
      * Optimization ratio 4/4 = 1.0
      * PEG-based Optimization ratio 4/4 = 1.0
   - Done processing method <Benchmark: int functionInliningExpected(int)>
   - Processing method <Benchmark: int ifTrueOriginal(int)>
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
      * Optimization of method <Benchmark: int ifTrueOriginal(int)> SUCCESSFUL
      * Optimization took 408
      * PEG2PEGTIME 387
      * PBTIME 378
      * ENGINETIME 0
   - Done processing method <Benchmark: int ifTrueOriginal(int)>
   - Processing method <Benchmark: int ifTrueExpected(int)>
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
      * Optimization of method <Benchmark: int ifTrueExpected(int)> SUCCESSFUL
      * Optimization took 263
      * PEG2PEGTIME 260
      * PBTIME 248
      * ENGINETIME 1
   - Done processing method <Benchmark: int ifTrueExpected(int)>
   - Processing method <Benchmark: int infiniteEffectfulLoopOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1000 after 207 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1043
GLPKFormulation: Number of values: 716
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      ! Error processing method <Benchmark: int infiniteEffectfulLoopOriginal()> [
         java.lang.RuntimeException: Bad CFG: Should never happen
         	at eqsat.revert.RevertCFG.removeEmptyFallBlocks(RevertCFG.java:401)
         	at eqsat.revert.RevertCFG.simplify(RevertCFG.java:311)
         	at eqsat.revert.CFGReverter.<init>(CFGReverter.java:38)
         	at peggy.optimize.SingleStageOptimizer.optimize(SingleStageOptimizer.java:104)
         	at peggy.optimize.java.Main.optimizeAll(Main.java:875)
         	at peggy.optimize.java.Main.optimizeClass(Main.java:704)
         	at peggy.optimize.java.Main.main(Main.java:2881)
         
      ]
      * Reverting to original method body
      * Optimization of method <Benchmark: int infiniteEffectfulLoopOriginal()> FAILED
   - Processing method <Benchmark: int infiniteLoopOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1000 after 1189 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 787
GLPKFormulation: Number of values: 134
```

### Optimized
```java
16:01:48.837 INFO  jd.cli.Main - Decompiling optimized/Benchmark.class
import java.io.PrintStream;

public class Benchmark
{
  public static int branchHoistingOriginal(int paramInt)
  {
    int i;
    for (int j = 0;; j = i)
    {
      i = j + 1;
      if (500 <= i) {
        break;
      }
    }
    j = i + i;
    if (paramInt == 0) {
      i = j;
    }
    for (;;)
    {
      return i;
      i += j;
    }
  }
  
  public static int branchHoistingExpected(int paramInt)
  {
    int i = 0;
    while (i < 500) {
      i += 1;
    }
    int j = i + i;
    if (paramInt == 0) {}
    for (paramInt = j;; paramInt = j + i) {
      return paramInt;
    }
  }
  
  public static int conditionalConstantFoldingOriginal(int paramInt)
  {
    if (paramInt == 5)
    {
      paramInt = 4 * paramInt;
      return paramInt;
    }
    if (paramInt == 4) {
      paramInt *= 5;
    }
    for (;;)
    {
      paramInt = paramInt;
      break;
      paramInt = 20;
    }
  }
  
  public static int conditionalConstantFoldingExpected()
  {
    return 20;
  }
  
  public static int constantFoldOriginal()
  {
    return -4;
  }
  
  public static int deadLoopDeletionOriginal()
  {
    return 2;
  }
  
  public static int functionInliningFoo()
  {
    return 1;
  }
  
  public static int functionInliningOriginal(int paramInt)
  {
    return functionInliningFoo() + 1;
  }
  
  public static int functionInliningExpected(int paramInt)
  {
    return paramInt + 2;
  }
  
  public static int ifTrueOriginal(int paramInt)
  {
    return paramInt;
  }
  
  public static int ifTrueExpected(int paramInt)
  {
    return paramInt;
  }
  
  public static int infiniteEffectfulLoopOriginal()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      System.out.println(i);
    }
    return 2;
  }
  
  public static int infiniteLoopOriginal()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      i++;
    }
    return i;
  }
  
  public static int loopBasedCodeMotionOriginal()
  {
    int i = 0;
    while (3 > i) {
      i += 1;
    }
    return i * 50;
  }
  
  public static int loopBasedCodeMotionExpected()
  {
    for (int i = 0; 150 > i; i = 50 + i) {}
    return i;
  }
  
  public static void loopInvariantCodeMotionOriginal(int paramInt1, int paramInt2)
  {
    paramInt1 *= 20;
    if (paramInt1 < paramInt2) {}
    for (paramInt2 = paramInt1 + 1;; paramInt2 = paramInt1)
    {
      paramInt1 = 0;
      int i = 0;
      while (i < 20)
      {
        paramInt1 += paramInt2;
        System.out.println(paramInt1);
        i += 1;
      }
      return;
    }
  }
  
  public static void loopInvariantCodeMotionExpected(int paramInt1, int paramInt2)
  {
    paramInt1 *= 20;
    if (paramInt2 > paramInt1) {}
    for (paramInt1 = 1 + paramInt1;; paramInt1 = paramInt1)
    {
      int i = 0;
      for (paramInt2 = 0; i < 20; paramInt2 = paramInt1 + paramInt2)
      {
        i += 1;
        System.out.println(paramInt2);
      }
      return;
    }
  }
  
  public static int loopPeelingOriginal(int paramInt)
  {
    int i = 0;
    int j = 0;
    while (i < paramInt)
    {
      i = 1 + i;
      j += 5;
    }
    return j;
  }
  
  public static int loopPeelingExpected(int paramInt)
  {
    if (paramInt <= 0) {}
    int i;
    for (int j = 0;; j = i)
    {
      return j;
      i = 5;
      j = 1;
      while (paramInt > j)
      {
        i = 5 + i;
        j += 1;
      }
    }
  }
  
  public static void loopStrengthReductionOriginal()
  {
    int i = 0;
    while (i < 300)
    {
      int tmp15_14 = (i + i);
      System.out.println(tmp15_14 + tmp15_14 + i);
      i += 1;
    }
  }
  
  public static void loopStrengthReductionExpected()
  {
    int j = 0;
    for (int i = 0; i < 300; i = 1 + i)
    {
      j += 5;
      System.out.println(j);
    }
  }
  
  public static void loopStrengthReductionModifiedOriginal()
  {
    int j = 0;
    int i = 0;
    if (i < 300)
    {
      int k = 1 + j;
      if (i == 150) {
        k += 3;
      }
      for (;;)
      {
        System.out.println(50 * j);
        j = k;
        i += 1;
        break;
        k = k;
      }
    }
  }
  
  public static void loopStrengthReductionModifiedExpected()
  {
    int j = 0;
    int i = 0;
    if (300 > i)
    {
      int k = j + 50;
      if (i == 150) {
        k += 150;
      }
      for (;;)
      {
        j = k;
        System.out.println(j);
        i += 1;
        break;
        k = k;
      }
    }
  }
  
  public static int loopUnrollOriginal(int paramInt)
  {
    paramInt = 0;
    while (1 > paramInt) {
      paramInt += 1;
    }
    return paramInt;
  }
  
  public static int loopUnrollExpected(int paramInt)
  {
    return 1;
  }
  
  public static int simpleLoopUnswitchOriginal(int paramInt)
  {
    int k = 0;
    int j = 0;
    if (paramInt < 0) {
      while (paramInt > j)
      {
        int i = j;
        k = 2 + 1;
        j = 1 + j;
        System.out.println(i);
      }
    }
    while (paramInt > j)
    {
      k += 1;
      j = 1 + j;
      System.out.println(j);
    }
    return k;
  }
}

/* Location:
 * Qualified Name:     Benchmark
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
## Run 

{'axioms': 'peggy/axioms/java_arithmetic_axioms.xml:peggy/axioms/java_operator_axioms.xml:peggy/axioms/java_operator_costs.xml:peggy/axioms/java_util_axioms.xml', 'optimization_level': 'O2', 'tmpFolder': 'tmp', 'pb': 'glpk', 'eto': '1000'}

### Peggy output
```
+ Successfully added axiom file: peggy/axioms/java_operator_axioms.xml
+ Successfully added axiom file: peggy/axioms/java_util_axioms.xml
+ Successfully added axiom file: peggy/axioms/java_operator_costs.xml
+ Successfully added axiom file: peggy/axioms/java_arithmetic_axioms.xml
+ Loading class file Benchmark
+ Optimizing class Benchmark
   - Processing method <Benchmark: void <init>()>
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
      * Optimization of method <Benchmark: void <init>()> SUCCESSFUL
      * Optimization took 630
      * PEG2PEGTIME 528
      * PBTIME 406
      * ENGINETIME 7
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <Benchmark: void <init>()>
   - Processing method <Benchmark: int branchHoistingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1000 after 1930 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1899
GLPKFormulation: Number of values: 618
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingOriginal(int)> SUCCESSFUL
      * Optimization took 26577
      * PEG2PEGTIME 26514
      * PBTIME 24536
      * ENGINETIME 1930
      * Optimization ratio 375/695 = 0.539568345323741
      * PEG-based Optimization ratio 375/695 = 0.539568345323741
   - Done processing method <Benchmark: int branchHoistingOriginal(int)>
   - Processing method <Benchmark: int branchHoistingExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1000 after 1206 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1725
GLPKFormulation: Number of values: 733
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingExpected(int)> SUCCESSFUL
      * Optimization took 26979
      * PEG2PEGTIME 26957
      * PBTIME 25711
      * ENGINETIME 1206
      * Optimization ratio 203/235 = 0.8638297872340426
      * PEG-based Optimization ratio 203/235 = 0.8638297872340426
   - Done processing method <Benchmark: int branchHoistingExpected(int)>
   - Processing method <Benchmark: int conditionalConstantFoldingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 16 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 16
GLPKFormulation: Number of values: 14
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int conditionalConstantFoldingOriginal(int)> SUCCESSFUL
      * Optimization took 476
      * PEG2PEGTIME 466
      * PBTIME 445
      * ENGINETIME 5
      * Optimization ratio 50/50 = 1.0
      * PEG-based Optimization ratio 50/50 = 1.0
   - Done processing method <Benchmark: int conditionalConstantFoldingOriginal(int)>
   - Processing method <Benchmark: int conditionalConstantFoldingExpected()>
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
      * Optimization of method <Benchmark: int conditionalConstantFoldingExpected()> SUCCESSFUL
      * Optimization took 359
      * PEG2PEGTIME 357
      * PBTIME 346
      * ENGINETIME 1
   - Done processing method <Benchmark: int conditionalConstantFoldingExpected()>
   - Processing method <Benchmark: int constantFoldOriginal()>
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
      * Optimization of method <Benchmark: int constantFoldOriginal()> SUCCESSFUL
      * Optimization took 266
      * PEG2PEGTIME 262
      * PBTIME 252
      * ENGINETIME 2
   - Done processing method <Benchmark: int constantFoldOriginal()>
   - Processing method <Benchmark: int deadLoopDeletionOriginal()>
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
      * Optimization of method <Benchmark: int deadLoopDeletionOriginal()> SUCCESSFUL
      * Optimization took 271
      * PEG2PEGTIME 266
      * PBTIME 257
      * ENGINETIME 0
   - Done processing method <Benchmark: int deadLoopDeletionOriginal()>
   - Processing method <Benchmark: int functionInliningFoo()>
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
      * Optimization of method <Benchmark: int functionInliningFoo()> SUCCESSFUL
      * Optimization took 249
      * PEG2PEGTIME 245
      * PBTIME 236
      * ENGINETIME 1
   - Done processing method <Benchmark: int functionInliningFoo()>
   - Processing method <Benchmark: int functionInliningOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 12 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 12
GLPKFormulation: Number of values: 11
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int functionInliningOriginal(int)> SUCCESSFUL
      * Optimization took 417
      * PEG2PEGTIME 411
      * PBTIME 397
      * ENGINETIME 5
      * Optimization ratio 2004/2004 = 1.0
      * PEG-based Optimization ratio 2004/2004 = 1.0
   - Done processing method <Benchmark: int functionInliningOriginal(int)>
   - Processing method <Benchmark: int functionInliningExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 8 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 8
GLPKFormulation: Number of values: 7
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int functionInliningExpected(int)> SUCCESSFUL
      * Optimization took 380
      * PEG2PEGTIME 373
      * PBTIME 365
      * ENGINETIME 0
      * Optimization ratio 4/4 = 1.0
      * PEG-based Optimization ratio 4/4 = 1.0
   - Done processing method <Benchmark: int functionInliningExpected(int)>
   - Processing method <Benchmark: int ifTrueOriginal(int)>
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
      * Optimization of method <Benchmark: int ifTrueOriginal(int)> SUCCESSFUL
      * Optimization took 350
      * PEG2PEGTIME 345
      * PBTIME 336
      * ENGINETIME 1
   - Done processing method <Benchmark: int ifTrueOriginal(int)>
   - Processing method <Benchmark: int ifTrueExpected(int)>
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
      * Optimization of method <Benchmark: int ifTrueExpected(int)> SUCCESSFUL
      * Optimization took 258
      * PEG2PEGTIME 255
      * PBTIME 245
      * ENGINETIME 0
   - Done processing method <Benchmark: int ifTrueExpected(int)>
   - Processing method <Benchmark: int infiniteEffectfulLoopOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1000 after 251 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1043
GLPKFormulation: Number of values: 716
```

### Optimized
```java
14:48:12.152 INFO  jd.cli.Main - Decompiling optimized/Benchmark.class
import java.io.PrintStream;

public class Benchmark
{
  public static int branchHoistingOriginal(int paramInt)
  {
    int i;
    for (int j = 0;; j = i)
    {
      i = 1 + j;
      if (i >= 500) {
        break;
      }
    }
    j = i + i;
    if (paramInt == 0) {
      i = j;
    }
    for (;;)
    {
      return i;
      i += j;
    }
  }
  
  public static int branchHoistingExpected(int paramInt)
  {
    for (int j = 0; j < 500; j = 1 + j) {}
    int i = j + j;
    if (paramInt == 0) {}
    for (paramInt = i;; paramInt = j + i) {
      return paramInt;
    }
  }
  
  public static int conditionalConstantFoldingOriginal(int paramInt)
  {
    if (paramInt == 5)
    {
      paramInt = 4 * paramInt;
      return paramInt;
    }
    if (paramInt == 4) {
      paramInt *= 5;
    }
    for (;;)
    {
      paramInt = paramInt;
      break;
      paramInt = 20;
    }
  }
  
  public static int conditionalConstantFoldingExpected()
  {
    return 20;
  }
  
  public static int constantFoldOriginal()
  {
    return -4;
  }
  
  public static int deadLoopDeletionOriginal()
  {
    return 2;
  }
  
  public static int functionInliningFoo()
  {
    return 1;
  }
  
  public static int functionInliningOriginal(int paramInt)
  {
    return 1 + functionInliningFoo();
  }
  
  public static int functionInliningExpected(int paramInt)
  {
    return 2 + paramInt;
  }
  
  public static int ifTrueOriginal(int paramInt)
  {
    return paramInt;
  }
  
  public static int ifTrueExpected(int paramInt)
  {
    return paramInt;
  }
  
  public static int infiniteEffectfulLoopOriginal()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      System.out.println(i);
    }
    return 2;
  }
  
  public static int infiniteLoopOriginal()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      i++;
    }
    return i;
  }
  
  public static int loopBasedCodeMotionOriginal()
  {
    int i = 0;
    while (3 > i) {
      i += 1;
    }
    return i * 50;
  }
  
  public static int loopBasedCodeMotionExpected()
  {
    for (int i = 0; 150 > i; i = 50 + i) {}
    return i;
  }
  
  public static void loopInvariantCodeMotionOriginal(int paramInt1, int paramInt2)
  {
    int i = 20 * paramInt1;
    if (i < paramInt2) {}
    for (paramInt1 = i + 1;; paramInt1 = i)
    {
      paramInt2 = 0;
      i = 0;
      while (20 > i)
      {
        System.out.println(paramInt2);
        paramInt2 = paramInt1 + paramInt2;
        i += 1;
      }
      return;
    }
  }
  
  public static void loopInvariantCodeMotionExpected(int paramInt1, int paramInt2)
  {
    paramInt1 *= 20;
    if (paramInt2 > paramInt1) {}
    for (paramInt2 = 1 + paramInt1;; paramInt2 = paramInt1)
    {
      int i = 0;
      paramInt1 = 0;
      while (20 > i)
      {
        i += 1;
        System.out.println(paramInt1);
        paramInt1 += paramInt2;
      }
      return;
    }
  }
  
  public static int loopPeelingOriginal(int paramInt)
  {
    int j = 0;
    int i = 0;
    while (i < paramInt)
    {
      j = 5 + j;
      i += 1;
    }
    return j;
  }
  
  public static int loopPeelingExpected(int paramInt)
  {
    if (paramInt <= 0) {}
    int i;
    for (int j = 0;; j = i)
    {
      return j;
      i = 5;
      j = 1;
      while (paramInt > j)
      {
        i += 5;
        j += 1;
      }
    }
  }
  
  public static void loopStrengthReductionOriginal()
  {
    int i = 0;
    int j = 0;
    while (300 > j)
    {
      i = 5 + i;
      j += 1;
      System.out.println(i);
    }
  }
  
  public static void loopStrengthReductionExpected()
  {
    int i = 0;
    for (int j = 0; i < 300; j = 5 + j)
    {
      System.out.println(j);
      i = 1 + i;
    }
  }
  
  public static void loopStrengthReductionModifiedOriginal()
  {
    int j = 0;
    int i = 0;
    if (j < 300)
    {
      if (j == 150) {}
      for (int k = 4 + i;; k = 1 + i)
      {
        j = 1 + j;
        i = k;
        System.out.println(i * 50);
        break;
      }
    }
  }
  
  public static void loopStrengthReductionModifiedExpected()
  {
    int k = 0;
    int j = 0;
    if (300 > j)
    {
      int i = 50 + k;
      if (j == 150) {
        i += 150;
      }
      for (;;)
      {
        k = i;
        j = 1 + j;
        System.out.println(k);
        break;
        i = i;
      }
    }
  }
  
  public static int loopUnrollOriginal(int paramInt)
  {
    paramInt = 0;
    while (1 > paramInt) {
      paramInt += 1;
    }
    return paramInt;
  }
  
  public static int loopUnrollExpected(int paramInt)
  {
    return 1;
  }
  
  public static int simpleLoopUnswitchOriginal(int paramInt)
  {
    int i = 0;
    int j = 0;
    if (paramInt < 0) {
      while (j < paramInt)
      {
        System.out.println(j);
        i = 1 + 2;
        j = 1 + j;
      }
    }
    while (j < paramInt)
    {
      i = 1 + i;
      System.out.println(j);
      j = 1 + j;
    }
    return i;
  }
}

/* Location:
 * Qualified Name:     Benchmark
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
## Run 

{'axioms': 'peggy/axioms/java_arithmetic_axioms.xml:peggy/axioms/java_operator_axioms.xml:peggy/axioms/java_operator_costs.xml:peggy/axioms/java_util_axioms.xml', 'optimization_level': 'O2', 'tmpFolder': 'tmp', 'pb': 'glpk', 'eto': '1000'}

### Peggy output
```
+ Successfully added axiom file: peggy/axioms/java_operator_axioms.xml
+ Successfully added axiom file: peggy/axioms/java_util_axioms.xml
+ Successfully added axiom file: peggy/axioms/java_operator_costs.xml
+ Successfully added axiom file: peggy/axioms/java_arithmetic_axioms.xml
+ Loading class file Benchmark
+ Optimizing class Benchmark
   - Processing method <Benchmark: void <init>()>
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
      * Optimization of method <Benchmark: void <init>()> SUCCESSFUL
      * Optimization took 668
      * PEG2PEGTIME 545
      * PBTIME 411
      * ENGINETIME 8
      * Optimization ratio 2000/2000 = 1.0
      * PEG-based Optimization ratio 2000/2000 = 1.0
   - Done processing method <Benchmark: void <init>()>
   - Processing method <Benchmark: int branchHoistingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1000 after 1691 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1775
GLPKFormulation: Number of values: 621
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingOriginal(int)> SUCCESSFUL
      * Optimization took 25080
      * PEG2PEGTIME 25027
      * PBTIME 23301
      * ENGINETIME 1691
      * Optimization ratio 375/695 = 0.539568345323741
      * PEG-based Optimization ratio 375/695 = 0.539568345323741
   - Done processing method <Benchmark: int branchHoistingOriginal(int)>
   - Processing method <Benchmark: int branchHoistingExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1000 after 859 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1752
GLPKFormulation: Number of values: 747
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int branchHoistingExpected(int)> SUCCESSFUL
      * Optimization took 25900
      * PEG2PEGTIME 25856
      * PBTIME 24973
      * ENGINETIME 859
      * Optimization ratio 203/235 = 0.8638297872340426
      * PEG-based Optimization ratio 203/235 = 0.8638297872340426
   - Done processing method <Benchmark: int branchHoistingExpected(int)>
   - Processing method <Benchmark: int conditionalConstantFoldingOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 16 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 16
GLPKFormulation: Number of values: 14
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int conditionalConstantFoldingOriginal(int)> SUCCESSFUL
      * Optimization took 490
      * PEG2PEGTIME 479
      * PBTIME 444
      * ENGINETIME 19
      * Optimization ratio 50/50 = 1.0
      * PEG-based Optimization ratio 50/50 = 1.0
   - Done processing method <Benchmark: int conditionalConstantFoldingOriginal(int)>
   - Processing method <Benchmark: int conditionalConstantFoldingExpected()>
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
      * Optimization of method <Benchmark: int conditionalConstantFoldingExpected()> SUCCESSFUL
      * Optimization took 250
      * PEG2PEGTIME 247
      * PBTIME 236
      * ENGINETIME 0
   - Done processing method <Benchmark: int conditionalConstantFoldingExpected()>
   - Processing method <Benchmark: int constantFoldOriginal()>
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
      * Optimization of method <Benchmark: int constantFoldOriginal()> SUCCESSFUL
      * Optimization took 254
      * PEG2PEGTIME 250
      * PBTIME 239
      * ENGINETIME 2
   - Done processing method <Benchmark: int constantFoldOriginal()>
   - Processing method <Benchmark: int deadLoopDeletionOriginal()>
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
      * Optimization of method <Benchmark: int deadLoopDeletionOriginal()> SUCCESSFUL
      * Optimization took 245
      * PEG2PEGTIME 242
      * PBTIME 234
      * ENGINETIME 1
   - Done processing method <Benchmark: int deadLoopDeletionOriginal()>
   - Processing method <Benchmark: int functionInliningFoo()>
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
      * Optimization of method <Benchmark: int functionInliningFoo()> SUCCESSFUL
      * Optimization took 245
      * PEG2PEGTIME 242
      * PBTIME 236
      * ENGINETIME 0
   - Done processing method <Benchmark: int functionInliningFoo()>
   - Processing method <Benchmark: int functionInliningOriginal(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 12 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 12
GLPKFormulation: Number of values: 11
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int functionInliningOriginal(int)> SUCCESSFUL
      * Optimization took 396
      * PEG2PEGTIME 389
      * PBTIME 376
      * ENGINETIME 5
      * Optimization ratio 2004/2004 = 1.0
      * PEG-based Optimization ratio 2004/2004 = 1.0
   - Done processing method <Benchmark: int functionInliningOriginal(int)>
   - Processing method <Benchmark: int functionInliningExpected(int)>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine saturated in 8 iterations
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 8
GLPKFormulation: Number of values: 7
         @ Running solver
         @ GLPK solver returned nonempty result
      * Building reversion graph
      * Building revert CFG
      * Building output CFG
      * Encoding output CFG
      * Optimization completed
      * Optimization of method <Benchmark: int functionInliningExpected(int)> SUCCESSFUL
      * Optimization took 378
      * PEG2PEGTIME 374
      * PBTIME 366
      * ENGINETIME 1
      * Optimization ratio 4/4 = 1.0
      * PEG-based Optimization ratio 4/4 = 1.0
   - Done processing method <Benchmark: int functionInliningExpected(int)>
   - Processing method <Benchmark: int ifTrueOriginal(int)>
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
      * Optimization of method <Benchmark: int ifTrueOriginal(int)> SUCCESSFUL
      * Optimization took 260
      * PEG2PEGTIME 258
      * PBTIME 247
      * ENGINETIME 1
   - Done processing method <Benchmark: int ifTrueOriginal(int)>
   - Processing method <Benchmark: int ifTrueExpected(int)>
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
      * Optimization of method <Benchmark: int ifTrueExpected(int)> SUCCESSFUL
      * Optimization took 365
      * PEG2PEGTIME 360
      * PBTIME 349
      * ENGINETIME 0
   - Done processing method <Benchmark: int ifTrueExpected(int)>
   - Processing method <Benchmark: int infiniteEffectfulLoopOriginal()>
      * Building original PEG
      * Setting up engine
      * Running engine
      * Engine reached iteration bound of 1000 after 259 milliseconds
      * Building optimal PEG
      * Begin GLPK solving
         @ Writing formulation
GLPKFormulation: Number of nodes: 1043
GLPKFormulation: Number of values: 716
```

### Optimized
```java
15:26:58.809 INFO  jd.cli.Main - Decompiling optimized/Benchmark.class
import java.io.PrintStream;

public class Benchmark
{
  public static int branchHoistingOriginal(int paramInt)
  {
    int j;
    for (int i = 0;; i = j)
    {
      j = 1 + i;
      if (j >= 500) {
        break;
      }
    }
    i = j + j;
    if (paramInt == 0) {}
    for (i = i;; i = j + i) {
      return i;
    }
  }
  
  public static int branchHoistingExpected(int paramInt)
  {
    for (int i = 0; i < 500; i = 1 + i) {}
    int j = i + i;
    if (paramInt == 0) {}
    for (paramInt = j;; paramInt = j + i) {
      return paramInt;
    }
  }
  
  public static int conditionalConstantFoldingOriginal(int paramInt)
  {
    if (paramInt == 5)
    {
      paramInt *= 4;
      return paramInt;
    }
    if (paramInt == 4) {}
    for (paramInt = 5 * paramInt;; paramInt = 20)
    {
      paramInt = paramInt;
      break;
    }
  }
  
  public static int conditionalConstantFoldingExpected()
  {
    return 20;
  }
  
  public static int constantFoldOriginal()
  {
    return -4;
  }
  
  public static int deadLoopDeletionOriginal()
  {
    return 2;
  }
  
  public static int functionInliningFoo()
  {
    return 1;
  }
  
  public static int functionInliningOriginal(int paramInt)
  {
    return functionInliningFoo() + 1;
  }
  
  public static int functionInliningExpected(int paramInt)
  {
    return 2 + paramInt;
  }
  
  public static int ifTrueOriginal(int paramInt)
  {
    return paramInt;
  }
  
  public static int ifTrueExpected(int paramInt)
  {
    return paramInt;
  }
  
  public static int infiniteEffectfulLoopOriginal()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      System.out.println(i);
    }
    return 2;
  }
  
  public static int infiniteLoopOriginal()
  {
    int i = 0;
    int j = 5;
    while (j == 5) {
      i++;
    }
    return i;
  }
  
  public static int loopBasedCodeMotionOriginal()
  {
    for (int i = 0; 3 > i; i = 1 + i) {}
    return 50 * i;
  }
  
  public static int loopBasedCodeMotionExpected()
  {
    int i = 0;
    while (150 > i) {
      i += 50;
    }
    return i;
  }
  
  public static void loopInvariantCodeMotionOriginal(int paramInt1, int paramInt2)
  {
    int i = 20 * paramInt1;
    if (i < paramInt2) {}
    for (paramInt1 = 1 + i;; paramInt1 = i)
    {
      i = 0;
      for (paramInt2 = 0; i < 20; paramInt2 = paramInt1 + paramInt2)
      {
        i = 1 + i;
        System.out.println(paramInt2);
      }
      return;
    }
  }
  
  public static void loopInvariantCodeMotionExpected(int paramInt1, int paramInt2)
  {
    paramInt1 = 20 * paramInt1;
    if (paramInt2 > paramInt1) {}
    for (paramInt2 = paramInt1 + 1;; paramInt2 = paramInt1)
    {
      paramInt1 = 0;
      for (int i = 0; i < 20; i = 1 + i)
      {
        System.out.println(paramInt1);
        paramInt1 += paramInt2;
      }
      return;
    }
  }
  
  public static int loopPeelingOriginal(int paramInt)
  {
    int j = 0;
    for (int i = 0; i < paramInt; i = 1 + i) {
      j = 5 + j;
    }
    return j;
  }
  
  public static int loopPeelingExpected(int paramInt)
  {
    if (paramInt <= 0) {}
    for (int i = 0;; i = i)
    {
      return i;
      i = 5;
      int j = 1;
      while (j < paramInt)
      {
        i += 5;
        j += 1;
      }
    }
  }
  
  public static void loopStrengthReductionOriginal()
  {
    int i = 0;
    for (int j = 0; 300 > j; j = 1 + j)
    {
      i = 5 + i;
      System.out.println(i);
    }
  }
  
  public static void loopStrengthReductionExpected()
  {
    int i = 0;
    int j = 0;
    while (j < 300)
    {
      i += 5;
      System.out.println(i);
      j += 1;
    }
  }
  
  public static void loopStrengthReductionModifiedOriginal()
  {
    int j = 0;
    int i = 0;
    if (300 > j)
    {
      if (j == 150) {}
      for (int k = 4 + i;; k = i + 1)
      {
        j += 1;
        System.out.println(50 * i);
        i = k;
        break;
      }
    }
  }
  
  public static void loopStrengthReductionModifiedExpected()
  {
    int j = 0;
    int i = 0;
    if (j < 300)
    {
      int k = i + 50;
      if (j == 150) {}
      for (k = 150 + k;; k = k)
      {
        j = 1 + j;
        i = k;
        System.out.println(i);
        break;
      }
    }
  }
  
  public static int loopUnrollOriginal(int paramInt)
  {
    for (paramInt = 0; paramInt < 1; paramInt = 1 + paramInt) {}
    return paramInt;
  }
  
  public static int loopUnrollExpected(int paramInt)
  {
    return 1;
  }
  
  public static int simpleLoopUnswitchOriginal(int paramInt)
  {
    int j = 0;
    int i = 0;
    if (paramInt < 0) {
      while (i < paramInt)
      {
        System.out.println(i);
        j = 2 + 1;
        i += 1;
      }
    }
    while (i < paramInt)
    {
      System.out.println(i);
      j += 1;
      i += 1;
    }
    return j;
  }
}

/* Location:
 * Qualified Name:     Benchmark
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.1
 */
```
