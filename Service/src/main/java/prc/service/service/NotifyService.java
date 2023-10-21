package prc.service.service;

import prc.service.model.entity.ITenantMerchant;
import prc.service.model.entity.ITenantSupplier;
import prc.service.model.entity.IUMerchantOrder;
import prc.service.model.entity.IUSupplierOrder;

import java.util.concurrent.CompletableFuture;

public interface NotifyService {
    CompletableFuture<Boolean> notifyMerchantSuccess(IUMerchantOrder iuMerchantOrder, ITenantMerchant iTenantMerchant);

    CompletableFuture<Boolean> notifySupplierSuccess(IUSupplierOrder supplierOrder, ITenantSupplier iTenantSupplier,String paymentNo);

    CompletableFuture<Boolean> notifySupplierError(IUSupplierOrder iuSupplierOrder, ITenantSupplier iTenantSupplier);
}
