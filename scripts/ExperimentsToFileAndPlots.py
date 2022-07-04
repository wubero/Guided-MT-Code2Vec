# This python file parses the data from the experiments and creates csv files for easier reading.
# It also creates all plots used in the thesis for visualization of the data.
# For use of this you should change the result_path and the random_result_path to the correct directories.
# Lastly, it also plots the transformers used in the best individuals after the genetic algorithm.

import collections
import os
import csv
from collections import Counter

import matplotlib.pyplot as plt
import numpy as np
import pylab as plb
from scipy import stats
from cliffs_delta import cliffs_delta

# All transformations used to create the transformer counter.
globalTransformations = []
# The experiments in order
experiments = []
# The average initial scores in the same order as the experiments list.
avgInitial = []
# The score after the GA in the same order as the experiments list.
avgBest = []
# The transformations of the best individuals after the GA
transformationsBest = []
# All paretoFront fronts
paretoFront = []
# The paretoFront experiments in the same order as the paretoFront list.
paretoExperiments = []

# The path of the genetic search experiments.
result_path = 'C:/Users/Ruben/Documents/Master_thesis/compose_output/'
# The path of the random search experiments.
random_result_path = 'C:/Users/Ruben/Documents/Master_thesis/Experiments_random/compose_output/'


# Check if the data is normally distributed
def checkNormal():
    metrics = ['F1', 'MRR', 'Precision', 'Recall', 'PMRR']
    for i in metrics:
        initial = []
        best = []
        averages = []
        for seed in os.listdir(result_path + i):
            with open(result_path + i + "/" + seed + '/GA_results.txt', 'r') as f,\
                    open(random_result_path + i + "/" + seed + '/GA_results.txt', 'r') as g:
                lines = f.readlines()
                for line in lines:
                    if 'Initial fitness without transformations: ' in line:
                        initial.append(float(line.split('Initial fitness without transformations: ')[1]))
                    elif 'Max fitness: ' in line:
                        best.append(float(line.split('Max fitness: ')[1]))
                random_lines = g.readlines()
                for line in random_lines:
                    if 'At the end of the algorithm the results are: , ' in line:
                        res = line.split(', ')
                        average = res[3].split('average: ')[1].strip()  # average
                        averages.append(float(average))
        diffs = []
        for j in range(len(best)):
            diffs.append(averages[j] - best[j])
        print(f"diffs for metric {i} is {stats.kstest(diffs, 'norm')}")


# Run the Wilcoxon test on the data.
def wilcoxTest():
    metrics = ['F1', 'MRR', 'Precision', 'Recall', 'PMRR']
    for i in metrics:
        initial = []
        best = []
        averages = []
        for seed in os.listdir(result_path + i):
            with open(result_path + i + "/" + seed + '/GA_results.txt', 'r') as f,\
                    open(random_result_path + i + "/" + seed + '/GA_results.txt', 'r') as g:
                lines = f.readlines()
                for line in lines:
                    if 'Initial fitness without transformations: ' in line:
                        initial.append(float(line.split('Initial fitness without transformations: ')[1]))
                    elif 'Max fitness: ' in line:
                        best.append(float(line.split('Max fitness: ')[1]))
                random_lines = g.readlines()
                for line in random_lines:
                    if 'At the end of the algorithm the results are: , ' in line:
                        res = line.split(', ')
                        average = res[3].split('average: ')[1].strip()  # average
                        averages.append(float(average))

        # print(f"Initial scores for all seeds: {initial}")
        # print(f"Average scores for all seeds: {averages}")
        # print(f"Scores after the GA for all seeds: {best}")
        x, p = stats.wilcoxon(x=averages, y=best)
        print(f"Wilcoxon test of {i} is {x}, {p}")
        d, res = cliffs_delta(averages, best)
        print(f"Cliffs delta effect size of {i} is {d}, {res}")


# Method that determines the cohen d effect size.
def cohend(d1, d2):
    n1, n2 = len(d1), len(d2)
    s1, s2 = np.var(d1, ddof=1), np.var(d2, ddof=1)
    s = np.sqrt(((n1 - 1) * s1 + (n2 - 1) * s2) / (n1 + n2 - 2))
    u1, u2 = np.mean(d1), np.mean(d2)
    return (u1 - u2) / s


# Write genetic algorithm data to the csv file
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


# Parse all Pareto data and put this data in the global list variables.
def paretoFront(path, experiment):
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


# Main method that parses the data and writes the transformation counts and the results of the random algorithm to
# two seperate csv files.
def writeGAData():
    global globalTransformations
    global transformationsBest
    global experiments
    global avgInitial
    global avgBest

    with open(result_path + 'results_random.csv', 'w') as f, open(result_path + 'count_random.csv', 'w') as k:
        writer = csv.writer(f)
        counter = csv.writer(k)
        header = ['Experiment', 'seed', 'Generations used', 'Initial', 'Best', 'Final size', 'Means', 'Std']
        writer.writerow(header)
        # header2 = ['Experiment', 'Transformer', 'Count']
        # counter.writerow(header2)
        total = []
        F1_total = []
        MRR_total = []
        PMRR_total = []
        for curr in os.listdir(result_path):
            if os.path.isdir(result_path + curr):
                paretoFront(result_path, curr)
                transformations = []
                experiments.append(curr)
                initials = []
                bests = []
                trans = 0
                for file in os.listdir(result_path + curr):
                    t = writeData(curr, file, result_path + curr + '/' + file + '/GA_results.txt', writer)
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
        plotTransformations(temp)
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


