package com.shkjs.patient.bean;

import com.shkjs.patient.base.BaseModel;
import com.shkjs.patient.data.em.Coexist;
import com.shkjs.patient.data.em.DiscountType;

import java.util.List;

public class PlatformDiscount extends BaseModel {
    /**
     *
     */
    private static final long serialVersionUID = -4412384827432825786L;

    private Long id;

    private String startDate;

    private String endDate;

    private Long userGradeId;

    private DiscountType type;

    private Long delta;

    private Coexist coexist;

    private String status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Long getUserGradeId() {
        return userGradeId;
    }

    public void setUserGradeId(Long userGradeId) {
        this.userGradeId = userGradeId;
    }


    public Long getDelta() {
        return delta;
    }

    public void setDelta(Long delta) {
        this.delta = delta;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public Coexist getCoexist() {
        return coexist;
    }

    public void setCoexist(Coexist coexist) {
        this.coexist = coexist;
    }

    public DiscountType getType() {
        return type;
    }

    public void setType(DiscountType type) {
        this.type = type;
    }


    /**
     * 得到优惠金额 （优惠金额不会大于总价格）
     *
     * @param totalCount 总价格
     * @return
     * @author ZHANGYUKUN
     */
    public Long getDiscountMoney(Long totalCount) {

        if (type.equals(DiscountType.FIX)) {
            return delta < totalCount ? delta : totalCount;
        } else if (type.equals(DiscountType.PERCENT)) {
            if (delta > 100 || delta < 0) {
                return 0l;
            }

            Double total = Double.parseDouble(totalCount + "");
            Double temp = (total * (100 - delta)) / 100;
            temp = Math.floor(temp);
            return temp.longValue();
        } else {
            return 0l;
        }

    }

    /**
     * 得到优惠最大的一项
     *
     * @param platformDiscountList
     * @param totalCount
     * @return
     * @author ZHANGYUKUN
     */
    public static PlatformDiscount getMax(List<PlatformDiscount> platformDiscountList, Long totalCount) {
        if (platformDiscountList == null || platformDiscountList.isEmpty()) {
            return null;
        }
        int maxIndex = 0;
        for (int i = 1; i < platformDiscountList.size(); i++) {

            if (platformDiscountList.get(i).getDiscountMoney(totalCount) > platformDiscountList.get(maxIndex)
                    .getDiscountMoney(totalCount)) {
                maxIndex = i;
            }
        }
        return platformDiscountList.get(maxIndex);
    }


}