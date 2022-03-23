package com.valiit.svrd.repository;

import com.valiit.svrd.dto.VRGameDTO;
import io.jsonwebtoken.impl.JwtMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class SVRD_repository {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public void getAllGames(String appID, String nimi) {
        String sql = "insert into vrgame(app_id, game_name) values (:a, :b)";
        Map<String, Object> paraMap = new HashMap<>();
        paraMap.put("a", appID);
        paraMap.put("b", nimi);
        jdbcTemplate.update(sql, paraMap);
    }

    public Integer checkIfGameInDatabase(String appId) {
        String sql = "select count(*) from vrgame where app_id = :a";
        Map<String, Object> paraMap = new HashMap<>();
        paraMap.put("a", appId);
        return jdbcTemplate.queryForObject(sql, paraMap, Integer.class);
    }

    public Double checkLowestHistoricPrice(String appId) {
        String sql = "select lowest_historic_price from vrgame where app_id =:a";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("a", appId);
        return jdbcTemplate.queryForObject(sql, paramMap, Double.class);
    }

    public Integer checkIfPublisherInDatabase(String name) {
        String sql = "select count(*) from publisher where publisher = :a";
        Map<String, Object> paraMap = new HashMap<>();
        paraMap.put("a", name);
        return jdbcTemplate.queryForObject(sql, paraMap, Integer.class);
    }

    public void insertOneGamePublisher(String publisher) {
        String sql = "insert into publisher(publisher) values(:a)";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("a", publisher);
        jdbcTemplate.update(sql, paramMap);
    }

    public Integer insertOneGameDataValues(String appID, String nimi, Double currentPrice, Double currentDiscountedPrice,
                                           Double lowestHistoricPrice, LocalDate releaseDate, boolean earlyAccess,
                                           Double metascore, int reviews, Double positiveReviews,
//                                           String osRecommended,
//                                           String processorRecommended, String memoryRecommended,
//                                           String graphicsRecommended, String directxRecommended, String networkRecommended, String storageRecommended,
//                                           String osMinimum, String processorMinimum, String memoryMinimum,
//                                           String graphicsMinimum, String directxMinimum, String networkMinimum, String storageMinimum,
                                           String about, int publisher) {
        String sql = "insert into vrgame(current_price, current_discounted_price, lowest_historic_price, " +
                "release_date, early_access, metascore, all_reviews, positive_reviews, " +
//                "os_recommended, " +
//                "processor_recommended, memory_recommended, graphics_recommended, directx_recommended, network_recommended, " +
//                "storage_recommended, os_minimum, processor_minimum, memory_minimum, graphics_minimum, directx_minimum, network_minimum, " +
//                "storage_minimum, " +
                "about, publisher_fid, app_id, game_name) " +
                "values(:a1, :a2, :a3, :a4," +
                " :a5, :a6, :a7, :a8, " +
//                ":a9, :a10, :a11, :a12, :a13, :a14, :a15, :a16, :a17, :a18, :a19, :a20, :a21, " +
//                ":a22, " +
                ":a23, :a24, :a, :b)";
        Map<String, Object> paraMap = new HashMap<>();
        paraMap.put("a1", currentPrice);
        paraMap.put("a2", currentDiscountedPrice);
        paraMap.put("a3", lowestHistoricPrice);
        paraMap.put("a4", releaseDate);
        paraMap.put("a5", earlyAccess);
        paraMap.put("a6", metascore);
        paraMap.put("a7", reviews);
        paraMap.put("a8", positiveReviews);
//        paraMap.put("a9", osRecommended);
//        paraMap.put("a10", processorRecommended);
//        paraMap.put("a11", memoryRecommended);
//        paraMap.put("a12", graphicsRecommended);
//        paraMap.put("a13", directxRecommended);
//        paraMap.put("a14", networkRecommended);
//        paraMap.put("a15", storageRecommended);
//        paraMap.put("a16", osMinimum);
//        paraMap.put("a17", processorMinimum);
//        paraMap.put("a18", memoryMinimum);
//        paraMap.put("a19", graphicsMinimum);
//        paraMap.put("a20", directxMinimum);
//        paraMap.put("a21", networkMinimum);
//        paraMap.put("a22", storageMinimum);
        paraMap.put("a23", about);
        paraMap.put("a24", publisher);
        paraMap.put("a", appID);
        paraMap.put("b", nimi);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, new MapSqlParameterSource(paraMap), keyHolder);
        return (Integer) keyHolder.getKeys().get("id");

    }

    public void updateGameData(String appId, Double currentPrice, Double currentDiscountedPrice, double v, LocalDate releaseDate, boolean earlyAccess, Double metascore, int reviews, Double positiveReviews) {
        String sql = "update vrgame set current_price = :a1, lowest_historic_price = :a2, release_date = :a3," +
                " early_access = :a4, metascore = :a5, all_reviews = :a6, positive_reviews = :a7, current_discounted_price = :a8 where" +
                " app_id = :a";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("a1", currentPrice);
        paramMap.put("a2", v);
        paramMap.put("a3", releaseDate);
        paramMap.put("a4", earlyAccess);
        paramMap.put("a5", metascore);
        paramMap.put("a6", reviews);
        paramMap.put("a7", positiveReviews);
        paramMap.put("a8", currentDiscountedPrice);
        paramMap.put("a", appId);
        jdbcTemplate.update(sql, paramMap);
    }

    public Integer insertPublisher(String name) {
        String sql = "insert into publisher (publisher) values (:nimi)";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("nimi", name);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, new MapSqlParameterSource(paramMap), keyHolder);
        return (Integer) keyHolder.getKeys().get("id");
    }


    public Integer getPublisherId(String name) {
        String sql = "SELECT id FROM publisher WHERE publisher = :a";
        Map<String, Object> paraMap = new HashMap<>();
        paraMap.put("a", name);
        return jdbcTemplate.queryForObject(sql, paraMap, Integer.class);
    }

    public void looKasutaja(String username, String encodedPassword) {
        String sql = "insert into kasutaja(username, encoded_password) values(:a, :b)";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("a", username);
        paramMap.put("b", encodedPassword);
        jdbcTemplate.update(sql, paramMap);
    }

    public String getEncodedPassword(String username) {
        String sql = "select encoded_password from kasutaja where username = :a";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("a", username);
        return jdbcTemplate.queryForObject(sql, paramMap, String.class);
    }

    public int checkDeveloper(String data) {
        String sql = "select count(*) from developer where developer =:a";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("a", data);
        return jdbcTemplate.queryForObject(sql, paramMap, Integer.class);

    }

    public int getDeveloper(String data) {
        String sql = "select id from developer where developer = :a";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("a", data);
        return jdbcTemplate.queryForObject(sql, paramMap, Integer.class);
    }


    public int insertDeveloper(String data) {
        String sql = "insert into developer(developer) values(:a)";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("a", data);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, new MapSqlParameterSource(paramMap), keyHolder);
        return (Integer) keyHolder.getKeys().get("id");
    }

    public void insertDeveloperJoin(int gameId, int developerId) {
        String sql = "insert into vrgame_developer_join(vrgame_id, developer_id) values(:a, :b)";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("a", gameId);
        paramMap.put("b", developerId);
        jdbcTemplate.update(sql, paramMap);
    }

    public int checkPlayarea(String data) {
        String sql = "select count(*) from playarea where playarea =:a";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("a", data);
        return jdbcTemplate.queryForObject(sql, paramMap, Integer.class);

    }

    public int getPlayarea(String data) {
        String sql = "select id from playarea where playarea = :a";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("a", data);
        return jdbcTemplate.queryForObject(sql, paramMap, Integer.class);
    }

    public int insertPlayarea(String data) {
        String sql = "insert into playarea(playarea) values(:a)";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("a", data);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, new MapSqlParameterSource(paramMap), keyHolder);
        return (Integer) keyHolder.getKeys().get("id");
    }

    public void insertPlayareaJoin(int gameId, int playareaId) {
        String sql = "insert into vrgame_playarea_join(vrgame_id, playarea_id) values(:a, :b)";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("a", gameId);
        paramMap.put("b", playareaId);
        jdbcTemplate.update(sql, paramMap);
    }

    public int checkPlayercount(String data) {
        String sql = "select count(*) from player_count where number_of_players =:a";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("a", data);
        return jdbcTemplate.queryForObject(sql, paramMap, Integer.class);

    }

    public int getPlayercount(String data) {
        String sql = "select id from player_count where number_of_players = :a";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("a", data);
        return jdbcTemplate.queryForObject(sql, paramMap, Integer.class);
    }

    public int insertPlayercount(String data) {
        String sql = "insert into player_count(number_of_players) values(:a)";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("a", data);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, new MapSqlParameterSource(paramMap), keyHolder);
        return (Integer) keyHolder.getKeys().get("id");
    }

    public void insertPlayercountJoin(int gameId, int playercountId) {
        String sql = "insert into vrgame_player_count_join(vrgame_id, player_count_id) values(:a, :b)";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("a", gameId);
        paramMap.put("b", playercountId);
        jdbcTemplate.update(sql, paramMap);
    }

    public int checkTag(String data) {
        String sql = "select count(*) from tag where tag =:a";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("a", data);
        return jdbcTemplate.queryForObject(sql, paramMap, Integer.class);

    }

    public int getTag(String data) {
        String sql = "select id from tag where tag = :a";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("a", data);
        return jdbcTemplate.queryForObject(sql, paramMap, Integer.class);
    }

    public int insertTag(String data) {
        String sql = "insert into tag(tag) values(:a)";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("a", data);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, new MapSqlParameterSource(paramMap), keyHolder);
        return (Integer) keyHolder.getKeys().get("id");
    }

    public void insertTagJoin(int gameId, int tagId) {
        String sql = "insert into vrgame_tag_join(vrgame_id, tag_id) values(:a, :b)";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("a", gameId);
        paramMap.put("b", tagId);
        jdbcTemplate.update(sql, paramMap);
    }

    public int checkGenre(String data) {
        String sql = "select count(*) from genre where genre_type =:a";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("a", data);
        return jdbcTemplate.queryForObject(sql, paramMap, Integer.class);
    }

    public int getGenre(String data) {
        String sql = "select id from genre where genre_type = :a";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("a", data);
        return jdbcTemplate.queryForObject(sql, paramMap, Integer.class);
    }

    public int insertGenre(String data) {
        String sql = "insert into genre(genre_type) values(:a)";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("a", data);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, new MapSqlParameterSource(paramMap), keyHolder);
        return (Integer) keyHolder.getKeys().get("id");
    }

    public void insertGenreJoin(int gameId, int genreId) {
        String sql = "insert into vrgame_genre_join(vrgame_id, genre_id) values(:a, :b)";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("a", gameId);
        paramMap.put("b", genreId);
        jdbcTemplate.update(sql, paramMap);
    }

    public int checkHeadset(String data) {
        String sql = "select count(*) from headset where headset_type =:a";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("a", data);
        return jdbcTemplate.queryForObject(sql, paramMap, Integer.class);

    }

    public int getHeadset(String data) {
        String sql = "select id from headset where headset_type = :a";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("a", data);
        return jdbcTemplate.queryForObject(sql, paramMap, Integer.class);
    }

    public int insertHeadset(String data) {
        String sql = "insert into headset(headset_type) values(:a)";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("a", data);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, new MapSqlParameterSource(paramMap), keyHolder);
        return (Integer) keyHolder.getKeys().get("id");
    }

    public void insertHeadsetJoin(int gameId, int headsetId) {
        String sql = "insert into vrgame_headset_join(vrgame_id, headset_id) values(:a, :b)";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("a", gameId);
        paramMap.put("b", headsetId);
        jdbcTemplate.update(sql, paramMap);
    }

    public int checkInputType(String data) {
        String sql = "select count(*) from input_type where input_type =:a";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("a", data);
        return jdbcTemplate.queryForObject(sql, paramMap, Integer.class);

    }

    public int getInputType(String data) {
        String sql = "select id from input_type where input_type = :a";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("a", data);
        return jdbcTemplate.queryForObject(sql, paramMap, Integer.class);
    }

    public int insertInputType(String data) {
        String sql = "insert into input_type(input_type) values(:a)";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("a", data);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, new MapSqlParameterSource(paramMap), keyHolder);
        return (Integer) keyHolder.getKeys().get("id");
    }

    public void insertInputTypeJoin(int gameId, int inputTypeId) {
        String sql = "insert into vrgame_input_type_join(vrgame_id, input_type_id) values(:a, :b)";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("a", gameId);
        paramMap.put("b", inputTypeId);
        jdbcTemplate.update(sql, paramMap);
    }

    public List<VRGameDTO> getVrGameData(Integer rowLimit, Integer startId, String columnName, String orderType, String tag, String genre) {
        String sql = "SELECT vrgame.id,\n" +
                "       game_name,\n" +
                "       current_price,\n" +
                "       metascore,\n" +
                "       current_discounted_price,\n" +
                "       all_reviews,\n" +
                "       positive_reviews,\n" +
                "       release_date,\n" +
                "       String_agg(distinct tag, ', ') as tagagg,\n" +
                "       String_agg(distinct genre_type, ', ') as genreagg\n" +
                "FROM vrgame\n" +
                "         join vrgame_genre_join vgj on vrgame.id = vgj.vrgame_id\n" +
                "         join genre g on vgj.genre_id = g.id\n" +
                "         join vrgame_tag_join vtj on vrgame.id = vtj.vrgame_id\n" +
                "         join tag t on vtj.tag_id = t.id\n" +
                "WHERE tag ILIKE '%" +tag+ "%' AND genre_type ILIKE '%" +genre+ "%' " +
                " GROUP BY vrgame.id, " + columnName +
                " ORDER BY " + columnName + " " + orderType + " LIMIT :a OFFSET :b";
        Map<String, Object> paraMap = new HashMap<>();
        paraMap.put("a", rowLimit);
        paraMap.put("b",startId);
        List<VRGameDTO> väärtus = jdbcTemplate.query(sql, paraMap, new SampleVRGameDTORowMapper());
        return väärtus;
    }

    public int getCount() { // loeb kokku kirjed vrgame tabelis
        String sql = "SELECT count(*) FROM vrgame";
        Map<String, Object> paramMap = new HashMap<>();
        return jdbcTemplate.queryForObject(sql, paramMap, Integer.class);
    }

    public static class SampleVRGameDTORowMapper implements RowMapper<VRGameDTO> {

        @Override
        public VRGameDTO mapRow(ResultSet resultSet, int i) throws SQLException {
            VRGameDTO result = new VRGameDTO();
            result.setGame_id(resultSet.getInt("id"));
            result.setGameName(resultSet.getString("game_name"));
            result.setMetascore(resultSet.getDouble("metascore"));
            result.setDiscountedPrice(resultSet.getDouble("current_discounted_price"));
            result.setReviews(resultSet.getInt("all_reviews"));
            result.setPositiveReviews(resultSet.getDouble("positive_reviews"));
            result.setReleaseDate(resultSet.getObject("release_date", LocalDateTime.class));
            result.setGenre(resultSet.getString("genreagg"));
            result.setTag(resultSet.getString("tagagg"));
            result.setCurrentPrice(resultSet.getDouble("current_price"));
            return result;
        }
    }

}
