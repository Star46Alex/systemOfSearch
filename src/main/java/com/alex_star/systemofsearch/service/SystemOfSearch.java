package com.alex_star.systemofsearch.service;

import com.alex_star.systemofsearch.dto.response.SearchResponse;
import com.alex_star.systemofsearch.dto.searchResponseStorage.RelevanceStorage;
import com.alex_star.systemofsearch.lemmatizer.Lemmatizer;
import com.alex_star.systemofsearch.model.*;
import com.alex_star.systemofsearch.repository.LemmaRepository;
import com.alex_star.systemofsearch.util.MapUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SystemOfSearch {

  private final Lemmatizer lemmatizer;
  private final SiteRepositoryService siteRepositoryService;
  private final IndexRepositoryService indexRepositoryService;
  private final LemmaRepositoryService lemmaRepositoryService;
  private final LemmaRepository lemmaRepository;
  private final PageRepositoryService pageRepositoryService;


  public SystemOfSearch(SiteRepositoryService siteRepositoryService,
      IndexRepositoryService indexRepositoryService,
      LemmaRepositoryService lemmaRepositoryService, LemmaRepository lemmaRepository,
      PageRepositoryService pageRepositoryService) {
    this.lemmaRepository = lemmaRepository;
    this.lemmatizer = new Lemmatizer();
    this.siteRepositoryService = siteRepositoryService;
    this.indexRepositoryService = indexRepositoryService;
    this.lemmaRepositoryService = lemmaRepositoryService;
    this.pageRepositoryService = pageRepositoryService;
  }

  public ArrayList<Integer> initialPageIds = new ArrayList<>();


  public SearchResponse searchResult(Request request, String url, int offset, int limit) {
    try {
      Site site = siteRepositoryService.getSite(url);
      initialPageIds = new ArrayList<>();
      HashMap<String, Integer> lemmas = lemmatizer.lemmatize(request.getRequest());
      if (site != null) {
        lemmaRepositoryService.sortLemmasByFrequency(lemmas, site.getId());
      }
      LinkedHashMap<String, Integer> sortedLemmas = sortingLemmasByFrequency(lemmas);
      filterPagesByLemmas(sortedLemmas);
      List<RankResult> ranksList = buildRankResults(site, lemmas);
      if (ranksList.isEmpty()) {
        return new SearchResponse(false);
      }
      sortResults(ranksList);
      int count = ranksList.size();
      ranksList = limitRankResults(ranksList, limit, offset);
      return buildSearchResponse(ranksList, request,count);
    } catch (SQLException throwables) {
      throwables.printStackTrace();
    }
    return new SearchResponse();
  }

private List<RankResult> buildRankResults(Site site,HashMap<String, Integer> lemmas) {
  List<RankResult> ranksList;
  if (site == null) {
    ranksList = new ArrayList<>();
    List<Site> sites = siteRepositoryService.getAllSites();
    for (Site el : sites) {
      List<RankResult> ranks = calculateRanks(lemmas.keySet(), el.getId());
      ranksList.addAll(ranks);
    }
  } else {
    int siteId = 0;
    if (site != null) {
      siteId = site.getId();
    }
    ranksList = calculateRanks(lemmas.keySet(), siteId);
  }
  return ranksList;
}
  private List<RankResult> limitRankResults(List<RankResult> ranksList, int limit, int offset)
  {
    List<RankResult> limited = new ArrayList<>();
    for (int i = offset; i < offset + limit && i < ranksList.size(); i++) {
      limited.add(ranksList.get(i));
    }
    return  limited;
  }

  private List<RankResult> sortResults(List<RankResult> ranksList)
  {
    ranksList.sort(Comparator.comparingDouble(RankResult::getSumRanking));
    Collections.reverse(ranksList);
    return ranksList;
  }

  private SearchResponse buildSearchResponse(List<RankResult> ranksList, Request request,int count) {
    ArrayList<RelevanceStorage> relevanceStoragesL = new ArrayList<>(calculateRelevance(ranksList, request));
    return new SearchResponse(true, count, relevanceStoragesL);
  }

  public RelevanceStorage buildRelevanceStorage(
      Page page, Request request) {
    RelevanceStorage response = new RelevanceStorage();
    Site site = siteRepositoryService.getSite(page.getSiteId());
    String siteName = site.getName();
    String uri = page.getPath();
    String title = getTitle(page.getContent());
    String snippet = getSnippet(page.getContent(), request);
    response.setSiteName(siteName);
    response.setUri(uri);
    response.setTitle(title);
    response.setSnippet(snippet);
    return response;
  }


  public List<RankResult> calculateRanks(Collection<String> lemmas,
      int siteId) {
    if (!initialPageIds.isEmpty()) {
      return  lemmaRepository.getRanks(initialPageIds, lemmas, siteId);
    }
    return new ArrayList<>();
  }

  private ArrayList<RelevanceStorage> calculateRelevance(List<RankResult> rankResults,
      Request request) {
    ArrayList<RelevanceStorage> relevanceStorages = new ArrayList<>();
    double maxRelevance = 0.0;
    for (RankResult rankResult : rankResults) {
      Optional<Page> page =
          pageRepositoryService.findPageById(rankResult.getPageId());
      if (page.isPresent()) {
        RelevanceStorage relevanceStorage = buildRelevanceStorage(page.get(), request);
        relevanceStorage.setRelevance(rankResult.getSumRanking());
        relevanceStorages.add(relevanceStorage);
      }
      if (maxRelevance < rankResult.getSumRanking()) {
        maxRelevance = rankResult.getSumRanking();
      }
    }
    for (RelevanceStorage storage : relevanceStorages) {
      if (maxRelevance != 0) {
        storage.setRelevance(storage.getRelevance() / maxRelevance);
      }
    }
    return relevanceStorages;
  }


  public void filterPagesByLemmas(LinkedHashMap<String, Integer> sorted) throws SQLException {
    for (Map.Entry<String, Integer> entry : sorted.entrySet()) {
      ArrayList<Integer> currentPageIds = new ArrayList<>();
      List<Lemma> lemma = lemmaRepositoryService.getLemma(entry.getKey());
      List<Indexing> res = indexRepositoryService.getAllIndexingByLemmaId(lemma.get(0).getId());
      for (Indexing item : res) {
        currentPageIds.add(item.getPageId());
      }
      for (Indexing item : res) {
        if (!currentPageIds.contains(item.getPageId())) {
          initialPageIds.remove((item.getPageId()));
        }
      }
    }
  }

  public LinkedHashMap<String, Integer> sortingLemmasByFrequency(HashMap<String, Integer> lemmas) {
    LinkedHashMap<String, Integer> sorted = new LinkedHashMap<>(MapUtil.sortByValue(lemmas));
    for (String key : sorted.keySet()) {
      List<Lemma> lemma = lemmaRepositoryService.getLemma(key);
      if (lemma != null && !lemma.isEmpty()) {
        for (Lemma lemmaKey : lemma) {
          List<Indexing> initialResultSet = indexRepositoryService.getAllIndexingByLemmaId(
              lemmaKey.getId());
          for (Indexing indexing : initialResultSet) {
            int element = indexing.getPageId();
            if (!initialPageIds.contains(element)) {
              initialPageIds.add(element);
            }
          }
        }
      }
    }
    return sorted;
  }

  private List<TreeSet<Integer>> getSearchingIndexes(String string, Set<Integer> indexesOfBolt) {
    ArrayList<Integer> indexes = new ArrayList<>(indexesOfBolt);
    List<TreeSet<Integer>> list = new ArrayList<>();
    TreeSet<Integer> temp = new TreeSet<>();
    for (int i = 0; i < indexes.size(); i++) {
      String s = string.substring(indexes.get(i));
      int end = s.indexOf(" ");
      if ((i + 1) <= indexes.size() - 1 && (indexes.get(i + 1) - indexes.get(i)) < end) {
        temp.add(indexes.get(i));
        temp.add(indexes.get(i + 1));
      } else {
        if (!temp.isEmpty()) {
          list.add(temp);
          temp = new TreeSet<>();
        }
        temp.add(indexes.get(i));
        list.add(temp);
        temp = new TreeSet<>();
      }
    }
    list.sort((Comparator<Set<Integer>>) (o1, o2) -> o2.size() - o1.size());
    ArrayList<TreeSet<Integer>> searchingIndexes = new ArrayList<>();
    for (int i = 0; i < 2; i++) {
      if (list.size() > i) {
        searchingIndexes.add(list.get(i));
      }
    }
    return searchingIndexes;
  }

  private String getSnippet(String html, Request request) {
    String string = "";
    Document document = Jsoup.parse(html);
    Elements titleElements = document.select("title");
    Elements bodyElements = document.select("body");
    StringBuilder builder = new StringBuilder();
    titleElements.forEach(element -> builder.append(element.text()).append(" ").append("\n"));
    bodyElements.forEach(element -> builder.append(element.text()).append(" "));
    if (!builder.isEmpty()) {
      string = builder.toString();
    }
    List<String> req = request.getRequestLemmas();
    Set<Integer> integerList = new TreeSet<>();
    for (String s : req) {
      integerList.addAll(lemmatizer.findLemmaIndexInText(string, s));
    }
    return setLemmaBold(string,integerList);
  }

  private String getTitle(String html) {
    String string = "";
    Document document = Jsoup.parse(html);
    Elements elements = document.select("title");
    StringBuilder builder = new StringBuilder();
    elements.forEach(element -> builder.append(element.text()).append(" "));
    if (!builder.isEmpty()) {
      string = builder.toString();
    }
    return string;
  }
  private String setLemmaBold(String string,Set<Integer> integerList){
    List<TreeSet<Integer>> indexesList = getSearchingIndexes(string, integerList);
    StringBuilder builder1 = new StringBuilder();
    for (TreeSet<Integer> set : indexesList) {
      int from = set.first();
      int to = set.last();
      Pattern pattern = Pattern.compile("\\p{Punct}|\\s");
      Matcher matcher = pattern.matcher(string.substring(to));
      int offset = 0;
      if (matcher.find()) {
        offset = matcher.end();
      }
      builder1.append("<b>")
          .append(string, from, to + offset)
          .append("</b>");
      int snippetMaxStringSize = 30;
      int snippetSymbolsCount = 90;
      if (!((string.length() - to) < snippetMaxStringSize)) {
        builder1.append(string, to + offset, string.indexOf(" ", to
            + offset + snippetSymbolsCount)).append("... ");
      }
    }
    return builder1.toString();
  }
}