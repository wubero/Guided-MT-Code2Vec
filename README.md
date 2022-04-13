# Guided-MT-Code2vec
This repository works with genetic programming to optimize metrics for code2vec method name prediction.
The rough architecture for this is depicted in the image below. (This will be updated asap)
![Architecture plan](./src/main/resources/Architecture_plan.png)

This is still under development

## Build & Run

To build the project, simply do:

```sh
mvn clean package verify
```

To build an executable:

```sh
mvn package verify
```

## How to get started

It's highly recommended that new users start by looking at both other projects that are used here, code2vec and Lampion.
Code2Vec is used for evaluating its trained model, besides that the Lampion project is used for the Java metamorphic transformers.

To get started with the project you

1. Download a trained code2vec model
2. Adjust paths and dataset names in preprocess.sh
3. Adjust paths and dataset in PipelineSupport.java
4. As a dataset you can you use the provided one in ./code2vec/, or you can download your own.
5. If you do download your own dataset there might occur errors with the spoon library. These can be resolved by running the EditData.py script

## Requirements

- Maven
- Jdk 17
- Python 3.9
