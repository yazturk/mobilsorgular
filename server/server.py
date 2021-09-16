#!/usr/bin/python3

import socket
import os

s = socket.socket()
print("Soket oluşturuldu.")
port = 1163
s.bind(("",port))
print("Soket %s porta bağlandı." %(port))
s.listen(5) # En fazla 5 bağlantı
print("Soket dinleniyor")
while True:
    c, addr = s.accept()
    print(addr, " bağlandı")
    sorgu = c.recv(1024).decode().split()
    if sorgu is None:
        pass
    elif sorgu[0] == "sorgu1":
        stream = os.popen('awk -f Sorgu1.awk tripdata.csv')
        output = stream.read()
        c.send(output.encode())
    elif sorgu[0] == "sorgu2":
        ilktarih = sorgu[1] + ' ' + sorgu[2]
        sontarih = sorgu[3] + ' ' + sorgu[4]
        sorgu2 = 'awk -f Sorgu2.awk -v ilktarih="' + ilktarih + '" -v sontarih="' + sontarih + '" tripdata.csv'
        stream = os.popen(sorgu2)
        output = stream.read()
        list1 = output.split()
        list2 = []
        i = 0
        while i<5:
            list2.append(list1[i*5])
            list2.append(list1[i*5 + 1])
            list2.append(list1[i*5 + 2])
            if list1[i*5] == "NULL":
                list2.append("NULL")
                list2.append("NULL")
                list2.append("NULL")
                list2.append("NULL")
            else:
                stream = os.popen('awk -f FindZone.awk -v locationid="' + list1[i*5 + 3] + '" taxi+_zone_lookup.csv')
                output = stream.read()
                output = output[1:-2]
                output = output.split('" "')
                list2.append(output[0])
                list2.append(output[1])
 
                stream = os.popen('awk -f FindZone.awk -v locationid="' + list1[i*5 + 4] + '" taxi+_zone_lookup.csv')
                output = stream.read()
                output = output[1:-2]
                output = output.split('" "')
                list2.append(output[0])
                list2.append(output[1])
            i+=1
        for item in list2:
            c.send((item + "\n").encode())
    elif sorgu[0] == "sorgu3":
        stream = os.popen('awk -f Sorgu3.awk tripdata.csv')
        output = stream.read()
        list1 = output.split()
        print(len(list1))
        list1 = [list1[2], list1[3], list1[6], list1[7]]
        for locationID in list1:
            stream = os.popen('awk -f FindZone.awk -v locationid="' + locationID + '" taxi+_zone_lookup.csv')
            output = stream.read()
            if output == "error":
                c.send("Hata: LocationID bulunamadı\n".encode())
            else:
                output = output[1:-2]
                output = output.split('" "')
                c.send((output[0] + "\n").encode())
                c.send((output[1] + "\n").encode())
    c.close()
