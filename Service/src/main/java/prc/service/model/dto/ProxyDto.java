package prc.service.model.dto;

import lombok.Data;
import prc.service.model.entity.ISProxy;

import java.net.Proxy;

@Data
public class ProxyDto {
    //省份id
    private Integer proId;

    //代理IP
    private String ip;

    //代理端口号
    private String port;

    private String createTime;

    private String orgIp;

    private String province;

    private String userProvince;

    private String cid;

    private String pid;

    private Proxy.Type proxyType;
}
