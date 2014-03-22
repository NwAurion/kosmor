/* Created on 18.12.2004 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class SVGMapTool {

   public static void main (String[] args) {
	   args = new String[3];
	   args[0] = "-in:kosmor.svg";
	   args[1] = "-out:test.svg";
	   args[2] = "-dir:d:/";
      for (int i = 0; i < args.length; i++) {
         if (args[i].startsWith ("-in:")) inFile = args[i].substring ("-in:".length ());
         if (args[i].startsWith ("-out:")) outFile = args[i].substring ("-out:".length ());
         if (args[i].startsWith ("-dir:")) dir = args[i].substring ("-dir:".length ());
         if (args[i].startsWith ("-date:")) {
            dateFile = args[i].substring ("-date:".length ());
            parseDate = true;
         }
         if (args[i].startsWith ("-title:")) dateHeader = args[i].substring ("-title:".length ());
         if (args[i].startsWith ("-removeHouseWPs")) removeOwnWPs = true;
         if (args[i].startsWith ("-removeHouseShips")) removeOwnShips = true;
         if (args[i].startsWith ("-hideOwnStarSystems")) hideOwnSystems = true;
         if (args[i].startsWith ("-writeDateFile")) exportDate = true;
         if (args[i].startsWith ("-center:")) centerName = args[i].substring ("-center:".length ()).trim ();
         if (args[i].startsWith ("-combine:")) combine.add (args[i].substring ("-combine:".length ()).trim ());
         if (args[i].startsWith ("-removeColor")) removeColor = true;
         if (args[i].startsWith ("-updateMapFile:")) updateMapFile = args[i].substring ("-updateMapFile:".length ()).trim ();
      }
      if (inFile != null && outFile != null && dir != null) {
         action ();
         System.exit (1);
      } else {
         System.err.println ("parameters are missing");
         System.exit (-1);
      }
   } // main
   
   /** die eigenen WPs entfernen */
   private static boolean removeOwnWPs;
   /** die Farbe bei den eigenen Systemen entfernen */
   private static boolean hideOwnSystems;
   /** eigene Schiffe entfernen */
   private static boolean removeOwnShips;
   /** ob das Datum gesetzt werden soll */
   private static boolean parseDate;
   /** der Header für das Datum */
   private static String dateHeader = "";
   /** das Verzeichnis mit den Dateien */
   private static String dir = null;
   /** die Datei mit dem Datum */
   private static String dateFile = null;
   /** die Dati mit der Karte */
   private static String inFile = null;
   /** die Ausgabedatei */
   private static String outFile = null;
   /** ob eine Datei mit dem Datum ausgegeben werden soll */
   private static boolean exportDate;
   /** das neue initiale Objekt zum zentrieren */
   private static String centerName;
   /** diese Maps mit dazubasteln */
   private static LinkedList<String> combine = new LinkedList<String> ();
   /** ob alle Farben entfernt werden sollen */
   private static boolean removeColor;
   /** Datei mit den Planis drin */
   private static String updateMapFile = null;
   
   private static void action () {
      try {
         String dateS = "";
         if (parseDate) {
            System.out.println ("loading date file");
            try {
               String s = readFile (new File (dir + File.separator + dateFile));
               if (s.indexOf ("Date: <b>") != -1) {
                  dateS = s.substring (s.indexOf ("Date: <b>") + 9, s.indexOf ("</b>", s.indexOf ("Date: <b>")));
               } else {
                  dateS = s.substring (s.indexOf ("Datum: <b>") + 10, s.indexOf ("</b>", s.indexOf ("Datum: <b>")));
               }
               dateS = dateS.substring (dateS.indexOf ('/')+1) + "-" + dateS.substring (0, dateS.indexOf ('/'));
            } catch (Exception exc) {
               parseDate = false;
               exc.printStackTrace (System.err);
            }
         }
         System.out.println ("loading map");
         String s = readFile (new File (dir + File.separator + inFile));
         System.out.println ("removing line breaks");
         // die unnützen Zeilenumbrüche bei den Namen entfernen
         s = s.replaceAll ("\r?\n?</text>", "</text>");
         s = s.replaceAll ("\"\r?\nclass=", "\" class=");
         s = s.replaceAll ("\">\r?\n", "\">");
         int idx = s.indexOf ("class=\"name");
         List<Planet> map = null;
         if (updateMapFile != null) {
            System.out.println ("loading map data file");
            // laden
            map = loadMap ();
            // aktuelle planis
            List<Planet> newPlanis = parsePlanets (s, true);
            List<Planet> both = new LinkedList<Planet> (map);
            both.retainAll (newPlanis);
            System.out.print ("checking for new planets ... ");
            // gibt es neue?
            if (both.size () > 0) {
               Planet p = (Planet)both.get (0);
               Planet p1 = (Planet)newPlanis.get (newPlanis.indexOf (p));
               int dx = p1.x - p.x;
               int dy = p1.y - p.y;
               List<Planet> l = new LinkedList<Planet> (newPlanis);
               l.removeAll (map);
               System.out.println (l.size () + " found");
               for (Iterator<Planet> it = l.iterator (); it.hasNext ();) {
                  p = (Planet)it.next ();
                  p.transform (dx, dy);
                  map.add (p);
               }
            }
            System.out.println ("removing unnames planets");
            // alle unbekannten entfernen
            for (Iterator<Planet> it = map.iterator (); it.hasNext ();) {
               Planet p = (Planet)it.next ();
               if (p.name.equals ("???"))
                  for (Iterator<Planet> itm = map.iterator (); itm.hasNext ();) {
                     Planet p1 = (Planet)itm.next ();
                     if (p != p1 && p.x == p1.x && p.y == p1.y) {
                        it.remove ();
                        break;
                     }
                  }
            }
            System.out.println ("saving map data file");
            // wieder rausschreiben
            if (map.size () > 0) {
               FileWriter fw = new FileWriter (dir + File.separator + updateMapFile);
               for (Iterator<Planet> it = map.iterator (); it.hasNext ();) {
                  Planet p = (Planet)it.next ();
                  fw.write (p.save () + "\r\n");
               }
               fw.flush ();
               fw.close ();
            }
         }
         for (Iterator<String> it = combine.iterator (); it.hasNext ();) {
            try {
               String combine = readFile (new File (dir + File.separator + it.next ()));
               s = combine (s, combine);
            } catch (Exception exc) {}
         }
         if (map != null) {
            System.out.println ("adding planets from the map data file to the map");
            s = combine (s, parsePlanets (s, false), map);
         }
         if (parseDate || dateHeader.length () > 0) {
            System.out.println ("replacing header");
            s = s.replaceFirst (".title.>.*<", "\"title\">" + (dateHeader.length () > 0 ? dateHeader + (parseDate ? " - " : ""): "") + dateS + "<");
         }
         if (parseDate && exportDate) {
            System.out.println ("saving data file");
            FileWriter fw = new FileWriter (dir + File.separator + dateS + ".svg");
            fw.write (s);
            fw.flush ();
            fw.close ();
         }
         if (hideOwnSystems) {
            System.out.println ("removing own systems");
            s = s.replaceAll ("ffff00", "ff8000");
            s = s.replaceAll ("8c8c00", "8c4600");
            s = s.replaceAll ("4c4d00", "572300");
            s = s.replaceAll ("radarown", "radar");
         }
         if (removeOwnShips) {
            System.out.println ("removing own ships");
            s = s.replaceAll ("class=.wpnameships. style=.fill..ff8000", "class=\"wpname\" style=\"fill:#ff8000");
            s = s.replaceAll ("class=.wpnameships. style=.fill..ffff00", "class=\"wpname\" style=\"fill:#ffff00");
            s = s.replaceAll ("class=.wpnameships. style=.fill..ff0000", "class=\"wpname\" style=\"fill:#ffff00");
            s = s.replaceAll ("class=.nameships. style=.fill..ffff00", "class=\"name\" style=\"fill:#ffff00");
            s = s.replaceAll ("class=.nameships. style=.fill..8c8c00", "class=\"name\" style=\"fill:#8c8c00");
            s = s.replaceAll ("class=.nameships. style=.fill..4c4d00", "class=\"name\" style=\"fill:#4c4d00");
            s = s.replaceAll ("class=.nameships. style=.fill..ff8000", "class=\"name\" style=\"fill:#ff8000");
            s = s.replaceAll ("class=.nameships. style=.fill..8c4600", "class=\"name\" style=\"fill:#8c4600");
            s = s.replaceAll ("class=.nameships. style=.fill..572300", "class=\"name\" style=\"fill:#572300");
            s = s.replaceAll ("class=.nameships. style=.fill..ff0000", "class=\"name\" style=\"fill:#ff0000");
            s = s.replaceAll ("class=.nameships. style=.fill..570000", "class=\"name\" style=\"fill:#570000");
            s = s.replaceAll ("class=.nameships. style=.fill..8c0000", "class=\"name\" style=\"fill:#8c0000");
         }
         if (removeOwnWPs) {
            System.out.println ("removing own WPs");
            s = s.replaceAll ("<line .*\r?\n?.* style=.stroke..3B5775.. .>.*\r*\n*.*<circle .* r=.2. fill=..ff0000. .>\r?\n?", "");
            s = s.replaceAll ("<line .*\r?\n?.* style=.stroke..3B5775.. .>.*\r*\n*.*<text .*\r?\n?.*class=.wp.*. style=.fill..ff0000..*<.text>\r?\n?", "");
            s = s.replaceAll ("<text .*\r?\n?.*class=.wp.*. style=.fill..ff0000..*<.text>\r?\n?", "");
            s = s.replaceAll ("<line .*\r?\n?.* style=.stroke..3B5775.. .>.*\r*\n*.*<circle .* r=.2. fill=..ffff00. .>\r?\n?", "");
            s = s.replaceAll ("<line .*\r?\n?.* style=.stroke..3B5775.. .>.*\r*\n*.*<text .*\r?\n?.*class=.wp.*. style=.fill..ffff00..*<.text>\r?\n?", "");
            s = s.replaceAll ("<text .*\r?\n?.*class=.wp.*. style=.fill..ffff00..*<.text>\r?\n?", "");
            s = s.replaceAll ("<line .*\r?\n?.* style=.stroke..3B5775.. .>.*\r*\n*.*<circle .* r=.2. fill=..ff8000. .>\r?\n?", "");
            s = s.replaceAll ("<line .*\r?\n?.* style=.stroke..3B5775.. .>.*\r*\n*.*<text .*\r?\n?.*class=.wp.*. style=.fill..ff8000..*<.text>\r?\n?", "");
            s = s.replaceAll ("<text .*\r?\n?.*class=.wp.*. style=.fill..ff8000..*<.text>\r?\n?", "");
         }
         if (centerName != null && centerName.length () > 0) {
            System.out.println ("changing center position");
            idx = s.indexOf (centerName);
            if (idx != -1) {
               idx = s.substring (0, idx).lastIndexOf ("<text x=\"") + "<text x=\"".length ();
               int idx2 = s.indexOf ('\"', idx);
               long x = Long.parseLong (s.substring (idx, idx2));
               idx = s.indexOf ("y=\"", idx2) + "y=\"".length ();
               idx2 = s.indexOf ('\"', idx);
               long y = Long.parseLong (s.substring (idx, idx2));
               s = s.replaceFirst ("svgDocument.documentElement.currentTranslate.x = -.*-window.innerWidth.2.;",
                              "svgDocument.documentElement.currentTranslate.x = -("+ x + "-window.innerWidth/2);");
               s = s.replaceFirst ("svgDocument.documentElement.currentTranslate.y = -.*-window.innerHeight.2.;",
                              "svgDocument.documentElement.currentTranslate.y = -("+ y + "-window.innerHeight/2);");
            }
         }
         if (removeColor) {
            for (Iterator<String> it = colorReplacer.keySet ().iterator (); it.hasNext ();) {
               String color = (String)it.next ();
               s = s.replaceAll (color, (String)colorReplacer.get (color));
            }
         }
         System.out.println ("saving map");
         FileWriter fw = new FileWriter (dir + File.separator + outFile);
         fw.write (s);
         fw.flush ();
         fw.close ();
      } catch (Exception exc) {
         exc.printStackTrace (System.err);
      }
   } // action
   
   public static String readFile (File f) throws Exception {
      StringBuffer sb = new StringBuffer ();
      BufferedReader br = new BufferedReader (new FileReader (f));
      char[] buf = new char[1024];
      int read = -1;
      while ((read = br.read (buf)) != -1) {
         sb.append (buf, 0, read);
      }
      br.close ();
      return (sb.toString ());
   } // readFile

   private static String combine (String map, String combine) {
      return (combine (map, parsePlanets (map, false), parsePlanets (combine, false)));
   } // combine
   
   private static String combine (String map, List<Planet> exist, List<Planet> add) {
      List<Planet> both = new LinkedList<Planet> (add);
      both.retainAll (exist);
      if (both.size () > 0) {
         Planet p = (Planet)both.get (0);
         Planet p1 = (Planet)exist.get (exist.indexOf (p));
         int dx = p.x - p1.x;
         int dy = p.y - p1.y;
         StringBuffer sb = new StringBuffer (map);
         int pos = map.indexOf ("     <g id=\"tools\"");
         add.removeAll (exist);
         int c = 0;
         for (Iterator<Planet> it = add.iterator (); it.hasNext ();) {
            c++;
            p = (Planet)it.next ();
            p.transform (dx, dy);
            sb.insert (pos, p.printSVG ());
         }
         System.out.println (dx + " " + dy + " " + c);
         map = sb.toString ();
      }
      return (map);
   } // combine
   
   private static List<Planet> parsePlanets (String map, boolean replaceColor) {
      List<Planet> l = new LinkedList<Planet> ();
      int idx = map.indexOf ("class=\"name");
      while (idx != -1) {
         int start = map.substring (0, idx).lastIndexOf ("<circle cx=\"");
         int end = map.indexOf ("</text>", start);
         l.add (Planet.create (map.substring (start, end+7), replaceColor));
         map = map.substring (end);
         idx = map.indexOf ("class=\"name");
      }
      return (l);
   } // parsePlanets
   
   private static class Planet {
      int x, y;
      String name, color;
      static Planet create (String s, boolean replaceColor) {
         int start = s.indexOf (";\">") + 3;
         int end = s.indexOf ("</text>");
         String name = s.substring (start, end);
         start = s.indexOf ("cx=\"") + 4;
         end = s.indexOf ("\"", start);
         int x = Integer.parseInt (s.substring (start, end));
         start = s.indexOf ("cy=\"") + 4;
         end = s.indexOf ("\"", start);
         int y = Integer.parseInt (s.substring (start, end));
         start = s.indexOf ("style=\"fill:#") + 13;
         end = s.indexOf (";", start);
         String color = s.substring (start, end);
         if (replaceColor) {
            String c = (String)colorReplacer.get (color);
            if (c != null) color = c;
         }
         return (new Planet (name, x, y, color));
      } // create
      Planet (String name, int x, int y, String color) {
         this.name = name;
         this.x = x;
         this.y = y;
         this.color = color;
      } // constructor
      void transform (int dx, int dy) {
         this.x = this.x - dx;
         this.y = this.y - dy;
      } // getString
      String printSVG () {
         return ("<circle cx=\"" + this.x + "\" cy=\"" + this.y + "\" r=\"1\" fill=\"#" + this.color + "\" />\r\n" +
               (this.name.equals ("???") ? "" :
               ("<text x=\"" + (this.x+1) + "\" y=\"" + (this.y-1) + "\" " +
               "class=\"name\" style=\"fill:#" + this.color + ";\">" +
               this.name + "</text>")));
      } // printSVG
      static Planet load (String s) {
         Planet p = null;
         try {
            String[] values = s.split (":");
            p = new Planet (values[0], Integer.parseInt (values[1]),
                        Integer.parseInt (values[2]), values[3]);
         } catch (Exception exc) {}
         return (p);
      } // load
      String save () {
         return (this.name + ":" + this.x + ":" + this.y + ":" + this.color);
      } // save
      public boolean equals (Object obj) {
         return (this.name.equals("???") ? super.equals (obj) : this.name.equals (((Planet)obj).name));
      } // equals
      public int hashCode () { return (this.name.equals ("???") ? super.hashCode () : this.name.hashCode ()); }
      public String toString () { return (this.name); }
   } // class
   
   static List<Planet> loadMap () {
      List<Planet> l = new LinkedList<Planet> ();
      try {
         BufferedReader r = new BufferedReader (new FileReader (new File (dir + File.separator + updateMapFile)));
         String s;
         while ((s = r.readLine ()) != null) {
            try {
               l.add (Planet.load (s));
            } catch (Exception exc) {
               System.out.println ("invalid planet: " + s);
            }
         }
         r.close ();
      } catch (Exception exc) {
         exc.printStackTrace (System.err);
      }
      return (l);
   } // loadMap
   
   private static HashMap<String,String> colorReplacer = new HashMap<String, String> ();
   
   static {
      // gelb
      colorReplacer.put ("ffff00", "dcdcdc");
      colorReplacer.put ("8c8c00", "b4b4b4");
      colorReplacer.put ("4c4d00", "787878");
      // orange
      colorReplacer.put ("ff8000", "dcdcdc");
      colorReplacer.put ("8c4600", "b4b4b4");
      colorReplacer.put ("4c4d00", "787878");
      colorReplacer.put ("572300", "787878");
      // rot
      colorReplacer.put ("ff0000", "dcdcdc");
      colorReplacer.put ("8c0000", "b4b4b4");
      colorReplacer.put ("570000", "787878");
      // lila
      colorReplacer.put ("ff00ff", "dcdcdc");
      colorReplacer.put ("8c008c", "b4b4b4");
      colorReplacer.put ("4c004d", "787878");
      colorReplacer.put ("aa00ff", "dcdcdc");
      colorReplacer.put ("5d008c", "787878");
      colorReplacer.put ("470061", "787878");
      colorReplacer.put ("4d0033", "787878");
      // türkis
      colorReplacer.put ("00ffaa", "dcdcdc");
      colorReplacer.put ("008c5e", "b4b4b4");
      colorReplacer.put ("004d33", "787878");
      // hellblau
      colorReplacer.put ("00aaff", "dcdcdc");
      colorReplacer.put ("", "");
      colorReplacer.put ("00334d", "787878");
      // hellblau 2
      colorReplacer.put ("00ffff", "dcdcdc");
      colorReplacer.put ("008c8c", "b4b4b4");
      colorReplacer.put ("004d4d", "787878");
      // pink
      colorReplacer.put ("ff00aa", "dcdcdc");
      colorReplacer.put ("8c005e", "b4b4b4");
      colorReplacer.put ("", "");
      // blau
      colorReplacer.put ("0000ff", "dcdcdc");
      colorReplacer.put ("0055ff", "dcdcdc");
      colorReplacer.put ("5500ff", "dcdcdc");
      colorReplacer.put ("00008c", "b4b4b4");
      colorReplacer.put ("002f8c", "b4b4b4");
      colorReplacer.put ("005e8c", "b4b4b4");
      colorReplacer.put ("2f008c", "b4b4b4");
      colorReplacer.put ("1a007f", "787878");
      // hellgrün
      colorReplacer.put ("aaff00", "dcdcdc");
      colorReplacer.put ("5d8c00", "b4b4b4");
      colorReplacer.put ("", "");
      // fremdrot
      colorReplacer.put ("ff0055", "dcdcdc");
      colorReplacer.put ("8c002f", "b4b4b4");
      colorReplacer.put ("570024", "787878");
      // grün
      colorReplacer.put ("00ff55", "dcdcdc");
      colorReplacer.put ("008c2f", "b4b4b4");
      colorReplacer.put ("004d1a", "787878");
      //
      colorReplacer.put ("334d00", "787878");
      colorReplacer.put ("", "");
      colorReplacer.put ("", "");
   } // static
} // class