package com.game.controller;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/rest")
public class PlayerController {


    private PlayerService playerService;

    @Autowired
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping("/players")
    public List<Player> getPlayersByParams(@RequestParam(required = false) String name,
                                   @RequestParam(required = false) String title,
                                   @RequestParam(required = false) Race race,
                                   @RequestParam(required = false) Profession profession,
                                   @RequestParam(required = false) Long after,
                                   @RequestParam(required = false) Long before,
                                   @RequestParam(required = false) Boolean banned,
                                   @RequestParam(required = false) Integer minExperience,
                                   @RequestParam(required = false) Integer maxExperience,
                                   @RequestParam(required = false) Integer minLevel,
                                   @RequestParam(required = false) Integer maxLevel,
                                   @RequestParam(defaultValue = "ID") PlayerOrder order,
                                   @RequestParam(defaultValue = "0") Integer pageNumber,
                                   @RequestParam(defaultValue = "3") Integer pageSize) {
        return playerService.getPlayersByParams(name, title, race, profession, after,  before,
                banned, minExperience, maxExperience, minLevel, maxLevel, order, pageNumber, pageSize);
    }

    @GetMapping("/players/{id}")
    public Player getPlayerById(@PathVariable String id) {
        return playerService.getPlayerById(id);
    }

    @GetMapping("/players/count")
    public Integer getPlayersByParams(@RequestParam(required = false) String name,
                                             @RequestParam(required = false) String title,
                                             @RequestParam(required = false) Race race,
                                             @RequestParam(required = false) Profession profession,
                                             @RequestParam(required = false) Long after,
                                             @RequestParam(required = false) Long before,
                                             @RequestParam(required = false) Boolean banned,
                                             @RequestParam(required = false) Integer minExperience,
                                             @RequestParam(required = false) Integer maxExperience,
                                             @RequestParam(required = false) Integer minLevel,
                                             @RequestParam(required = false) Integer maxLevel) {
        return playerService.getCountByParams(name, title, race, profession, after,  before,
                banned, minExperience, maxExperience, minLevel, maxLevel);
    }

    @PostMapping("/players")
    public Player createPlayer(@RequestBody Map<String, String> map) {
        return playerService.createPlayer(map);
    }

    @PostMapping("/players/{id}")
    public Player updatePlayer(@RequestBody Map<String, String> map, @PathVariable String id) {
        return playerService.updatePlayer(map, id);
    }

    @DeleteMapping("/players/{id}")
    public void deletePlayer(@PathVariable String id) {
        playerService.deletePlayer(id);
    }

}
