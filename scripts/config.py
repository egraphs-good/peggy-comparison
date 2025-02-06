results_dir = "results"

# NOTE: if you create the docker container with a different name,
# update this value
docker_containername = "peggy"

oc_benchmark_dir = "java/benchmark"
oc_benchmark_classname = "Benchmark"

# TODO: add the rest of specjvm
ps_benchmark_dirs = [
    "java/uninlined-spec/scimark/",
    "java/inlined-spec/scimark/",
    "java/benchmark/",
    "java/uninlined-spec/compress/",
    "java/inlined-spec/compress/",
    "java/polybench/",
]
ps_results_filename = "perf.csv"
ps_output_filename = "output.txt"
ps_time_vs_lines_filename = "time_vs_lines.png"
ps_time_vs_nodes_filename = "time_vs_nodes.png"
ps_per_method_timeout_seconds = 1800
