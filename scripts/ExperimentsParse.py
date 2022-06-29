import collections
import os
import csv
from collections import Counter

import matplotlib.pyplot as plt
import numpy as np
import pylab as plb
from scipy import stats
from cliffs_delta import cliffs_delta

globalTransformations = []
experiments = []
avgInitial = []
avgBest = []
transformationsBest = []
# [F1_Edit, ....]
# -> [[seed1, seed2, ....], []]
# -> [[[], [], []], []]
paretoFront = []
paretoExperiments = []


def checkNormal():
    metrics = ['F1', 'MRR', 'Precision', 'Recall']
    path = 'C:/Users/Ruben/Documents/Master_thesis/compose_output/'
    for i in metrics:
        initial = []
        best = []
        for seed in os.listdir(path + i):
            with open(path + i + "/" + seed + '/GA_results.txt', 'r') as f:
                lines = f.readlines()
                for line in lines:
                    if 'Initial fitness without transformations: ' in line:
                        initial.append(float(line.split('Initial fitness without transformations: ')[1]))
                    elif 'Max fitness: ' in line:
                        best.append(float(line.split('Max fitness: ')[1]))
        print(stats.kstest(initial, 'norm'))
        print(stats.kstest(best, 'norm'))


def wilcoxTest():
    metrics = ['F1', 'MRR', 'Precision', 'Recall']
    path = 'C:/Users/Ruben/Documents/Master_thesis/compose_output/'
    for i in metrics:
        initial = []
        best = []
        for seed in os.listdir(path + i):
            with open(path + i + "/" + seed + '/GA_results.txt', 'r') as f:
                lines = f.readlines()
                for line in lines:
                    if 'Initial fitness without transformations: ' in line:
                        initial.append(float(line.split('Initial fitness without transformations: ')[1]))
                    elif 'Max fitness: ' in line:
                        best.append(float(line.split('Max fitness: ')[1]))
        print(f"Initial scores for all seeds: {initial}")
        print(f"Scores after the GA for all seeds: {best}")
        _, p = stats.wilcoxon(x=initial, y=best)
        print(f"Wilcoxon test of {i} is {p}")
        d, res = cliffs_delta(initial, best)
        print(f"Cliffs delta effect size of {i} is {d}, {res}")


def cohend(d1, d2):
    n1, n2 = len(d1), len(d2)
    s1, s2 = np.var(d1, ddof=1), np.var(d2, ddof=1)
    s = np.sqrt(((n1 - 1) * s1 + (n2 - 1) * s2) / (n1 + n2 - 2))
    u1, u2 = np.mean(d1), np.mean(d2)
    return (u1 - u2) / s


def writeData(experiment, seed, file, writer):
    global globalTransformations
    row = [experiment, seed, 0, 0, 0, 0, 0, 0, 0]
    with open(file, 'r') as f:
        lines = f.readlines()
        flag = False
        for line in lines:
            if 'Generation used: ' in line:
                row[2] = line.split('Generation used: ')[1]
            elif 'Initial fitness without transformations: ' in line:
                row[3] = line.split('Initial fitness without transformations: ')[1].replace('.', ',')
            elif 'Max fitness: ' in line:
                row[4] = line.split('Max fitness: ')[1].replace('.', ',')
            elif 'Best individual: ' in line:
                flag = True
            elif flag:
                globalTransformations = line.replace('[', '').replace(']', '').replace('\n', '').split(', ')
                row[5] = len(globalTransformations)
                flag = False
            elif 'The metric means are: ' in line:
                row[6] = line.split('The metric means are: ')[1]
            elif 'The metric standard deviation are: ' in line:
                row[7] = line.split('The metric standard deviation are: ')[1]
        writer.writerow(row)
    return [row[3], row[4]]


def pareto(path, experiment):
    global paretoFront
    arrExp = []
    with open(path + experiment + '_pareto.txt', 'w') as f:
        for seed in os.listdir(path + experiment):
            arrSeed = []
            with open(path + experiment + '/' + seed + '/GA_results.txt', 'r') as lines:
                for line in lines.readlines():
                    if 'Pareto set: ' in line:
                        # {[0.511175898931001, 0.3857142925262451], [0.47441860465116276, 0.3928571343421936]}
                        p = line.split('Pareto set: ')[1].replace('{[', '').replace(']}', '').strip().split('], [')
                        for i in p:
                            arr = []
                            if ', ' in i:
                                current = i.split(', ')
                                arr.append(float(current[0]))
                                arr.append(float(current[1]))
                            if len(arr) != 0:
                                arrSeed.append(arr)
                            t = i.replace(', ', '\t').replace('.', ',')
                            temp = experiment + '\t' + seed + '\t' + t
                            f.write(temp + '\n')
                        if len(arrSeed) != 0:
                            arrExp.append(arrSeed)
    if len(arrExp) != 0:
        paretoExperiments.append(experiment)
        paretoFront.append(arrExp)


