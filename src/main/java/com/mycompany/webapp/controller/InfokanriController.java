package com.mycompany.webapp.controller;

import org.springframework.web.bind.annotation.*;
import com.mycompany.webapp.model.Infokanri;
import com.mycompany.webapp.service.InfokanriService;
import java.util.List;

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

    @GetMapping
    public List<Infokanri> getAllData() {
        // すべてのデータを取得（テスト用）
        return service.getAllInfokanri();
    }
}
