/* Created on 16.05.2004 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class GNAnalyser {

	public final static String dir = "D:\\test\\";

	public final static String DATE = "Galaktische Nachrichten des Tages ";
	public final static String NAMECHANGE = "<h3>Galaktische Namens&auml;nderungen</h3>";
	public final static String FIGHTS = "<h3>Kämpfe in der Galaxie</h3>";
	public final static String INDEPENDENCE = "<h3>Unabhängigkeitserklärungen</h3>";
	public final static String END = "<td class=\"row1\" width=\"150\" align=\"left\" valign=\"middle\"><span class=\"nav\"><a href=\"#top\" class=\"nav\">Nach oben</a></span></td>";
	public final static String HOUSE = "Haus <b>";
	public final static String COMBAT_POWER = "(vernichtete Kampfkraft: <b>";
	public final static String IND_MID = " erklärt sich unabhängig vom Haus ";

	static class Conquest {
		static enum Type {
			PLANET, IMPERATOR
		}

		final String date;
		final Imperator imperator;
		final Type type;

		Conquest(String date, Imperator imperator, Type type) {
			this.date = date;
			this.imperator = imperator;
			this.type = type;
		} // constructor

		@Override
		public String toString() {
			return (date + " - "
					+ (type == Type.IMPERATOR ? "imperator" : "planet") + " " + imperator.name);
		} // toString
	} // class

	static class Action {
		static enum Type {
			PLANET, IMPERATOR, PLANET_LOST, CONQUESTED, INDEPENDENCE, RENAME, REVOLUTION
		}

		final Type type;
		final Imperator imp;
		final String date;

		Action(String date, Type type, Imperator imp) {
			this.date = date;
			this.type = type;
			this.imp = imp;
		} // constructor

		@Override
		public String toString() {
			if (type == Type.PLANET) {
				return (date + " planet " + imp.getAllNames());
			}
			if (type == Type.IMPERATOR) {
				return (date + " imperator " + imp.getAllNames());
			}
			if (type == Type.PLANET_LOST) {
				return (date + " planet lost to " + imp.getAllNames());
			}
			if (type == Type.CONQUESTED) {
				return (date + " conquered by " + imp.getAllNames());
			}
			if (type == Type.INDEPENDENCE) {
				return (date + " independence");
			}
			return ("");
		}
	} // class

	static class HouseAction extends Action {
		final Imperator owner;
		final String house;

		HouseAction(Imperator owner, String date, Action.Type type,
				Imperator imp, String house) {
			super(date, type, imp);
			this.owner = owner;
			this.house = house;
		} // constructor

		@Override
		public String toString() {
			if (type == Type.PLANET) {
				return (date + " " + owner.name + " conquers a planet from "
						+ imp.name + " - " + house);
			}
			if (type == Type.IMPERATOR) {
				return (date + " " + owner.name + " conquers the imperator "
						+ imp.name + " - " + house);
			}
			if (type == Type.PLANET_LOST) {
				return (date + " " + owner.name + " lost a planet to "
						+ imp.name + " - " + house);
			}
			if (type == Type.CONQUESTED) {
				return (date + " " + owner.name + " was conquered by "
						+ imp.name + " - " + house);
			}
			if (type == Type.INDEPENDENCE) {
				return (date + " " + owner.name + " declares independence");
			}
			return ("");
		} // toString
	} // class

	static class Imperator {
		String name;
		String date;
		final LinkedList<String> names = new LinkedList<String>();
		final LinkedList<Conquest> conquests = new LinkedList<Conquest>();
		final LinkedList<Action> actions = new LinkedList<Action>();

		Imperator(String date, String name) {
			this.date = date;
			this.name = name;
		} // constructor

		void setName(String date, String name) {
			names.add(0, this.name);
			this.date = date;
			this.name = name;
		} // addName

		void addPlanetConquest(String date, Imperator imperator) {
			plConquestCount++;
			conquests.add(new Conquest(date, imperator, Conquest.Type.PLANET));
			actions.add(new Action(date, Action.Type.PLANET, imperator));
			imperator.actions.add(new Action(date, Action.Type.PLANET_LOST,
					this));
		} // addPlanetConquest

		void addImperatorConquest(String date, Imperator imperator) {
			impConquestCount++;
			conquests
					.add(new Conquest(date, imperator, Conquest.Type.IMPERATOR));
			actions.add(new Action(date, Action.Type.IMPERATOR, imperator));
			imperator.actions
					.add(new Action(date, Action.Type.CONQUESTED, this));
		} // addImperatorConquest

		void addIndependence(String date) {
			actions.add(new Action(date, Action.Type.INDEPENDENCE, null));
		} // addImperatorConquest

		String getAllNames() {
			String s = name;
			for (int i = 0; i < names.size(); i++) {
				if (i == 0) {
					s += " (";
				}
				s += names.get(i);
				if (i == names.size() - 1) {
					s += ")";
				} else {
					s += ", ";
				}
			}
			return (s);
		} // getAllNames

		public String getHeader() {
			return (getAllNames() + " - " + getConquestCount() + ", "
					+ getImperatorConquestCount() + ", "
					+ getPlanetConquestCount() + "\r\n");
		} // getHeader

		@Override
		public String toString() {
			if (conquests.size() == 0) {
				return ("");
			}
			String s = getHeader();
			for (Conquest c : conquests) {
				s += "  " + c.toString() + "\r\n";
			}
			return (s);
		} // toString

		int getConquestCount() {
			return (conquests.size());
		}

		int plConquestCount = 0;

		int getPlanetConquestCount() {
			return (plConquestCount);
		} // getPlanetConquestCount

		int impConquestCount = 0;

		int getImperatorConquestCount() {
			return (impConquestCount);
		} // getImperatorConquestCount
	} // class

	static class House extends Imperator {
		House(String date, String name) {
			super(date, name);
		} // constructor

		void addPlanetConquest(String date, Imperator attacker,
				Imperator target, String house) {
			conquests.add(new Conquest(date, target, Conquest.Type.PLANET));
			actions.add(new HouseAction(attacker, date, Action.Type.PLANET,
					target, house));
		} // addPlanetConquest

		void addPlanetLost(String date, Imperator attacker, Imperator target,
				String house) {
			actions.add(new HouseAction(attacker, date,
					Action.Type.PLANET_LOST, target, house));
		} // addPlanetConquest

		void addImperatorConquest(String date, Imperator attacker,
				Imperator target, String house) {
			conquests.add(new Conquest(date, target, Conquest.Type.IMPERATOR));
			actions.add(new HouseAction(attacker, date, Action.Type.IMPERATOR,
					target, house));
		} // addImperatorConquest

		void addImperatorLost(String date, Imperator attacker,
				Imperator target, String house) {
			actions.add(new HouseAction(attacker, date, Action.Type.CONQUESTED,
					target, house));
		} // addImperatorConquest

		void addIndependence(String date, Imperator imperator) {
			actions.add(new HouseAction(imperator, date,
					Action.Type.INDEPENDENCE, null, null));
		} // addImperatorConquest
	} // class

	static class Fight {
		final Imperator looser, winner;
		final House looserHouse, winnerHouse;
		final String date;
		long combatPower;

		Fight(Imperator looser, House looserHouse, Imperator winner,
				House winnerHouse, String date, long cp) {
			if (looser == null) {
				throw new IllegalArgumentException("looser is null");
			}
			if (looserHouse == null) {
				throw new IllegalArgumentException("looserHouse is null");
			}
			if (winner == null) {
				throw new IllegalArgumentException("winner is null");
			}
			this.looser = looser;
			this.winner = winner;
			this.looserHouse = looserHouse;
			this.winnerHouse = winnerHouse;
			this.date = date;
			combatPower = cp;
		} // constructor

		@Override
		public boolean equals(Object o) {
			if (o == null) {
				return (false);
			}
			if (!(o instanceof Fight)) {
				return (false);
			}
			Fight f = (Fight) o;
			return (f.looser == looser && f.winner == winner && f.date
					.equals(date));
		} // equals
	} // class

	static final HashMap<String, Imperator> imperators = new HashMap<String, Imperator>();

	static final LinkedList<Imperator> imperatorsList = new LinkedList<Imperator>();

	static final HashMap<String, House> houses = new HashMap<String, House>();

	static final LinkedList<House> housesList = new LinkedList<House>();

	static String date;

	static final LinkedList<String> dates = new LinkedList<String>();

	static final LinkedList<Fight> fights = new LinkedList<Fight>();

	static long impConquests, planetConquests;

	public static void main(String[] args) {
		File f = new File(dir);
		String[] files = f.list();
		for (String file : files) {
			if (file.endsWith(".php") || file.endsWith(".htm")) {
				renameFile(dir, file);
			}
		}
		f = new File(dir);
		files = f.list();
		Arrays.sort(files);
		for (String file : files) {
			if (file.endsWith(".html")) {
				parse(dir + file);
			}
		}
		try {
			f = new File(dir + "imperators.txt");
			File f1 = new File(dir + "imperators.ranking.txt");
			f.createNewFile();
			f1.createNewFile();
			LinkedList<Imperator> l = new LinkedList<Imperator>();
			for (Imperator im : imperatorsList) {
				if (im.getConquestCount() > 0 && !l.contains(im)) {
					l.add(im);
				}
			}
			Collections.sort(l, new Comparator<Imperator>() {
				@Override
				public int compare(Imperator i1, Imperator i2) {
					int ret = new Integer(i2.getConquestCount())
							.compareTo(new Integer(i1.getConquestCount()));
					if (ret == 0) {
						ret = new Integer(i2.getImperatorConquestCount())
								.compareTo(new Integer(i1
										.getImperatorConquestCount()));
					}
					return (ret);
				}
			});
			BufferedWriter w = new BufferedWriter(new FileWriter(f));
			BufferedWriter w1 = new BufferedWriter(new FileWriter(f1));
			w.write(l.size() + "\r\n");
			w1.write(l.size() + "\r\n");
			int count = 0;
			for (Imperator im : l) {
				w.write(++count + ". " + im.toString());
				w1.write(count + ". " + im.getHeader());
			}
			w.flush();
			w1.flush();
			w.close();
			w1.close();

			f = new File(dir + "imperators.names.txt");
			f.createNewFile();
			w = new BufferedWriter(new FileWriter(f));
			count = 0;
			for (Imperator im : l) {
				w.write(++count + ". " + im.getAllNames() + "\r\n");
			}
			w.flush();
			w.close();

			f = new File(dir + "imperators.complete.txt");
			f.createNewFile();
			l = new LinkedList<Imperator>();
			for (Imperator im : imperatorsList) {
				if (!l.contains(im)) {
					l.add(im);
				}
			}
			Collections.sort(l, new Comparator<Imperator>() {
				@Override
				public int compare(Imperator i1, Imperator i2) {
					return (i1.name.compareTo(i2.name));
				}
			});
			w = new BufferedWriter(new FileWriter(f));
			w.write(l.size() + "\r\n");
			count = 0;
			for (Imperator im : l) {
				w.write(im.getAllNames() + "\r\n");
				for (Action a : im.actions) {
					w.write("  " + a.toString() + "\r\n");
				}
			}
			w.flush();
			w.close();

			f = new File(dir + "houses.txt");
			f.createNewFile();
			f1 = new File(dir + "houses.ranking.txt");
			f1.createNewFile();
			LinkedList<House> lHouse = new LinkedList<House>();
			for (House h : housesList) {
				if (h.getConquestCount() > 0 && !lHouse.contains(h)) {
					lHouse.add(h);
				}
			}
			Collections.sort(lHouse, new Comparator<House>() {
				@Override
				public int compare(House h1, House h2) {
					int ret = new Integer(h2.getConquestCount())
							.compareTo(new Integer(h1.getConquestCount()));
					if (ret == 0) {
						ret = new Integer(h2.getImperatorConquestCount())
								.compareTo(new Integer(h1
										.getImperatorConquestCount()));
					}
					return (ret);
				}
			});
			w = new BufferedWriter(new FileWriter(f));
			w1 = new BufferedWriter(new FileWriter(f1));
			w.write(lHouse.size() + "\r\n");
			w1.write(lHouse.size() + "\r\n");
			count = 0;
			for (House h : lHouse) {
				w.write(++count + ". " + h.toString());
				w1.write(count + ". " + h.getHeader());
			}
			w.flush();
			w.close();
			w1.flush();
			w1.close();

			f = new File(dir + "houses.complete.txt");
			f.createNewFile();
			lHouse = new LinkedList<House>();
			for (House h : housesList) {
				if (!lHouse.contains(h)) {
					lHouse.add(h);
				}
			}
			Collections.sort(lHouse, new Comparator<House>() {
				@Override
				public int compare(House h1, House h2) {
					return (h1.name.compareTo(h2.name));
				}
			});
			w = new BufferedWriter(new FileWriter(f));
			w.write(lHouse.size() + "\r\n");
			count = 0;
			for (House h : lHouse) {
				w.write(h.getAllNames() + "\r\n");
				for (Action a : h.actions) {
					w.write("  " + a.toString() + "\r\n");
				}
			}
			w.flush();
			w.close();

			f = new File(dir + "fights.txt");
			long cp = 0;
			f.createNewFile();
			Collections.sort(fights, new Comparator<Fight>() {
				@Override
				public int compare(Fight f1, Fight f2) {
					return (new Long(f2.combatPower).compareTo(new Long(
							f1.combatPower)));
				}
			});
			w = new BufferedWriter(new FileWriter(f));
			int c = 0;
			w.write("# fights:     " + fights.size() + "\r\n");
			w.write("# imperators: " + impConquests + "\r\n");
			w.write("# planets:    " + planetConquests + "\r\n");
			for (Fight fight : fights) {
				cp += fight.combatPower;
				if (fight.combatPower > 0) {
					w.write(++c + ". " + fight.combatPower + " " + fight.date
							+ " ");
					if (fight.winnerHouse == null) {
						w.write(fight.winner.name + " ");
					} else {
						w.write(fight.winnerHouse.name + " ("
								+ fight.winner.name + ") vs. ");
					}
					w.write(fight.looserHouse.name + " (" + fight.looser.name
							+ ")\r\n");
				}
			}
			w.write("sum:     " + cp + "\r\n");
			w.write("cleared: "
					+ (cp - impConquests * 7 - planetConquests * 12) + "\r\n");
			w.flush();
			w.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	} // main

	static boolean renameFile(String dir, String fileName) {
		File f = new File(dir + fileName);
		String data = "";
		try {
			BufferedReader r = new BufferedReader(new FileReader(f));
			String s;
			while ((s = r.readLine()) != null) {
				data += s;
			}
			r.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (data.length() > 100) {
			int idx0 = data.indexOf(DATE);
			if (idx0 == -1) {
				System.err.println("kein Datum!");
				return (false);
			}
			idx0 += DATE.length();
			date = data.substring(idx0, data.indexOf("</h3>"));
			while (date.length() < 8) {
				date = "0" + date;
			}
			date = date.substring(4) + "-" + date.substring(0, 3);
			if (dates.contains(date)) {
				return (false);
			}
			dates.add(date);
			f.renameTo(new File(dir + date + ".html"));
		}
		return (true);
	} // renameFile

	static boolean parse(String fileName) {
		File f = new File(fileName);
		String data = "";
		try {
			BufferedReader r = new BufferedReader(new FileReader(f));
			String s;
			while ((s = r.readLine()) != null) {
				data += s;
			}
			r.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (data.length() > 100) {
			int idx0 = data.indexOf(DATE);
			if (idx0 == -1) {
				System.err.println("kein Datum!");
				return (false);
			}
			idx0 += DATE.length();
			date = data.substring(idx0, data.indexOf("</h3>"));
			while (date.length() < 8) {
				date = "0" + date;
			}
			date = date.substring(4) + "-" + date.substring(0, 3);
			if (dates.contains(date)) {
				return (false);
			}
			dates.add(date);
			System.out.println(date);
			int idx1 = data.indexOf(NAMECHANGE);
			int idx2 = data.indexOf(FIGHTS);
			int idx3 = data.indexOf(INDEPENDENCE);
			int idx4 = data.indexOf(END);
			if (idx1 != -1) {
				parseNameChanges(data.substring(idx1, idx2));
			}
			if (idx2 != -1) {
				parseConquests(data.substring(idx2, idx3));
			}
			if (idx3 != -1) {
				parseDeclarationOfIndependence(data.substring(idx3, idx4));
			}
		}
		return (true);
	} // parse

	static void parseNameChanges(String data) {
		String[] items = splitString(data, "<br>");
		for (String item : items) {
			parseNameChange(item);
		}
	} // parseNameChanges

	static void parseNameChange(String data) {
		String[] items = splitString(data, "</b>");
		if (items.length < 4) {
			return;
		}
		String oldName = items[0].substring(items[0].indexOf("<b>") + 3);
		String oldHouse = items[1].substring(items[1].indexOf("<b>") + 3);
		String newName = items[2].substring(items[2].indexOf("<b>") + 3);
		String newHouse = items[3].substring(items[3].indexOf("<b>") + 3);
		if (!oldName.equals(newName)) {
			Imperator i = getImperator(oldName);
			if (i == null) {
				i = new Imperator("3500-000", oldName);
			}
			imperators.remove(oldName);
			i.setName(date, newName);
			imperators.put(newName, i);
		}
		if (!oldHouse.equals(newHouse)) {
			House h = getHouse(oldName);
			if (h == null) {
				h = new House("3500-000", oldHouse);
			}
			houses.remove(oldHouse);
			h.setName(date, newHouse);
			houses.put(newHouse, h);
		}
	} // parseNameChange

	static void parseConquests(String data) {
		String[] items = splitString(data, "<LI>");
		for (int i = 1; i < items.length; i++) {
			parseConquest(items[i]);
		}
	} // parseConquests

	static void parseConquest(String data) {
		boolean failedImpConquest = data
				.startsWith("Ein Schiffsangriff auf den");
		String[] items = splitString(data, "<br>");
		Imperator i1 = null;
		House h1 = null;
		String s = items[1];
		String name2 = s.substring(s.indexOf("<b>") + 3, s.indexOf("</b>"));
		String house2 = s.substring(s.indexOf(HOUSE) + HOUSE.length(),
				s.indexOf("</b>)"));
		Imperator i2 = createImperator(name2);
		House h2 = createHouse(house2);
		if (!failedImpConquest) {
			s = items[0];
			boolean impConquest = s.indexOf("gliedert den Herrscher") != -1;
			String name1 = s.substring(s.indexOf("<b>") + 3, s.indexOf("</b>"));
			String house1 = s.substring(s.indexOf(HOUSE) + HOUSE.length(),
					s.indexOf("</b>)"));
			i1 = createImperator(name1);
			h1 = createHouse(house1);
			if (impConquest) {
				i1.addImperatorConquest(date, i2);
				h1.addImperatorConquest(date, i1, i2, house2);
				h2.addImperatorLost(date, i2, i1, house1);
				impConquests++;
			} else {
				i1.addPlanetConquest(date, i2);
				h1.addPlanetConquest(date, i1, i2, house2);
				h2.addPlanetLost(date, i2, i1, house1);
				planetConquests++;
			}
		} else {
			i1 = createImperator("failed wp attack");
		}
		int idx1 = data.indexOf(COMBAT_POWER);
		if (idx1 != -1) {
			idx1 += COMBAT_POWER.length();
			String cp = data.substring(idx1, data.indexOf("</b>", idx1));
			try {
				Fight f = new Fight(i2, h2, i1, h1, date, Long.parseLong(cp));
				if (f.combatPower > 0) {
					addFight(f);
				}
			} catch (Exception e) {
				e.printStackTrace(System.err);
			}
		}
	} // parseConquest

	static void addFight(Fight f) {
		if (fights.contains(f)) {
			int idx = fights.indexOf(f);
			Fight ff = fights.get(idx);
			if (ff.combatPower < f.combatPower) {
				fights.remove(ff);
				fights.add(f);
			}
		} else {
			fights.add(f);
		}
	} // addFight

	static void parseDeclarationOfIndependence(String data) {
		String[] items = null;
		if (data.indexOf("</LI>") == -1) {
			items = splitString(data, "<br>");
		} else {
			items = splitString(data, "</LI>");
		}
		for (String item : items) {
			if (item.indexOf(IND_MID) != -1) {
				String imp = item.substring(item.indexOf(' ') + 1,
						item.indexOf(IND_MID));
				while (imp.indexOf(' ') != -1) {
					imp = imp.substring(imp.indexOf(' ') + 1);
				}
				createImperator(imp).addIndependence(date);
			}
		}
	} // parseDeclarationOfIndependence

	static Imperator createImperator(String name) {
		Imperator i = getImperator(name);
		if (i == null) {
			i = new Imperator(date, name);
			imperators.put(name, i);
			imperatorsList.add(i);
		}
		return (i);
	} // createImperator

	static Imperator getImperator(String name) {
		return (imperators.get(name));
	} // getImperator

	static House createHouse(String name) {
		House h = getHouse(name);
		if (h == null) {
			h = new House(date, name);
			houses.put(name, h);
			housesList.add(h);
		}
		return (h);
	} // createHouse

	static House getHouse(String name) {
		return (houses.get(name));
	} // getHouse

	public static String[] splitString(String s, String d) {
		if (s == null || d == null) {
			return (new String[0]);
		}
		int start = 0;
		int end = 0;
		LinkedList<String> l = new LinkedList<String>();
		try {
			end = s.indexOf(d, start);
			while (end != -1) {
				l.add(s.substring(start, end));
				start = end + d.length();
				end = s.indexOf(d, start);
			}
		} catch (Exception exc) {
			exc.printStackTrace(System.err);
		}
		if (start != s.length()) {
			l.add(s.substring(start));
		}
		String[] tmp = new String[l.size()];
		Iterator<String> it = l.iterator();
		int pos = 0;
		while (it.hasNext()) {
			tmp[pos++] = it.next();
		}
		return (tmp);
	} // splitString
} // class