# Plot the total transformations from all final individuals
def plotTransformations(counter):
    transformers = ['IfFalseElse', 'Lambda', 'RandomParameter', 'UnusedVariable', 'RenameVariable', 'IfTrue', 'AddNeutral']
    # transformers = []
    counts = []
    for i in counter.keys():
        # transformers.append(i.split('Transformer')[0])
        counts.append(counter.get(i))
    plt.bar(transformers, counts, color='red')
    plt.xlabel('Metamorphic transformers')
    plt.ylabel('Number of usages')
    plt.title('Occurrences per transformer in the final individuals after the genetic search')
    plt.show()


# Plots the baseline score and the score after the ga for the specified single metric experiments.
def plots():
    global experiments
    global avgInitial
    global avgBest
    basics = ['F1', 'MRR', 'Recall', 'Precision']
    pmrr_mrr = ['MRR', 'PMRR']
    pmrr = ['PMRR', 'MRR', 'F1', 'Recall', 'Precision']

    x = []
    y1 = []
    y2 = []
    for j in pmrr:
        i = experiments.index(j)
        x.append(experiments[i])
        y1.append(avgInitial[i])
        y2.append(avgBest[i])

    X_axis = np.arange(len(x))

    plt.bar(X_axis - 0.2, y1, 0.4, color='blue', label='Baseline score')
    plt.bar(X_axis + 0.2, y2, 0.4, color='darkorange', label='Score after GA')

    plt.ylim([0, 1])
    plt.xticks(X_axis, x)
    plt.xlabel('Experiments')
    plt.ylabel('Score')
    plt.title('Initial score vs best score averaged over 10 random seeds')
    plt.legend()
    plt.show()


# Creates the Pareto front for the genetic algorithm of all multi-metric experiments that include the number of
# transformations.
def paretoNoT():
    global paretoExperiments
    global paretoFront
    global experiments
    global avgInitial
    global transformationsBest
    global avgBest

    NoT = ['F1_NoT', 'MRR_NoT', 'Recall_NoT', 'Precision_NoT']
    metrics = ['F1', 'MRR', 'Recall', 'Precision']
    titles = ['F1-score and number of transformations', 'MRR and number of transformations',
              'Recall and number of transformations', 'Precision and number of transformations']
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
        plt.title(f"Pareto front {titles[label]}")
        plt.show()
        label += 1


# Creates a scatter plot for the Pareto front of the multi-metric experiments.
def scatter():
    global paretoExperiments
    global paretoFront

    metrics = ['F1_MRR', 'PMRR_MRR', 'PMRR_F1', 'RePr']
    titles = ['F1-score and MRR', 'percentage_MRR and MRR', 'percentage_MRR and F1-score', 'recall and precision']
    index = 0
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
            plt.xlabel(name[1])
            plt.ylabel(name[0])
        else:
            plt.ylabel('Recall')
            plt.xlabel('Precision')
        # plt.legend()
        plt.title(f"Pareto front {titles[index]}")
        plt.show()
        index += 1


# Parses the data from the random algorithm and writes this to a csv. Underneath this method the plot functions are
# called.
def basic_random_plots():
    with open(random_result_path + 'results_random.csv', 'w') as f:
        header = ['Experiment', 'Seed', 'Averages', 'Medians', 'Total average', 'Total median', 'Best', 'Worst']
        writer = csv.writer(f)
        writer.writerow(header)
        experimentsAverage = dict()
        for curr in os.listdir(random_result_path):
            if os.path.isdir(random_result_path + curr):
                fileMedians = []
                fileAverages = []
                histAverages = []
                for file in os.listdir(random_result_path + curr):
                    row = [curr, file, '', '', '', '', '', '']
                    result_path = random_result_path + curr + '/' + file + '/GA_results.txt'
                    with open(result_path, 'r') as results:
                        lines = results.readlines()
                        averages = []
                        medians = []
                        for line in lines:
                            if 'Generation: ' in line:
                                res = line.split(', ')
                                averages.append(res[3].split('average: ')[1].strip())  # average
                                medians.append(res[4].split('median: ')[1].strip())  # median
                            elif 'At the end of the algorithm the results are: , ' in line:
                                res = line.split(', ')
                                row[6] = res[1].split('best: ')[1].strip().replace('.', ',')  # Best
                                row[7] = res[2].split('worst: ')[1].strip().replace('.', ',')  # Worst
                                average = res[3].split('average: ')[1].strip()  # average
                                row[4] = average.replace('.', ',')
                                median = res[4].split('median: ')[1].strip()  # median
                                row[5] = median.replace('.', ',')
                                histAverages.append(float(average))
                        row[2] = averages
                        row[3] = medians
                        fileMedians.append(medians)
                        fileAverages.append(averages)
                    writer.writerow(row)

                metrics = ['PMRR', 'MRR', 'F1', 'Recall', 'Precision']
                # if curr in metrics:
                #     experimentsAverage[curr] = np.median(histAverages)
                #     random_Plot(fileAverages, curr)
                if 'NoT' in curr:
                    generations = random_Pareto(fileMedians)
                    random_scatter(generations, curr)
        # tripleHist(experimentsAverage)


