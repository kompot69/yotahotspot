package ru.kompot69.yotahotspot;

import static ru.kompot69.yotahotspot.Utils.convertToTime;

public class StationData {

    private final String macAddr;
    private final String connectTime;
    private final String hostname;
    private final String ipAddr;

    public StationData(String macAddr, String connectTime, String hostname, String ipAddr) {
        this.macAddr = "("+macAddr+")";
        this.connectTime = "подключен "+convertToTime(Integer.parseInt(connectTime));
        this.hostname = hostname;
        this.ipAddr = ipAddr;
    }

    public String getMacAddr() {return macAddr;}
    public String getConnectTime() {
        return connectTime;
    }
    public String getHostname() {return hostname;}
    public String getIpAddr() {
        return ipAddr;
    }
}
