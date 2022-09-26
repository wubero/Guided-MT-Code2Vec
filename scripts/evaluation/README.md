# Guided-MT Code2Vec Evaluation 

The [nearby Jupyter Notebook](./evaluation.ipynb) holds more information how to create and evaluate the experiment data. 
For it to run you need the experiments finished and their output copied next to this README. 

## Expected Layout

```
├── data
│   └── random-MRR-max
│       ├── seed-2880
│       │   ├── data
│       │   │   ├── gen0
│       │   │   │   ├── 3b2459
│       │   │   │   ├── 3b2459.json
│       │   │   │   ├── 447e22
│       │   │   │   ├── 447e22.json
│       │   │   │   ├── 4495c7
│       │   │   │   ├── 4495c7.json
│       │   │   │   ├── 52667b
│       │   │   │   ├── 52667b.json
│       │   │   │   ├── 6855ba
│       │   │   │   ├── 6855ba.json
│       │   │   │   ├── 68ec75
│       │   │   │   ├── 68ec75.json
│       │   │   │   ├── 6cc14d
│       │   │   │   ├── 6cc14d.json
│       │   │   │   ├── 6d6845
│       │   │   │   ├── 6d6845.json
│       │   │   │   ├── 7a2d67
│       │   │   │   ├── 7a2d67.json
│       │   │   │   ├── ed0dd9
│       │   │   │   └── ed0dd9.json
│       │   │   ├── gen1
│       │   │   ├── ...
│       │   │   ├── gen8
│       │   │   ├── ...
│       │   │   ├── generation_0
│       │   │   │   ├── Some.java
│       │   │   │   ├── ...
│       │   │   │   ├── Other.java
│       │   │   │   └── Different.java
│       │   │   └── initialGen
│       │   │       └── 3bf9ce
│       │   └── results.txt
│       ├── seed-5142
│           └── results.txt
│       ...
├── evaluation.ipynb
└── requirements.txt
```

