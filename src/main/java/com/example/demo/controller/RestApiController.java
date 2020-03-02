package com.example.demo.controller;

import com.fasterxml.jackson.databind.util.JSONPObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.Arrays;

@RestController
public class RestApiController {
    List<String> board = new ArrayList<String>(Collections.nCopies(25, ""));

    List<Integer> treasureInd = new ArrayList<>();
    List<Integer> closestInd = new ArrayList<>();
    List<Integer> secondInd = new ArrayList<>();
    List<Map<String, String>> scores = new ArrayList<Map<String, String >>();

    public RestApiController() {
        for (int i = 0; i < 3; i++) {
            Random r = new Random();
            int ind = r.nextInt((24 - 0)) + 0;
            if(!treasureInd.contains(ind)){
                this.board.set(ind, "T");
                this.treasureInd.add(ind);
            } else {
                i--;
            }
        }
        Collections.sort(this.treasureInd);
        this.treasureInd.forEach((ind) -> {
            if (ind % 5 != 0 && this.board.get(ind - 1) == "") {
                this.closestInd.add(ind - 1);
                this.board.set(ind - 1, "3");
            }
            if ((ind + 1) % 5 != 0 && this.board.get(ind + 1) == "") {
                this.closestInd.add(ind + 1);
                this.board.set(ind + 1, "3");
            }
            if (ind + 5 < 25 && this.board.get(ind + 5) == "") {
                this.closestInd.add(ind + 5);
                this.board.set(ind+5, "3");
            }
            if (ind - 5 >= 0 && this.board.get(ind - 5) == "") {
                this.closestInd.add(ind - 5);
                this.board.set(ind-5, "3");
            }
        });

        Collections.sort(this.closestInd);
        this.closestInd.forEach((i) -> {
            if (i % 5 != 0 && board.get(i - 1) == "") {
                this.secondInd.add(i - 1);
                this.board.set(i - 1, "2");
            }
            if ((i + 1) % 5 != 0 && this.board.get(i + 1) == "") {
                this.secondInd.add(i + 1);
                this.board.set(i + 1, "2");
            }
            if (i + 5 < 25 && this.board.get(i + 5) == "") {
                this.secondInd.add(i + 5);
                this.board.set(i+5, "2");
            }
            if (i - 5 >= 0 && this.board.get(i - 5) == "") {
                this.secondInd.add(i - 5);
                this.board.set(i - 5, "2");
            }
        });
        Collections.sort(this.secondInd);
    }

    @CrossOrigin
    @RequestMapping(value="/api/startGame", method= RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity startGame(@RequestBody Map<String, String> request) {
        String playerName = request.get("playerName");
        if(playerName == null || playerName == "") {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } else {
            HashMap<String, Boolean> map = new HashMap<>();
            map.put("gameStarted", true);
            return ResponseEntity.ok(map);
        }
    }

    @CrossOrigin
    @RequestMapping(value="/api/guess", method= RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public HashMap guess(@RequestBody Map<String, List<Integer>> parameters) {
        List<Integer> guesses = parameters.get("guesses");
        List<String> answers = new ArrayList<>();
        guesses.forEach((i) -> {
            answers.add(board.get(i));
        });
        HashMap<String, List<String>> response = new HashMap<>();
        response.put("answers", answers);
        return response;
    }

    @CrossOrigin
    @RequestMapping(value="/api/newGame", method= RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public String newGame(@RequestBody Map request) {
        HashMap<String, String> newGame = new HashMap<String, String>();
        newGame.put("name", request.get("playerName").toString());
        newGame.put("turn", request.get("turn").toString());
        scores.add(newGame);

        System.out.println(scores);

        return "Ok";
    }

    @CrossOrigin
    @RequestMapping(value="/api/top10", method= RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity top10() {
        HashMap<String, List> top10 = new HashMap<String, List>();
        Collections.sort(scores, valueComparator);

        if(scores.size() > 10)
            top10.put("top10", scores.subList(0, 10));
        else
            top10.put("top10", scores);

        return ResponseEntity.ok(top10);
    }

    public Comparator<Map<String, String>> valueComparator = new Comparator<Map<String,String>>() {
        @Override
        public int compare(Map<String, String> e1, Map<String, String> e2) {
            int v1 = Integer.parseInt(e1.get("turn").toString());
            int v2 = Integer.parseInt(e2.get("turn").toString());
            return v1 - v2;
        }
    };
}
