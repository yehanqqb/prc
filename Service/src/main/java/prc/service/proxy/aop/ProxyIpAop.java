package prc.service.proxy.aop;

import lombok.AllArgsConstructor;
import prc.service.model.dto.ProxyDto;
import prc.service.service.ProxyService;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.util.Objects;

@AllArgsConstructor
public class ProxyIpAop {
    private final ProxyService proxyService;

    public Proxy getRandomIp() {
        ProxyDto proxyDto = proxyService.getRandomHttp();
        if (Objects.nonNull(proxyDto)) {
            SocketAddress pro = new InetSocketAddress(proxyDto.getIp(), Integer.parseInt(proxyDto.getPort()));
            return new Proxy(Proxy.Type.HTTP, pro);
        }
        return null;
    }
}
