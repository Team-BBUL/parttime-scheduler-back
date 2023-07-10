package com.sidam_backend.service;

import com.sidam_backend.data.Store;
import com.sidam_backend.repo.StoreRepository;
import com.sidam_backend.resources.DTO.GetStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;

    public List<GetStore> findStore(String input) throws IllegalArgumentException {

        List<Store> stores = storeRepository.findAllByName(input)
                .orElseThrow(() -> new IllegalArgumentException(input + " is invalid."));
        List<GetStore> result = new ArrayList<>();

        if (stores.size() == 0) {
            throw new IllegalArgumentException("no search results found.");
        }

        for (Store store : stores) {
            result.add(store.toGetStore());
        }

        return result;
    }

    public List<String> findAllStoreName() {

        return storeRepository.findNameAll();
    }
}
