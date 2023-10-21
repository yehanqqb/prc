package prc.service.common.page;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import org.springframework.util.CollectionUtils;
import prc.service.model.entity.ISUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
public class PageReq<T> {
    private Integer page;

    private Integer limit;

    private T common;

    private List<OrderItem> sort;

    private List<RangeDto> range;

    private List<ReqTitle> titleCount;


    public Page<T> createPage() {
        Page<T> page = new Page<>();
        page.setSize(getLimit());
        page.setCurrent(getPage());
        page.setOrders(getSort());
        return page;
    }

    public QueryWrapper<T> createMapper() {
        QueryWrapper<T> query = new QueryWrapper<>();
        query.setEntity(getCommon());
        if (!CollectionUtils.isEmpty(getRange())) {
            getRange().forEach(item -> {
                query.le(item.getColumn(), item.getEnd());
                query.ge(item.getColumn(), item.getStart());
            });
        }

        return query;
    }

    public LambdaQueryWrapper<T> createTitle(QueryWrapper<T> createSql) {
        if (!CollectionUtils.isEmpty(getTitleCount())) {
            String[] tagt = new String[getTitleCount().size() + 1];
            tagt[0] = "count(1) as ALLCOUNT";
            for (int i = 1; i < getTitleCount().size() + 1; i++) {
                ReqTitle item = getTitleCount().get(i - 1);
                if (item.getType().equals(ReqTitle.TYPE.COUNT)) {
                    tagt[i] = "count(case when " + item.getName() + " = " + item.getValue() + " then 1 else 0 end) as " + item.getAsName() + "";
                } else if (Objects.nonNull(item.getValue())) {
                    tagt[i] = "sum(case when " + item.getName() + " = " + item.getValue() + " then " + item.getOther() + " else 0 end) as " + item.getAsName() + " ";
                } else {
                    tagt[i] = "sum(" + item.getName() + ") as " + item.getAsName() + "";
                }
            }
            createSql.select(tagt);
        }
        return createSql.lambda();
    }
}
