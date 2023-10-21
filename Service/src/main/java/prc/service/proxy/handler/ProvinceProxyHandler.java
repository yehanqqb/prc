package prc.service.proxy.handler;

import lombok.AllArgsConstructor;
import prc.service.proxy.aop.ProxyIpAop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.Proxy;

@AllArgsConstructor
public class ProvinceProxyHandler implements InvocationHandler {
    private final ProxyIpAop proxyIpAop;

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {

        if ("setProxy".equalsIgnoreCase(method.getName())) {
            /*Proxy proxy = proxyIpAop.getRandomIp();
            objects[1] = proxy;*/
        }
        return method.invoke(o, objects);
    }
}
