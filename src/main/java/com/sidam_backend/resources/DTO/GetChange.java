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
        this.target = target;

        this.requester = req.toWorker(req);
        this.receiver = rec != null ? rec.toWorker(rec) : null;
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
}
