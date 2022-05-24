FROM python:3.9 as builder

ARG Guided_MT_Code2Vec_VERSION="1.0-SNAPSHOT"
LABEL maintainer="rmar@live.nl"
LABEL name="ciselab/Guided-MT-Code2Vec"
LABEL description="A genetic search algorithm for testing metamorphic transformations on a trained code2vec model"
LABEL org.opencontainers.image.source="https://github/wubero/Guided-MT-Code2Vec"
LABEL url="https://github/wubero/Guided-MT-Code2Vec"
LABEL vcs="https://github/wubero/Guided-MT-Code2Vec"

RUN apt-get update && apt install openjdk-17-jdk -y && apt install maven -y && apt install bash -y

COPY . /app/Guided-MT-Code2Vec/
WORKDIR /app/Guided-MT-Code2Vec/code2vec
RUN pip install -r requirements.txt

WORKDIR /app
RUN git clone https://github.com/ciselab/Lampion.git
WORKDIR /app/Lampion/Transformers/Java
RUN git fetch && git checkout 81d8361953ca3565dae34f6e77ba6ba944a031c7 && mvn -P nofiles -DskipShade install

WORKDIR /app/Guided-MT-Code2Vec
RUN mvn -P nofiles install package verify

# Copy entrypoint & sample config file
COPY src/main/resources/Docker/entrypoint.sh /app/Guided-MT-Code2Vec/
COPY src/main/resources/Docker/config.properties /config/

ENV targetDir="/app/Guided-MT-Code2Vec/genetic_input"
ENV configfile="/config/config.properties"
ENV outputDir="/app/Guided-MT-Code2Vec/genetic_output"

RUN mv target/Guided-MT-Code2Vec-jar-with-dependencies.jar Guided-MT-Code2Vec.jar
RUN chmod +x ./entrypoint.sh
ENTRYPOINT ["bash","./entrypoint.sh"]
