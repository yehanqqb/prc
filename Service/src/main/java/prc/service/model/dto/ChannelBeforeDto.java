package prc.service.model.dto;


import lombok.Data;
import prc.service.model.entity.IUPayment;
import prc.service.model.entity.IUSupplierOrder;

import java.io.Serializable;


/**
 * 下单基类
 */
@Data
public class ChannelBeforeDto implements Serializable {
    private IUSupplierOrder iuSupplierOrder;

    private IUPayment payment;
}
