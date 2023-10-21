package prc.service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import prc.service.model.enumeration.Operator;

@Data
@AllArgsConstructor
public class ProvinceNameCodeDto {
    private Integer provinceId;

    private String provinceName;

    private Operator operator;

    private String city;

    public ProvinceNameCodeDto() {

    }
}
