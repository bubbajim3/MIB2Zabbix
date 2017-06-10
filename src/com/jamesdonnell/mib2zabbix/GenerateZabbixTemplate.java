package com.jamesdonnell.mib2zabbix;

import net.percederberg.mibble.Mib;
import net.percederberg.mibble.MibSymbol;
import net.percederberg.mibble.snmp.SnmpObjectType;
import net.percederberg.mibble.value.ObjectIdentifierValue;

/** Generates the Zabbix XML Template.
 * @author James A. Donnell Jr. */
public class GenerateZabbixTemplate {

	/** Default XML Header. */
	private static final String xmlHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	
	/** Current Zabbix Version. */
	private static final String currentVersion = "3.2";
	
	/** Default group for new Templates. */
	private static final String defaultTemplate = "Templates";
	
	/** Default application group for new items. */
	private static final String defaultApplication = "MIB2Zabbix Convert";
	
	/** Default template description. */
	private static final String defaultDescription = "MIB2Zabbix Conversion";
	
	/** Generates the Template using given MIB file.
	 * @param mib MIB file to generate template from.
	 * @param v3 Whether or not template should be SNMPv3.
	 * @return String of XML Template. */
	public static String generate(Mib mib, boolean v3) {
		if (v3) {
			String tempMessage = "SNMPv3 support not available yet!";
			System.out.println(tempMessage);
			return tempMessage;
		}
		
		String result = Utility.addTabNewLine(0, xmlHeader); // XML Header
		result += Utility.addTabNewLine(0, Utility.createXMLOpenTag(ZabbixTags.zabbixExport)); // Zabbix export open 

		// Basic info
		result += Utility.addTabNewLine(1, Utility.createXMLOpenTag(ZabbixTags.version) + currentVersion + Utility.createXMLCloseTag(ZabbixTags.version));
		result += Utility.addTabNewLine(1, Utility.createXMLOpenTag(ZabbixTags.date) + Utility.currentTimeISO8601Long() + Utility.createXMLCloseTag(ZabbixTags.date));

		// Defines Zabbix-wide "Templates" Group
		result += Utility.addTabNewLine(1, Utility.createXMLOpenTag(ZabbixTags.groups));
		result += Utility.addTabNewLine(2, Utility.createXMLOpenTag(ZabbixTags.group));
		result += Utility.addTabNewLine(3, Utility.createXMLOpenTag(ZabbixTags.name) + defaultTemplate + Utility.createXMLCloseTag(ZabbixTags.name));
		result += Utility.addTabNewLine(2, Utility.createXMLCloseTag(ZabbixTags.group));
		result += Utility.addTabNewLine(1, Utility.createXMLCloseTag(ZabbixTags.groups));

		result += Utility.addTabNewLine(1, Utility.createXMLOpenTag(ZabbixTags.templates)); // Templates open
		result += Utility.addTabNewLine(2, Utility.createXMLOpenTag(ZabbixTags.template)); // Template open

		// Template information
		result += Utility.addTabNewLine(3, Utility.createXMLOpenTag(ZabbixTags.template) + mib.getName() + Utility.createXMLCloseTag(ZabbixTags.template));
		result += Utility.addTabNewLine(3, Utility.createXMLOpenTag(ZabbixTags.name) + mib.getName() + Utility.createXMLCloseTag(ZabbixTags.name));
		result += Utility.addTabNewLine(3, Utility.createXMLOpenTag(ZabbixTags.description) + mib.getName() + " via " + defaultDescription + Utility.createXMLCloseTag(ZabbixTags.description));

		// Template group(s)
		result += Utility.addTabNewLine(3, Utility.createXMLOpenTag(ZabbixTags.groups));
		result += Utility.addTabNewLine(4, Utility.createXMLOpenTag(ZabbixTags.group));
		result += Utility.addTabNewLine(5, Utility.createXMLOpenTag(ZabbixTags.name) + defaultTemplate + Utility.createXMLCloseTag(ZabbixTags.name));
		result += Utility.addTabNewLine(4, Utility.createXMLCloseTag(ZabbixTags.group));
		result += Utility.addTabNewLine(3, Utility.createXMLCloseTag(ZabbixTags.groups));

		// Template application(s)
		result += Utility.addTabNewLine(3, Utility.createXMLOpenTag(ZabbixTags.applications));
		result += Utility.addTabNewLine(4, Utility.createXMLOpenTag(ZabbixTags.application));
		result += Utility.addTabNewLine(5, Utility.createXMLOpenTag(ZabbixTags.name) + defaultApplication + Utility.createXMLCloseTag(ZabbixTags.name));
		result += Utility.addTabNewLine(4, Utility.createXMLCloseTag(ZabbixTags.application));
		result += Utility.addTabNewLine(3, Utility.createXMLCloseTag(ZabbixTags.applications));

		// Template items
		result += Utility.addTabNewLine(3, Utility.createXMLOpenTag(ZabbixTags.items));
		for(MibSymbol symbol : mib.getAllSymbols()) {
			result += generateItem(symbol, 4, v3);
		}
		result += Utility.addTabNewLine(3, Utility.createXMLCloseTag(ZabbixTags.items));

		// Template Misc (Unused on conversion)
		result += Utility.addTabNewLine(3, Utility.createXMLUnusedTag(ZabbixTags.discoveryRules));
		result += Utility.addTabNewLine(3, Utility.createXMLUnusedTag(ZabbixTags.macros));
		result += Utility.addTabNewLine(3, Utility.createXMLUnusedTag(ZabbixTags.templates));
		result += Utility.addTabNewLine(3, Utility.createXMLUnusedTag(ZabbixTags.screens));

		result += Utility.addTabNewLine(2, Utility.createXMLCloseTag(ZabbixTags.template)); // Template close
		result += Utility.addTabNewLine(1, Utility.createXMLCloseTag(ZabbixTags.templates)); // Templates close

		result += Utility.addTabNewLine(1, Utility.createXMLUnusedTag(ZabbixTags.triggers)); // Triggers
		result += Utility.addTabNewLine(1, Utility.createXMLUnusedTag(ZabbixTags.valuemaps)); // Value maps

		result += Utility.addTabNewLine(0, Utility.createXMLCloseTag(ZabbixTags.zabbixExport)); // Zabbix export close
		
		return result;
	}

