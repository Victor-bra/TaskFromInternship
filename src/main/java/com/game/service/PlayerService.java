package com.game.service;

import com.game.entity.Player;

import java.util.List;

public interface PlayerService {
    Player getById(long id);

    long validationId(String id);

    List<Player> getPlayerList(String name, String title, String race, String profession, String after, String before,
                               String banned, String minExperience, String maxExperience, String minLevel, String maxLevel);

    List<Player> output(List<Player> getPlayerList, String order, String pageNumber, String pageSize);

    void createPlayer(Player player);

    void updatePlayer(Player playerForDB, Player player);

    void deletePlayer(long id);
}
