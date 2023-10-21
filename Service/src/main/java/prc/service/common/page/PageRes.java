package prc.service.common.page;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import prc.service.model.entity.ISUser;

import java.util.List;

@Data
public class PageRes<T> {
    private long count;

    private List<T> data;

    private List<ResTitle> resTitle;

    public PageRes<T> trans(Page<T> object) {
        this.count = object.getTotal();
        this.data = object.getRecords();
        return this;
    }
}