	/** Generate individual item.
	 * @param sym MibSymbol to generate item from/
	 * @param tabNum Number of tabs item starts at.
	 * @param v3 Whether or not item should be SNMPv3.
	 * @return String of item in XML. */
	private static String generateItem(MibSymbol sym, int tabNum, boolean v3) {
		ObjectIdentifierValue oid = Utility.extractOID(sym);
		SnmpObjectType type = Utility.extractType(sym);
		if (oid == null || type == null)
			return "";
		
		String syntax = type.getSyntax().getName();
		if (syntax.equals("SEQUENCE") || syntax.equals("OBJECT IDENTIFIER"))
			return "";
		
		String valueType = "";
		if (syntax.equals("INTEGER"))
			valueType = "0";
		else
			valueType = "4";
		
		String result = Utility.addTabNewLine(tabNum, Utility.createXMLOpenTag(ZabbixTags.item));
		result += Utility.addTabNewLine(tabNum+1, Utility.createXMLOpenTag(ZabbixTags.name) + sym.getName() + Utility.createXMLCloseTag(ZabbixTags.name));
		result += Utility.addTabNewLine(tabNum+1, Utility.createXMLOpenTag(ZabbixTags.type) + "4" + Utility.createXMLCloseTag(ZabbixTags.type));
		result += Utility.addTabNewLine(tabNum+1, Utility.createXMLOpenTag(ZabbixTags.snmpCommunity) + "{$SNMP_COMMUNITY}" + Utility.createXMLCloseTag(ZabbixTags.snmpCommunity));
		result += Utility.addTabNewLine(tabNum+1, Utility.createXMLOpenTag(ZabbixTags.multiplier) + "0" + Utility.createXMLCloseTag(ZabbixTags.multiplier));
		result += Utility.addTabNewLine(tabNum+1, Utility.createXMLOpenTag(ZabbixTags.snmpOID) + oid.toString() + Utility.createXMLCloseTag(ZabbixTags.snmpOID));
		result += Utility.addTabNewLine(tabNum+1, Utility.createXMLOpenTag(ZabbixTags.key) + sym.getName() + Utility.createXMLCloseTag(ZabbixTags.key));
		result += Utility.addTabNewLine(tabNum+1, Utility.createXMLOpenTag(ZabbixTags.delay) + "300" + Utility.createXMLCloseTag(ZabbixTags.delay));
		result += Utility.addTabNewLine(tabNum+1, Utility.createXMLOpenTag(ZabbixTags.history) + "90" + Utility.createXMLCloseTag(ZabbixTags.history));
		result += Utility.addTabNewLine(tabNum+1, Utility.createXMLOpenTag(ZabbixTags.trends) + "365" + Utility.createXMLCloseTag(ZabbixTags.trends));
		result += Utility.addTabNewLine(tabNum+1, Utility.createXMLOpenTag(ZabbixTags.status) + "0" + Utility.createXMLCloseTag(ZabbixTags.status));
		result += Utility.addTabNewLine(tabNum+1, Utility.createXMLOpenTag(ZabbixTags.valueType) + valueType + Utility.createXMLCloseTag(ZabbixTags.valueType));
		result += Utility.addTabNewLine(tabNum+1, Utility.createXMLUnusedTag(ZabbixTags.allowedHosts));
		result += Utility.addTabNewLine(tabNum+1, Utility.createXMLUnusedTag(ZabbixTags.units));
		result += Utility.addTabNewLine(tabNum+1, Utility.createXMLOpenTag(ZabbixTags.delta) + valueType + Utility.createXMLCloseTag(ZabbixTags.delta));
		result += Utility.addTabNewLine(tabNum+1, Utility.createXMLUnusedTag(ZabbixTags.snmpv3ContextName));
		result += Utility.addTabNewLine(tabNum+1, Utility.createXMLUnusedTag(ZabbixTags.snmpv3SecurityName));
		result += Utility.addTabNewLine(tabNum+1, Utility.createXMLOpenTag(ZabbixTags.snmpv3SecurityLevel) + "0" + Utility.createXMLCloseTag(ZabbixTags.snmpv3SecurityLevel));
		result += Utility.addTabNewLine(tabNum+1, Utility.createXMLOpenTag(ZabbixTags.snmpv3AuthProtocol) + "0" + Utility.createXMLCloseTag(ZabbixTags.snmpv3AuthProtocol));
		result += Utility.addTabNewLine(tabNum+1, Utility.createXMLUnusedTag(ZabbixTags.snmpv3AuthPassphrase));
		result += Utility.addTabNewLine(tabNum+1, Utility.createXMLOpenTag(ZabbixTags.snmpv3PrivProtocol) + "0" + Utility.createXMLCloseTag(ZabbixTags.snmpv3PrivProtocol));
		result += Utility.addTabNewLine(tabNum+1, Utility.createXMLUnusedTag(ZabbixTags.snmpv3PrivPassphrase));
		result += Utility.addTabNewLine(tabNum+1, Utility.createXMLOpenTag(ZabbixTags.formula) + "1" + Utility.createXMLCloseTag(ZabbixTags.formula));
		result += Utility.addTabNewLine(tabNum+1, Utility.createXMLUnusedTag(ZabbixTags.delayFlex));
		result += Utility.addTabNewLine(tabNum+1, Utility.createXMLUnusedTag(ZabbixTags.params));
		result += Utility.addTabNewLine(tabNum+1, Utility.createXMLUnusedTag(ZabbixTags.ipmiSensor));
		result += Utility.addTabNewLine(tabNum+1, Utility.createXMLOpenTag(ZabbixTags.dataType) + "0" + Utility.createXMLCloseTag(ZabbixTags.dataType));
		result += Utility.addTabNewLine(tabNum+1, Utility.createXMLOpenTag(ZabbixTags.authtype) + "0" + Utility.createXMLCloseTag(ZabbixTags.authtype));
		result += Utility.addTabNewLine(tabNum+1, Utility.createXMLUnusedTag(ZabbixTags.username));
		result += Utility.addTabNewLine(tabNum+1, Utility.createXMLUnusedTag(ZabbixTags.password));
		result += Utility.addTabNewLine(tabNum+1, Utility.createXMLUnusedTag(ZabbixTags.publickey));
		result += Utility.addTabNewLine(tabNum+1, Utility.createXMLUnusedTag(ZabbixTags.privatekey));
		result += Utility.addTabNewLine(tabNum+1, Utility.createXMLUnusedTag(ZabbixTags.port));
		result += Utility.addTabNewLine(tabNum+1, Utility.createXMLUnusedTag(ZabbixTags.description));
		result += Utility.addTabNewLine(tabNum+1, Utility.createXMLOpenTag(ZabbixTags.inventoryLink) + "0" + Utility.createXMLCloseTag(ZabbixTags.inventoryLink));
		result += Utility.addTabNewLine(tabNum+1, Utility.createXMLOpenTag(ZabbixTags.applications));
		result += Utility.addTabNewLine(tabNum+2, Utility.createXMLOpenTag(ZabbixTags.application));
		result += Utility.addTabNewLine(tabNum+3, Utility.createXMLOpenTag(ZabbixTags.name) + defaultApplication + Utility.createXMLCloseTag(ZabbixTags.name));
		result += Utility.addTabNewLine(tabNum+2, Utility.createXMLCloseTag(ZabbixTags.application));
		result += Utility.addTabNewLine(tabNum+1, Utility.createXMLCloseTag(ZabbixTags.applications));
		result += Utility.addTabNewLine(tabNum+1, Utility.createXMLUnusedTag(ZabbixTags.valuemap));
		result += Utility.addTabNewLine(tabNum+1, Utility.createXMLUnusedTag(ZabbixTags.logtimefmt));
		result += Utility.addTabNewLine(tabNum, Utility.createXMLCloseTag(ZabbixTags.item));
		return result;
	}
}