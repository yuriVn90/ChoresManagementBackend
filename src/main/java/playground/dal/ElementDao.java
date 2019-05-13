package playground.dal;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.repository.query.Param;

import playground.logic.EntityComponents.ElementEntity;
import playground.logic.EntityComponents.ElementId;

@RepositoryRestResource
public interface ElementDao extends PagingAndSortingRepository<ElementEntity, ElementId> {
	
	public List<ElementEntity> findAllByNameLike(@Param("value") String value, Pageable pageable);

	public List<ElementEntity> findAllByTypeLike(@Param("value") String value, Pageable pageable);
	
	public List<ElementEntity> findAllByXBetweenAndYBetween(@Param("xBottom") double xBottom,
															@Param("xTop") double xTop,
															@Param("yBottom") double yBottom,
															@Param("yTop") double yTop,
															Pageable pageable);
	
	public ElementEntity findByElementIdAndExpirationDateAfterOrExpirationDateIsNull(@Param("id") ElementId id, @Param("now") Date now);
	
	public List<ElementEntity> findAllByExpirationDateAfterOrExpirationDateIsNull(@Param("now") Date now, Pageable pageable);
	
	public List<ElementEntity> findAllByXBetweenAndYBetweenAndExpirationDateAfterOrExpirationDateIsNull(@Param("xBottom") double xBottom,
																										@Param("xTop") double xTop,
																										@Param("yBottom") double yBottom,
																										@Param("yTop") double yTop,
																										@Param("now") Date now,
																										Pageable pageable);
	
	public List<ElementEntity> findAllByNameLikeAndExpirationDateAfterOrExpirationDateIsNull(@Param("value") String value, @Param("now") Date now, Pageable pageable);
	
	public List<ElementEntity> findAllByTypeLikeAndExpirationDateAfterOrExpirationDateIsNull(@Param("value") String value, @Param("now") Date now, Pageable pageable);
}
