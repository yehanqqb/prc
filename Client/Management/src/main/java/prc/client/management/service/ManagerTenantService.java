package prc.client.management.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import prc.client.management.dto.TenantCreateDto;
import prc.client.management.dto.TenantDto;
import prc.client.management.dto.TenantUpdateDto;
import prc.service.common.constant.Constants;
import prc.service.common.exception.BizException;
import prc.service.common.page.PageReq;
import prc.service.common.page.PageRes;
import prc.service.common.utils.BeanUtil;
import prc.service.config.RedisCache;
import prc.service.dao.*;
import prc.service.model.dto.TenantAisleDto;
import prc.service.model.dto.UserDto;
import prc.service.model.entity.*;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ManagerTenantService {
    @Autowired
    private ITenantDao iTenantDao;
    @Autowired
    private ISUserDao isUserDao;
    @Autowired
    private BCryptPasswordEncoder cryptPasswordEncoder;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private ITenantAisleDao iTenantAisleDao;
    @Autowired
    private ISAisleDao isAisleDao;
    @Autowired
    private ITenantAisleSupplierDao iTenantAisleSupplierDao;


    public PageRes<TenantDto> page(PageReq<ITenant> pageReq) {
        Page<ITenant> pageRes = iTenantDao.getBaseMapper().selectPage(pageReq.createPage(), pageReq.createMapper());

        Set<Integer> userIds = pageRes.getRecords().stream().map(ITenant::getUserId).collect(Collectors.toSet());

        Map<Integer, UserDto> userIdMap = isUserDao.findByUserIds(userIds).stream().collect(Collectors.toMap(UserDto::getId, fs -> fs));


        Page<TenantDto> dtoPage = new Page<TenantDto>();
        BeanUtil.copyBeanProp(dtoPage, pageRes);
        dtoPage.setRecords(pageRes.getRecords().stream().map(item -> {
            TenantDto dto = new TenantDto();
            dto.setITenant(item);
            dto.setIsUser(userIdMap.get(item.getUserId()));
            dto.setITenantAisleLists(iTenantAisleDao.findAisleByTenantId(item.getId(), false));
            return dto;
        }).collect(Collectors.toList()));

        return new PageRes<TenantDto>().trans(dtoPage);
    }

    public boolean addTenant(TenantCreateDto createDto) {
        ISUser sysUser = new ISUser();
        sysUser.setUsername(createDto.getUsername());
        if (isUserDao.getBaseMapper().selectCount(new LambdaQueryWrapper<ISUser>().eq(ISUser::getUsername, createDto.getUsername())) >= 1) {
            throw new BizException("用户名已存在");
        }
        // 创建用户
        // 写死默认是2
        sysUser.setRoleId(2);
        sysUser.setStatus(true);
        sysUser.setAuthority(Lists.newArrayList());
        sysUser.setPassword(cryptPasswordEncoder.encode(createDto.getPassword()));
        isUserDao.save(sysUser);
        // 创建租户
        ITenant sysTenant = new ITenant();
        sysTenant.setMountIds(Lists.newArrayList());
        sysTenant.setSecret(UUID.randomUUID().toString().replace("-", ""));
        sysTenant.setStatus(true);
        sysTenant.setName(createDto.getUsername());
        sysTenant.setUserId(sysUser.getId());
        sysTenant.setStatus(true);
        sysTenant.setUid(UUID.randomUUID().toString().replace("-", ""));
        iTenantDao.save(sysTenant);
        return true;
    }

    @Transactional
    public boolean update(TenantUpdateDto tenantUpdateDto) {
        ISUser user = isUserDao.getById(tenantUpdateDto.getUserId());
        Assert.notNull(user, "用户不存在");
        redisCache.deleteObject(Constants.TENANT_KEY + tenantUpdateDto.getTenantId());
        ITenant tenant = iTenantDao.findByIdAndExist(tenantUpdateDto.getTenantId());
        Assert.notNull(tenant, "租户不存在");
        user.setUsername(tenantUpdateDto.getUsername());
        user.setStatus(tenantUpdateDto.isStatus());
        if (Objects.nonNull(tenantUpdateDto.getPassword())) {
            user.setPassword(cryptPasswordEncoder.encode(tenantUpdateDto.getPassword()));
        }
        user.setAuthority(tenantUpdateDto.getAuthorityIds());
        isUserDao.saveOrUpdate(user);
        if (Objects.nonNull(tenantUpdateDto.getAisleIds())) {
            iTenantAisleDao.update(tenantUpdateDto.getTenantId(), tenantUpdateDto.getAisleIds());
        }
        if (Objects.nonNull(tenantUpdateDto.getMountIds())) {
            tenant.setMountIds(tenantUpdateDto.getMountIds());
        }
        tenant.setSecret(tenantUpdateDto.getSecret());
        tenant.setStatus(tenantUpdateDto.isStatus());
        iTenantDao.saveOrUpdate(tenant);
        return true;
    }


    public boolean saveAisle(Integer tenantId, List<Integer> aisleIds) {
        Set<TenantAisleDto> aisles = iTenantAisleDao.findAisleByTenantId(tenantId, false);
        Set<Integer> dbIds = aisles.stream().map(item -> item.getIsAisle().getId()).collect(Collectors.toSet());

        Set<Integer> exist = Sets.newHashSet();
        Set<Integer> notExist = dbIds.stream().map(item -> {
            if (aisleIds.contains(item)) {
                exist.add(item);
                return null;
            } else {
                return item;
            }
        }).filter(Objects::nonNull).collect(Collectors.toSet());


        Set<Integer> add = aisleIds.stream().filter(item -> !exist.contains(item) && !notExist.contains(item))
                .collect(Collectors.toSet());

        if (!CollectionUtils.isEmpty(add)) {
            Map<Integer, ISAisle> idToMap = isAisleDao.getBaseMapper().selectBatchIds(add).stream().collect(Collectors.toMap(ISAisle::getId, fs -> fs));
            Set<ITenantAisle> tenantAisles = add.stream().map(item -> {
                ITenantAisle iTenantAisle = new ITenantAisle();
                iTenantAisle.setStatus(true);
                iTenantAisle.setName(idToMap.get(item).getName());
                iTenantAisle.setNotProvince(Lists.newArrayList());
                iTenantAisle.setAisleId(item);
                iTenantAisle.setTenantId(tenantId);
                return iTenantAisle;
            }).collect(Collectors.toSet());
            if (!CollectionUtils.isEmpty(tenantAisles)) {
                iTenantAisleDao.saveBatch(tenantAisles);

            }
        }

        if (!CollectionUtils.isEmpty(notExist)) {
            iTenantAisleDao.getBaseMapper().delete(new LambdaQueryWrapper<ITenantAisle>().in(ITenantAisle::getAisleId, notExist));
            iTenantAisleSupplierDao.getBaseMapper().delete(new LambdaQueryWrapper<ITenantAisleSupplier>().in(ITenantAisleSupplier::getAisleId, notExist).eq(ITenantAisleSupplier::getTenantId, tenantId));
        }
        redisCache.deleteObject(String.format(Constants.TENANT_AISLE, tenantId));

        return true;
    }
}
