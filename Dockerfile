FROM python:3.9 as builder

RUN apt-get update
RUN apt install openjdk-17-jdk -y
RUN apt install maven -y

COPY . /app/Guided-MT-Code2Vec/
WORKDIR /app/Guided-MT-Code2Vec/code2vec
RUN pip install -r requirements.txt

WORKDIR /app
RUN git clone https://github.com/ciselab/Lampion.git
WORKDIR /app/Lampion/Transformers/Java
RUN git fetch && git checkout 81d8361953ca3565dae34f6e77ba6ba944a031c7 && mvn -P nofiles -DskipShade install

WORKDIR /app/Guided-MT-Code2Vec
RUN mvn -P nofiles install verify
