package prc.service.dao;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Service;
import prc.service.mapper.IUMerchantOrderMapper;
import prc.service.mapper.IUOrderErrorNotifyMapper;
import prc.service.model.entity.BaseEntity;
import prc.service.model.entity.IUMerchantOrder;
import prc.service.model.entity.IUOrderErrorNotify;
import prc.service.model.enumeration.FinishStatus;
import prc.service.model.enumeration.PayStatus;

@Service
public class IUOrderErrorNotifyDao extends ServiceImpl<IUOrderErrorNotifyMapper, IUOrderErrorNotify> {

}
