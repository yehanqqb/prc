package prc.service.model.vo;

import lombok.Data;
import prc.service.model.entity.IUPayment;

@Data
public class ChannelMonitoringVo {
    private boolean payStatus;

    // 返销===true 默认false
    private boolean orderStatus;

    // 到账状态
    private boolean finishStatus;

    private String paymentNo;

    private IUPayment iuPayment;
}
