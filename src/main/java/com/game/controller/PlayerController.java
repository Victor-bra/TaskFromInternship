package com.game.controller;

import com.game.entity.Player;
import com.game.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest")
public class PlayerController {

    private final PlayerService playerService;

    @Autowired
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping("/players")
    public List<Player> getPlayerList(@RequestParam(value = "name", required = false) String name,
                                      @RequestParam(value = "title", required = false) String title,
                                      @RequestParam(value = "race", required = false) String race,
                                      @RequestParam(value = "profession", required = false) String profession,
                                      @RequestParam(value = "after", required = false) String after,
                                      @RequestParam(value = "before", required = false) String before,
                                      @RequestParam(value = "banned", required = false) String banned,
                                      @RequestParam(value = "minExperience", required = false) String minExperience,
                                      @RequestParam(value = "maxExperience", required = false) String maxExperience,
                                      @RequestParam(value = "minLevel", required = false) String minLevel,
                                      @RequestParam(value = "maxLevel", required = false) String maxLevel,
                                      @RequestParam(value = "order", required = false) String order,
                                      @RequestParam(value = "pageNumber", defaultValue = "0") String pageNumber,
                                      @RequestParam(value = "pageSize", defaultValue = "3") String pageSize) {

        List<Player> getPlayerList = playerService.getPlayerList(name, title, race, profession, after,
                before, banned, minExperience, maxExperience, minLevel, maxLevel);

        return playerService.output(getPlayerList, order, pageNumber, pageSize);
    }

    @GetMapping("/players/count")
    public long getPlayerListCount(@RequestParam(value = "name", required = false) String name,
                                   @RequestParam(value = "title", required = false) String title,
                                   @RequestParam(value = "race", required = false) String race,
                                   @RequestParam(value = "profession", required = false) String profession,
                                   @RequestParam(value = "after", required = false) String after,
                                   @RequestParam(value = "before", required = false) String before,
                                   @RequestParam(value = "banned", required = false) String banned,
                                   @RequestParam(value = "minExperience", required = false) String minExperience,
                                   @RequestParam(value = "maxExperience", required = false) String maxExperience,
                                   @RequestParam(value = "minLevel", required = false) String minLevel,
                                   @RequestParam(value = "maxLevel", required = false) String maxLevel) {

        return playerService.getPlayerList(name, title, race, profession, after,
                before, banned, minExperience, maxExperience, minLevel, maxLevel).size();
    }


    @GetMapping("/players/{id}")
    public Player getPlayerById(@PathVariable String id) {
        long validationId = playerService.validationId(id);
        return playerService.getById(validationId);
    }

    @PostMapping("/players")
    public Player createPlayer(@RequestBody Player player) {
        playerService.createPlayer(player);
        return player;
    }

    @PostMapping("/players/{id}")
    public Player updatePlayer(@PathVariable String id, @RequestBody Player player) {
        Player playerForDB = getPlayerById(id);
        playerService.updatePlayer(playerForDB,player);
        return playerForDB;
    }

    @DeleteMapping("players/{id}")
    public String deletePlayer(@PathVariable String id){
        long validationId = playerService.validationId(id);
        playerService.getById(validationId);
        playerService.deletePlayer(validationId);
        return "Player with ID = " + id + " was deleted";
    }
}
