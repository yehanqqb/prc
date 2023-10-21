package prc.service.service;

import prc.service.model.dto.ProxyDto;

import java.net.Proxy;

public interface ProxyService {
    ProxyDto getRandomHttp();

    ProxyDto getRandomSocket();
}
