package com.sidam_backend.resources.DTO;

import com.sidam_backend.data.Incentive;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GetIncentivesRoleInfo {

    private Long roleId;
    private String alias;
    private List<GetIncentive> incentives = new ArrayList<>();
}
