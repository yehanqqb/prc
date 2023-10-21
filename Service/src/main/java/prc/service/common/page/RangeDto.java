package prc.service.common.page;

import lombok.Data;

import java.util.Date;

@Data
public class RangeDto {
    private String column;

    private Date start;

    private Date end;
}
