import config
import opt_capabilities
import os
import perf_scaling


if __name__ == "__main__":
    # TODO: check if the container exists and if it's running
    # add container name to config

    # Create results dir if not exists
    if not os.path.exists(config.results_dir):
        os.makedirs(config.results_dir)

    # Optimization capabilities benchmark
    oc_results_dir = os.path.join(config.results_dir, "oc")
    if not os.path.exists(oc_results_dir):
        os.makedirs(oc_results_dir)
    opt_capabilities.benchmark_file(
        classname=config.oc_benchmark_classname,
        benchmark_dir=config.oc_benchmark_dir,
        results_dir=oc_results_dir,
        container_name=config.docker_containername,
    )

    # Performance scaling benchmark
    ps_results_dir = os.path.join(config.results_dir, "ps")
    if not os.path.exists(ps_results_dir):
        os.makedirs(ps_results_dir)
    perf_scaling.benchmark_dirs(
        dirs=config.ps_benchmark_dirs,
        results_filename=os.path.join(ps_results_dir, config.ps_results_filename),
        time_vs_lines_filename=os.path.join(
            ps_results_dir, config.ps_time_vs_lines_filename
        ),
        time_vs_nodes_filename=os.path.join(
            ps_results_dir, config.ps_time_vs_nodes_filename
        ),
    )
