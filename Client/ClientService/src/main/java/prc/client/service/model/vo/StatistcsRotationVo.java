package prc.client.service.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class StatistcsRotationVo {
    private List<Integer> xAxis;

    private List<Integer> telecom;

    private List<Integer> uni;

    private List<Integer> mobile;

    private List<Integer> other;

    private String name;

    private Integer tenantId;

    private Integer telecomCount;

    private Integer uniCount;

    private Integer mobileCount;

    private Integer otherCount;
}
