package com.sidam_backend.resources.DTO;

import com.sidam_backend.data.AccountRole;
import com.sidam_backend.data.ChangeRequest;
import lombok.Getter;

@Getter
public class GetChange {

    public GetChange() {}
    public GetChange(Long id, AccountRole req, AccountRole rec,
                     ChangeRequest.State res, ChangeRequest.State own,
                     Long old, Long target) {

        this.id = id;
        this.old = old;
        this.target = target != null ? target : 0;

        this.requester = req.toWorker();
        this.receiver = rec != null ? rec.toWorker() : null;
        this.res = res;
        this.own = own;
    }

    private Long id;
    private Worker requester;
    private Worker receiver;
    private ChangeRequest.State res;
    private ChangeRequest.State own;
    private Long old;
    private Long target;
    private String oldSchedule;
    private String targetSchedule;

    public void setOldSchedule(String s) { oldSchedule = s; }
    public void setTargetSchedule(String s) { targetSchedule = s; }
}
