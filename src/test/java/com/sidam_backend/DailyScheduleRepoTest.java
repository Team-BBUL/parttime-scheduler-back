package com.sidam_backend;

import com.sidam_backend.data.DailySchedule;
import com.sidam_backend.data.User;
import com.sidam_backend.data.UserRole;
import com.sidam_backend.data.WorkerList;
import com.sidam_backend.repo.DailyScheduleRepository;
import net.bytebuddy.asm.Advice;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.ArrayList;
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
        role.setCost(10000);
        role.setColor("FFFFFF");
        role.setAlias("길동이");
        role.setSalary(true);
        role.setValid(true);
        role.setLevel(1);
        role.setUser(user);

        ArrayList<UserRole> users = new ArrayList<>();
        users.add(role);

        DailySchedule dailySchedule = new DailySchedule();

        dailySchedule.setDate(LocalDate.of(2023, 4, 23));
        dailySchedule.setUsers(users);

        ArrayList<Boolean> time = new ArrayList<>();
        for(int i = 10; i <= 23; i++) {
            if (17 <= i && i <= 22) {
                time.add(true);
            }
            else {
                time.add(false);
            }
        }
        dailySchedule.setTime(time);

        entityManager.persist(user);
        entityManager.persist(role);
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
