package com.alex_star.systemofsearch.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;

@Service
public class LemmatizerService {
    private static final Log log = LogFactory.getLog(LemmatizerService.class);
    String[] staffWords = new String[]{
            " СОЮЗ",
            " ПРЕДЛ",
            " МЕЖД",
            " ЧАСТ",
    };

    LuceneMorphology luceneMorph;

    public LemmatizerService() {
        try {
            luceneMorph = new RussianLuceneMorphology();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, Integer> lemmatizer(String text) {
        String[] words = splitText(text);
        List<String> officialParts = purgeOfOfficialPartsOfSpeech(words);
        HashMap<String, Integer> countOfLemmas = new HashMap<>();
        for (String w : officialParts) {
            if (!countOfLemmas.containsKey(w)) {
                countOfLemmas.put(w, 0);
            }
            countOfLemmas.replace(w, countOfLemmas.get(w) + 1);
        }
        return countOfLemmas;
    }


    public ArrayList<String> getLemmas(String text) {
        ArrayList<String> list = new ArrayList<>();
        text = text.replaceAll("[\\pP\\s]", "")
                .replace("_", "").replaceAll("[^А-Я,а-я]*", "").toLowerCase();
        String[] words = splitText(text);
        List<String> officialParts = purgeOfOfficialPartsOfSpeech(words);
        for (String word : officialParts) {
            list.add(word.trim());
        }
        return list;
    }


    public String[] splitText(String text) {
        return text.split(" ");
    }

    private List<String> purgeOfOfficialPartsOfSpeech(String[] wordsArray) {
        ArrayList<String> result = new ArrayList<>();
        try {
            for (String s : wordsArray) {
                String word = s.replaceAll("[\\pP\\s]", "")
                        .replace("_", "").replaceAll("[^А-Я,а-я]*", "").toLowerCase();
                if (word.length() < 1) {
                    continue;
                }
                List<String> morphInfo = luceneMorph.getNormalForms(word);
                for (String w : morphInfo) {
                    String handledWord = clearStaffWords(w);
                    if (!handledWord.isEmpty()) {
                        result.add(handledWord);
                    }
                }
            }
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            return result;
        }
    }


    private String clearStaffWords(String wordBaseForm) {
        for (String staff : staffWords) {
            if (wordBaseForm.contains(staff)) {
                return "";
            }
        }
        return wordBaseForm.replaceAll("[|].*", "");
    }

    public ArrayList<Integer> findLemmaIndexInText(String text, String lemma) {
        ArrayList<Integer> listOfIndexes = new ArrayList<>();
        String[] list = text.split("—|\\p{Punct}|\\s");
        int index = 0;
        for (String s1 : list) {
            List<String> lemmas = new ArrayList<>();
            try {
                lemmas = luceneMorph.getNormalForms(s1.toLowerCase(Locale.ROOT));
            } catch (Exception e) {
                log.debug("Ошибка морфологического анализа. Пропущенные символы: " + s1);
            }
            for (String s2 : lemmas) {
                if (s2.equals(lemma)) {
                    listOfIndexes.add(index);
                }
            }
            index += s1.length() + 1;
        }
        return listOfIndexes;
    }
}
