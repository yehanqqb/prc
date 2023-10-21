package prc.service.proxy.factory;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import prc.service.model.dto.ProvinceNameCodeDto;
import prc.service.proxy.aop.ProxyIpAop;
import prc.service.proxy.excute.ProvinceProxy;
import prc.service.proxy.excute.impl.ProvinceProxyAoyondImpl;
import prc.service.proxy.excute.impl.ProvinceProxyJdImpl;
import prc.service.proxy.handler.ProvinceProxyHandler;
import prc.service.service.ProxyService;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@AllArgsConstructor
public class ProvinceFactory {
    private final ProxyService proxyService;

    private final ThreadPoolTaskExecutor systemExecutor;

    private final static List<String> LOAD_CLASS_NAME = Lists.newArrayList(
            "prc.service.proxy.excute.impl.ProvinceProxyAoyondImpl",
            "prc.service.proxy.excute.impl.ProvinceProxyWechatImpl",
            "prc.service.proxy.excute.impl.ProvinceProxyJdImpl"
    );

    public ProvinceNameCodeDto getProvince(String productNo) {

       /* CompletableFuture<Object> future = CompletableFuture.anyOf(LOAD_CLASS_NAME.stream().map(item ->
                CompletableFuture.supplyAsync(() -> {
                    try {
                        ProvinceProxy provinceProxy = (ProvinceProxy) Class.forName(item).newInstance();
                        return provinceProxy.getProvinceByNo(productNo);
                    } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                        e.printStackTrace();
                        return null;
                    }
                }, systemExecutor)
        ).filter(Objects::nonNull).distinct().collect(Collectors.toList()).toArray(new CompletableFuture[]{}));*/

        try {
            ProvinceProxy provinceProxy = new ProvinceProxyJdImpl();
            return provinceProxy.getProvinceByNo(productNo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ProvinceNameCodeDto(-1, "未知", null, null);
    }
}
