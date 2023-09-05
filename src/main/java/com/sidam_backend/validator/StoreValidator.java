package com.sidam_backend.validator;

import com.sidam_backend.repo.StoreRepository;
import com.sidam_backend.resources.DTO.StoreForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Slf4j
@Component
@RequiredArgsConstructor
public class StoreValidator implements Validator {

    private final StoreRepository storeRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(StoreForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        StoreForm storeForm = (StoreForm) target;
        if(storeRepository.existsByName(storeForm.getName())){
            errors.rejectValue("name", "name_duplicated", "스토어 이름이 중복되었습니다");
        }
    }
}
