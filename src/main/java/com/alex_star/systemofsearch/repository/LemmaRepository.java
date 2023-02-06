package com.alex_star.systemofsearch.repository;

import com.alex_star.systemofsearch.model.Lemma;
import com.alex_star.systemofsearch.model.RankResult;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository

public interface LemmaRepository extends CrudRepository<Lemma, Integer> {

  List<Lemma> findSiteByLemma(String lemma);

  @Query(value = "SELECT * from lemma WHERE id IN(:id)", nativeQuery = true)
  List<Lemma> findSiteById(int[] id);

  @Query(value = "SELECT count(*) from Lemma where site_id = :id", nativeQuery = true)
  long count(@Param("id") long id);

  @Query(value = "SELECT * FROM  Lemma WHERE lemma =(:key) and site_id=(:siteId)", nativeQuery = true)
  Lemma getLemmaByName(String key, int siteId);

  @Query(value = """
            select distinct page_id as pageId,
                   sum(ranking) over (partition by page_id) as sumRanking
            from Indexing i
                     join Lemma l on i.lemma_id = l.id
            where (
                              page_id in (:pageIds)
                          and lemma in (:lemmas)
                          and site_id = :site_id
                      )
            """,
      nativeQuery = true)
  List<RankResult> getRanks(List<Integer> pageIds, Collection<String> lemmas, int site_id);
}
