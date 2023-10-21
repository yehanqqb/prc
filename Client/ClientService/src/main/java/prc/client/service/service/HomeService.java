package prc.client.service.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import prc.client.service.model.vo.StatistcsRotationVo;
import prc.client.service.model.vo.StatistcsTenantPayVo;
import prc.client.service.model.vo.StatisticsStageVo;
import prc.service.common.constant.Constants;
import prc.service.common.utils.DateUtil;
import prc.service.common.utils.TimeUtil;
import prc.service.config.RedisCache;
import prc.service.dao.*;
import prc.service.model.dto.MerchantGroupRadioDto;
import prc.service.model.dto.PaymentGroupMoneyTenantDto;
import prc.service.model.dto.ProvinceCountCacheDto;
import prc.service.model.entity.ITenant;
import prc.service.model.entity.ITenantMerchant;
import prc.service.model.entity.IUMerchantOrder;
import prc.service.model.entity.IUPayment;
import prc.service.model.enumeration.Operator;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class HomeService {
    @Autowired
    private ITenantDao iTenantDao;
    @Autowired
    private ISDictDao isDictDao;
    @Autowired
    private ITenantSupplierDao iTenantSupplierDao;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private IUPaymentDao iuPaymentDao;
    @Autowired
    private IUMerchantOrderDao iuMerchantOrderDao;
    @Autowired
    private ITenantMerchantDao iTenantMerchantDao;


    public List<StatistcsRotationVo> managerFindAllRepertory(boolean slow) {
        List<ITenant> tenantAll = iTenantDao.list(new LambdaQueryWrapper<ITenant>().eq(ITenant::getStatus, true));
        return tenantAll.stream().map(item -> {
            StatistcsRotationVo vo = findRotationByTenantId(item.getId(), slow);
            vo.setTenantId(item.getId());
            vo.setName(item.getName());
            return vo;
        }).collect(Collectors.toList());
    }


    /**
     * 根据时间获得统计金额
     *
     * @param start
     * @param end
     * @return
     */
    public List<PaymentGroupMoneyTenantDto> finTenantMoney(Date start, Date end) {
        List<PaymentGroupMoneyTenantDto> list = iuPaymentDao.getBaseMapper().findMoneyGroupBy(TimeUtil.getTimeFormat(start), TimeUtil.getTimeFormat(end));
        return list.stream().peek(item -> item.setName(iTenantDao.findByIdAndExist(item.getTenantId()).getName())).collect(Collectors.toList());
    }


    public StatisticsStageVo findStageAll(Date start) {
        Date endDate = DateUtil.beforeDay(start, -1);
        StatisticsStageVo statisticsStageVo = new StatisticsStageVo();
        List<PaymentGroupMoneyTenantDto> dayList = iuPaymentDao.getBaseMapper().findMoneyGroupBy(TimeUtil.getTimeFormat(start), TimeUtil.getTimeFormat(endDate));
        List<PaymentGroupMoneyTenantDto> yesterdayList = iuPaymentDao.getBaseMapper().findMoneyGroupBy(TimeUtil.getTimeFormat(DateUtil.beforeDay(start, 2)), TimeUtil.getTimeFormat(DateUtil.beforeDay(start, 1)));

        dayList.forEach(item -> {
            statisticsStageVo.setBankCount(statisticsStageVo.getBankCount() + item.getBankCount());
            statisticsStageVo.setDayPayCount(statisticsStageVo.getDayPayCount() + item.getPayCount());
            statisticsStageVo.setDayFinishCount(statisticsStageVo.getDayFinishCount() + item.getFinishCount());

            statisticsStageVo.setBankMoney(statisticsStageVo.getBankMoney().add(item.getBankMoney()));
            statisticsStageVo.setDayPayMoney(statisticsStageVo.getDayPayMoney().add(item.getPayMoney()));
            statisticsStageVo.setDayFinishMoney(statisticsStageVo.getDayFinishMoney().add(item.getFinishMoney()));
        });

        yesterdayList.forEach(item -> {
            statisticsStageVo.setYesterdayFinishCount(statisticsStageVo.getYesterdayFinishCount() + item.getFinishCount());
            statisticsStageVo.setYesterdayPayCount(statisticsStageVo.getYesterdayPayCount() + item.getPayCount());
            statisticsStageVo.setYesterdayPayMoney(statisticsStageVo.getYesterdayPayMoney().add(item.getPayMoney()));
            statisticsStageVo.setYesterdayFinishMoney(statisticsStageVo.getYesterdayFinishMoney().add(item.getFinishMoney()));

        });

        return statisticsStageVo;
    }

    public StatisticsStageVo findStageAll(Date start, Integer tenantId) {
        Date endDate = DateUtil.beforeDay(start, -1);
        StatisticsStageVo statisticsStageVo = new StatisticsStageVo();
        List<PaymentGroupMoneyTenantDto> dayList = iuPaymentDao.getBaseMapper().findMoneyGroupByTenantId(TimeUtil.getTimeFormat(start), TimeUtil.getTimeFormat(endDate), tenantId);
        List<PaymentGroupMoneyTenantDto> yesterdayList = iuPaymentDao.getBaseMapper().findMoneyGroupByTenantId(TimeUtil.getTimeFormat(DateUtil.beforeDay(start, 2)), TimeUtil.getTimeFormat(DateUtil.beforeDay(start, 1)), tenantId);

        dayList.forEach(item -> {
            statisticsStageVo.setBankCount(statisticsStageVo.getBankCount() + item.getBankCount());
            statisticsStageVo.setDayPayCount(statisticsStageVo.getDayPayCount() + item.getPayCount());
            statisticsStageVo.setDayFinishCount(statisticsStageVo.getDayFinishCount() + item.getFinishCount());

            statisticsStageVo.setBankMoney(statisticsStageVo.getBankMoney().add(item.getBankMoney()));
            statisticsStageVo.setDayPayMoney(statisticsStageVo.getDayPayMoney().add(item.getPayMoney()));
            statisticsStageVo.setDayFinishMoney(statisticsStageVo.getDayFinishMoney().add(item.getFinishMoney()));
        });

        yesterdayList.forEach(item -> {
            statisticsStageVo.setYesterdayFinishCount(statisticsStageVo.getYesterdayFinishCount() + item.getFinishCount());
            statisticsStageVo.setYesterdayPayCount(statisticsStageVo.getYesterdayPayCount() + item.getPayCount());
            statisticsStageVo.setYesterdayPayMoney(statisticsStageVo.getYesterdayPayMoney().add(item.getPayMoney()));
            statisticsStageVo.setYesterdayFinishMoney(statisticsStageVo.getYesterdayFinishMoney().add(item.getFinishMoney()));

        });

        return statisticsStageVo;
    }


    public List<StatistcsTenantPayVo> findAllMoneyByTimeTenant(Integer tenantId, Date start) {
        List<MerchantGroupRadioDto> list = iuMerchantOrderDao.getBaseMapper().findByTenantGroup(tenantId, TimeUtil.getTimeFormat(start), TimeUtil.getTimeFormat( DateUtil.beforeDay(start, -1)));
        return list.stream().map(item -> {
            StatistcsTenantPayVo statistcsTenantPayVo = new StatistcsTenantPayVo();
            statistcsTenantPayVo.setNickname(iTenantMerchantDao.getMerchantByTenantIdAndMerchant(tenantId, item.getMerchantId()).getName());
            if (item.getCount() == 0) {
                statistcsTenantPayVo.setRatio(BigDecimal.ZERO);
            } else {
                statistcsTenantPayVo.setRatio(BigDecimal.valueOf(item.getSuccessCount()).divide(BigDecimal.valueOf(item.getCount())).multiply(BigDecimal.valueOf(100)));
            }
            return statistcsTenantPayVo;
        }).collect(Collectors.toList());
    }

    public StatistcsRotationVo findRotationByTenantId(Integer tenantId, boolean slow) {
        StatistcsRotationVo vo = new StatistcsRotationVo();
        List<Integer> moneyMin = isDictDao.getMoneyList();
        vo.setXAxis(moneyMin);
        vo.setMobile(Lists.newArrayList());
        vo.setTelecom(Lists.newArrayList());
        vo.setUni(Lists.newArrayList());
        vo.setOther(Lists.newArrayList());
        Arrays.stream(Operator.values()).forEach(item -> {
            moneyMin.forEach(item2 -> {
                AtomicInteger count = new AtomicInteger(0);
                iTenantSupplierDao.getSupplierByTenantId(tenantId).forEach(ity -> {
                    String provinceKey = slow ? String.format(Constants.SLOW_PAYMENT_TENANT_SUPPLIER_OPERATOR_MONEY_NUMBER, tenantId, ity.getId(), item.getKey(), item2) : String.format(Constants.FAST_PAYMENT_TENANT_SUPPLIER_OPERATOR_MONEY_NUMBER, tenantId, ity.getId(), item.getKey(), item2);
                    List<ProvinceCountCacheDto> provincePayments = redisCache.getCacheObject(provinceKey);
                    if (Objects.nonNull(provincePayments)) {
                        count.addAndGet(provincePayments.stream().mapToInt(ProvinceCountCacheDto::getNumber).sum());
                    }
                });
                if (item.equals(Operator.TELECOM)) {
                    vo.getTelecom().add(count.get());
                } else if (item.equals(Operator.UNI)) {
                    vo.getUni().add(count.get());
                } else if (item.equals(Operator.MOBILE)) {
                    vo.getMobile().add(count.get());
                } else {
                    vo.getOther().add(count.get());
                }
            });

        });
        vo.setMobileCount(vo.getMobile().stream().mapToInt(Integer::intValue).sum());
        vo.setUniCount(vo.getUni().stream().mapToInt(Integer::intValue).sum());
        vo.setTelecomCount(vo.getTelecom().stream().mapToInt(Integer::intValue).sum());
        vo.setOtherCount(vo.getOther().stream().mapToInt(Integer::intValue).sum());
        return vo;
    }
}
