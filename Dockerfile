FROM python:3.9 as builder

RUN apt-get update \
    && apt-get install --no-cache bash \
    && apt-get install --no-cache --virtual=build-dependencies unzip \
    && apt-get install --no-cache curl \
    && apt-get install --no-cache openjdk-17 \
    && apt-get install --no-cache maven

COPY . /app/Guided-MT-Code2Vec/
WORKDIR /app/Guided-MT-Code2Vec/code2vec
RUN pip install -r requirements.txt

WORKDIR /app
RUN git clone https://github.com/ciselab/Lampion.git
WORKDIR /app/Lampion/Transformers/Java
RUN git fetch && git checkout JavaTransformer-Library && mvn -P nofiles package

WORKDIR /app/Guided-MT-Code2Vec
RUN mvn -P nofiles verify

#FROM python:3.9 as builder
#WORKDIR /app/Guided-MT-Code2Vec/code2vec
#COPY code2vec/ .
#RUN pip install -r requirements.txt
#
#FROM maven:3.8.4-openjdk-17
#WORKDIR /app
#COPY . /app/Guided-MT-Code2vec/
#COPY --from=builder / /
### should first pull and then checkout different branch
#RUN git clone https://github.com/ciselab/Lampion.git
#WORKDIR /app/Lampion/Transformers/Java
#RUN git fetch && git checkout JavaTransformer-Library && mvn -P nofiles package
#
#WORKDIR /app/Guided-MT-Code2Vec
#RUN mvn -P nofiles verify