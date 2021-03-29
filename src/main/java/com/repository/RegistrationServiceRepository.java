package com.repository;

import com.entity.custom.Registration_Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface RegistrationServiceRepository extends JpaRepository<Registration_Service,Integer> {
    List<Registration_Service> findAllByServiceIdAndSpecIdOrderById(long serviceId, long specChatId);
    List<Registration_Service> findAllBySpecId(long serviceId);

    Registration_Service findById(long id);

//    List<Registration_Service> findAllByIinAndDateRegBetween(String iin, Date dateBegin, Date dateEnd);
//    List<Registration_Service> findAllByServiceIdAndDateRegBetween(long service_id, Date dateBegin, Date dateEnd);

    //List<Registration_Service> findAllByIinAndDateRegBetweenAndIsComeTrue(String iin, Date dateBegin, Date dateEnd);


    List<Registration_Service> findAllByServiceIdAndSpecIdAndIsFinishOrderById(long serviceId, long specId, boolean isFinish);

    List<Registration_Service> findAllByIinAndDateRegBetweenOrderById(String iin, Date dateBegin, Date dateEnd);
    List<Registration_Service> findAllByServiceIdAndDateRegBetweenOrderById(long service_id, Date dateBegin, Date dateEnd);
    List<Registration_Service> findAllByDateRegBetweenOrderById(Date dateBegin, Date dateEnd);
}
