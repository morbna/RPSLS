package acs.dal;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import acs.data.ElementEntity;
import acs.data.sub.ElementIdPk;

public interface ElementDao extends PagingAndSortingRepository<ElementEntity, ElementIdPk> {

	public List<ElementEntity> findAllByNameLike(@Param("name") String name, Pageable pageable);

	public List<ElementEntity> findAllByNameLikeAndActiveTrue(@Param("name") String name, Pageable pageable);

	public List<ElementEntity> findAllByTypeLike(@Param("type") String type, Pageable pageable);

	public List<ElementEntity> findAllByTypeLikeAndActiveTrue(@Param("name") String name, Pageable pageable);

	public List<ElementEntity> findAllByActiveTrue(Pageable pageable);

	public List<ElementEntity> findAllByLatBetweenAndLngBetween(@Param("latMin") double latMin,
			@Param("latMax") double latMax, @Param("lngMin") double lngMin, @Param("lngMax") double lngMax,
			Pageable pageable);

	public List<ElementEntity> findAllByLatBetweenAndLngBetweenAndActiveTrue(@Param("latMin") double latMin,
			@Param("latMax") double latMax, @Param("lngMin") double lngMin, @Param("lngMax") double lngMax,
			Pageable pageable);

	public List<ElementEntity> findAllByParentEntitiesElementId(ElementIdPk id, Pageable pageable);

	public List<ElementEntity> findAllByActiveTrueAndParentEntitiesElementId(ElementIdPk id, Pageable pageable);

	public List<ElementEntity> findAllByChildEntitiesElementId(ElementIdPk id, Pageable pageable);

	public List<ElementEntity> findAllByActiveTrueAndChildEntitiesElementId(ElementIdPk id, Pageable pageable);

	public List<ElementEntity> findAllByLatBetweenAndLngBetweenAndActiveTrueAndTypeLike(@Param("latMin") double latMin,
			@Param("latMax") double latMax, @Param("lngMin") double lngMin, @Param("lngMax") double lngMax,
			@Param("type") String type, Pageable pageable);

	public ElementEntity findByUserDomainAndUserEmailAndTypeLike(@Param("userDomain") String userDomain,
			@Param("userEmail") String userEmail, @Param("type") String type);

}