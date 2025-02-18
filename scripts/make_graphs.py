import pandas as pd
import matplotlib
import matplotlib.pyplot as plt
from run_utils import ResultType


def make_graphs(results_file, time_vs_lines_filename, time_vs_nodes_filename):
    # for kirsten thesis font
    # matplotlib.rcParams["text.usetex"] = True TODO was this important? not working nightly
    #matplotlib.rcParams["font.family"] = "STIXGeneral"
    plt.rcParams["font.size"] = 20
    transparency = 0.2
    size = 100

    data = clean_data(results_file)
    time_vs_x_plot(
        data,
        transparency,
        size,
        xcol="length",
        xlabel="method length (number of lines)",
        output_filename=time_vs_lines_filename,
    )
    time_vs_x_plot(
        data,
        transparency,
        size,
        xcol="nodes:",
        xlabel="solver formulation size (nodes)",
        output_filename=time_vs_nodes_filename,
    )


TIMEOUT = -1
FAILURE = -2


def clean_data(results_file):
    data = pd.read_csv(results_file)

    # strip spaces from each column name
    data.columns = data.columns.str.strip()

    # filter out <init> and <clinit> methods
    data = data[
        ~(data["name"].str.contains("<init>"))
        & ~(data["name"].str.contains("<clinit>"))
    ]

    # scale to seconds,
    # indicate timeout and failure by -1 or -2
    for timecol in ["PEG2PEGTIME", "PBTIME", "ENGINETIME", "Optimization took"]:
        data[timecol] = data[timecol].apply(
            lambda x: (
                TIMEOUT
                if x == "TIMEOUT"
                else (FAILURE if x == "FAILURE" else int(x) / 1000)
            )
        )

    return data


def time_vs_x_plot(data, transparency, size, xcol, xlabel, output_filename):
    fig, ax = plt.subplots()

    ax.set_xlabel(xlabel)
    ax.set_ylabel("solver time (seconds)")

    # passing
    work = data[data["PBTIME"].apply(lambda x: x != FAILURE and x != TIMEOUT)]
    ax.scatter(work[xcol], work["PBTIME"], c="green", s=size, alpha=transparency)

    # failing
    ymax = max(work["PBTIME"])
    fail = data[(data["PBTIME"] == FAILURE)]
    ax.scatter(
        fail[xcol], [ymax * 1.3] * len(fail), c="red", s=size, alpha=transparency
    )

    # timeout
    timeout = data[(data["PBTIME"] == TIMEOUT)]
    ax.scatter(
        timeout[xcol],
        [ymax * 1.3] * len(timeout),
        c="purple",
        s=size,
        alpha=transparency,
    )

    fig.savefig(output_filename, bbox_inches="tight")
    fig.clear()
