package com.gymcrm.dao;

import com.gymcrm.model.TrainingType;
import org.springframework.stereotype.Repository;

@Repository
public class TrainingTypeDao extends AbstractJpaDao<TrainingType, Long>{
    public TrainingTypeDao() {
        super(TrainingType.class);
    }

    @Override
    public TrainingType save(TrainingType entity) {
        throw new UnsupportedOperationException("Save is not allowed for TrainingType. It's read-only.");
    }

    @Override
    public void deleteById(Long aLong) {
        throw new UnsupportedOperationException("Delete is not allowed for TrainingType. It's read-only.");
    }
}
