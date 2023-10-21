package prc.service.model.vo;

import lombok.Data;

@Data
public class ChannelPayVo {
    private String tradeId;

    private String payUrl;

    private String productNo;

    private long createTime;

    private boolean status;
}
