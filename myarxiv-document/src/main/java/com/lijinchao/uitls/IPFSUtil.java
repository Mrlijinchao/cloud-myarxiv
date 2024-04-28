package com.lijinchao.uitls;

import io.ipfs.api.IPFS;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class IPFSUtil {
    @Value("${ipfs.ip}")
    private String ipAddr;
    @Value("${ipfs.port}")
    private Integer port;
    private volatile IPFS ipfs;

    public IPFS getIPFSInstance(){
        if(ipfs == null){
            synchronized (IPFSUtil.class){
                if(ipfs == null){
                    ipfs = new IPFS(ipAddr, port);
                }
            }
        }
        return ipfs;
    }

//    @Value("${ipfs.ip}")
//    public void setIpAddr(String ip){
//        ipAddr = ip;
//    }
//
//    @Value("${ipfs.port}")
//    public void setPort(Integer p){
//        port = p;
//    }


}
