
import csv
import matplotlib.pyplot as plt






cat_name_list = [
    "rmOneCAT",
    "rmTwoCAT",
    "rmThreeCAT",
    "rmFourCAT",
    "rmFiveCAT",
    "rmSixCAT",
    "rmSevenCAT",
    "rmEightCAT",
    "rmNineCAT",
    "rmTenCAT",
]

cat_name_dicts = {k: v+1 for v, k in enumerate(cat_name_list)}
stats_dict = dict()
nodes_stats_dict = dict()
time_stats_dict = dict()
avg_stats_dict = dict()

for cat_name in cat_name_dicts:
    csv_cat = cat_name + '.key.csv'

    with open(csv_cat, newline='') as csvfile:
        statsreader = csv.reader(csvfile, delimiter=',', quotechar='|')
        stats_dict[cat_name] = dict()
        for row in statsreader:
            if(len(row) ==  2):
                stats_dict[cat_name][row[0]] = row[1]
            if(len(row) >  2):
                stats_dict[cat_name][row[0]] = "".join(row[1:])


for cat_name in cat_name_dicts:
    nodes_stats_dict[cat_name] = int(stats_dict[cat_name]["Nodes"])
    time_stats_dict[cat_name] = float(stats_dict[cat_name]["Automode time"].split("ms")[0])
    avg_stats_dict[cat_name] = float(stats_dict[cat_name]["Avg. time per step"].split("ms")[0])


metrics = {
    "Nodes": nodes_stats_dict,
    "Time": time_stats_dict,
    "Avg. Time": avg_stats_dict}

fig, axs = plt.subplots(1,3)
fig.subplots_adjust(wspace=0.5, hspace=10)
fig.set_figheight(5)
fig.set_figwidth(15)
axis_n = 0
for key in metrics.keys():
    axs[axis_n].set_title(key + " Metric")
    axs[axis_n].set_xlabel("Nr. of Calls")
    axs[axis_n].set_ylabel(key)

    new_dict = dict((cat_name_dicts[key], value) for (key, value) in metrics[key].items())
    axs[axis_n].plot(*zip(*sorted(new_dict.items())), linestyle='-', marker='o')
    axis_n += 1
fig.savefig('result_metrics.png', dpi=100)
fig.canvas.manager.set_window_title('Results of 2-hoare-logic/2-simple-CATs-benchmark.sh')

with open('result-benchmark-hoare-logic.csv', 'w') as f:
    f.write("NCAT;nodes;time;avgtimerule")
    for cat_name in cat_name_dicts:
        f.write("\n" + str(cat_name_dicts[cat_name]) + ";" + str(nodes_stats_dict[cat_name]) + ";" + str(time_stats_dict[cat_name]) + ";" + str(avg_stats_dict[cat_name]) )

plt.show()





