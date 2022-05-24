# Guided-MT-Code2vec
This repository works with genetic programming to optimize metrics for code2vec method name prediction.
The architecture for this is depicted in the image below.
![Architecture plan](./src/main/resources/Thesis_pipeline.png)

This is still under development

## Cloning the repository
In this project recursive repositories are used. This means that before you try to build the project you should clone both the repositories.
For this simple run the following command in your git bash:

```sh
git clone --recursive -j8 https://github.com/wubero/Guided-MT-Code2Vec.git
```

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
4. As a dataset you can you use the provided one in ./code2vec/data, or you can download your own. (The next sections describes how this projects got its test datasets.)
5. If you do download your own dataset there might occur errors with the spoon library. These can be resolved by running the EditData.py script

## Dataset

For the datasets used in our experiments we randomly selected 30 from the java-small dataset provided by code2vec
through the following link: 

```sh
wget https://s3.amazonaws.com/code2vec/data/java-small_data.tar.gz
```

As mentioned the dataset we used is provided in ./code2vec/data. However, you can also generate your own random dataset
by executing the filePicker.sh script located in ./scripts. To do this only the directory and target paths need to be changed.

## Requirements

- Maven
- Jdk 17
- Python 3.9

## Troubleshooting
If the bash commands in the program are not working you might need to adjust the file endings of 
the shell scripts in the code2vec folder. You can do this by running the dos2unix command in 
something like git bash. This will readjust the file endings and make sure that the docker build 
runs smoothly.
