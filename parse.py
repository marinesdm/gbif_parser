# coding: utf-8

import sys
import subprocess
import re
import time
from tqdm import tqdm
from random import randint

jar_path = "target/gbif-parser-wrapper-1.0.0.jar"

def parse_date(date_str):

    # Execute the java command with Popen and get the stdout from it
    cmd = ["java", "-jar", jar_path, date_str]

    a = subprocess.Popen(
        cmd,
        stdin=subprocess.PIPE,
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
    )

    # Process the stdout of the command
    keys = []
    stdout = []

    i=0
    for line in a.stdout:
        i += 1
        print(i, line)
        line = line.decode("utf-8").strip()
        nsep = line.count("=")
        if nsep==1:
            stdout.append(line.split("=")[1])
        elif nsep==2:
            date = re.search(r'DATE=(.*)\s',line).group(1)
            ordering = re.search(r'ORDERING=(.*)$',line).group(1)
            keys.append((date,ordering))
        else:
            raise Exception('Unexpected string')

    stderr = []
    for line in a.stderr:

        line = line.decode("utf-8").strip()
        iserror = (len(line.split("=")[1])!=0)
        if iserror:
            stderr.append(line)
        else:
            stderr.append('')

    a.terminate()

    #print(f"key: {keys}, stdout: {stdout}, stderr : {stderr}")

    return stdout, stderr


if __name__ == "__main__":

    # start=time.time()
    # datestr="('2003-06-02', 'YMD')"
    # for i in tqdm(range(10)):
    #     _, _= parse_date(datestr)
    # end=time.time()
    # print(f'TIME YMD [loop]: {round(end-start)}s')

#    start=time.time()
#    datestr="('2003-06-02', 'YDM')"
#    for i in tqdm(range(1000)):
#        _, _= parse_date(datestr)
#    end=time.time()
#    print(f'TIME YDM [loop]: {round(end-start)}s')

#    start=time.time()
#    datestr="('2003-06-02',)"
#    for i in tqdm(range(1000)):
#        _, _= parse_date(datestr)
#    end=time.time()
#    print(f'TIME None [loop]: {round(end-start)}s')

    print("YMD")
    print("**************************")
    start=time.time()
    datestr=[f"('2003-05-02', 'YMD')" for i in range(1000)]
    datestr=';'.join(datestr)
    stdout, stderr = parse_date(datestr)
    end=time.time()
    print(f'TIME YMD: {round(end-start)}s')

    print(f"Stdout : {stdout}")
    print(f"Stderr : {stderr}")
