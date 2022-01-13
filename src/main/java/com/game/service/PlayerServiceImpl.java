package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.exceptions.BadRequestException;
import com.game.exceptions.ResourceNotFoundException;
import com.game.repository.PlayerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepo playerRepo;

    @Autowired
    public PlayerServiceImpl(PlayerRepo playerRepo) {
        this.playerRepo = playerRepo;
    }

    @Override
    @Transactional
    public Player getById(long id) {
        Player player = null;
        Optional<Player> optional = playerRepo.findById(id);
        if (optional.isPresent()) {
            player = optional.get();
            return player;
        } else {
            throw new ResourceNotFoundException();//можно дописать класс исключения
        }
    }

    @Override
    @Transactional
    public long validationId(String id) {
        if (id == null || id.equals("") || id.equals("0"))
            throw new BadRequestException("Exception for method (validationID): Id value is not valid");
        try {
            long longID = Long.parseLong(id);
            return longID;
        } catch (Exception e) {
            throw new BadRequestException("Exception for method (validationID): Id is not a number");
        }
    }

    @Override
    @Transactional
    public List<Player> getPlayerList(String name, String title, String race, String profession, String after, String before,
                                      String banned, String minExperience, String maxExperience, String minLevel, String maxLevel) {
        List<Player> allPlayer = playerRepo.findAll();

        if (name != null)
            allPlayer = allPlayer.stream().filter(player -> player.getName().toLowerCase().
                    contains(name.toLowerCase())).collect(Collectors.toList());

        if (title != null)
            allPlayer = allPlayer.stream().filter(player -> player.getTitle().toLowerCase().
                    contains(title.toLowerCase())).collect(Collectors.toList());

        if (race != null)
            allPlayer = allPlayer.stream().filter(player -> player.getRace().equals(Race.valueOf(race))).collect(Collectors.toList());

        if (profession != null)
            allPlayer = allPlayer.stream().filter(
                    player -> player.getProfession().equals(Profession.valueOf(profession))).collect(Collectors.toList());

        if (after != null)
            allPlayer = allPlayer.stream().filter(player -> player.getBirthday().after(new Date(Long.parseLong(after)))).collect(Collectors.toList());

        if (before != null)
            allPlayer = allPlayer.stream().filter(player -> player.getBirthday().before(new Date(Long.parseLong(before)))).collect(Collectors.toList());

        if (banned != null)
            allPlayer = allPlayer.stream().filter(player -> player.getBanned() == Boolean.parseBoolean(banned)).collect(Collectors.toList());

        if (minExperience != null || maxExperience != null) {
            int minExper = minExperience == null ? 0 : Integer.parseInt(minExperience);
            int maxExper = maxExperience == null ? 10_000_000 : Integer.parseInt(maxExperience);

            allPlayer = allPlayer.stream().filter(player -> (player.getExperience() >= minExper
                    && player.getExperience() <= maxExper)).collect(Collectors.toList());
        }

        if (minLevel != null || maxLevel != null) {
            int minLv = minLevel == null ? 0 : Integer.parseInt(minLevel);
            int maxLv = maxLevel == null ? Integer.MAX_VALUE : Integer.parseInt(maxLevel);
            allPlayer = allPlayer.stream().filter(player -> player.getLevel() >= minLv
                    && player.getLevel() <= maxLv).collect(Collectors.toList());
        }
        return allPlayer;
    }

    @Override
    @Transactional
    public List<Player> output(List<Player> getPlayerList, String order, String pageNumber, String pageSize) {
        PlayerOrder pOrder = PlayerOrder.ID;
        if (order != null)
            pOrder = PlayerOrder.valueOf(order);

        int pNumber = Integer.parseInt(pageNumber);
        int pSize = Integer.parseInt(pageSize);

        PlayerOrder finalPOrder = pOrder;
        Comparator<Player> comparator = new Comparator<Player>() {
            @Override
            public int compare(Player o1, Player o2) {
                switch (finalPOrder) {
                    case ID:
                        return o1.getId() - o2.getId() > 0 ? 1 :
                                o1.getId() - o2.getId() < 0 ? -1 : 0;
                    case NAME:
                        return o1.getName().compareTo(o2.getName());
                    case EXPERIENCE:
                        return o1.getExperience() - o2.getExperience();
                    case BIRTHDAY:
                        return o1.getBirthday().compareTo(o2.getBirthday());
                }
                return 0;
            }
        };

        getPlayerList.sort(comparator);
        int initPage = pSize * pNumber;
        int finalPage = Math.min(pSize * (pNumber + 1), getPlayerList.size());
        return getPlayerList.subList(initPage, finalPage);
    }

    @Override
    @Transactional
    public void createPlayer(Player player) {
        player = checkPlayers(player);
        player = checkingPlayerForCorrectInput(player);
        player = calculatedParameters(player);
        playerRepo.save(player);
    }

    @Override
    @Transactional
    public void updatePlayer(Player playerForDB, Player player) {
        player = checkingPlayerForCorrectInput(player);
        if(player.getName()!= null)
            playerForDB.setName(player.getName());
        if(player.getTitle()!= null)
            playerForDB.setTitle(player.getTitle());
        if(player.getRace()!=null)
            playerForDB.setRace(player.getRace());
        if(player.getProfession() != null)
            playerForDB.setProfession(player.getProfession());
        if(player.getBirthday() != null)
            playerForDB.setBirthday(player.getBirthday());
        if(player.getExperience()!= null){
            playerForDB.setExperience(player.getExperience());
            playerForDB = calculatedParameters(playerForDB);
        }
        if(player.getBanned()!=null)
            playerForDB.setBanned(player.getBanned());
        playerRepo.save(playerForDB);
    }

    @Override
    @Transactional
    public void deletePlayer(long id) {
        playerRepo.deleteById(id);
    }

    public Player checkPlayers(Player player) {
        if (player.getName() == null
                || player.getTitle() == null
                || player.getRace() == null
                || player.getProfession() == null
                || player.getBirthday() == null
                || player.getExperience() == null) {
            throw new BadRequestException("Введены не все параметры");
        }
        if (player.getBanned() == null)
            player.setBanned(false);
        return player;
    }

    public Player checkingPlayerForCorrectInput(Player player){
        if (player.getName()!= null && (player.getName().length() < 1 || player.getName().length() > 12))
            throw new BadRequestException("Некорректная длинна поля 'name'");
        if (player.getTitle()!= null && (player.getTitle().length() < 0 || player.getTitle().length() > 30))
            throw new BadRequestException("Некорректная длинна поля 'title'");
        if (player.getExperience()!= null && (player.getExperience() < 0 || player.getExperience() > 10_000_000))
            throw new BadRequestException("Опыт персонажа должен находится в пределах 0-10000000");


        if(player.getBirthday()!= null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(player.getBirthday());
            if (calendar.get(Calendar.YEAR) < 2000 || calendar.get(Calendar.YEAR) > 3000)
                throw new BadRequestException("Дата рождения не корректна");
        }
        return player;
    }

    public Player calculatedParameters(Player player) {
        int level = (int) Math.floor((Math.sqrt(2500 + 200 * player.getExperience()) - 50) / 100);
        player.setLevel(level);
        int untilNextLevel = 50 * (level + 1) * (level + 2) - player.getExperience();
        player.setUntilNextLevel(untilNextLevel);
        return player;
    }
}
