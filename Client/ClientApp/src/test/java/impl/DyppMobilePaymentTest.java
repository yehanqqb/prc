package impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.netflix.discovery.converters.Auto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import prc.ClientAppApplication;
import prc.service.channel.payment.impl.DyppMobilePayment;
import prc.service.dao.IUPaymentDao;
import prc.service.model.entity.IUPayment;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ClientAppApplication.class)//这里Application是启动类
public class DyppMobilePaymentTest {
    @Autowired
    private DyppMobilePayment dyppMobilePayment;

    @Autowired
    private IUPaymentDao iuPaymentDao;

    @Test
    public void testMonitoringChannel(){
        dyppMobilePayment.monitoringChannel(iuPaymentDao.getBaseMapper().selectOne(new LambdaQueryWrapper<IUPayment>().eq(IUPayment::getPaymentId,"prc2023101022051311448")));
    }
}