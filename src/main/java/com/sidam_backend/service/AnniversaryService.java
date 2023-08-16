package com.sidam_backend.service;

import com.sidam_backend.data.AccountRole;
import com.sidam_backend.data.Anniversary;
import com.sidam_backend.repo.AnniversaryRepository;
import com.sidam_backend.resources.DTO.PostAnniversary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnniversaryService {

    private final AnniversaryRepository anniversaryRepository;

    public List<Anniversary> getAnniversaries(AccountRole accountRole){
        return anniversaryRepository.findAnniversariesByAccountRole(accountRole)
                .orElse(Collections.emptyList());
    }

    public Anniversary getAnniversary(Long anniversaryId){
        return anniversaryRepository.findById(String.valueOf(anniversaryId)).
                orElseThrow(()-> new IllegalArgumentException( anniversaryId + " anniversary is not exist."));
    }

    public Anniversary createNewAnniversary(PostAnniversary postAnniversary, AccountRole employee){
        Anniversary anniversary = new Anniversary();
        anniversary.setName(postAnniversary.getName());
        anniversary.setDate(postAnniversary.getDate());
        anniversary.setAccountRole(employee);
        return anniversaryRepository.save(anniversary);
    }

    public Anniversary updateAnniversary(PostAnniversary postAnniversary, Long anniversaryId){
        Anniversary anniversary = this.getAnniversary(anniversaryId);
        anniversary.setName(postAnniversary.getName());
        anniversary.setDate(postAnniversary.getDate());
        return anniversaryRepository.save(anniversary);
    }

    public void deleteAnniversary(Long anniversaryId){
        Anniversary anniversary  = this.getAnniversary(anniversaryId);
        anniversaryRepository.delete(anniversary);
    }

}
