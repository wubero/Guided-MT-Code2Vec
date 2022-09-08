# Experiment Creator 

This python script utilizes template to create a set of experiments. 

## How to Run 

Setup (Python 3.8): 

```
pip install -r requirements.txt
```

Run: 

```
python main.py experiment-configuration.json
```

Zip: 

```
tar -cvzf experiment-package.tar.gz experiment-package
```

## Experiments 

Adjust the [experiment-configuration.json](experiment-configuration.json) by adding / changing the `experiments` section of it. 
You find available variables in the [config-template](./templates/).

**Note** The experiments are deterministic - there is no need to run an experiment with the same configuration twice. 

You might want to consider making two or three packages to split the load over multiple machines. 