#!/usr/bin/awk -f
BEGIN {
#	FS = ","
	FPAT = "([^,]+)|(\"[^\"]+\")"
}
{
	if(locationid == $1)
	{
		ilce = $2
		semt = $3
	}
}
END {
	if(ilce) {
		print ilce, semt
	}
	else print "error"
}
