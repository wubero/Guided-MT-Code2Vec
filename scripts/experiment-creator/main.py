from jinja2 import Environment, FileSystemLoader    # For templating with jinja
import shutil                                       # For shell utilities such as mkdir or copytree
import os                                           # For File/Directory Creation
import json                                         # For reading in the configurations
import argparse                                     # For handling a nice commandline interface


def run(
        grid_config_file: str,
        dockerimage: str = None) -> None:
    """
    Primary Method of this file. It will
    1. Read the templates
    2. Read the grid-experiment-configs
    3. Create a set of derivative experiment-configs
    4.1 Sort Configs in Compose-Batches if configured
    4.2 Fill all Templates accordingly
    5. Print the filled templates to files
    6. Copy helpers and nearby contents necessary for a replication package

    :param grid_config_file: the path to a .json file containing the information how to do the grid experiment.
    :param dockerimage: The image used in the composes.
        Must have label attached, e.g.: "ciselab/guided-mt-code2vec:1.1"
    
    """
    file_loader = FileSystemLoader('templates')
    env = Environment(loader=file_loader)

    compose_template = env.get_template('docker-compose.yaml.j2')
    config_template = env.get_template('config.properties.j2')

    with open(grid_config_file) as f:
        grid_configurations = json.load(f)

    configurations = []
    counter = 0

    seeds = grid_configurations['seeds']
    experiments = grid_configurations['experiments']
    genetic_configuration = grid_configurations["genetic_configuration"]

    output_dir_grid_experiment = "experiment-package"
    os.makedirs(output_dir_grid_experiment, exist_ok=True)

    for experiment in experiments:
        experiment_dir =  os.path.join(output_dir_grid_experiment,experiment['metric']+"-"+experiment['modifier'])
        print(f"Filling {experiment_dir} ...")
        os.makedirs(experiment_dir, exist_ok=True)

        config_file_path = os.path.join(experiment_dir,experiment["properties_file"])
        config_file = open(config_file_path, "w")
        config_content = config_template.render(**experiment,**genetic_configuration)
        config_file.write(config_content)
        config_file.close()

        for seed in seeds:
            compose_file_path = os.path.join(experiment_dir,f"docker-compose-{experiment['metric']}-{seed}-{experiment['modifier']}.yaml")
            compose_file = open(compose_file_path, "w")
            compose_content = compose_template.render(**experiment,seed=seed,docker_image=dockerimage)
            compose_file.write(compose_content)
            compose_file.close()

    # Last step: Copy helper files and config
    copy_other_files(target_dir=output_dir_grid_experiment,config_file_path=grid_config_file)


def copy_other_files(target_dir: str, config_file_path: str = None) -> None:
    path_to_runner_file = "templates/runner.sh"
    path_to_instructions_file = "templates/INSTRUCTIONS.md"

    if os.path.exists(path_to_runner_file) and os.path.isfile(path_to_runner_file):
        shutil.copyfile(src=path_to_runner_file, dst=os.path.join(target_dir, "runner.sh"))
    else:
        print("Did not find the runner file nearby - not packaging it")

    if os.path.exists(path_to_instructions_file) and os.path.isfile(path_to_instructions_file):
        shutil.copyfile(src=path_to_instructions_file, dst=os.path.join(target_dir, "INSTRUCTIONS.md"))
    else:
        print("Did not find instructions file nearby - not packaging it")

    # Copy the Config too
    if config_file_path:
        shutil.copyfile(config_file_path, os.path.join(target_dir, config_file_path))

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Creates the sub-configs and docker compose files for a Lampion-Grid '
                                                 'Experiment')
    parser.add_argument('configfile', metavar='cf', type=str, nargs=1,
                        help='The config file to create the grid experiment from')
    parser.add_argument('-dockerimage', nargs='?', type=str,
                        default="ciselab/guided-mt-code2vec:latest",
                        help="Which  docker-image (and version) to use. Version must be specified. ")

    args = parser.parse_args()

    run(args.configfile[0],
        dockerimage=args.dockerimage)