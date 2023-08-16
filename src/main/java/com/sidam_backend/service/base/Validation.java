package com.sidam_backend.service.base;

import com.sidam_backend.data.Account;
import com.sidam_backend.data.DailySchedule;
import com.sidam_backend.data.Store;
import com.sidam_backend.data.AccountRole;

public interface Validation {

    Store validateStoreId(Long storeId);
    AccountRole validateRoleId(Long roleId);
    Account validateAccount(Long accountId);

    DailySchedule validateSchedule(Long scheduleId);
}
