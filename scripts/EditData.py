import os


def main():
    path = "C:/Users/Ruben-pc/Documents/Master_thesis/Guided-MT-Code2Vec/code2vec/data/test_0"
    for filename in os.listdir(path):
        f = os.path.join(path, filename)
        if os.path.isfile(f):
            with open(f, "r") as myFile:
                data = myFile.read()
                if "class " not in data:
                    myFile.close()
                    os.remove(f)
                elif "extends " in data:
                    myFile.close()
                    os.remove(f)
                elif "package " not in data:
                    newData = data.split('\n')
                else:
                    newData = removeComments(data)

                newData = removeAbstractMethods(newData)
                with open(f, "w") as writing:
                    for line in newData:
                        writing.write(line + '\n')
        else:
            print("file not found")


def removeAbstractMethods(data):
    newData = []
    flag = False
    for line in data:
        if not flag:
            if "abstract " in line and "abstract class" not in line:
                if ';' not in line:
                    flag = True
            else:
                newData.append(line)
        else:
            if ';' in line:
                flag = False
    return newData


def removeComments(file):
    data = file.split('\n')
    newData = []
    for i in data:
        if not ("package " in i and ("* " not in i or "//" not in i)):
            newData.append(i)
    return newData


if __name__ == "__main__":
    main()
