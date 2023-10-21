package prc.service.model.vo;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import prc.service.model.entity.IUPayment;

@Data
public class ChannelPaymentVo {
    private String payUrl;

    private JSONObject query;

    private String proxyIp;

    // 系统单号
    private String paymentNo;

    //
    private boolean status;

    // 失败的说明
    private String remark;

    private String productNo;

    private long createTime;
}
