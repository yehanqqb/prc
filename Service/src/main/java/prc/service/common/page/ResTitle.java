package prc.service.common.page;

import lombok.Data;

@Data
public class ResTitle {
    private String name;

    private Object count;

    private ReqTitle.TYPE type;
}
