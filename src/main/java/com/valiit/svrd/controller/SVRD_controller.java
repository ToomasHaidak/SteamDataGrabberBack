package com.valiit.svrd.controller;

import com.valiit.svrd.dto.VRGameDTO;
import com.valiit.svrd.service.SVRD_service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SVRD_controller {

    @Autowired
    private SVRD_service svrd_service;

    @GetMapping("update/")
    public String getAllGames() {
        svrd_service.getAllGames();
        return "vastus";
    }

    @GetMapping("looKasutaja/{a}/{b}")
    public String looKasutaja(@PathVariable("a") String username, @PathVariable("b") String password) {
        svrd_service.looKasutaja(username, password);
        return "Kasutaja loodud";
    }

    @GetMapping("logiSisse/{a}/{b}")
    public String logiSisse(@PathVariable("a") String username, @PathVariable("b") String password) {
        return svrd_service.logiSisse(username, password);
    }

    @GetMapping("/getvrgamedata/")
    public VrGamePageResponse getVrGameData(@RequestParam("a") Integer rowLimit,
                                            @RequestParam("b") Integer startId,
                                            @RequestParam("c") String columnName,
                                            @RequestParam("d") String orderType,
                                            @RequestParam("e") String tag,
                                            @RequestParam("f") String genre) {
        return svrd_service.getVrGameData(rowLimit,startId, columnName, orderType, tag, genre);
    }

}
