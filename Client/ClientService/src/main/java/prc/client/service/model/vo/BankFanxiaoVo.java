package prc.client.service.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class BankFanxiaoVo {
    private List<String> xAxis;

    private List<Integer> count;

    private List<Integer> telecom;

    private List<Integer> uni;

    private List<Integer> mobile;
}
