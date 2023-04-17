package com.sidam_backend;

import com.sidam_backend.data.DailySchedule;
import com.sidam_backend.data.User;
import com.sidam_backend.data.UserRole;
import com.sidam_backend.data.WorkerList;
import com.sidam_backend.repo.DailyScheduleRepository;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.Optional;

@DataJpaTest
public class DailyScheduleRepoTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DailyScheduleRepository dailyScheduleRepository;

    @Test
    public void whenMakeSchedule() {

        User user = new User();
        user.setKakaoId("asdf");
        user.setName("홍길동");
        user.setEmail("asdf@gmail.com");
        user.setPhone("010-1234-5678");
        user.setProfile("이거무슨필드더라");
        user.setDevice("기기 original code");

        UserRole role = new UserRole();
        role.setId("0001-1-123456");
        role.setCost(10000);
        role.setColor("FFFFFF");
        role.setAlias("길동이");
        role.setSalary(true);
        role.setValid(true);
        role.setLevel(1);
        role.setUser(user);

        WorkerList workerList = new WorkerList();
        workerList.setState(false);
        workerList.setWorker(role);

        DailySchedule dailySchedule = new DailySchedule();

        dailySchedule.setDate(LocalDateTime.of(2023,04,15, 14,0));
        dailySchedule.setStartTime(10);
        dailySchedule.setEndTime(17);
        dailySchedule.setWorkerList(workerList);

        entityManager.persist(user);
        entityManager.persist(role);
        entityManager.persist(workerList);
        entityManager.flush();

        dailyScheduleRepository.save(dailySchedule);

        Optional<DailySchedule> ds;
        ds = dailyScheduleRepository.findById(dailySchedule.getId());

        if (ds.isPresent()) {
            DailySchedule now = ds.get();
            System.out.println(now.getDate());
        }
    }
}
