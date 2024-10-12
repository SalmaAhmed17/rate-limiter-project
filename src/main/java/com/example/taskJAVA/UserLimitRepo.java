package com.example.taskJAVA;

import com.example.taskJAVA.UserLimit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLimitRepo extends JpaRepository<UserLimit, Integer> {
      UserLimit findByUserId(int userId);
}

/* the method findByUserId will be used as a called fn to give it the user id and req any CRUD op*/