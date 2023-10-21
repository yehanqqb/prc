package prc.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;
import prc.gateway.config.RedisCache;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.List;

@Component
public class RedisRouteDefinitionRepository implements RouteDefinitionRepository, ApplicationEventPublisherAware {
    @Resource
    private RedisCache redisCache;

    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        JSONArray gatewayJson = redisCache.getCacheObject("gateway");
        List<RouteDefinition> routeDefinitions = JSON.parseArray(gatewayJson.toJSONString(), RouteDefinition.class);
        return Flux.fromIterable(routeDefinitions);
    }

    @Override
    public Mono<Void> save(Mono<RouteDefinition> route) {
        return null;
    }

    @Override
    public Mono<Void> delete(Mono<String> routeId) {
        return null;
    }

    @Override
    public void setApplicationEventPublisher(@NotNull ApplicationEventPublisher applicationEventPublisher) {

    }
}