def main():
    global globalTransformations
    global transformationsBest
    global experiments
    global avgInitial
    global avgBest

    path = 'C:/Users/Ruben/Documents/Master_thesis/compose_output/'
    with open(path + 'results.csv', 'w') as f, open(path + 'count.csv', 'w') as k:
        writer = csv.writer(f)
        counter = csv.writer(k)
        header = ['Experiment', 'seed', 'Generations used', 'Initial', 'Best', 'Final size', 'Means', 'Std']
        writer.writerow(header)
        header2 = ['Experiment', 'Transformer', 'Count']
        counter.writerow(header2)
        total = []
        F1_total = []
        MRR_total = []
        PMRR_total = []
        for curr in os.listdir(path):
            if os.path.isdir(path + curr):
                pareto(path, curr)
                transformations = []
                experiments.append(curr)
                initials = []
                bests = []
                trans = 0
                for file in os.listdir(path + curr):
                    t = writeData(curr, file, path + curr + '/' + file + '/GA_results.txt', writer)
                    initials.append(float(t[0].strip().replace(',', '.')))
                    bests.append(float(t[1].strip().replace(',', '.')))
                    trans += len(globalTransformations)
                    for i in globalTransformations:
                        transformations.append(i)
                        total.append(i)
                        if 'F1' in curr:
                            F1_total.append(i)
                        if 'MRR' in curr:
                            MRR_total.append(i)
                        if 'PMRR' in curr:
                            PMRR_total.append(i)
                transformationsBest.append(trans/10)
                avgBest.append(sum(bests) / len(bests))
                avgInitial.append(sum(initials) / len(initials))
                row = [curr, '', '']
                temp = Counter(transformations)
                for i in temp.keys():
                    row[1] = i
                    row[2] = temp.get(i)
                    counter.writerow(row)
        row = ['Total', '', '']
        temp = Counter(total)
        for i in temp.keys():
            row[1] = i
            row[2] = temp.get(i)
            counter.writerow(row)
        row = ['F1_total', '', '']
        temp = Counter(F1_total)
        for i in temp.keys():
            row[1] = i
            row[2] = temp.get(i)
            counter.writerow(row)
        row = ['MRR_total', '', '']
        temp = Counter(MRR_total)
        for i in temp.keys():
            row[1] = i
            row[2] = temp.get(i)
            counter.writerow(row)
        row = ['PMRR_total', '', '']
        temp = Counter(PMRR_total)
        for i in temp.keys():
            row[1] = i
            row[2] = temp.get(i)
            counter.writerow(row)


def plots():
    global experiments
    global avgInitial
    global avgBest
    basics = ['F1', 'MRR', 'Recall', 'Precision']
    pmrr_mrr = ['MRR', 'PMRR']
    pmrr = ['PMRR', 'MRR', 'F1', 'MRR', 'Recall', 'Precision']

    x = []
    y1 = []
    y2 = []
    for j in pmrr:
        i = experiments.index(j)
        x.append(experiments[i])
        y1.append(avgInitial[i])
        y2.append(avgBest[i])

    X_axis = np.arange(len(x))

    plt.bar(X_axis - 0.2, y1, 0.4, label='Baseline score')
    plt.bar(X_axis + 0.2, y2, 0.4, label='Score after GA')

    plt.ylim([0, 1])
    plt.xticks(X_axis, x)
    plt.xlabel('Experiments')
    plt.ylabel('Score')
    plt.title('Initial score vs best score averaged over 10 random seeds')
    plt.legend()
    plt.show()


def paretoNoT():
    global paretoExperiments
    global paretoFront
    global experiments
    global avgInitial
    global transformationsBest
    global avgBest

    NoT = ['F1_NoT', 'MRR_NoT', 'Recall_NoT', 'Precision_NoT']
    metrics = ['F1', 'MRR', 'Recall', 'Precision']
    label = 0
    for i in NoT:
        j = paretoExperiments.index(i)
        plt.plot(avgInitial[experiments.index(i)], 0, 'o', label='Average initial score')
        plt.plot(avgBest[experiments.index(i)], transformationsBest[j], 'o', label='Average score after GA')
        points = {}
        for seed in paretoFront[j]:
            for point in seed:
                if point[1] not in points:
                    points[point[1]] = []
                points[point[1]].append(point[0])
        points.pop(0)
        points = collections.OrderedDict(sorted(points.items()))
        xMeans = []
        ys = []
        for key in points:
            ys.append(key)
            xMeans.append(np.median(points[key]))
        plt.plot(xMeans, ys, 'o', label='Median')
        z = np.polyfit(xMeans, ys, 2)
        p = np.poly1d(z)
        xnew = np.linspace(min(xMeans), max(xMeans), 300)
        plb.plot(xnew, p(xnew))

        ax = plt.gca()
        ax.invert_xaxis()
        if 'NoT' not in i:
            ax.invert_yaxis()
        plt.xlabel(metrics[label])
        plt.ylabel('Number of transformations')
        plt.legend()
        plt.title(f"Pareto front {i}")
        plt.show()
        label += 1


def scatter():
    global paretoExperiments
    global paretoFront

    metrics = ['F1_MRR', 'PMRR_MRR', 'PMRR_F1', 'RePr']
    for i in metrics:
        j = paretoExperiments.index(i)
        x = []
        y = []
        for seed in paretoFront[j]:
            for point in seed:
                x.append(point[0])
                y.append(point[1])
        plt.plot(x, y, 'o')
        z = np.polyfit(x, y, 1)
        p = np.poly1d(z)
        xnew = np.linspace(min(x), max(x), 300)
        plb.plot(xnew, p(xnew))

        ax = plt.gca()
        ax.invert_xaxis()
        ax.invert_yaxis()
        if '_' in i:
            name = i.split('_')
            plt.xlabel(name[0])
            plt.ylabel(name[1])
        else:
            plt.xlabel('Recall')
            plt.ylabel('Precision')
        # plt.legend()
        plt.title(f"Pareto front {i}")
        plt.show()


if __name__ == '__main__':
    # checkNormal()
    wilcoxTest()
    # main()
    # plots()
    # paretoNoT()
    # scatter()
