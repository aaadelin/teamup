package com.team.teamup.utils.query;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class ReflectionLQPUserTet {

    private List<MockUser> users;

    @Mock
    private JpaRepository<MockUser, Integer> userRepository;
    @InjectMocks
    private ReflectionQueryLanguageParser<MockUser, Integer> qlpUser;

    @Before
    public void setUp(){
        initializeData();
        when(userRepository.findAll()).thenReturn(users);

    }

    private void initializeData() {
        qlpUser.setClazz(MockUser.class);
        LocalDateTime dateTime = LocalDateTime.of(2020, 10, 2, 10, 10);

        MockUser user1 = MockUser.builder()
                .id(1)
                .name("Ana")
                .age(10)
                .bornDate(dateTime)
                .build();
        MockUser user2 = MockUser.builder()
                .id(2)
                .name("Matei")
                .age(11)
                .bornDate(dateTime.minusYears(1))
                .build();
        MockUser user3 = MockUser.builder()
                .id(3)
                .name("Andrei")
                .age(12)
                .bornDate(dateTime.minusYears(2))
                .build();

        users = List.of(user1, user2, user3);
    }


    @Test
    public void getUsersOverEleven(){
        String search = "select where age > 11";
        List<MockUser> users = qlpUser.getAllByQuery(search);
        assertEquals(1, users.size());
    }

    @Test
    public void getUsersBornBefore2020(){
        String search = "select where borndate<2020-01-01";
        List<MockUser> users = qlpUser.getAllByQuery(search);
        assertEquals(2, users.size());
    }

    @Test
    public void getUsersNameLike(){
        String search = "select where name like \"an\"";
        List<MockUser> users = qlpUser.getAllByQuery(search);
        assertEquals(2, users.size());
    }
}
