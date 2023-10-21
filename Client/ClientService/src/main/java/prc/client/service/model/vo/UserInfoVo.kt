package prc.client.app.model.vo;

data class UserInfoVo(
        var nickname: String = "",
        var avatar: String = "",
        var roles: List<Role> = mutableListOf(),
        var authorities: List<Authorities> = mutableListOf(),
        var userId: Int = 0,
        var tenantId: Int = 0
) {
    data class Role(
            var roleCode: String = ""
    )

    data class Authorities(
            var menuId: Int = 0,
            var parentId: Int = 0,
            var title: String = "",
            var path: String = "",
            var component: String = "",
            var menuType: Int = 0,
            var sortNumber: Int = 0,
            var target: String = "",
            var icon: String = "",
            var hide: Int = 0
    )
}


