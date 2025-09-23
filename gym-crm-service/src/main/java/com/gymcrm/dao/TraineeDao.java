package com.gymcrm.dao;

import com.gymcrm.model.Trainee;
import org.springframework.stereotype.Repository;

@Repository
public class TraineeDao extends AbstractUserJpaDao<Trainee> {

    public TraineeDao() {
        super(Trainee.class);
    }
}
