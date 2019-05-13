package playground.dal;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import playground.logic.EntityComponents.UserEntity;
import playground.logic.EntityComponents.UserId;

@RepositoryRestResource
public interface UserDao extends PagingAndSortingRepository<UserEntity, UserId>{
	
	public List<UserEntity> findAllByOrderByPointsDesc();
}
