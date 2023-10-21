package prc.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import prc.gateway.NetWorkUtil;
import reactor.core.publisher.Mono;
import sun.nio.ch.Net;

@Component
@Slf4j
public class CheckIPFilter implements GlobalFilter, Ordered {

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String ip = NetWorkUtil.getIpAddress(exchange.getRequest());
        log.info("ip:{}", ip);
        return chain.filter(exchange);
    }
}
