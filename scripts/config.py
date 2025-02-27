data_dir = "nightly/data"


# NOTE: if you create the docker container with a different name,
# update this value
docker_containername = "peggy3"

oc_benchmark_dir = "java/benchmark"
oc_benchmark_classname = "Benchmark"

# TODO: add the rest of specjvm
ps_benchmark_dirs = [
    # ("smoke/", "eggcc/benchmarks/passing/polybench/linear-algebra/kernels")
    ("java/polybench/linear-algebra/blas/", "eggcc/benchmarks/passing/polybench/linear-algebra/blas/"),
    ("java/polybench/linear-algebra/kernels/", "eggcc/benchmarks/passing/polybench/linear-algebra/kernels/"),
    ("java/polybench/linear-algebra/solvers/", "eggcc/benchmarks/passing/polybench/linear-algebra/solvers/"),

    # "java/polybench/datamining/",
    # "java/polybench/medley/",
    # "java/polybench/stencils/",

    # "java/uninlined-spec/scimark/",
    # "java/inlined-spec/scimark/",
    # "java/uninlined-spec/compress/",
    # "java/inlined-spec/compress/",

    #"java/benchmark/",
]
ps_results_filename = "perf.csv"
ps_output_filename = "output.txt"
ps_time_vs_lines_filename = "time_vs_lines.pdf"
ps_ratio_vs_lines_filename = "ratio_vs_lines.pdf"
ps_per_method_timeout_seconds = 600 # 10 minute timeout
