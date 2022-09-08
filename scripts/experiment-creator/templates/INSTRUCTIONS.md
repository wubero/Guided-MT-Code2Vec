# Running the experiments

Pre-Requisites:

- Docker Images Available
- Docker Compose Ready and installed
- Experiment-data ready

**Expected File System Layout**: 

```
experiment-package/
├── INSTRUCTIONS.md
├── experiment-configuration.json
├── experiment-data
│   ├── File1.java
│   ├── [...]
│   └── File300.java
├── pareto-F1-MRR-min
│   ├── docker-compose-pareto-F1-MRR-1105.yaml
│   ├── [...]
│   ├── docker-compose-pareto-F1-MRR-9001.yaml
│   └── f1-mrr-min.properties
├── pareto-MRR-trans-min
│   ├── docker-compose-pareto-MRR-trans-1105.yaml
│   ├── [...]
│   ├── docker-compose-pareto-MRR-trans-9001.yaml
│   └── mrr-trans-min.properties
├── ...
└── runner.sh
```

To run the Shell scripts, do: 

```sh
PARALLEL_EXPERIMENTS=10
bash experiments.sh $PARALLEL_EXPERIMENTS
```

To run in background: 

```
PARALLEL_EXPERIMENTS=10
nohup sh experiments.sh $PARALLEL_EXPERIMENTS > experiments.log &
```

We recommend 1 or 2 experiments in parallel for desktop users and ~10 Experiments on a proper server.

## Data
The dataset we used for our experiments was selected by running the filePicker.sh script in the scripts folder. 
This selects the files at random from a larger database. 

**Note** There are some unsupported elements, such as enums, which might force a re-draw of elements. 

## Docker Image

The expected Docker image is `ciselab/guided-mt-code2vec`. 
Follow the instructions in [the github repository](https://github.com/ciselab/Guided-MT-Code2Vec) to obtain it.

The default for the experiment is `latest` but the replication package will have one specified.
