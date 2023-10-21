package prc.service.proxy.excute;

import com.google.common.collect.Lists;
import prc.service.model.dto.ProvinceNameCodeDto;

import java.net.Proxy;
import java.util.List;

public interface ProvinceProxy {
    ProvinceNameCodeDto getProvinceByNo(String No);
}
