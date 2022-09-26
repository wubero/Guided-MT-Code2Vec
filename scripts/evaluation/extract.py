import sys
import json
import os
import regex as re
import pandas as pd


def make_csv(path_to_data_dir: str, filename: str = "results.csv") -> None:
    """
    Extracts all data from the given dirs jsonfiles,
    prints them to a .csv file nearby
    :param path_to_data_dir:
    :param filename:
    :return: Nothing, but write file next to it
    """
    df = make_df(path_to_data_dir)
    df.to_csv(filename)


def make_df(path_to_data_dir: str) -> pd.DataFrame:
    """
    Walks over the given datapath and finds all .json files from the Guided-MT-Code2Vec Experiment.
    The path-names are very important, as the experiment and generation are extracted from the path.
    Please consult the nearby README.md for expected Folder-Layout.
    """
    json_files: [str] = []

    # iterate over files in
    # that directory
    for root, dirs, files in os.walk(path_to_data_dir):
        for filename in files:
            if ".json" in filename:
                json_files.append(os.path.join(root, filename))

    print(f"found {len(json_files)} .json-files in {path_to_data_dir}")

    datapoints = []
    for file in json_files:
        with open(file) as f:
            datapoint = json.loads(f.read())
            datapoint["path"] = file
            datapoint["seed"] = extract_seed_from_path(file)
            datapoint["experiment"] = extract_experiment_from_path(path_to_data_dir, file)
            datapoint["TRANSFORMATIONS"] = count_transformers(datapoint)
            datapoint["generation"] = extract_generation_from_path(file)

            datapoints.append(datapoint)

    df = pd.DataFrame(datapoints)

    return df


def extract_seed_from_path(path: str) -> int:
    pattern = r'seed-\d+'
    match = re.findall(pattern, path)[0]
    return match[5:]


def extract_experiment_from_path(directory: str, path: str) -> str:
    pattern = directory + r'.*?/seed'
    match = re.findall(pattern, path)[0]
    return match[len(directory)+1:-5]


def extract_generation_from_path(path: str) -> int:
    pattern = r'gen\d+'
    match = re.findall(pattern, path)[0]
    return match[3:]


def count_transformers(datapoint):
    # There was an issue with the json, the genotype is just a string as some quotes were missing
    raw = datapoint["genotype"]
    pattern = "transformer"
    matches = re.findall(pattern, raw)
    return len(matches)


if __name__ == "__main__":
    print(f"Starting to extract data from {sys.argv[1]}")
    make_csv(path_to_data_dir=sys.argv[1])
    print(f"Finished, closing the program")
