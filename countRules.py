import re
from collections import Counter
d = dict(Counter(re.findall('rule "\w+"', open("key.ui/examples/traces/test/checking_new.proof", "r").read())))
print(*sorted(d.items(), key=lambda kv: kv[1], reverse=True),sep='\n')
