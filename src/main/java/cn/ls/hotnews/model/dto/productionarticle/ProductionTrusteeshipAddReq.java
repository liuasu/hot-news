package cn.ls.hotnews.model.dto.productionarticle;

import cn.ls.hotnews.model.dto.thirdpartyaccount.AccountTrusteeship;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * title: ProductionTrusteeshipAddReq
 * author: liaoshuo
 * date: 2024/11/19 20:21
 * description:
 */
@ApiModel(value = "ProductionTrusteeshipAddReq", description = "托管")
@Data
public class ProductionTrusteeshipAddReq implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 提示名称
     */
    private String promptName;
    /**
     * ai平台
     */
    private String aiPlatForm;

    /**
     * 第三方账号平台
     */
    private List<AccountTrusteeship> accountTrusteeshipsList;
}
