#!/usr/bin/awk -f
#en uzun mesafeli 5 yolculuk
BEGIN {
        FS = ","
}

{
	eslesme = 0
        for(i=1; i <= 5; i++)
        {
           if(eslesme) #döngünün bir önceki adımından gelen değerler ile dizi elemanları arasında takas yapılır
           {
                gun = gecici_gun
                mesafe = gecici_mesafe
                gecici_gun = gunler[i]
                gecici_mesafe = mesafeler[i]
                gunler[i] = gun
                mesafeler[i] = mesafe
           }
           else {
                if (($1 == 1 || $1 == 2) && $5 > mesafeler[i]) #yeni bulunan mesafe listedekinden büyükse
                {
                     eslesme = 1
                     gecici_mesafe = mesafeler[i]
                     gecici_gun = gunler[i]
                     mesafeler[i] = $5
                     gunler[i] = $2
                }
            }
        }
}
 
END {
  for (i=1;i<=5; i++)
  {
    if (mesafeler[i]) {
        print mesafeler[i]
        print gunler[i]
    }
    else {
	print "NULL"
        print "NULL"
    }
  }
}
