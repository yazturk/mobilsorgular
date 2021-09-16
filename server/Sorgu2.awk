#!/usr/bin/awk -f
# Verilen iki tarih arasında en kısa mesafeli 5 yolculuk
BEGIN {
        for(i=1;i<=5;i++) mesafeler[i] = 1000000000
        FS = ","
}

{
        eslesme = 0
        for(i=1; i <=5; i++)
        {
            if(eslesme) #döngünün bir önceki adımından gelen değerler ile dizi elemanları arasında takas yapılır
            {
               gun = gecici_gun
               mesafe = gecici_mesafe
	       kalkis = gecici_kalkis
	       varis = gecici_varis

               gecici_gun = gunler[i]
               gecici_mesafe = mesafeler[i]
	       gecici_kalkis = kalkislar[i]
	       gecici_varis = varislar[i]

               gunler[i] = gun
               mesafeler[i] = mesafe
	       kalkislar[i] = kalkis
	       varislar[i] = varis
            }
            else if ($5 < mesafeler[i] && ilktarih < $2 && sontarih > $2) 
            #yeni bulunan mesafe listedekinden küçükse
            {
                eslesme = 1
                gecici_mesafe = mesafeler[i]
                gecici_gun = gunler[i]
		gecici_kalkis = kalkislar[i]
		gecici_varis = varislar[i]

                mesafeler[i] = $5
                gunler[i] = $2     
		kalkislar[i] = $8
		varislar[i] = $9
            }
        }
 }
 
 END {
 for (i=1;i<=5; i++)
 {
        if (gunler[i]) {
		print mesafeler[i]
	        print gunler[i]
		print kalkislar[i]
		print varislar[i]
	}
	else {
		print "NULL"
		print "NULL"
		print "NULL"
		print "NULL"
		print "NULL"
	}
 }
 }
