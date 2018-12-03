package com.sensing.core.utils.task;

import java.util.List;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import com.sensing.core.thrift.serverconfig.CapChannelConfig;
import com.sensing.core.thrift.serverconfig.LbServerInfo;
import com.sensing.core.thrift.serverconfig.LoadBalance.Client;

public class LbServerInfoTest {

    public static void main(String[] args) {
		printfLbChannel("192.168.0.157", 7102);
//        printfLbChannel("192.168.0.182", 7102);
    }

    public static void printfLbChannel(String capAddr, Integer port) {
        String mqIp = null;
        try {
            TTransport transportCMP = new TSocket(capAddr, port, 2000);
            Client client = null;
            try {
                transportCMP.open();
            } catch (Exception e) {
                e.printStackTrace();
            }
            TProtocol protocolCMP = new TBinaryProtocol(transportCMP);
            client = new Client(protocolCMP);
            try {
                LbServerInfo lbServerInfo = client.GetServersInfo();
                System.out.println("lstChannel==" + lbServerInfo.lstChannel.size() + "==lstManageChannel==" + lbServerInfo.lstManageChannel.size());
                List<LbServerInfo> lstSubSvrInfo = lbServerInfo.getLstSubSvrInfo();
                for (LbServerInfo lbServerInfo2 : lstSubSvrInfo) {
                    List<CapChannelConfig> lstChannel = lbServerInfo2
                            .getLstChannel();
                    for (CapChannelConfig capChannelConfig : lstChannel) {
                        System.out.println(capChannelConfig.getChannel_uid() + ":" + capChannelConfig.getChannel_name());
                    }
                    if (mqIp != null) {
                        break;
                    }
                }
                if (transportCMP.isOpen()) {
                    transportCMP.close();
                }
            } finally {
                if (transportCMP.isOpen()) {
                    transportCMP.close();
                }
            }
        } catch (Exception e) {
        }
    }
}
