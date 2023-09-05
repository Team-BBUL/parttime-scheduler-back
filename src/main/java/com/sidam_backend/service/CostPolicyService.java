package com.sidam_backend.service;

import com.sidam_backend.data.CostPolicy;
import com.sidam_backend.data.Store;
import com.sidam_backend.repo.CostPolicyRepository;
import com.sidam_backend.resources.DTO.PostPolicy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CostPolicyService {

    private final CostPolicyRepository costPolicyRepository;

    public List<CostPolicy> getAllPolicy(Store store) {
        return costPolicyRepository.findByStore(store)
                .orElse(Collections.emptyList());
    }

    public CostPolicy getPolicy(Long policyId){
        return costPolicyRepository.findById(policyId)
                .orElseThrow(() -> new IllegalArgumentException(policyId + "policy is not exist"));
    }
    public CostPolicy createNewPolicy(PostPolicy postPolicy, Store store){
        CostPolicy costPolicy = new CostPolicy();
        costPolicy.setDescription(postPolicy.getDescription());
        costPolicy.setMultiplyCost(postPolicy.getMultiplyCost());
        costPolicy.setDate(postPolicy.getDate());
        costPolicy.setStore(store);
        return costPolicyRepository.save(costPolicy);
    }

    public void deletePolicy(Long policyId){
        CostPolicy costPolicy = this.getPolicy(policyId);
        costPolicyRepository.delete(costPolicy);
    }
}
