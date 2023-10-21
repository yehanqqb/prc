package prc.api.service.vo;

import lombok.Data;
import prc.service.model.enumeration.FinishStatus;
import prc.service.model.enumeration.PayStatus;

@Data
public class QueryVo {
    Integer payStatus;

    Integer finishStatus;
}
