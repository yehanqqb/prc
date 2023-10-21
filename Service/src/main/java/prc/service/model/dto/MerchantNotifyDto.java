package prc.service.model.dto;

import lombok.Data;

@Data
public class MerchantNotifyDto {
    private String orderId;

    private Integer money;

    private Integer status;

    private String sign;

    private String remark;

}
