package prc.service.channel.before;


import prc.service.model.dto.ChannelBeforeDto;
import prc.service.model.vo.ChannelBeforeVo;

public interface ChannelBefore {
    ChannelBeforeVo createPay(ChannelBeforeDto channelBaseDto);
}
