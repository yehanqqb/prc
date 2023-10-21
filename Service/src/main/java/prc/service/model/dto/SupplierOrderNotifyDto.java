package prc.service.model.dto;

import lombok.Data;

@Data
public class SupplierOrderNotifyDto {
    private String orderId;

    private Integer money;

    private Integer status;

    private String productNo;

    private String sign;

    private String remark;

    private String paymentNo;

    private String finishTime;
}
