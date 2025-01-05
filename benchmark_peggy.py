import config
import scripts.opt_capabilities
import os
import scripts.perf_scaling


if __name__ == "__main__":
    # Create results dir if not exists
    if not os.path.exists(config.results_dir):
        os.makedirs(config.results_dir)

    # Optimization capabilities benchmark
    oc_results_dir = os.path.join(config.results_dir, "oc")
    scripts.opt_capabilities.benchmark_file(
        config.oc_benchmark_classname, config.oc_benchmark_dir, oc_results_dir
    )

    # Performance scaling benchmark
    ps_results_dir = os.path.join(config.results_dir, "ps")
    scripts.perf_scaling.benchmark_dirs(
        dirs=config.ps_benchmark_dirs,
        results_filename=config.ps_results_filename,
        time_vs_lines_filename=config.ps_time_vs_lines_filename,
        time_vs_nodes_filename=config.ps_time_vs_nodes_filename,
    )
