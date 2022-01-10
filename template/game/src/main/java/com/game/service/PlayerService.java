package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.exception.NotFoundException;
import com.game.exception.ValidationException;
import com.game.repository.PlayerRepository;
import com.game.utils.CalculateAttributes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PlayerService {

    private static final String NAME = "name";
    private static final String TITLE = "title";
    private static final String RACE = "race";
    private static final String PROFESSION = "profession";
    private static final String BIRTHDAY = "birthday";
    private static final String EXPERIENCE = "experience";
    private static final String BANNED = "banned";

    private PlayerRepository playerRepository;

    @Autowired
    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public Player getPlayerById(String id) {
        Long longId = getIdFromString(id);
        return playerRepository.findById(longId).orElseThrow(NotFoundException::new);
    }


    public List<Player> getPlayersByParams(String name, String title, Race race,
                                           Profession profession, Long after,
                                           Long before, Boolean banned, Integer minExperience,
                                           Integer maxExperience, Integer minLevel,
                                           Integer maxLevel, PlayerOrder order, Integer pageNumber,
                                           Integer pageSize) {
        Date startDate = (after != null ? new Date(after) : null);
        Date endDate = (before != null ? new Date(before) : null);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(order.getFieldName()));
        return playerRepository.findAllByParams(name, title, race, profession, startDate, endDate, banned,
                minExperience, maxExperience, minLevel, maxLevel, pageable);
    }

    public Integer getCountByParams(String name, String title, Race race, Profession profession, Long after,
                                        Long before, Boolean banned, Integer minExperience, Integer maxExperience,
                                        Integer minLevel, Integer maxLevel) {
        Date startDate = (after != null ? new Date(after) : null);
        Date endDate = (before != null ? new Date(before) : null);
        return playerRepository.getCountOfPlayers(name, title, race, profession, startDate,
                endDate, banned, minExperience, maxExperience, minLevel, maxLevel);
    }

    public Player createPlayer(Map<String, String> map) {
        if (!map.containsKey(NAME)
                || !map.containsKey(TITLE)
                || !map.containsKey(RACE)
                || !map.containsKey(PROFESSION)
                || !map.containsKey(BIRTHDAY)
                || !map.containsKey(EXPERIENCE)) {
            throw new ValidationException();
        }
        Player player = new Player();
        fillPlayerData(map, player);
        return playerRepository.saveAndFlush(player);
    }

    public Player updatePlayer(Map<String, String> map, String id) {
        Long longId = getIdFromString(id);
        Player player = playerRepository.findById(longId).orElseThrow(NotFoundException::new);
        fillPlayerData(map, player);
        return playerRepository.saveAndFlush(player);
    }

    public void deletePlayer(String id) {
        Long longId = getIdFromString(id);
        if (!playerRepository.existsById(longId)) {
            throw new NotFoundException();
        }
        playerRepository.deleteById(longId);
    }

    private Long getIdFromString(String stringId)  {
        try {
            Long id = Long.parseLong(stringId);
            if (id <= 0) {
                throw new NumberFormatException();
            }
            return id;
        } catch (NumberFormatException e) {
            throw new ValidationException();

        }
    }

    private void fillPlayerData(Map<String, String> map, Player player) {
        if (map.containsKey(NAME)) {
            String name = map.get(NAME);
            if (name.length() > 0
                    && name.length() <= 12) {
                player.setName(name);
            } else {
                throw new ValidationException();
            }
        }

        if (map.containsKey(TITLE)) {
            String title = map.get(TITLE);
            if (title.length() <= 30) {
                player.setTitle(title);
            } else {
                throw new ValidationException();
            }
        }

        if (map.containsKey(RACE)) {
            Race race = Race.valueOf(map.get(RACE));
            player.setRace(race);
        }

        if (map.containsKey(PROFESSION)) {
            Profession profession = Profession.valueOf(map.get(PROFESSION));
            player.setProfession(profession);
        }

        if (map.containsKey(BIRTHDAY)) {
            long birthdayLong = Long.parseLong(map.get(BIRTHDAY));
            if (birthdayLong < 946663200000L ||
                    birthdayLong > 32535104400000L) {
                throw new ValidationException();
            }
            Date birthday = new Date(birthdayLong);
            player.setBirthday(birthday);
        }

        if (map.containsKey(EXPERIENCE)) {
            int experienceInteger = Integer.parseInt(map.get(EXPERIENCE));
            if (experienceInteger >= 0
                    && experienceInteger <= 10000000) {
                player.setExperience(experienceInteger);
            } else {
                throw new ValidationException();
            }
        }

        if (map.containsKey(NAME)) {
            player.setBanned(Boolean.parseBoolean(map.get(BANNED)));
        }

        player.setLevel(CalculateAttributes.getCurrentLevel(player.getExperience()));
        player.setUntilNextLevel(CalculateAttributes.
                getExperienceToNextLevel(player.getLevel(), player.getExperience()));
    }

}
