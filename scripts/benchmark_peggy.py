import config
import opt_capabilities
import os
import perf_scaling
import subprocess


if __name__ == "__main__":
    # Check if container exists and is running
    if "true" not in (
        subprocess.check_output(
            [
                "docker",
                "container",
                "inspect",
                "-f",
                "'{{.State.Running}}'",
                config.docker_containername,
            ],
        ).decode("utf-8")
    ):
        print(f"Container {config.docker_containername} is not running. Exiting...")
        exit()

    # Create results dir if not exists
    if not os.path.exists(config.data_dir):
        os.makedirs(config.data_dir)

    # Optimization capabilities benchmark
    oc_data_dir = os.path.join(config.data_dir, "oc")
    if not os.path.exists(oc_data_dir):
         os.makedirs(oc_data_dir)
    opt_capabilities.benchmark_file(
         classname=config.oc_benchmark_classname,
         benchmark_dir=config.oc_benchmark_dir,
         results_dir=oc_data_dir,
         container_name=config.docker_containername,
    )

    # Performance scaling benchmark
    ps_results_dir = os.path.join(config.data_dir, "ps")
    if not os.path.exists(ps_results_dir):
        os.makedirs(ps_results_dir)
    perf_scaling.benchmark_dirs(
        dirs=config.ps_benchmark_dirs,
        results_filename=os.path.join(ps_results_dir, config.ps_results_filename),
        time_vs_lines_filename=os.path.join(
            ps_results_dir, config.ps_time_vs_lines_filename
        ),
        ratio_vs_lines_filename=os.path.join(
            ps_results_dir, config.ps_ratio_vs_lines_filename
        ),
        output_filename=os.path.join(ps_results_dir, config.ps_output_filename),
    )
