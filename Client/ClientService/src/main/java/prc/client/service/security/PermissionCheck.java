package prc.client.service.security;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import prc.client.service.model.dto.LoginCacheDto;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service("pk")
public class PermissionCheck {
    /**
     * 所有权限标识
     */
    private static final String ALL_PERMISSION = "*:*:*";

    @Autowired
    private TokenConfig tokenService;

    @Autowired
    private HttpServletRequest request;

    private boolean hasPermissions(Set<String> permissions, String permission) {
        ArrayList<String> listPer = Lists.newArrayList(permission.split(";"));
        List<String> accountIdList = permissions.stream().filter(listPer::contains).collect(Collectors.toList());
        return permissions.contains(ALL_PERMISSION) || !accountIdList.isEmpty();
    }

    /**
     * 验证用户是否具备某权限
     *
     * @param permission 权限字符串
     * @return 用户是否具备某权限
     */
    public boolean hasPk(String permission) {
        if (StringUtils.isEmpty(permission)) {
            return false;
        }
        LoginCacheDto loginUser = tokenService.getLoginUser(request);
        if (Objects.isNull(loginUser) || CollectionUtils.isEmpty(loginUser.getAuthority())) {
            return false;
        }
        return hasPermissions(Sets.newConcurrentHashSet(loginUser.getAuthority()), permission);
    }
}
