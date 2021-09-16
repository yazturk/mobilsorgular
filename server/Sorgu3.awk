#!/usr/bin/awk -f
BEGIN {
        enkisa_mesafe = 1000000000
        enuzun_mesafe = 0
        FS = ","
}
{
        if (NR>1 && $1 > 0 && $4 >= 3)
        {
                if ($5 < enkisa_mesafe) 
                {
                        enkisa_mesafe = $5
                        enkisa_kalkis = $8
                        enkisa_varis = $9
                        enkisa_satir = NR
                }
                if ($5 > enuzun_mesafe) 
                {
                        enuzun_mesafe = $5
                        enuzun_kalkis = $8
                        enuzun_varis = $9
                        enuzun_satir = NR
                }
         }
}
END {
        print enkisa_satir, enkisa_mesafe, enkisa_kalkis, enkisa_varis
        print enuzun_satir, enuzun_mesafe, enuzun_kalkis, enuzun_varis
} 
