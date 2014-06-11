Kosmor
======

A collection of tools I made for Kosmor, a browser game you can find over at kosmor.de oder kosmor.com.

As I dropped development before cleaning up, some stuff is messy or redundant. Basically, there are three tools:

The Kosmor Ship Counter. Copy and paste a list of ships from the navigation view or from a battle report and press the button. It will show the combat power for each type of ship pe player and house, as well as the the totals. That's it.

The Distance and Traveltime Calculator allows users to enter two coordinates, select whether the use can jump and tell it to use the longer ranged jump of independent players. Output is the distance in LY and the days it takes to fly there. It will assume that the jump drive is ready right now.

Finally there is the Kosmor Map Tool. I allows parsing the .svg based map Kosmor, extracting the info available there (so mostly planets with their name, owner, colour etc.). You can choose whether to download the maps yourself or to allow the Map Tool to login to Kosmor using your account.

You need to have a settings.ini with your password in plain text in the same folder as the map tool if you want to automate the download, so it's not advisable to expose that folder. Obviously the data avaiable at runtime, but the Map Tool itself does not store it.

Once it parsed the map, which might take a while for big maps, it will create an .html file with a nice table that's even sortable and filterable, assuming you got the right .css and .js files, which are not included in the project itself right now. You can sort and filter by all (?) columns, and calculate the distance between up to three objects.

It will also put out a .csv with the most important info, and rename and copy the map for archive purposes.

You can configure the behaviour via parameters on the command line, manually editting the settings.ini or using the Map Tool Configuration Tool to do the later for you.

I don't think I added an overview over all parameters, so check out what the Configuration Tool does to the .ini...

The other stuff is mostly either the early stages of the Map Tool, or something I began near the end of development for Kosmor, so likely unfished.

Overall, there is a lot i'd do different now, but they did (and do, just that I don't need them currently) their job.
