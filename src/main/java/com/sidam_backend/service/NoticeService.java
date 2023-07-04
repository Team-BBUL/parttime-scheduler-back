package com.sidam_backend.service;

import com.sidam_backend.data.Notice;
import com.sidam_backend.data.Store;
import com.sidam_backend.data.UserRole;
import com.sidam_backend.repo.NoticeRepository;
import com.sidam_backend.repo.StoreRepository;
import com.sidam_backend.repo.UserRoleRepository;

import com.sidam_backend.resources.GetNotice;
import com.sidam_backend.resources.GetNoticeList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final StoreRepository storeRepository;
    private final UserRoleRepository roleRepository;

    public Store validatedStoreId(Long id) {

        return storeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(id + " store is not exist."));
    }

    public UserRole validatedUserRoleId(Long id) {

        UserRole role = roleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(id + " role is not exist."));

        if (role.getSalary()) {
            throw new IllegalArgumentException(id + " user is not store manager.");
        }

        return role;
    }

    public void saveNotice(Notice content) {

        noticeRepository.save(content);

        noticeRepository.findById(content.getId())
                .orElseThrow(() -> new IllegalArgumentException("save failed"));
    }

    public List<GetNoticeList> findAllList(Store store) {

        List<Notice> list = noticeRepository.findAllByStore(store);
        List<GetNoticeList> resultNotice = new ArrayList<>();

        for (Notice notice : list) {
            resultNotice.add(notice.toGetNoticeList());
        }

        return resultNotice;
    }

    public GetNotice findId(Long id, String url) {

        return noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(id + " notice is not exist."))
                .toGetNotice(url);
    }
}
