package prc.service.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import prc.service.common.utils.BeanUtil;
import prc.service.mapper.ISUserMapper;
import prc.service.model.dto.UserDto;
import prc.service.model.entity.ISAuthority;
import prc.service.model.entity.ISUser;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ISUserDao extends ServiceImpl<ISUserMapper, ISUser> {
    @Autowired
    private ISAuthorityDao isAuthorityDao;

    public List<UserDto> findByUserIds(Set<Integer> ids) {
        List<ISUser> userList = super.baseMapper.selectBatchIds(ids);

        Set<Integer> authIds = userList.stream().map(ISUser::getAuthority).flatMap(Collection::stream).collect(Collectors.toSet());
        Map<Integer, ISAuthority> authIdMap = Maps.newHashMap();
        if (!CollectionUtils.isEmpty(authIds)) {
            authIdMap = isAuthorityDao.getBaseMapper().selectBatchIds(authIds).stream().collect(Collectors.toMap(ISAuthority::getId, fs -> fs));
        }
        Map<Integer, ISAuthority> finalAuthIdMap = authIdMap;
        return userList.stream().map(item -> {
            UserDto dto = BeanUtil.convertToBean(item, UserDto.class);
            List<ISAuthority> authorities = Lists.newArrayList();
            item.getAuthority().forEach(i2 -> {
                if (Objects.nonNull(finalAuthIdMap.get(i2))) {
                    authorities.add(finalAuthIdMap.get(i2));
                }
            });
            dto.setAuthority(authorities);
            return dto;
        }).collect(Collectors.toList());

    }
}
