package prc.client.management.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import prc.client.management.dto.RoleDto;
import prc.service.common.page.PageReq;
import prc.service.common.page.PageRes;
import prc.service.common.utils.BeanUtil;
import prc.service.dao.ISAuthorityDao;
import prc.service.dao.ISRoleDao;
import prc.service.model.entity.ISAuthority;
import prc.service.model.entity.ISRole;

import javax.management.relation.Role;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ManagerRoleService {
    @Autowired
    private ISRoleDao isRoleDao;
    @Autowired
    private ISAuthorityDao isAuthorityDao;


    public PageRes<RoleDto> page(PageReq<ISRole> pageReq) {
        Page<ISRole> pageRes = isRoleDao.getBaseMapper().selectPage(pageReq.createPage(), pageReq.createMapper());

        Page<RoleDto> res = new Page<RoleDto>();
        BeanUtil.copyBeanProp(res, pageReq);

        List<ISAuthority> auths = isAuthorityDao.getBaseMapper().selectBatchIds(pageRes.getRecords().stream().map(ISRole::getAuthority).flatMap(Collection::stream).collect(Collectors.toSet()));
        Map<Integer, ISAuthority> authsIdMap = auths.stream().collect(Collectors.toMap(ISAuthority::getId, fs -> fs));

        res.setRecords(pageRes.getRecords().stream().map(item -> {
            RoleDto dto = BeanUtil.convertToBean(item, RoleDto.class);
            ArrayList<ISAuthority> authority = Lists.newArrayList();
            item.getAuthority().forEach(i2 -> {
                authority.add(authsIdMap.get(i2));
            });
            dto.setAuthority(authority);
            return dto;
        }).collect(Collectors.toList()));
        return new PageRes<RoleDto>().trans(res);
    }

    public boolean saveOrUpdate(ISRole isRole) {
        return isRoleDao.saveOrUpdate(isRole);
    }
}
