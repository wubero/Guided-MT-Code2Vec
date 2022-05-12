##### Copy Lampion files
##### Build and Run tests
FROM maven:3.8.4-openjdk-17 as builder

WORKDIR /app

## should first pull and then checkout different branch
RUN git clone https://github.com/ciselab/Lampion.git
WORKDIR /app/Lampion/Transformers/Java
RUN git fetch && git checkout JavaTransformer-Library && mvn -P nofiles package

FROM python:3.9
WORKDIR /app/Guided-MT-Code2Vec/code2vec
COPY code2vec/ .
RUN pip install -r requirements.txt

FROM maven:3.8.4-openjdk-17
WORKDIR /app/Guided-MT-Code2Vec
COPY . /app/Guided-MT-Code2Vec/
RUN mvn -P nofiles verify