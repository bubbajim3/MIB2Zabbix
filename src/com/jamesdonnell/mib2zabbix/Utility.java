package com.jamesdonnell.mib2zabbix;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import net.percederberg.mibble.Mib;
import net.percederberg.mibble.MibLoader;
import net.percederberg.mibble.MibLoaderException;
import net.percederberg.mibble.MibSymbol;
import net.percederberg.mibble.MibType;
import net.percederberg.mibble.MibValue;
import net.percederberg.mibble.MibValueSymbol;
import net.percederberg.mibble.snmp.SnmpObjectType;
import net.percederberg.mibble.value.ObjectIdentifierValue;

/** Utility class for common operations.
 * @author James A. Donnell Jr. */
public class Utility {

	/** Load a MIB using a String for a file location.
	 * @param fileLocation File location of MIB.
	 * @return Mib using Mibble.
	 * @throws IOException If error accessing file.
	 * @throws MibLoaderException If error parsing file. */
	public static Mib loadMib(String fileLocation) throws IOException, MibLoaderException {
		return loadMib(new File(fileLocation));
	}
	
	/** Load a MIB using a File for a file location.
	 * @param fileLocation File location of MIB.
	 * @return Mib using Mibble.
	 * @throws IOException If error accessing file.
	 * @throws MibLoaderException If error parsing file. */
	public static Mib loadMib(File fileLocation) throws IOException, MibLoaderException {
		MibLoader mibLoader = new MibLoader();
		mibLoader.addDir(fileLocation.getParentFile());
		return mibLoader.load(fileLocation);
	}
	
	/** Gets OID from MibSymbol.
	 * @param sym MibSymbol to get OID from.
	 * @return ObjectIdentifierValue. */
	public static ObjectIdentifierValue extractOID(MibSymbol sym) {
		if (sym instanceof MibValueSymbol) {
			MibValue value = ((MibValueSymbol) sym).getValue();
			if (value instanceof ObjectIdentifierValue)
				return (ObjectIdentifierValue) value;
		}
		return null;
	}
	
	/** Gets the type from MibSymbol.
	 * @param sym MibSymble to get type from.
	 * @return SnmpObjectType. */
	public static SnmpObjectType extractType(MibSymbol sym) {
		if (sym instanceof MibValueSymbol) {
			MibType type = ((MibValueSymbol) sym).getType();
			if (type instanceof SnmpObjectType)
				return (SnmpObjectType) type;
		}
		return null;
	}
	
	/** Simple method to print all valid OIDs from Mibble parse.
	 * @param mib MIB to print to stdout.	 */
	public static void printOIDs(Mib mib) {
		for(MibSymbol sym : mib.getAllSymbols()) {
			ObjectIdentifierValue oid = extractOID(sym);
			SnmpObjectType type = extractType(sym);
			if (oid != null && type != null)
				System.out.println(sym.getName() + " -- " + oid.toString() + " -- " + type.getSyntax().getName());
		}
	}
	
	/** Adds desired number of tabs and a newline to given string.
	 * @param tabNumber Number of tabs to add.
	 * @param original Original string to modify.
	 * @return Modified string. */
	public static String addTabNewLine(int tabNumber, String original) {
		String tabs = "";
		for (int i = 0; i < tabNumber; i++)
			tabs += "\t";
		return tabs + original + "\n";
	}
	
	/** Creates an XML opening tag for given tag title.
	 * @param tagTitle Title to use.
	 * @return XML open tag. */
	public static String createXMLOpenTag(String tagTitle) {
		return "<" + tagTitle + ">";
	}
	
	/** Creates an XML closing tag for given tag title.
	 * @param tagTitle Title to use.
	 * @return XML close tag. */
	public static String createXMLCloseTag(String tagTitle) {
		return "</" + tagTitle + ">";
	}
	
	/** Creates an XML unused tag for given tag title.
	 * @param tagTitle Title to use.
	 * @return XML unused tag. */
	public static String createXMLUnusedTag(String tagTitle) {
		return "<" + tagTitle + "/>";
	}
	
	/** Gets the current time in ISO 8601 long format.
	 * @return String of time. */
	public static String currentTimeISO8601Long() {
		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		df.setTimeZone(tz);
		return df.format(new Date());
	}
	
	/** Writes string to given file location.
	 * @param string String to write.
	 * @param fileLocation Location to write string.
	 * @return True if written, false if failed. */
	public static boolean writeString(String string, String fileLocation) {
		try {
			File file = new File(fileLocation);
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(string);
			bw.close();
			fw.close();
			return true;
		} catch (IOException e) {
			return false;
		}
	}
}