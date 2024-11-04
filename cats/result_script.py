import os
import csv
import math
import matplotlib.pyplot as plt


directory = os.fsencode("results/")
filenames = []
for file in os.listdir(directory):
    print("loading " + os.fsdecode(file))
    filenames += [os.fsdecode(file)]
filenames.sort()

testFileNamesSet  = set()

for file in os.listdir("src/test/resources/"):
    if(os.fsdecode(file) != "tracerules.key" and os.fsdecode(file).endswith(".key")):
        testFileNamesSet.add(os.fsdecode(file))

for i in range(len(filenames)):
    filename = "results/" + filenames[i]
    with open(filename, newline='') as csvfile:
        reader = csv.reader(csvfile, delimiter=',')
        for i in reader:
            testFileNamesSet.add(i[0]+".key")

testFileNames = list(testFileNamesSet)
testFileNames.sort()
numberOfResults = len(filenames)
map_of_results = dict()
map_of_results['success'] = dict()
map_of_results['fail'] = dict()
for f in testFileNames:
    if(f.endswith("Fail.key")):
        map_of_results['fail'][f] = numberOfResults*[float('nan')]
    else:
        map_of_results['success'][f] = numberOfResults*[float('nan')]


for index in range(len(filenames)):
    filename = "results/" + filenames[index]
    with open(filename, newline='') as csvfile:
        reader = csv.reader(csvfile, delimiter=',')
        for i in reader:
            if(i[0].endswith("Fail")):
                map_of_results['fail'][i[0]+".key"][index] = int(i[1])
            else:
                map_of_results['success'][i[0]+".key"][index] = int(i[1])
x = range(numberOfResults)
fig, axs = plt.subplots(1,2)
for k in map_of_results['success']:
    l = map_of_results['success'][k]
    axs[0].plot(x, l,  marker='o')
#     axs[0].set_yscale('log')
    axs[0].set_title("Succeeding proofs")


for k in map_of_results['fail']:
    l = map_of_results['fail'][k]
    axs[1].plot(x, l, marker='o')
#     axs[1].set_yscale('log')
    axs[1].set_title("Failing proofs")
plt.show()
