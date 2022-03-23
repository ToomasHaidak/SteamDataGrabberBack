package com.valiit.svrd.service;

import com.valiit.svrd.controller.VrGamePageResponse;
import com.valiit.svrd.repository.SVRD_repository;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class SVRD_service {

    @Autowired
    private SVRD_repository svrd_repository;

    public void getAllGames() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.63 Safari/537.36");

        String url = "https://steamdb.info/tag/21978/";
        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);

        ResponseEntity<String> respEntity = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        String body = respEntity.getBody();
        int lastIndex = body.lastIndexOf("<td class=\"text-left\">");
        int index = 0;
        while (index <= lastIndex) {
            int startIndex = body.indexOf("<td class=\"text-left\">", index) + 36;
            index = startIndex;

            int endIndex = body.indexOf("</a>", startIndex);
            String dataThatINeed = body.substring(startIndex, endIndex);
            String[] data = dataThatINeed.split("/\">");
            if (data[0].equals("353370") || data[0].equals("250820") ) {
                continue;
            }
            getGameData(data[0], data[1]);

        }
    }

    public void getGameData(String app_id, String nimi) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
//        headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.63 Safari/537.36");
//
//        String url = "https://store.steampowered.com/app/" + app_id + "/";
//        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
//
//        ResponseEntity<String> respEntity = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.set("Cookie", "wants_mature_content=1; steamCountry=EE%7Ce694a0623764709451289745f496db27; sessionid=949017c0f5e75e2a9c5bf24b; timezoneOffset=10800,0; birthtime=-1864604388; lastagecheckage=1-0-1911; cookieSettings=%7B%22version%22%3A1%2C%22preference_state%22%3A1%2C%22content_customization%22%3Anull%2C%22valve_analytics%22%3Anull%2C%22third_party_analytics%22%3Anull%2C%22third_party_content%22%3Anull%2C%22utm_enabled%22%3Atrue%7D; browserid=2372824436322873330; _ga=GA1.2.518570665.1632812982; _gid=GA1.2.572596709.1632812982; recentapps=%7B%22418370%22%3A1632812984%7D");
        ResponseEntity<String> respEntity = restTemplate.exchange("https://store.steampowered.com/app/" + app_id + "/",
                HttpMethod.GET, new HttpEntity<String>(requestHeaders), String.class);
        String body = respEntity.getBody();
        insertOneGamePublisher(body);
        if (svrd_repository.checkIfGameInDatabase(app_id) == 0) {
            String publisher = insertOneGamePublisher(body);
            insertOneGameDataValues(body, app_id, nimi, publisher);
        } else {
            updateGameData(body, app_id);
        }

    }

    public String insertOneGamePublisher(String body) {
        String publisher = getPublisherName(body);
        if (svrd_repository.checkIfPublisherInDatabase(publisher) == 0) {
            svrd_repository.insertOneGamePublisher(publisher);
        }
        return publisher;
    }

    public void insertOneGameDataValues(String body, String appId, String nimi, String publisher) {

        Double currentPrice;
        Double currentDiscountedPrice = null;
//        Double lowestHistoricPrice = svrd_repository.checkLowestHistoricPrice(appId);;

        if (isDiscount(body)) {
            currentPrice = getNormalPriceWhenDiscounted(body);
            currentDiscountedPrice = getDiscountedPrice(body);

//            if (lowestHistoricPrice != null) {
//                if (currentDiscountedPrice < lowestHistoricPrice) {
//                    lowestHistoricPrice = currentDiscountedPrice;
//                }
//            } else {
//                lowestHistoricPrice = currentDiscountedPrice;
//            }
        } else {
            currentPrice = getNonDiscountedPrice(body);
//            if (lowestHistoricPrice != null) {
//                if (currentPrice < lowestHistoricPrice) {
//                    lowestHistoricPrice = currentPrice;
//                }
//            } else {
//                lowestHistoricPrice = currentPrice;
//            }
        }


        LocalDate releaseDate = getReleaseDate(body);
        boolean earlyAccess = getEarlyAccess(body);
        Double metascore = getMetascore(body);
        int reviews = getAllReviewsNumber(body);
        Double positiveReviews = getPositiveReviews(body);
//        String osRecommended = getOSRecommended(body);
//        String processorRecommended = getProcessorRecommended(body);
//        String memoryRecommended = getMemoryRecommended(body);
//        String graphicsRecommended = getGraphicsRecommended(body);
//        String directxRecommended = getDirectXRecommended(body);
//        String networkRecommended = getNetworkRecommended(body);
//        String storageRecommended = getStorageRecommended(body);
//        String osMinimum = getOSMinimum(body);
//        String processorMinimum = getProcessorMinimum(body);
//        String memoryMinimum = getMemoryMinimum(body);
//        String graphicsMinimum = getGraphicsMinimum(body);
//        String directxMinimum = getDirectXMinimum(body);
//        String networkMinimum = getNetworkMinimum(body);
//        String storageMinimum = getStorageMinimum(body);

        String about = getAboutGame(body);

        int publisherId =  svrd_repository.getPublisherId(publisher);


        int gameId = svrd_repository.insertOneGameDataValues(appId, nimi, currentPrice, currentDiscountedPrice,
                500.00, releaseDate, earlyAccess,
                metascore, reviews, positiveReviews, about, publisherId);
//                osRecommended,
//                processorRecommended, memoryRecommended,
//                graphicsRecommended, directxRecommended, networkRecommended, storageRecommended,
//                osMinimum, processorMinimum, memoryMinimum,
//                graphicsMinimum, directxMinimum, networkMinimum, storageMinimum,


        getAndInsertDeveloper(body, gameId);
        insertGenre(body, gameId);
        insertHeadset(body, gameId);
        insertInput(body, gameId);
        insertPlayarea(body, gameId);
        insertPlayerCount(body, gameId);
        insertTags(body, gameId);
    }

    public void updateGameData(String body, String appId) {
        Double currentPrice;
        Double currentDiscountedPrice = null;
//        Double lowestHistoricPrice = svrd_repository.checkLowestHistoricPrice(appId);;

        if (isDiscount(body)) {
            currentPrice = getNormalPriceWhenDiscounted(body);
            currentDiscountedPrice = getDiscountedPrice(body);

//            if (lowestHistoricPrice != null) {
//                if (currentDiscountedPrice < lowestHistoricPrice) {
//                    lowestHistoricPrice = currentDiscountedPrice;
//                }
//            } else {
//                lowestHistoricPrice = currentDiscountedPrice;
//            }
        } else {
            currentPrice = getNonDiscountedPrice(body);
//            if (lowestHistoricPrice != null) {
//                if (currentPrice < lowestHistoricPrice) {
//                    lowestHistoricPrice = currentPrice;
//                }
//            } else {
//                lowestHistoricPrice = currentPrice;
//            }
        }


        LocalDate releaseDate = getReleaseDate(body);
        boolean earlyAccess = getEarlyAccess(body);
        Double metascore = getMetascore(body);
        int reviews = getAllReviewsNumber(body);
        Double positiveReviews = getPositiveReviews(body);

       svrd_repository.updateGameData(appId, currentPrice, currentDiscountedPrice,
                500.00, releaseDate, earlyAccess,
                metascore, reviews, positiveReviews);
    }

    public String getOSRecommended(String body) {
        int index = body.indexOf("<strong>Recommended:");
        int startIndex = body.indexOf("<strong>OS:</strong>", index) + 20;
        int endIndex = body.indexOf("<br></li><li>", startIndex);
        if (endIndex == -1) {
            System.err.println("Viga: getOSRecommended. End Index = -1");
            return "";
        }
        String data = body.substring(startIndex, endIndex);
        return data;
    }

    public String getProcessorRecommended(String body) {
        int index = body.indexOf("<strong>Recommended:");
        int startIndex = body.indexOf("<strong>Processor:</strong>", index) + 27;
        int endIndex = body.indexOf("<br></li><li>", startIndex);
        if (endIndex == -1) {
            System.err.println("Viga: getProcessorRecommended. End Index = -1");
            return "";
        }
        String data = body.substring(startIndex, endIndex);
        return data;
    }

    public String getMemoryRecommended(String body) {
        int index = body.indexOf("<strong>Recommended:");
        int startIndex = body.indexOf("<strong>Memory:</strong>", index) + 24;
        int endIndex = body.indexOf("<br></li><li>", startIndex);
        if (endIndex == -1) {
            System.err.println("Viga: getMemoryRecommended. End Index = -1");
            return "";
        }
        String data = body.substring(startIndex, endIndex);
        return data;
    }

    public String getGraphicsRecommended(String body) {
        int index = body.indexOf("<strong>Recommended:");
        int startIndex = body.indexOf("<strong>Graphics:</strong>", index) + 26;
        int endIndex = body.indexOf("<br></li><li>", startIndex);
        if (endIndex == -1) {
            System.err.println("Viga: getGraphicsRecommended. End Index = -1");
            return "";
        }
        String data = body.substring(startIndex, endIndex);
        return data;
    }

    public String getDirectXRecommended(String body) {
        int index = body.indexOf("<strong>Recommended:");
        int startIndex = body.indexOf("<strong>DirectX:</strong>", index) + 25;
        int endIndex = body.indexOf("<br></li><li>", startIndex);
        if (endIndex == -1) {
            System.err.println("Viga: getDirectXRecommended. End Index = -1");
            return "";
        }
        String data = body.substring(startIndex, endIndex);
        return data;
    }

    public String getNetworkRecommended(String body) {
        int index = body.indexOf("<strong>Recommended:");
        int startIndex = body.indexOf("<strong>Network:</strong>", index) + 25;
        int endIndex = body.indexOf("<br></li><li>", startIndex);
        if (endIndex == -1) {
            System.err.println("Viga: getNetworkRecommended. End Index = -1");
            return "";
        }
        String data = body.substring(startIndex, endIndex);
        return data;
    }

    public String getStorageRecommended(String body) {
        int index = body.indexOf("<strong>Recommended:");
        int startIndex = body.indexOf("<strong>Storage:</strong>", index) + 25;
        int endIndex = body.indexOf("<br></li>", startIndex);
        if (endIndex == -1) {
            System.err.println("Viga: getAllReviewsNumber. End Index = -1");
            return "";
        }
        String data = body.substring(startIndex, endIndex);
        return data;
    }

    public String getOSMinimum(String body) {
        int index = body.indexOf("<strong>Minimum:");
        int startIndex = body.indexOf("<strong>OS:</strong>", index) + 20;
        int endIndex = body.indexOf("<br></li><li>", startIndex);
        if (endIndex == -1) {
            System.err.println("Viga: getOSMinimum. End Index = -1");
            return "";
        }
        String data = body.substring(startIndex, endIndex);
        return data;
    }

    public String getProcessorMinimum(String body) {
        int index = body.indexOf("<strong>Minimum:");
        int startIndex = body.indexOf("<strong>Processor:</strong>", index) + 27;
        int endIndex = body.indexOf("<br></li><li>", startIndex);
        if (endIndex == -1) {
            System.err.println("Viga: getProcessorMinimum. End Index = -1");
            return "";
        }
        String data = body.substring(startIndex, endIndex);
        return data;
    }

    public String getMemoryMinimum(String body) {
        int index = body.indexOf("<strong>Minimum:");
        int startIndex = body.indexOf("<strong>Memory:</strong>", index) + 24;
        int endIndex = body.indexOf("<br></li><li>", startIndex);
        if (endIndex == -1) {
            System.err.println("Viga: getMemoryMinimum. End Index = -1");
            return "";
        }
        String data = body.substring(startIndex, endIndex);
        return data;
    }

    public String getGraphicsMinimum(String body) {
        int index = body.indexOf("<strong>Minimum:");
        int startIndex = body.indexOf("<strong>Graphics:</strong>", index) + 26;
        int endIndex = body.indexOf("<br></li><li>", startIndex);
        if (endIndex == -1) {
            System.err.println("Viga: getGraphicsMinimum. End Index = -1");
            return "";
        }
        String data = body.substring(startIndex, endIndex);
        return data;
    }

    public String getDirectXMinimum(String body) {
        int index = body.indexOf("<strong>Minimum:");
        int startIndex = body.indexOf("<strong>DirectX:</strong>", index) + 25;
        int endIndex = body.indexOf("<br></li><li>", startIndex);
        if (endIndex == -1) {
            System.err.println("Viga: getDirectXMinimum. End Index = -1");
            return "";
        }
        String data = body.substring(startIndex, endIndex);
        return data;
    }

    public String getNetworkMinimum(String body) {
        int index = body.indexOf("<strong>Minimum:");
        int startIndex = body.indexOf("<strong>Network:</strong>", index) + 25;
        int endIndex = body.indexOf("<br></li><li>", startIndex);
        if (endIndex == -1) {
            System.err.println("Viga: getNetworkMinimum. End Index = -1");
            return "";
        }
        String data = body.substring(startIndex, endIndex);
        return data;
    }

    public String getStorageMinimum(String body) {
        int index = body.indexOf("<strong>Minimum:");
        int startIndex = body.indexOf("<strong>Storage:</strong>", index) + 25;
        int endIndex = body.indexOf("</li></ul>", startIndex);
        if (endIndex == -1) {
            System.err.println("Viga: getStorageMinimum. End Index = -1");
            return "";
        }
        String data = body.substring(startIndex, endIndex);
        return data;
    }

    public Double getMetascore(String body) {
        int index = body.indexOf("<div id=\"game_area_metascore\">");
        int startIndex = body.indexOf("<div class=\"score high\">", index) + 24;
        int endIndex = body.indexOf("</div>", startIndex);
        if (endIndex == -1) {
            System.err.println("Viga: getMetascore. End Index = -1");
            return 0.0;
        }
        String data = body.substring(startIndex, endIndex).trim();
        Double väärtus = parseDouble(data);
        return väärtus;
    }

    public String getAboutGame(String body) {
        int index = body.indexOf("<h2>About This Game</h2>");
        int startIndex = body.indexOf("</h2>", index) + 5;
        int endIndex = body.indexOf("</div>", startIndex);
        String data = body.substring(startIndex, endIndex);
        data = data.replaceAll("<i>", "");
        return data.replaceAll("</i>", "").trim();
    }

    public String getPublisherName(String body) {
        int index = body.indexOf("<b>Publisher:</b>");
        int startIndex = body.indexOf("\">", index) + 2;
        int endIndex = body.indexOf("</a>", startIndex);
        String publisher = body.substring(startIndex, endIndex);
        return publisher;
    }

    public void getAndInsertDeveloper(String body, int gameId) {
        int index = body.indexOf("<b>Developer:</b>");
        int startIndex = body.indexOf("\">", index) + 2;
        int endIndex = body.indexOf("</a>", startIndex);
        String developer = body.substring(startIndex, endIndex);
        int developerId;
        if (svrd_repository.checkDeveloper(developer) > 0) {
            developerId = svrd_repository.getDeveloper(developer);
        } else {
            developerId = svrd_repository.insertDeveloper(developer);
        }
        svrd_repository.insertDeveloperJoin(gameId, developerId);
    }

    public Double getPositiveReviews(String body) {
        int index = body.indexOf("<div class=\"title\">Overall Reviews:");
        int startIndex = body.indexOf("data-tooltip-html=\"", index) + 19;
        int endIndex = body.indexOf("% of the", startIndex);
        if (endIndex == -1) {
            System.err.println("Viga: getPositiveReviews. End Index = -1");
            return 0.0;
        }
        String data = body.substring(startIndex, endIndex).trim();
        Double väärtus = Double.valueOf(data);
        return väärtus;
    }

    public LocalDate getReleaseDate(String body) {
        int startIndex = body.indexOf("<div class=\"subtitle column\">Release Date:</div>", 0) + 77;
        int endIndex = body.indexOf("</div>", startIndex);
        if (endIndex == -1) {
            System.err.println("Viga: getReleaseDate. End Index = -1");
            return null;
        }
        String dataThatINeed = body.substring(startIndex, endIndex);
        System.out.println(dataThatINeed);
        return parseDate(dataThatINeed);
    }

    public boolean getEarlyAccess(String body) {
        boolean isEarlyAccess = false;
        int startIndex = body.indexOf("name\":\"Early Access", 0);
        if (startIndex >= 0) {
            isEarlyAccess = true;
        }
        return isEarlyAccess;
    }

    public int getAllReviewsNumber(String body) {
        int reviewsNumber;
        String substring = "<span class=\"game_review_summary positive\" data-tooltip-html=\"";
        int startIndex = body.indexOf(substring, 0) + substring.length();
        int startIndex2 = body.indexOf("of the ", startIndex) + 7;
        int endIndex = body.indexOf(" user", startIndex2);
        if (endIndex == -1) {
            System.err.println("Viga: getAllReviewsNumber. End Index = -1");
            return 0;
        }
        String komagaNumber = body.substring(startIndex2, endIndex);
        reviewsNumber = parseInt(komagaNumber);
        return reviewsNumber;
    }

    private int parseInt(String komagaNumber) {
        try {
            return Integer.parseInt(komagaNumber.replace(",", ""));
        } catch (Exception e) {
            System.err.println("Viga: parseInt: " + komagaNumber);
        }
        return 0;
    }

    public boolean isDiscount(String body) {
        int startIndex = body.indexOf("</div><div class=\"discount_final_price\">", 0);
        int bundleIndex = body.indexOf("<span>Bundle info</span>",0);
        if (startIndex == -1) {
            return false;
        }
        if (body.indexOf("<span>Bundle info</span>", 0) >= 0) {
            if (body.indexOf("<span>Bundle info</span>", 0) < startIndex) {
                return false;
            }
        }

        return true;
    }

    public Double getNonDiscountedPrice(String body) {
        int startIndex = body.indexOf("<meta itemprop=\"price\" content=\"", 0) + 32;
        int endIndex = body.indexOf("\">", startIndex);
        String komagaNumber = body.substring(startIndex, endIndex);
        Double nonDiscountedprice = parseDouble(komagaNumber.replace(",", "."));
        return nonDiscountedprice;
    }

    public Double getNormalPriceWhenDiscounted(String body) {
        int startIndex = body.indexOf("<div class=\"discount_prices\"><div class=\"discount_original_price\">", 0) + 66;
        int endIndex = body.indexOf("€</div>", startIndex);
        String komagaNumber = body.substring(startIndex, endIndex);
        komagaNumber = komagaNumber.replace("-", "0");
        System.out.println(komagaNumber);
        Double normalPrice = Double.parseDouble(komagaNumber.replace(",", "."));
        return normalPrice;
    }

    public Double getDiscountedPrice(String body) {
        int startIndex = body.indexOf("</div><div class=\"discount_final_price\">", 0) + 40;
        int endIndex = body.indexOf("€</div>", startIndex);
        String komagaNumber = body.substring(startIndex, endIndex);
        Double discountedPrice = Double.parseDouble(komagaNumber.replace(",", "."));
        return discountedPrice;
    }

    public List<String> getGenre(String body) {
        List<String> genreList = new ArrayList<>();
        int startIndex = 0;
        int startParse = body.indexOf("<b>Genre:</b>", 0);
        int endParse = body.indexOf("<b>Genre:</b>", 0);
        if(startParse >= 0) {
            int parseUntilIndex = body.indexOf("<div class=\"dev_row\">", startParse);
            while (startIndex >= 0) {
                startIndex = body.indexOf("\">", startParse);
                if (startIndex < 0 || startIndex > parseUntilIndex) {
                    break;
                }
                startIndex = startIndex + 2;
                startParse = startIndex;
                int endIndex = body.indexOf("</a>", startIndex);
                String dataThatINeed = body.substring(startIndex, endIndex);
                genreList.add(dataThatINeed);
            }
        }
        return genreList;
    }

    public void insertGenre(String body, int gameId) {
        List<String> genreList = getGenre(body);
        int genreId;
        System.out.println(genreList);
        for (int i = 0; i < genreList.size(); i++) {
            if (svrd_repository.checkGenre(genreList.get(i)) > 0) {
                genreId = svrd_repository.getGenre(genreList.get(i));
            } else {
                genreId = svrd_repository.insertGenre(genreList.get(i));
            }
            svrd_repository.insertGenreJoin(gameId, genreId);
        }
    }

    public List<String> getTags(String body) {
        List<String> tagsList = new ArrayList<>();
        int startIndex = 0;
        int startParse = body.indexOf("class=\"app_tag\" style=\"display: none;\">", 0);
        int endParse = body.indexOf("class=\"app_tag\" style=\"display: none;\">", 0);
        int parseUntilIndex = body.indexOf("onclick=", startParse);
        while (startIndex >= 0) {
            startIndex = body.indexOf(";\">", startParse);
            if (startIndex < 0 || startIndex > parseUntilIndex) {
                break;
            }
            startIndex = startIndex + 3;
            startParse = startIndex;
            int endIndex = body.indexOf("</a>", startIndex);
            String dataThatINeed = body.substring(startIndex, endIndex);
            tagsList.add(dataThatINeed.trim());
        }
        return tagsList;
    }

    public void insertTags(String body, int gameId) {
        List<String> tagsList = getTags(body);
        int tagsId;
        for (int i = 0; i < tagsList.size(); i++) {
            if (svrd_repository.checkTag(tagsList.get(i)) > 0) {
                tagsId = svrd_repository.getTag(tagsList.get(i));
            } else {
                tagsId = svrd_repository.insertTag(tagsList.get(i));
            }
            svrd_repository.insertTagJoin(gameId, tagsId);
        }
    }

    public List<String> getHeadset(String body) {
        List<String> headsetList = new ArrayList<>();
        if (body.indexOf("HTC Vive", 0) >= 0) {
            headsetList.add("HTC Vive");
        }
        if (body.indexOf("Valve Index", 0) >= 0) {
            headsetList.add("Valve Index");
        }
        if (body.indexOf("Oculus Rift", 0) >= 0) {
            headsetList.add("Oculus Rift");
        }
        if (body.indexOf("Windows Mixed Reality ", 0) >= 0) {
            headsetList.add("Windows Mixed Reality");
        }
        return headsetList;
    }

    public void insertHeadset(String body, int gameId) {
        List<String> headsetList = getHeadset(body);
        int headsetId;
        for (int i = 0; i < headsetList.size(); i++) {
            if (svrd_repository.checkHeadset(headsetList.get(i)) > 0) {
                headsetId = svrd_repository.getHeadset(headsetList.get(i));
            } else {
                headsetId = svrd_repository.insertHeadset(headsetList.get(i));
            }
            svrd_repository.insertHeadsetJoin(gameId, headsetId);
        }
    }

    public List<String> getInput(String body) {
        List<String> inputList = new ArrayList<>();
        if (body.indexOf("Gamepad</a></div>", 0) >= 0) {
            inputList.add("Gamepad");
        }
        if (body.indexOf("Keyboard / Mouse", 0) >= 0) {
            inputList.add("Keyboard / Mouse");
        }
        if (body.indexOf("Tracked Motion Controllers", 0) >= 0) {
            inputList.add("Tracked Motion Controllers");
        }
        return inputList;
    }

    public void insertInput(String body, int gameId) {
        List<String> inputList = getInput(body);
        int inputId;
        for (int i = 0; i < inputList.size(); i++) {
            if (svrd_repository.checkInputType(inputList.get(i)) > 0) {
                inputId = svrd_repository.getInputType(inputList.get(i));
            } else {
                inputId = svrd_repository.insertInputType(inputList.get(i));
            }
            svrd_repository.insertInputTypeJoin(gameId, inputId);
        }
    }

    public List<String> getPlayarea(String body) {
        List<String> playareaList = new ArrayList<>();
        if (body.indexOf("Seated", 0) >= 0) {
            playareaList.add("Seated");
        }
        if (body.indexOf("Standing", 0) >= 0) {
            playareaList.add("Standing");
        }
        if (body.indexOf("Room-Scale", 0) >= 0) {
            playareaList.add("Room-Scale");
        }
        return playareaList;
    }

    public void insertPlayarea(String body, int gameId) {
        List<String> playareaList = getPlayarea(body);
        int playareaId;
        for (int i = 0; i < playareaList.size(); i++) {
            if (svrd_repository.checkPlayarea(playareaList.get(i)) > 0) {
                playareaId = svrd_repository.getPlayarea(playareaList.get(i));
            } else {
                playareaId = svrd_repository.insertPlayarea(playareaList.get(i));
            }
            svrd_repository.insertPlayareaJoin(gameId, playareaId);
        }
    }

    public List<String> getNumberOfPlayers(String body) {
        List<String> numberOfPlayersList = new ArrayList<>();
        if (body.indexOf("Single-player", 0) >= 0) {
            numberOfPlayersList.add("Single-player");
        }
        if (body.indexOf("Online Co-Op", 0) >= 0) {
            numberOfPlayersList.add("Online Co-Op");
        }
        if (body.indexOf("Online PvP", 0) >= 0) {
            numberOfPlayersList.add("Online PvP");
        }
        if (body.indexOf("MMO</a></div>", 0) >= 0) {
            numberOfPlayersList.add("MMO");
        }
        if (body.indexOf("Cross-Platform Multiplayer", 0) >= 0) {
            numberOfPlayersList.add("Cross-Platform Multiplayer");
        }
        System.out.println(numberOfPlayersList);
        return numberOfPlayersList;
    }

    public void insertPlayerCount(String body, int gameId) {
        List<String> playerCountList = getNumberOfPlayers(body);
        int playerCountId;
        for (int i = 0; i < playerCountList.size(); i++) {
            if (svrd_repository.checkPlayercount(playerCountList.get(i)) > 0) {
                playerCountId = svrd_repository.getPlayercount(playerCountList.get(i));
            } else {
                playerCountId = svrd_repository.insertPlayercount(playerCountList.get(i));
            }
            svrd_repository.insertPlayercountJoin(gameId, playerCountId);
        }
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean validate(String username, String rawPassword) {
        String encodedPassword = "";
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public void savePassword(String password) {
        String encodedPassword = passwordEncoder.encode(password);
    }

    public void looKasutaja(String username, String password) {
        String encodedPassword = passwordEncoder.encode(password);
        svrd_repository.looKasutaja(username, encodedPassword);
    }

    public String logiSisse(String username, String password) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + 1000 * 60 * 60 * 24 * 10);
        JwtBuilder builder = Jwts.builder()
                .setExpiration(expiration)
                .setIssuedAt(now)
                .setIssuer("NASA")
                .signWith(SignatureAlgorithm.HS256, "secret")
                .claim("username", username);
        String jwt = builder.compact();

        String encodedPassword = svrd_repository.getEncodedPassword(username);
        boolean vastus = passwordEncoder.matches(password, encodedPassword);
        if (vastus) {
            return jwt;
        } else {
            return "vale";
        }
    }


    private Double parseDouble(String text) {
        try {
            return Double.valueOf(text);
        } catch (Exception e) {
            System.err.println("Can't parse to double: " + text);
        }
        return null;
    }

    private LocalDate parseDate(String dataThatINeed) {
        try {
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("d MMM, yyyy");
            return LocalDate.parse(dataThatINeed, dateFormat);
        } catch (Exception e) {
            System.err.println("Can't parse date: " + dataThatINeed);
        }
        return null;
    }


    public VrGamePageResponse getVrGameData(Integer rowLimit, Integer startId, String columnName, String orderType, String tag, String genre) {
        Integer nrOfPages = svrd_repository.getCount()/rowLimit;
        if (columnName.equals("")) {
            columnName = "vrgame.id";
        }
        if(orderType.equals("")){
            orderType="asc";
        }
        VrGamePageResponse response = new VrGamePageResponse();
        response.setData(svrd_repository.getVrGameData(rowLimit, startId-1, columnName, orderType, tag, genre));
        response.setTotalPages(nrOfPages);
        return response;
    }
}
