package prc.service.model.vo;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.io.Serializable;

@Data
public class ChannelBeforeVo implements Serializable {
    private String payUrl;

    private JSONObject query;

    private String paymentNo;

    // 额外的参数
    private JSONObject extJson;

    private boolean success;

    private String remark;
}
