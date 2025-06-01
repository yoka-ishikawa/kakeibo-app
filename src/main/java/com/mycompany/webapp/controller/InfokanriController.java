package com.mycompany.webapp.controller;

import org.springframework.web.bind.annotation.*;
import com.mycompany.webapp.model.Infokanri;
import com.mycompany.webapp.service.InfokanriService;

@RestController
@RequestMapping("/api/infokanri")
public class InfokanriController {

    private final InfokanriService service;

    public InfokanriController(InfokanriService service) {
        this.service = service;
    }

    @PostMapping
    public Infokanri add(@RequestBody Infokanri infokanri,
            @RequestHeader("X-User-Token") String userToken) {
        // ユーザートークンをInfokanriオブジェクトに設定
        infokanri.setUserToken(userToken);
        // Infokanriオブジェクトを保存
        return service.saveInfokanri(infokanri);
    }
}
