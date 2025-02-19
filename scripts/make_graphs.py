import pandas as pd
import matplotlib
import matplotlib.pyplot as plt
from run_utils import ResultType
import csv

def make_graphs(results_file, time_vs_lines_filename, time_vs_nodes_filename):
    # for kirsten thesis font
    # matplotlib.rcParams["text.usetex"] = True TODO was this important? not working nightly
    #matplotlib.rcParams["font.family"] = "STIXGeneral"
    plt.rcParams["font.size"] = 20
    transparency = 0.2
    size = 100

    peggy_data = clean_data(results_file)
    eggcc_data = pd.read_csv("eggcc.csv")
    time_vs_x_plot(
        peggy_data,
        eggcc_data,
        transparency,
        size,
        peggy_ycol="PEG2PEGTIME",
        eggcc_ycol="compile",
        xcol="length",
        xlabel="method length (number of lines)",
        ylabel="compilation time (seconds)",
        output_filename=time_vs_lines_filename,
    )
    time_vs_x_plot(
        peggy_data,
        eggcc_data,
        transparency,
        size,
        peggy_ycol="PBTIME",
        eggcc_ycol="extraction",
        xcol="length",
        xlabel="method length (number of lines)",
        ylabel="solver time (seconds)",
        output_filename='time_vs_lines_pb.png',
    )
    ratio_plot(
        peggy_data,
        eggcc_data,
        transparency,
        size,
        peggy_num="PBTIME",
        peggy_denom="PEG2PEGTIME",
        eggcc_num="extraction",
        eggcc_denom="compile",
        xcol="length",
        xlabel="method length (number of lines)",
        ylabel="solver time / compilation time",
        output_filename='ratio_pb.png',
    )


TIMEOUT = -1
FAILURE = -2


def clean_data(results_file):
    data = pd.read_csv(results_file)

    # strip spaces from each column name
    data.columns = data.columns.str.strip()

    data = data[data["length"] < 500]
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


def time_vs_x_plot(peggy_data, eggcc_data, transparency, size, peggy_ycol, eggcc_ycol, xcol, xlabel, ylabel, output_filename):
    fig, ax = plt.subplots()

    ax.set_xlabel(xlabel)
    ax.set_ylabel(ylabel)

    # passing
    work = peggy_data[peggy_data[peggy_ycol].apply(lambda x: x != FAILURE and x != TIMEOUT)]
    ax.scatter(work[xcol], work[peggy_ycol], c="green", s=size, alpha=transparency)

    ymax = max(work[peggy_ycol])

    # failing
    fail = peggy_data[(peggy_data[peggy_ycol] == FAILURE)]
    ax.scatter(
        fail[xcol], [ymax * 1.3] * len(fail), c="red", s=size, alpha=transparency
    )

    # timeout
    timeout = peggy_data[(peggy_data[peggy_ycol] == TIMEOUT)]
    ax.scatter(
        timeout[xcol],
        [ymax * 1.3] * len(timeout),
        c="purple",
        s=size,
        alpha=transparency,
    )

    length = eggcc_data['length']
    time = eggcc_data[eggcc_ycol]
    ax.scatter(length, time, c="blue", s=size, alpha=transparency)

    fig.savefig(output_filename, bbox_inches="tight")
    fig.clear()


def ratio_plot(peggy_data, eggcc_data, transparency, size, peggy_num, peggy_denom, eggcc_num, eggcc_denom, xcol, xlabel, ylabel, output_filename):
    fig, ax = plt.subplots()

    peggy_data = peggy_data[(peggy_data[peggy_num] != FAILURE) & (peggy_data[peggy_denom] != TIMEOUT)]
    ax.set_xlabel(xlabel)
    ax.set_ylabel(ylabel)

    ax.scatter(peggy_data[xcol], peggy_data[peggy_num] / peggy_data[peggy_denom], c="green", s=size, alpha=transparency)
    ax.scatter(eggcc_data[xcol], eggcc_data[eggcc_num] / eggcc_data[eggcc_denom], c="blue", s=size, alpha=transparency)

    fig.savefig(output_filename, bbox_inches="tight")
    fig.clear()

if __name__ == "__main__":
    make_graphs("perf.csv", "time_vs_lines_filename.png", "time_vs_nodes_filename.png")
