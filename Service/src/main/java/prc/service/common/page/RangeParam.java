package prc.service.common.page;

import lombok.Data;

import java.util.Date;

@Data
public class RangeParam {
    private String name;

    private Date start;

    private Date end;
}
