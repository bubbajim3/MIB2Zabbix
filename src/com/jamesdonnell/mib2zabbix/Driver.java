package com.jamesdonnell.mib2zabbix;

import java.io.IOException;

import net.percederberg.mibble.Mib;
import net.percederberg.mibble.MibLoaderException;

/** Driver class containing main method for application.
 * @author James A. Donnell Jr. */
public class Driver {

	public final static String SNMPv2 = "v2", SNMPv3="v3";
	
	/** Main method. Accepts MIB files as argument.
	 * @param args MIB file to convert. */
	public static void main(String[] args) {
		try {
			Mib mib = Utility.loadMib(args[0]);
			boolean v3 = false;
			if(args.length > 1)
				v3 = args[1].equals(SNMPv3);
			Utility.writeString(GenerateZabbixTemplate.generate(mib, v3), args[0] + ".zabbix.xml");
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return;
		} catch (MibLoaderException e) {
			System.out.println(e.getMessage());
			return;
		}
	}
}