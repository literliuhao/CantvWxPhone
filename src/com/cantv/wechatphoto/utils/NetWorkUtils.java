package com.cantv.wechatphoto.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;

public class NetWorkUtils {

	/**
	 * @category 判断是否有外网连接（普通方法不能判断外网的网络是否连接，比如连接上局域网）
	 * @return
	 */
	public static final boolean ping() {

		String result = null;
		try {
			String ip = "www.baidu.com";// ping 的地址，可以换成任何一种可靠的外网
			Process p = Runtime.getRuntime().exec("ping -c 3 -w 100 " + ip);// ping网址3次
			// 读取ping的内容，可以不加
			InputStream input = p.getInputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(input));
			StringBuffer stringBuffer = new StringBuffer();
			String content = "";
			while ((content = in.readLine()) != null) {
				stringBuffer.append(content);
			}
			Log.d("------ping-----", "result content : " + stringBuffer.toString());
			// ping的状态
			int status = p.waitFor();
			if (status == 0) {
				result = "success";
				return true;
			} else {
				result = "failed";
			}
		} catch (IOException e) {
			result = "IOException";
		} catch (InterruptedException e) {
			result = "InterruptedException";
		} finally {
			Log.d("----result---", "result = " + result);
		}
		return false;
	}

	/**
	 * 获取当前系统连接网络的网卡的mac地址
	 * 
	 * @return
	 */
	@SuppressLint("NewApi")
	public static String getMac() {
		byte[] mac = null;
		StringBuffer sb = new StringBuffer();
		try {
			Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
			while (netInterfaces.hasMoreElements()) {
				NetworkInterface ni = netInterfaces.nextElement();
				Enumeration<InetAddress> address = ni.getInetAddresses();

				while (address.hasMoreElements()) {
					InetAddress ip = address.nextElement();
					if (ip.isAnyLocalAddress() || !(ip instanceof Inet4Address) || ip.isLoopbackAddress())
						continue;
					if (ip.isSiteLocalAddress())
						mac = ni.getHardwareAddress();
					else if (!ip.isLinkLocalAddress()) {
						mac = ni.getHardwareAddress();
						break;
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}

		if (mac != null) {
			for (int i = 0; i < mac.length; i++) {
				sb.append(parseByte(mac[i]));
			}
			return sb.substring(0, sb.length() - 1);
		}

		return null;
	}

	// 获取当前连接网络的网卡的mac地址
	private static String parseByte(byte b) {
		String s = "00" + Integer.toHexString(b) + ":";
		return s.substring(s.length() - 3);
	}

	public static String getEthernetMac() {
		// return "C8:0E:77:30:77:62";
		String macAddr = null;
		macAddr = _getLocalEthernetMacAddress();
		if (macAddr == null) {
			macAddr = getMac();
		}
		if (TextUtils.isEmpty(macAddr)) {
			macAddr = _getEthMacAddress2();
			if (macAddr != null && macAddr.startsWith("0:")) {
				macAddr = "0" + macAddr;
			}
		}
		return macAddr;
	}

	/*
	 * PRIVATE METHODS
	 */
	@SuppressLint("NewApi")
	private static String _getLocalEthernetMacAddress() {
		String mac = null;
		try {
			Enumeration<NetworkInterface> localEnumeration = NetworkInterface.getNetworkInterfaces();

			while (localEnumeration.hasMoreElements()) {
				NetworkInterface localNetworkInterface = (NetworkInterface) localEnumeration.nextElement();
				String interfaceName = localNetworkInterface.getDisplayName();

				if (interfaceName == null) {
					continue;
				}

				if (interfaceName.equals("eth0")) {
					mac = _convertToMac(localNetworkInterface.getHardwareAddress());
					if (mac != null && mac.startsWith("0:")) {
						mac = "0" + mac;
					}
					break;
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return mac;
	}

	private static String _convertToMac(byte[] mac) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < mac.length; i++) {
			byte b = mac[i];
			int value = 0;
			if (b >= 0 && b < 16) {// Jerry(2013-11-6): if (b>=0&&b<=16) => if
									// (b>=0&&b<16)
				value = b;
				sb.append("0" + Integer.toHexString(value));
			} else if (b >= 16) {// Jerry(2013-11-6): else if (b>16) => else if
									// (b>=16)
				value = b;
				sb.append(Integer.toHexString(value));
			} else {
				value = 256 + b;
				sb.append(Integer.toHexString(value));
			}
			if (i != mac.length - 1) {
				sb.append(":");
			}
		}
		return sb.toString();
	}

	private static String _getEthMacAddress2() {
		String mac = _loadFileAsString("/sys/class/net/eth0/address");
		if (mac == null) {
			mac = "";
		} else {
			mac = mac.toUpperCase(Locale.CHINA);
			if (mac.length() > 17) {
				mac = mac.substring(0, 17);
			}
		}

		return mac;
	}

	private static String _loadFileAsString(String filePath) {
		try {
			if (new File(filePath).exists()) {
				StringBuffer fileData = new StringBuffer(1000);
				BufferedReader reader = new BufferedReader(new FileReader(filePath));
				char[] buf = new char[1024];
				int numRead = 0;
				while ((numRead = reader.read(buf)) != -1) {
					String readData = String.valueOf(buf, 0, numRead);
					fileData.append(readData);
				}
				reader.close();
				return fileData.toString();
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}
}
