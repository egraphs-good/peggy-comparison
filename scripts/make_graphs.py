import pandas as pd
import matplotlib
import matplotlib.pyplot as plt


def make_graphs(results_file, time_vs_lines_filename, time_vs_nodes_filename):
    matplotlib.rcParams["text.usetex"] = True
    matplotlib.rcParams["font.family"] = "STIXGeneral"
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


def clean_data(results_file):
    data = pd.read_csv(results_file)

    # strip spaces from each column name
    data.columns = data.columns.str.strip()

    # filter out <init> and <clinit> methods
    data = data[
        ~(data["name"].str.contains("<init>"))
        & ~(data["name"].str.contains("<clinit>"))
    ]

    # scale to seconds
    for timecol in ["PEG2PEGTIME", "PBTIME", "ENGINETIME", "Optimization took"]:
        data[timecol] = data[timecol] / 1000

    return data


def time_vs_x_plot(data, transparency, size, xcol, xlabel, output_filename):
    fig, ax = plt.subplots()

    ax.set_xlabel(xlabel)
    ax.set_ylabel("solver time (seconds)")

    # passing
    work = data[data["PBTIME"] >= 0]
    work = work[work[xcol] >= 0]
    ax.scatter(work[xcol], work["PBTIME"], c="blue", s=size, alpha=transparency)

    # failing
    # TODO: distinguish from timeout
    ymax = max(work["PBTIME"])
    fail = data[(data["PBTIME"] < 0)]
    ax.scatter(
        fail[xcol], [ymax * 1.3] * len(fail), c="red", s=size, alpha=transparency
    )

    ax.set_yscale("log")
    ax.set_xscale("log")

    fig.savefig(output_filename, bbox_inches="tight")
    fig.clear()
