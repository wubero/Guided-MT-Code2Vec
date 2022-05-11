##### Copy Lampion files
##### Build and Run tests
FROM maven:3.8.4-openjdk-17 as builder

WORKDIR /app
COPY . .

### should first pull and then checkout different branch
RUN git clone https://github.com/ciselab/Lampion.git && cd Lampion && git fetch && git checkout JavaTransformer-Library && cd Transformers/Java/Core && mvn install && mvn package verify && cd ../CLI && mvn install && mvn package verify

RUN cd /app/Guided-MT-Code2Vec/code2vec

FROM python:3.9
RUN pip install -r requirements.txt

FROM maven:3.8.4-openjdk-17
RUN cd ..
RUN mvn test -P nofiles
RUN mvn package verify