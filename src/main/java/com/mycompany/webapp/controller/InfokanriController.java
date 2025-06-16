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
        // すべてのデータを取得
        return service.getAllInfokanri();
    }

    @DeleteMapping("/{id}")
    public void deleteData(@PathVariable Long id) {
        // IDでデータを削除
        service.deleteInfokanri(id);
    }

    @PutMapping("/{id}")
    public Infokanri updateData(@PathVariable Long id, @RequestBody Infokanri infokanri,
            @RequestHeader("X-User-Token") String userToken) {
        // IDでデータを更新
        infokanri.setId(id);
        infokanri.setUserToken(userToken);
        return service.updateInfokanri(infokanri);
    }

    @GetMapping("/report")
    public List<Infokanri> getReportData(@RequestParam(required = false) String period,
                                         @RequestParam(required = false) String startDate,
                                         @RequestParam(required = false) String endDate) {
        // レポート用データを取得
        return service.getReportData(period, startDate, endDate);
    }
}
