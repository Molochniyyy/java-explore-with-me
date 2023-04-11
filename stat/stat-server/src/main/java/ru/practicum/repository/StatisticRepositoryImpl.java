package ru.practicum.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.ViewStatsDto;
import ru.practicum.model.EndpointHit;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class StatisticRepositoryImpl implements StatisticRepositoryOwn{
    private final EntityManager entityManager;
    @Override
    public List<ViewStatsDto> findAllWithHits(LocalDateTime startTime, LocalDateTime endTime, List<String> uris, Boolean unique) {
        // подготовливаем основу для запроса
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ViewStatsDto> criteriaQuery =
                criteriaBuilder.createQuery(ViewStatsDto.class);
        Root<EndpointHit> root = criteriaQuery.from(EndpointHit.class);

        // where
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.between(root.get("timestamp"), startTime, endTime));

        if (uris != null && uris.size() != 0) {
            predicates.add(root.get("uri").in(uris));
        }
        criteriaQuery.where(predicates.toArray(new Predicate[0]));

        // select
        criteriaQuery.multiselect(root.get("app"), root.get("uri"),
                unique != null && unique
                        ? criteriaBuilder.countDistinct(root.get("ip"))
                        : criteriaBuilder.count(root.get("ip")));

        // group and order
        criteriaQuery.groupBy(root.get("uri"), root.get("app"));
        criteriaQuery.orderBy(criteriaBuilder.desc(criteriaBuilder.literal(3)));

        // создаем запрос и отправляем
        TypedQuery<ViewStatsDto> query = entityManager.createQuery(criteriaQuery);
        return query.getResultList();
    }
}
