package prc.client.service.model.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StatisticsStageVo {
    public StatisticsStageVo() {
        this.yesterdayPayMoney = BigDecimal.ZERO;
        this.yesterdayPayCount = 0;
        this.yesterdayFinishMoney = BigDecimal.ZERO;
        this.yesterdayFinishCount = 0;
        this.dayFinishMoney = BigDecimal.ZERO;
        this.dayFinishCount = 0;
        this.dayPayCount = 0;
        this.dayPayMoney = BigDecimal.ZERO;
        this.bankMoney = BigDecimal.ZERO;
        this.bankCount = 0;
    }

    // 昨日支付
    private BigDecimal yesterdayPayMoney;

    private Integer yesterdayPayCount;

    private BigDecimal yesterdayFinishMoney;

    private Integer yesterdayFinishCount;

    // 今日到账
    private BigDecimal dayFinishMoney;

    private Integer dayFinishCount;

    private Integer dayPayCount;

    private BigDecimal dayPayMoney;

    // 今日返销总量
    private BigDecimal bankMoney;

    private Integer bankCount;
}
