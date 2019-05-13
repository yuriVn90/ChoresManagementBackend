package playground.dal;

import org.springframework.data.repository.CrudRepository;

import playground.logic.EntityComponents.ActivityEntity;
import playground.logic.EntityComponents.ActivityId;

public interface ActivityDao extends CrudRepository<ActivityEntity, ActivityId> {

}
