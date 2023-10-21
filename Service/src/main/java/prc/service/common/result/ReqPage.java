package prc.service.common.result;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

@Data
public class ReqPage {
    private Integer page;

    private Integer limit;

    private JSONObject common;
}
