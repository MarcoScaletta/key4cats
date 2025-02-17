import os
import csv
import math
import matplotlib.pyplot as plt
import sys
import argparse


parser = argparse.ArgumentParser()
parser.add_argument('--lastMany', dest='lastMany', type=str, help='Plot last Many \"n\" results"')
args = parser.parse_args()
lastMany = int(args.lastMany) if(args.lastMany != None) else None

directory = os.fsencode("results/")
filenames = []
for file in os.listdir(directory):
    print("loading " + os.fsdecode(file))
    filenames += [os.fsdecode(file)]
filenames.sort()

testFileNamesSet  = set()
numberOfResults = len(filenames)

for file in os.listdir("src/test/resources/"):
    if(os.fsdecode(file) != "tracerules.key" and os.fsdecode(file).endswith(".key")):
        testFileNamesSet.add(os.fsdecode(file))

for i in range(numberOfResults):
    filename = "results/" + filenames[i]
    with open(filename, newline='') as csvfile:
        reader = csv.reader(csvfile, delimiter=',')
        for i in reader:
            testFileNamesSet.add(i[0]+".key")

testFileNames = list(testFileNamesSet)
testFileNames.sort()
map_of_results = dict()
map_of_results['success'] = dict()
map_of_results['fail'] = dict()
for f in testFileNames:
    if(f.endswith("Fail.key")):
        map_of_results['fail'][f] = numberOfResults*[float('nan')]
    else:
        map_of_results['success'][f] = numberOfResults*[float('nan')]


for index in range(numberOfResults):
    filename = "results/" + filenames[index]
    with open(filename, newline='') as csvfile:
        reader = csv.reader(csvfile, delimiter=',')
        for i in reader:
            if(i[0].endswith("Fail")):
                map_of_results['fail'][i[0]+".key"][index] = int(i[1])
            else:
                map_of_results['success'][i[0]+".key"][index] = int(i[1])
x = range(numberOfResults if (lastMany == None) else lastMany)
fig, axs = plt.subplots(1,2)
mapSuccessLastMany = map_of_results['success']
for k in mapSuccessLastMany:
    l = mapSuccessLastMany[k] if (lastMany == None) else mapSuccessLastMany[k][-lastMany:]
    axs[0].plot(x, l,  marker='o')
#     axs[0].set_yscale('log')
    axs[0].set_title("Succeeding proofs")


mapFailLastMany = map_of_results['fail']

for k in mapFailLastMany:
    l = mapFailLastMany[k] if (lastMany == None) else mapFailLastMany[k][-lastMany:]
    axs[1].plot(x, l, marker='o')
#     axs[1].set_yscale('log')
    axs[1].set_title("Failing proofs")
plt.show()
