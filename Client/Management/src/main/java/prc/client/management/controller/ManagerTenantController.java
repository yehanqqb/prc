package prc.client.management.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import prc.client.management.dto.TenantCreateDto;
import prc.client.management.dto.TenantUpdateDto;
import prc.client.management.service.ManagerTenantService;
import prc.client.service.model.dto.IdsDto;
import prc.service.common.page.PageReq;
import prc.service.common.result.RetResponse;
import prc.service.common.result.RetResult;
import prc.service.model.entity.ITenant;

import javax.validation.Valid;

@RestController
@RequestMapping("/manager/tenant")
public class ManagerTenantController {
    @Autowired
    private ManagerTenantService managerTenantService;

    @PostMapping("/page")
    @PreAuthorize("@pk.hasPk('manager')")
    public RetResult getPageList(@RequestBody PageReq<ITenant> page) {
        return RetResponse.makeOKRsp(managerTenantService.page(page));
    }

    @PostMapping
    @PreAuthorize("@pk.hasPk('manager')")
    public RetResult create(@RequestBody @Valid TenantCreateDto tenantCreateDto) {
        return RetResponse.makeOKRsp(managerTenantService.addTenant(tenantCreateDto));
    }

    @PreAuthorize("@pk.hasPk('manager')")
    @PutMapping("/{tenantId}")
    public RetResult upAisleRate(@PathVariable Integer tenantId, @RequestBody TenantUpdateDto tenantUpdateDto) {
        tenantUpdateDto.setTenantId(tenantId);
        return RetResponse.makeOKRsp(managerTenantService.update(tenantUpdateDto));
    }

    @PreAuthorize("@pk.hasPk('manager')")
    @PutMapping("/aisle/{tenantId}")
    public RetResult saveAisle(@PathVariable Integer tenantId, @RequestBody IdsDto idsDto) {
        return RetResponse.makeOKRsp(managerTenantService.saveAisle(tenantId, idsDto.getIds()));
    }
}
