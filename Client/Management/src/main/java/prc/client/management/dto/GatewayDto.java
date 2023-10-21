package prc.client.management.dto;

import lombok.Data;
import net.sf.json.JSONObject;

import java.util.List;

@Data
public class GatewayDto {
    private List<JSONObject> route;
}
