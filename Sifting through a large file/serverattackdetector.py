import datetime


def string_to_time(string):
    date_time_obj = datetime.datetime.strptime(string, '%Y-%m-%d %H:%M:%S.%f')
    return date_time_obj


class ServerAttackDetector:
    def __init__(self, path):
        self.path = path
        self.file = self.readfile()
        self.count = -1

    def readfile(self):
        for line in open(self.path, "r"):
            yield line

    def detect(self):
        prev = None
        prev_time = None

        for row in self.file:
            string = row.split(',')
            self.count += 1
            if string[2] == "UDP  " and string[12] == "suspicious":
                if prev is None:
                    prev = row
                    prev_time = string[0]
                else:
                    if float(string[1]) < 1 and (string_to_time(string[0]) - string_to_time(prev_time)).total_seconds() < 1:
                        return self.count, row
                    prev = row
                    prev_time = string[0]
        return None



d = ServerAttackDetector("CIDDS-001-external-week1.csv")
a, b = d.detect()
print(a)  # to verify whether your code is, indeed, performing lazy evaluation
print(b)  # to verify whether your code is, indeed, able to detect the first
# instance of a potential attack
e, f = d.detect()
print(e)
print(f)
g, h = d.detect()
print(g)
print(h)
