package prc.client.management.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import prc.client.management.dto.UpdateUserDto;
import prc.service.common.page.PageReq;
import prc.service.common.page.PageRes;
import prc.service.common.utils.BeanUtil;
import prc.service.dao.ISUserDao;
import prc.service.model.entity.ISUser;

@Service
public class ManagerUserService {
    @Autowired
    private ISUserDao isUserDao;
    @Autowired
    private BCryptPasswordEncoder cryptPasswordEncoder;


    public PageRes<ISUser> page(PageReq<ISUser> pageReq) {
        Page<ISUser> pageRes = isUserDao.getBaseMapper().selectPage(pageReq.createPage(), pageReq.createMapper());
        return new PageRes<ISUser>().trans(pageRes);
    }

    public boolean updateUser(Integer userId, UpdateUserDto updateUserDto) {
        ISUser isUser = BeanUtil.convertToBean(updateUserDto, ISUser.class);
        isUser.setId(userId);
        isUser.setPassword(cryptPasswordEncoder.encode(isUser.getPassword()));
        return isUserDao.updateById(isUser);
    }
}