# Creates the scatter plot with the baseline score, the scores of the random algorithm for each respective
# transformations, and the same for the genetic algorithm.
def random_scatter(generations, experiment):
    global paretoExperiments
    global paretoFront
    global experiments
    global avgInitial
    global transformationsBest
    global avgBest

    NoT = ['F1_NoT', 'MRR_NoT', 'Recall_NoT', 'Precision_NoT']
    titles = ['F1-score and number of transformations', 'MRR and number of transformations',
              'Recall and number of transformations', 'Precision and number of transformations']
    if experiment in NoT:
        j = paretoExperiments.index(experiment)
        index = NoT.index(experiment)
        plt.plot(avgInitial[experiments.index(experiment)], 0, 'o', label='Average initial score')
        plt.plot(avgBest[experiments.index(experiment)], transformationsBest[j], 'o', label='Average score after GA')
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
        y = range(20)
        plt.plot(generations[1:], y[1:], 'o', label='Median with random experiments')
        zRandom = np.polyfit(generations, y, 2)
        pRandom = np.poly1d(zRandom)
        xnewRandom = np.linspace(min(generations), max(generations), 300)
        plb.plot(xnewRandom, pRandom(xnewRandom))

        plt.plot(xMeans, ys, 'o', label='Median with GA')
        z = np.polyfit(xMeans, ys, 2)
        p = np.poly1d(z)
        xnew = np.linspace(min(xMeans), max(xMeans), 300)
        plb.plot(xnew, p(xnew))

        ax = plt.gca()
        ax.invert_xaxis()
        plt.xlabel(experiment.split('_')[0])
        plt.ylabel('Number of transformations')
        plt.legend()
        plt.title(f"Pareto front {titles[index]}")
        plt.show()
        index += 1


# Creates a histogram for the baseline score, the score after the GA, and the score from the random algorithm after
# 5, 10, and 20 transformations
def random_Plot(averages, experiment):
    global experiments
    global avgInitial
    global avgBest

    five = []
    ten = []
    twenty = []
    for i in range(10):
        five.append(float(averages[i][4]))
        ten.append(float(averages[i][9]))
        twenty.append(float(averages[i][19]))
    medianFive = np.median(five)
    medianTen = np.median(ten)
    medianTwenty = np.median(twenty)
    x = ['Baseline', '5 random', '10 random', '20 random', 'GA']
    index = experiments.index(experiment)
    y = [avgInitial[index], medianFive, medianTen, medianTwenty, avgBest[index]]

    plt.bar(x, y, color=['blue', 'green', 'green', 'green', 'darkorange'])

    axes = plt.gca()
    axes.yaxis.grid()
    plt.ylim([0, 1])
    plt.ylabel(f'{experiment} Score')
    plt.title(f'Comparing baseline, random search and genetic search\n for the code2vec model performance with {experiment} metric')
    plt.show()


# Creates a plot that displays the random score, the initial baseline score and the score after the GA for all metrics
def tripleHist(averages):
    global experiments
    global avgInitial
    global avgBest
    pmrr = ['PMRR', 'MRR', 'F1', 'Recall', 'Precision']

    x = []
    y1 = []
    y2 = []
    yAverages = []
    for j in pmrr:
        i = experiments.index(j)
        x.append(experiments[i])
        y1.append(avgInitial[i])
        y2.append(avgBest[i])
        yAverages.append(averages.get(j))

    X_axis = np.arange(len(x))

    plt.bar(X_axis - 0.2, y1, 0.2, color='blue', label='Baseline score')
    plt.bar(X_axis, yAverages, 0.2, color='green', label='Random')
    plt.bar(X_axis + 0.2, y2, 0.2, color='darkorange', label='Score after GA')

    plt.ylim([0, 1])
    plt.xticks(X_axis, x)
    plt.xlabel('Experiments')
    plt.ylabel('Score')
    plt.title('Initial, random search and best score averaged over 10 random seeds')
    plt.legend()
    plt.show()


# Returns list of all medians averaged over the random seeds
def random_Pareto(medians):
    generations = []
    for i in range(20):
        medianSum = 0
        for exp in medians:
            medianSum += float(exp[i])
        generations.append(medianSum/10)
    return generations


if __name__ == '__main__':
    # checkNormal()
    # wilcoxTest()
    writeGAData()
    # plots()
    # paretoNoT()
    scatter()
    # basic_random_plots()
