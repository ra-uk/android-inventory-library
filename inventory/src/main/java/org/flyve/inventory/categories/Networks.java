/**
 *
 * Copyright 2017 Teclib.
 * Copyright 2010-2016 by the FusionInventory Development
 *
 * http://www.fusioninventory.org/
 * https://github.com/fusioninventory/fusioninventory-android
 *
 * ------------------------------------------------------------------------
 *
 * LICENSE
 *
 * This file is part of FusionInventory project.
 *
 * FusionInventory is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * FusionInventory is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * ------------------------------------------------------------------------------
 * @update    07/06/2017
 * @license   GPLv2 https://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * @link      https://github.com/fusioninventory/fusioninventory-android
 * @link      http://www.fusioninventory.org/
 * ------------------------------------------------------------------------------
 */

package org.flyve.inventory.categories;

import android.app.Service;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import org.flyve.inventory.FILog;

/**
 * This class get all the information of the Network
 */
public class Networks extends Categories {

	/*
     * The serialization runtime associates with each serializable class a version number,
     * called a serialVersionUID, which is used during deserialization to verify that the sender
     * and receiver of a serialized object have loaded classes for that object that are compatible
     * with respect to serialization. If the receiver has loaded a class for the object that has a
     * different serialVersionUID than that of the corresponding sender's class, then deserialization
     * will result in an  InvalidClassException
     *
     *  from: https://stackoverflow.com/questions/285793/what-is-a-serialversionuid-and-why-should-i-use-it
     */
	private static final long serialVersionUID = 6829495385432791427L;

	// Properties of this component
	private static final String TYPE = "WIFI";
	private DhcpInfo dhcp;
	private WifiInfo wifi;

	/**
     * Indicates whether some other object is "equal to" this one
     * @param Object obj the reference object with which to compare
     * @return boolean true if the object is the same as the one given in argument
     */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		return (!super.equals(obj));
	}

	/**
     * Returns a hash code value for the object
     * @return int a hash code value for the object
     */
	@Override
	public int hashCode() {
		int hash = super.hashCode();
		hash = 89 * hash + (this.dhcp != null ? this.dhcp.hashCode() : 0);
		hash = 89 * hash + (this.wifi != null ? this.wifi.hashCode() : 0);
		return hash;
	}

	/**
	 * This constructor load the context and the Networks information
	 * @param xCtx Context where this class work
	 */
	public Networks(Context xCtx) {
		super(xCtx);

		try {
			WifiManager pWM = (WifiManager) xCtx.getSystemService(Service.WIFI_SERVICE);
			boolean wasWifiEnabled = pWM.isWifiEnabled();

			// Enable Wifi State if not
			if (!wasWifiEnabled) {
				pWM.setWifiEnabled(true);
			}
			Category c = new Category("NETWORKS", "networks");
			c.put("TYPE", new CategoryValue(TYPE, "TYPE", "type"));

			dhcp = pWM.getDhcpInfo();
			wifi = pWM.getConnectionInfo();

			FILog.d("<===WIFI DHCP===>");
			FILog.d("dns1=" + StringUtils.intToIp(dhcp.dns1));
			FILog.d("dns2=" + StringUtils.intToIp(dhcp.dns2));
			FILog.d("leaseDuration=" + dhcp.leaseDuration);

			c.put("MACADDR", new CategoryValue(getMacaddr(), "MACADDR", "macAddress"));
			c.put("SPEED", new CategoryValue(getSpeed(), "SPEED", "speed"));
			c.put("BSSID", new CategoryValue(getBSSID(), "BSSID", "bssid"));
			c.put("SSID", new CategoryValue(getSSID(), "SSID", "ssid"));
			c.put("IPGATEWAY", new CategoryValue(getIpgateway(), "IPGATEWAY", "ipGateway"));
			c.put("IPADDRESS", new CategoryValue(getIpaddress(), "IPADDRESS", "ipAddress", true));
			c.put("IPMASK", new CategoryValue(getIpmask(), "IPMASK", "ipMask", true));
			c.put("IPDHCP", new CategoryValue(getIpdhcp(), "IPDHCP", "ipDhcp", true));

			this.add(c);
			// Restore Wifi State
			if (!wasWifiEnabled) {
				pWM.setWifiEnabled(false);
			}
		} catch (Exception ex) {
			FILog.e(ex.getMessage());
		}
	}

	/**
	 * Get the Media Access Control address
	 * @return string the MAC address
	 */
	public String getMacaddr() {
		return wifi.getMacAddress();
	}

	/**
	 * Get the speed of the Wifi connection
	 * @return string the current speed in Mbps
	 */
	public String getSpeed() {
		return String.valueOf(wifi.getLinkSpeed());
	}

	/**
	 * Get the Basic Service Set Identifier (BSSID)
	 * @return string the BSSID of the current access point
	 */
	public String getBSSID() {
		return String.valueOf(wifi.getBSSID());
	}

	/**
	 * Get the Service Set Identifier (SSID)
	 * @return string the SSID of the current network
	 * 
	 */
	public String getSSID() {
		return String.valueOf(wifi.getBSSID());
	}

	/**
	 * Get the IP address of the gateway
	 * @return string the gateway IP address
	 */
	public String getIpgateway() {
		return StringUtils.intToIp(dhcp.gateway);
	}

	/**
	 * Get the IP address
	 * @return string the current IP address
	 */
	public String getIpaddress() {
		return StringUtils.intToIp(dhcp.ipAddress);
	}

	/**
	 * Get the IP address of the netmask
	 * @return string the netmask
	 */
	public String getIpmask() {
		return StringUtils.intToIp(dhcp.netmask);
	}

	/**
	 * Get the IP address of the DHCP
	 * @return string the server address
	 */
	public String getIpdhcp() {
		return StringUtils.intToIp(dhcp.serverAddress);
	}
}
